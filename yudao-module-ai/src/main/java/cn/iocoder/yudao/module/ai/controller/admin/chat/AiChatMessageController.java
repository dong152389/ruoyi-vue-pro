package cn.iocoder.yudao.module.ai.controller.admin.chat;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.iocoder.yudao.module.ai.controller.admin.chat.vo.message.MessageRespVO;
import cn.iocoder.yudao.module.ai.controller.admin.chat.vo.message.MessageSendReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.chat.AiChatMessageDO;
import cn.iocoder.yudao.module.ai.service.chat.AiAgentService;
import cn.iocoder.yudao.module.ai.service.chat.AiChatConversationService;
import cn.iocoder.yudao.module.ai.service.chat.AiChatMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - AI 聊天消息")
@RestController
@RequestMapping("/ai/chat/message")
@Validated
public class AiChatMessageController {

    @Resource
    private AiChatMessageService messageService;

    @Resource
    private AiAgentService agentService;

    @Resource
    private AiChatConversationService conversationService;

    @PostMapping(value = "/send-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "流式发送聊天消息",
            description = "SSE 按模型平台输出官方流式协议（由密钥 platform 决定）："
                    + "OpenAI兼容 → chat.completion.chunk + [DONE]；"
                    + "Anthropic → 官方 messages 事件流；"
                    + "Gemini → 官方 streamGenerateContent 流")
    public SseEmitter sendMessageStream(@Valid @RequestBody MessageSendReqVO reqVO) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        Long tenantId = TenantContextHolder.getRequiredTenantId();
        return agentService.sendStream(userId, tenantId, reqVO);
    }

    @GetMapping("/my-list")
    @Operation(summary = "获得我的会话消息列表", description = "加载历史对话，按时间正序")
    @Parameter(name = "conversationId", description = "会话编号", required = true, example = "1")
    public CommonResult<List<MessageRespVO>> getMessageListMy(@RequestParam("conversationId") Long conversationId) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        // 校验会话归属，防止越权读取他人会话
        conversationService.getConversationMy(userId, conversationId);
        List<AiChatMessageDO> list = messageService.getMessageListByConversationId(conversationId);
        return success(BeanUtils.toBean(list, MessageRespVO.class));
    }

}
