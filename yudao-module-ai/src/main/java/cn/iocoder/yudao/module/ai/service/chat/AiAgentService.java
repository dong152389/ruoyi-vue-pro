package cn.iocoder.yudao.module.ai.service.chat;

import cn.iocoder.yudao.module.ai.controller.admin.chat.vo.message.MessageSendReqVO;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 医疗 AI Agent Service 接口
 *
 * 负责编排：会话上下文 + 角色系统提示词 + 知识库 RAG + 医疗工具调用 + SSE 流式输出
 *
 * SSE 按模型平台输出对应官方流式协议（方言），由 ai_api_key.platform 决定：
 * - OpenAI兼容（默认）：chat.completion.chunk 数据帧（delta.role / delta.content / finish_reason / usage），以 data: [DONE] 结束；错误帧为 {"error":{"message":...}}
 * - Anthropic：官方 messages 命名事件流（message_start / content_block_delta(text_delta) / message_stop 等）；错误为 event:error
 * - Gemini：官方 streamGenerateContent 流（candidates 增量块，末块带 finishReason:"STOP" 与 usageMetadata）；错误为 {"error":{...}}
 *
 * 会话结束前落库 AI 回复（含尽力获取的 token 用量）；messageId 由前端结束后重新拉取历史获得
 * 流开始前的校验失败（会话不存在/模型停用等）以普通 JSON 错误响应返回，不进入 SSE
 *
 * @author 芋道源码
 */
public interface AiAgentService {

    /**
     * 流式发送聊天消息（SSE）
     *
     * @param userId 当前登录用户
     * @param tenantId 当前租户（用于异步线程恢复上下文）
     * @param reqVO 发送请求
     * @return SseEmitter
     */
    SseEmitter sendStream(Long userId, Long tenantId, MessageSendReqVO reqVO);

}
