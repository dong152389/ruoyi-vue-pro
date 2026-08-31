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
import cn.iocoder.yudao.module.ai.framework.ai.AiModelFactory;
import cn.iocoder.yudao.module.ai.framework.chat.MedicalChatTools;
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
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.ai.enums.ErrorCodeConstants.CHAT_PROCESS_ERROR;

/**
 * 医疗 AI Agent Service 实现类
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
    private AiMedicalRecordService recordService;

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

        // ========== 3. 知识库 RAG 检索（失败不阻断对话）==========
        String ragContext = buildRagContext(role, reqVO.getContent());

        // ========== 4. 组装 ChatClient：系统提示词 + 历史 + 医疗工具 ==========
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

        OpenAiChatOptions.Builder optionsBuilder = OpenAiChatOptions.builder().model(model.getModel());
        if (conversation.getTemperature() != null) {
            optionsBuilder.temperature(conversation.getTemperature());
        }
        if (conversation.getMaxTokens() != null) {
            optionsBuilder.maxTokens(conversation.getMaxTokens());
        }

        MedicalChatTools tools = new MedicalChatTools(tenantId, userId, conversation.getId(),
                departmentService, drugService, scheduleService, recordService);

        // ========== 5. 流式调用，推送 SSE 事件，结束后落库 ==========
        StringBuilder contentBuffer = new StringBuilder();
        AtomicReference<Disposable> disposableRef = new AtomicReference<>();
        Flux<String> flux = chatClient.prompt()
                .system(systemPrompt)
                .messages(historyMessages)
                .user(userContent)
                .options(optionsBuilder.build())
                .tools(tools)
                .stream()
                .content();

        Disposable disposable = flux.subscribe(
                // onNext：推送增量内容
                chunk -> {
                    contentBuffer.append(chunk);
                    if (!sendEvent(emitter, "content", Map.of("content", chunk))) {
                        Disposable current = disposableRef.get();
                        if (current != null) {
                            current.dispose();
                        }
                    }
                },
                // onError：保存已生成的部分内容，推送错误事件
                error -> {
                    log.error("[sendStream] 对话失败，conversationId={}", conversation.getId(), error);
                    String partialContent = contentBuffer.toString();
                    if (StrUtil.isNotBlank(partialContent)) {
                        TenantUtils.execute(tenantId, () -> messageService.createMessage(conversation.getId(), userId,
                                AiMessageTypeEnum.ASSISTANT.getType(), model.getModel(), partialContent, null));
                    }
                    sendEvent(emitter, "error", Map.of("message", resolveErrorMessage(error)));
                    emitter.complete();
                },
                // onComplete：保存 AI 回复，推送完成事件
                () -> {
                    String content = contentBuffer.toString();
                    AiChatMessageDO savedMessage = TenantUtils.execute(tenantId, () ->
                            messageService.createMessage(conversation.getId(), userId,
                                    AiMessageTypeEnum.ASSISTANT.getType(), model.getModel(), content, null));
                    sendEvent(emitter, "done", Map.of("messageId", savedMessage != null ? savedMessage.getId() : 0L));
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

    private boolean sendEvent(SseEmitter emitter, String type, Map<String, Object> data) {
        try {
            Map<String, Object> event = new LinkedHashMap<>(data);
            event.put("type", type);
            emitter.send(SseEmitter.event().data(JsonUtils.toJsonString(event)));
            return true;
        } catch (Exception ex) {
            log.warn("[sendEvent] SSE 推送失败，客户端可能已断开：{}", ex.getMessage());
            return false;
        }
    }

    private String resolveErrorMessage(Throwable error) {
        if (error.getMessage() != null && !error.getMessage().isBlank()) {
            return exception(CHAT_PROCESS_ERROR, error.getMessage()).getMessage();
        }
        return exception(CHAT_PROCESS_ERROR, "未知错误").getMessage();
    }

}
