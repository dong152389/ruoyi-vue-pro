package cn.iocoder.yudao.module.ai.service.chat;

import cn.iocoder.yudao.module.ai.controller.admin.chat.vo.message.MessageSendReqVO;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 医疗 AI Agent Service 接口
 *
 * 负责编排：会话上下文 + 角色系统提示词 + 知识库 RAG + 医疗工具调用 + SSE 流式输出
 *
 * @author 芋道源码
 */
public interface AiAgentService {

    /**
     * 流式发送聊天消息（SSE）
     *
     * 事件格式（data 为 JSON 字符串）：
     * {"type":"content","content":"增量文本"}
     * {"type":"done","messageId":1}
     * {"type":"error","message":"失败原因"}
     *
     * @param userId 当前登录用户
     * @param tenantId 当前租户（用于异步线程恢复上下文）
     * @param reqVO 发送请求
     * @return SseEmitter
     */
    SseEmitter sendStream(Long userId, Long tenantId, MessageSendReqVO reqVO);

}
