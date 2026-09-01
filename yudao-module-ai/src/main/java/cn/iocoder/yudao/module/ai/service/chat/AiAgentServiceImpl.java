package cn.iocoder.yudao.module.ai.service.chat;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.tenant.core.util.TenantUtils;
import cn.iocoder.yudao.module.ai.controller.admin.chat.vo.message.MessageSendReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.chat.AiChatConversationDO;
import cn.iocoder.yudao.module.ai.dal.dataobject.chat.AiChatMessageDO;
import cn.iocoder.yudao.module.ai.dal.dataobject.chat.AiChatRoleDO;
import cn.iocoder.yudao.module.ai.dal.dataobject.knowledge.AiKnowledgeSegmentDO;
import cn.iocoder.yudao.module.ai.dal.dataobject.model.AiModelDO;
import cn.iocoder.yudao.module.ai.enums.AiMessageTypeEnum;
import cn.iocoder.yudao.module.ai.enums.AiPlatformEnum;
import cn.iocoder.yudao.module.ai.framework.ai.AiModelFactory;
import cn.iocoder.yudao.module.ai.framework.chat.MedicalChatTools;
import cn.iocoder.yudao.module.ai.framework.chat.stream.AnthropicChatStreamFormatter;
import cn.iocoder.yudao.module.ai.framework.chat.stream.AiChatStreamFormatter;
import cn.iocoder.yudao.module.ai.framework.chat.stream.GeminiChatStreamFormatter;
import cn.iocoder.yudao.module.ai.framework.chat.stream.OpenAiChatStreamFormatter;
import cn.iocoder.yudao.module.ai.service.medical.AiMedicalDepartmentService;
import cn.iocoder.yudao.module.ai.service.medical.AiMedicalDrugService;
import cn.iocoder.yudao.module.ai.service.medical.AiMedicalRecordService;
import cn.iocoder.yudao.module.ai.service.medical.AiMedicalScheduleService;
import cn.iocoder.yudao.module.ai.service.model.AiModelService;
import cn.iocoder.yudao.module.ai.service.knowledge.AiKnowledgeService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.metadata.ChatResponseMetadata;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.ai.enums.ErrorCodeConstants.CHAT_PROCESS_ERROR;

/**
 * 医疗 AI Agent Service 实现类
 *
 * 编排：会话上下文 + 角色系统提示词 + 知识库 RAG + 医疗工具调用，
 * 并将 Spring AI 的 ChatResponse 流翻译为模型平台的官方流式协议输出
 *
 * @author 芋道源码
 */
@Slf4j
@Service
@Validated
public class AiAgentServiceImpl implements AiAgentService {

    /**
     * SSE 超时时间：5 分钟
     */
    private static final long SSE_TIMEOUT = 5 * 60 * 1000L;

    /**
     * 未绑定角色时的默认医疗助手系统提示词
     */
    private static final String DEFAULT_MEDICAL_SYSTEM_PROMPT = """
            你是医院的医疗健康咨询助手。你可以解答常见健康问题、提供就医指引。
            工作规范：
            1. 你不能给出确定性诊断，不能开具处方；症状持续或加重时，建议患者及时就医。
            2. 如症状紧急（胸痛、呼吸困难、大出血、意识模糊等），立即建议拨打 120 或前往急诊科。
            3. 回复末尾附上提醒：「以上内容仅供参考，不能替代专业医疗建议。」
            """;

    /**
     * RAG 注入模板
     */
    private static final String RAG_CONTEXT_PREFIX = "【参考资料】以下是检索到的院内知识库内容，回答时请优先参考：\n\n";

    /**
     * 会话标题生成提示词（首轮提问后自动重命名会话）
     */
    private static final String CONVERSATION_TITLE_PROMPT = """
            请根据下面的用户问题，为本次对话生成一个简短的中文标题。
            要求：不超过 12 个字；不要引号、句号、感叹号或任何前缀说明；直接输出标题本身。

            用户问题：
            """;

    /**
     * 会话标题生成的最长等待时间（超时后使用问题前缀兜底）
     */
    private static final Duration TITLE_TIMEOUT = Duration.ofSeconds(60);

    @Resource
    private AiChatConversationService conversationService;
    @Resource
    private AiChatMessageService messageService;
    @Resource
    private AiChatRoleService roleService;
    @Resource
    private AiModelService modelService;
    @Resource
    private AiModelFactory modelFactory;
    @Resource
    private AiKnowledgeService knowledgeService;
    @Resource
    private AiMedicalDepartmentService departmentService;
    @Resource
    private AiMedicalDrugService drugService;
    @Resource
    private AiMedicalScheduleService scheduleService;
    @Resource
    private AiMedicalRecordService medicalRecordService;

    @Override
    public SseEmitter sendStream(Long userId, Long tenantId, MessageSendReqVO reqVO) {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);
        // ========== 1. 校验会话归属、角色与模型（当前线程已具备租户上下文）==========
        AiChatConversationDO conversation = conversationService.getConversationMy(userId, reqVO.getConversationId());
        AiChatRoleDO role = conversation.getRoleId() != null ? roleService.getChatRole(conversation.getRoleId()) : null;
        AiModelDO model = modelService.validateChatModel(conversation.getModelId());

        // ========== 2. 查询历史消息（保存当前消息之前），并保存用户新消息 ==========
        int maxContexts = conversation.getMaxContexts() != null ? conversation.getMaxContexts() : 10;
        List<AiChatMessageDO> history = messageService.getMessageListByConversationIdWithLimit(
                conversation.getId(), maxContexts);
        messageService.createMessage(conversation.getId(), userId,
                AiMessageTypeEnum.USER.getType(), model.getModel(), reqVO.getContent(), null);

        boolean firstRound = history.isEmpty();

        // ========== 3. 知识库 RAG 检索（失败不阻断对话）==========
        String ragContext = buildRagContext(role, reqVO.getContent());

        // ========== 4. 组装 ChatClient：系统提示词 + 历史 + 医疗工具 + 平台级 Options ==========
        AiPlatformEnum platform = modelFactory.getChatPlatform(model);
        ChatModel chatModel = modelFactory.getOrCreateChatModel(model);
        ChatClient chatClient = ChatClient.create(chatModel);
        String systemPrompt = role != null && StrUtil.isNotBlank(role.getSystemPrompt())
                ? role.getSystemPrompt() : DEFAULT_MEDICAL_SYSTEM_PROMPT;
        String userContent = ragContext != null ? RAG_CONTEXT_PREFIX + ragContext + "\n\n【用户问题】" + reqVO.getContent()
                : reqVO.getContent();

        List<Message> historyMessages = new ArrayList<>();
        for (AiChatMessageDO historyMessage : history) {
            if (AiMessageTypeEnum.USER.getType().equals(historyMessage.getType())) {
                historyMessages.add(new UserMessage(historyMessage.getContent()));
            } else if (AiMessageTypeEnum.ASSISTANT.getType().equals(historyMessage.getType())) {
                historyMessages.add(new AssistantMessage(historyMessage.getContent()));
            }
        }

        ChatOptions chatOptions = modelFactory.buildChatOptions(model,
                conversation.getTemperature(), conversation.getMaxTokens());

        MedicalChatTools tools = new MedicalChatTools(tenantId, userId, conversation.getId(),
                departmentService, drugService, scheduleService, medicalRecordService);

        // ========== 5. 按平台官方协议流式输出，结束后落库 ==========
        AiChatStreamFormatter formatter = switch (platform) {
            case ANTHROPIC -> new AnthropicChatStreamFormatter(model.getModel());
            case GEMINI -> new GeminiChatStreamFormatter(model.getModel());
            default -> new OpenAiChatStreamFormatter(model.getModel());
        };
        StringBuilder contentBuffer = new StringBuilder();
        AtomicReference<Disposable> disposableRef = new AtomicReference<>();
        AtomicReference<Usage> usageRef = new AtomicReference<>();
        Flux<ChatResponse> flux = chatClient.prompt()
                .system(systemPrompt)
                .messages(historyMessages)
                .user(userContent)
                .options(chatOptions)
                .tools(tools)
                .stream()
                .chatResponse();

        if (!formatter.onStart(emitter)) {
            return emitter;
        }
        Disposable disposable = flux.subscribe(
                // onNext：翻译为官方增量帧推送，并累计文本与用量
                chunk -> {
                    if (!formatter.onDelta(emitter, chunk)) {
                        Disposable current = disposableRef.get();
                        if (current != null) {
                            current.dispose();
                        }
                        return;
                    }
                    AssistantMessage output = AiChatStreamFormatter.getOutput(chunk);
                    if (output != null && StrUtil.isNotBlank(output.getText())) {
                        contentBuffer.append(output.getText());
                    }
                    ChatResponseMetadata metadata = chunk.getMetadata();
                    if (metadata != null && metadata.getUsage() != null) {
                        usageRef.set(metadata.getUsage());
                    }
                },
                // onError：保存已生成的部分内容，推送官方错误帧；首轮提问时同样触发标题生成
                error -> {
                    log.error("[sendStream] 对话失败，conversationId={}", conversation.getId(), error);
                    String partialContent = contentBuffer.toString();
                    if (StrUtil.isNotBlank(partialContent)) {
                        TenantUtils.execute(tenantId, () -> messageService.createMessage(conversation.getId(), userId,
                                AiMessageTypeEnum.ASSISTANT.getType(), model.getModel(), partialContent, null));
                    }
                    if (firstRound) {
                        renameConversationByQuestionAsync(conversation, model, tenantId, userId, reqVO.getContent());
                    }
                    formatter.onError(emitter, resolveErrorMessage(error));
                    emitter.complete();
                },
                // onComplete：保存 AI 回复（含 token 用量），推送收尾帧与终止符；首轮提问后生成会话标题
                () -> {
                    Usage usage = usageRef.get();
                    TenantUtils.execute(tenantId, () -> messageService.createMessage(conversation.getId(), userId,
                            AiMessageTypeEnum.ASSISTANT.getType(), model.getModel(), contentBuffer.toString(),
                            usage != null ? usage.getTotalTokens() : null));
                    // 标题生成放在主流式调用之后，避免与回复请求并发触发中转站限流/超时
                    if (firstRound) {
                        renameConversationByQuestionAsync(conversation, model, tenantId, userId, reqVO.getContent());
                    }
                    formatter.onComplete(emitter, usage);
                    emitter.complete();
                });
        disposableRef.set(disposable);

        // 客户端断开或超时时，停止生成
        emitter.onCompletion(disposable::dispose);
        emitter.onError(t -> disposable.dispose());
        emitter.onTimeout(() -> {
            disposable.dispose();
            emitter.complete();
        });
        return emitter;
    }

    /**
     * 首轮提问后，基于问题用 AI 生成会话标题并落库（异步执行；仅当标题仍为默认「新对话」时生效）
     *
     * 标题调用也走流式（collect 后拼接），避免推理类模型非流式请求等待完整推理、
     * 被中转站网关 60 秒超时 504 的问题；失败时兜底截取问题前缀作为标题
     */
    private void renameConversationByQuestionAsync(AiChatConversationDO conversation, AiModelDO model,
                                                   Long tenantId, Long userId, String question) {
        // 标题 AI 调用限时 60 秒：超时/失败则立即用问题前缀兜底，
        // 之后迟到的 AI 标题也会被 updateConversationTitleIfDefault 的默认标题守卫拦截，不会覆盖
        CompletableFuture<Void> titleFuture = CompletableFuture.runAsync(() -> {
            try {
                String generated = TenantUtils.execute(tenantId, () -> {
                    ChatModel chatModel = modelFactory.getOrCreateChatModel(model);
                    ChatClient chatClient = ChatClient.create(chatModel);
                    ChatOptions chatOptions = modelFactory.buildChatOptions(model, null, null);
                    return chatClient.prompt()
                            .user(CONVERSATION_TITLE_PROMPT + question)
                            .options(chatOptions)
                            .call()
                            .content();
                });
                String title = normalizeTitle(generated);
                if (StrUtil.isNotBlank(title)) {
                    final String finalTitle = title;
                    TenantUtils.execute(tenantId, () ->
                            conversationService.updateConversationTitleIfDefault(userId, conversation.getId(), finalTitle));
                }
            } catch (Exception ex) {
                log.warn("[renameConversationByQuestionAsync] 会话标题 AI 生成失败，conversationId={}：{}",
                        conversation.getId(), ex.getMessage());
            }
        });
        try {
            titleFuture.get(TITLE_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);
            return;
        } catch (Exception ex) {
            log.warn("[renameConversationByQuestionAsync] 会话标题 AI 生成超时，使用问题前缀兜底，conversationId={}",
                    conversation.getId());
        }
        // 兜底：问题前缀（最长 12 字）
        String fallback = question.replaceAll("\\s+", "");
        if (fallback.length() > 12) {
            fallback = fallback.substring(0, 12);
        }
        final String finalFallback = fallback;
        TenantUtils.execute(tenantId, () ->
                conversationService.updateConversationTitleIfDefault(userId, conversation.getId(), finalFallback));
    }

    /**
     * 清理标题文本：去掉包裹引号与首尾空白，超长截断
     */
    private String normalizeTitle(String title) {
        if (StrUtil.isBlank(title)) {
            return null;
        }
        String result = StrUtil.trim(title);
        result = StrUtil.removeSuffix(StrUtil.removePrefix(result, "\""), "\"");
        result = StrUtil.removeSuffix(StrUtil.removePrefix(result, "「"), "」");
        result = StrUtil.removeSuffix(StrUtil.removePrefix(result, "“"), "”");
        if (result.length() > 30) {
            result = result.substring(0, 30);
        }
        return StrUtil.isBlank(result) ? null : result;
    }

    /**
     * 检索角色绑定的知识库，返回拼接的参考资料，失败或无结果时返回 null
     */
    private String buildRagContext(AiChatRoleDO role, String query) {
        if (role == null || StrUtil.isBlank(role.getKnowledgeIds())) {
            return null;
        }
        List<Long> knowledgeIds = JsonUtils.parseArray(role.getKnowledgeIds(), Long.class);
        if (knowledgeIds.isEmpty()) {
            return null;
        }
        try {
            List<AiKnowledgeSegmentDO> segments = knowledgeService.searchSimilarSegments(knowledgeIds, query, 5);
            if (segments.isEmpty()) {
                return null;
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < segments.size(); i++) {
                sb.append("[").append(i + 1).append("] ").append(segments.get(i).getContent()).append("\n\n");
            }
            return sb.toString();
        } catch (Exception ex) {
            log.warn("[buildRagContext] 知识库检索失败，忽略 RAG。原因：{}", ex.getMessage());
            return null;
        }
    }

    private String resolveErrorMessage(Throwable error) {
        if (error.getMessage() != null && !error.getMessage().isBlank()) {
            return exception(CHAT_PROCESS_ERROR, error.getMessage()).getMessage();
        }
        return exception(CHAT_PROCESS_ERROR, "未知错误").getMessage();
    }

}
