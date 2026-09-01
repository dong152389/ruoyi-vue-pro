package cn.iocoder.yudao.module.ai.framework.chat.stream;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * AI 流式输出格式化器接口
 *
 * 将 Spring AI 的 ChatResponse 流翻译为模型平台对应的官方流式协议（方言）。
 * 每次 sendStream 生命周期创建一个实例，内部持有该流的帧状态（id、created 等）。
 *
 * 三种方言：
 * - OpenAI：chat.completion.chunk 数据帧，以 data: [DONE] 结束；
 * - Anthropic：message_start / content_block_start / content_block_delta /
 *   content_block_stop / message_delta / message_stop 命名事件；
 * - Gemini：candidates 增量块数据帧，以带 finishReason 的收尾块结束（无 [DONE]）。
 *
 * @author 芋道源码
 */
public interface AiChatStreamFormatter {

    /**
     * 发送流起始帧（无起始帧的平台直接返回 true）
     */
    boolean onStart(SseEmitter emitter);

    /**
     * 发送单个增量帧
     *
     * @return false 表示推送失败（客户端可能已断开），调用方应停止生成
     */
    boolean onDelta(SseEmitter emitter, ChatResponse response);

    /**
     * 发送收尾帧与终止符；usage 可为 null（无法获得用量时省略相关字段）
     */
    boolean onComplete(SseEmitter emitter, Usage usage);

    /**
     * 发送错误帧（各方言的官方错误形状），发送后由调用方 complete
     */
    boolean onError(SseEmitter emitter, String message);

    /**
     * 提取增量响应中的助手消息
     */
    static AssistantMessage getOutput(ChatResponse response) {
        if (response == null || response.getResult() == null) {
            return null;
        }
        return response.getResult().getOutput();
    }

}
