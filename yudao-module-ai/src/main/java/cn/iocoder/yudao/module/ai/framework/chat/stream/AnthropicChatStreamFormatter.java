package cn.iocoder.yudao.module.ai.framework.chat.stream;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Anthropic 官方流式方言：/v1/messages 的命名 SSE 事件流
 *
 * 事件序（与官方一致）：
 * message_start → content_block_start(text) → content_block_delta(text_delta)... →
 * content_block_stop → message_delta(stop_reason:end_turn, usage) → message_stop
 *
 * 工具调用块在服务端执行（Spring AI 内部处理），不对客户端透传非文本块
 *
 * @author 芋道源码
 */
public class AnthropicChatStreamFormatter extends AbstractAiChatStreamFormatter {

    private final String messageId;
    private boolean blockOpen = false;

    public AnthropicChatStreamFormatter(String model) {
        super(model);
        this.messageId = "msg_" + IdUtil.fastSimpleUUID();
    }

    @Override
    public boolean onStart(SseEmitter emitter) {
        Map<String, Object> usage = new LinkedHashMap<>();
        usage.put("input_tokens", 0);
        usage.put("output_tokens", 0);
        Map<String, Object> message = new LinkedHashMap<>();
        message.put("id", messageId);
        message.put("type", "message");
        message.put("role", "assistant");
        message.put("model", model);
        message.put("content", List.of());
        message.put("usage", usage);
        Map<String, Object> start = new LinkedHashMap<>();
        start.put("type", "message_start");
        start.put("message", message);
        if (!sendEvent(emitter, "message_start", toJson(start))) {
            return false;
        }

        blockOpen = true;
        Map<String, Object> block = new LinkedHashMap<>();
        block.put("type", "text");
        block.put("text", "");
        Map<String, Object> blockStart = new LinkedHashMap<>();
        blockStart.put("type", "content_block_start");
        blockStart.put("index", 0);
        blockStart.put("content_block", block);
        return sendEvent(emitter, "content_block_start", toJson(blockStart));
    }

    @Override
    public boolean onDelta(SseEmitter emitter, ChatResponse response) {
        AssistantMessage output = AiChatStreamFormatter.getOutput(response);
        if (output == null || StrUtil.isBlank(output.getText())) {
            return true;
        }
        Map<String, Object> delta = new LinkedHashMap<>();
        delta.put("type", "text_delta");
        delta.put("text", output.getText());
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("type", "content_block_delta");
        body.put("index", 0);
        body.put("delta", delta);
        return sendEvent(emitter, "content_block_delta", toJson(body));
    }

    @Override
    public boolean onComplete(SseEmitter emitter, Usage usage) {
        if (blockOpen) {
            blockOpen = false;
            Map<String, Object> stop = new LinkedHashMap<>();
            stop.put("type", "content_block_stop");
            stop.put("index", 0);
            if (!sendEvent(emitter, "content_block_stop", toJson(stop))) {
                return false;
            }
        }
        Map<String, Object> delta = new LinkedHashMap<>();
        delta.put("stop_reason", "end_turn");
        Map<String, Object> usageBody = new LinkedHashMap<>();
        usageBody.put("output_tokens", usage != null && usage.getCompletionTokens() != null
                ? usage.getCompletionTokens() : 0);
        Map<String, Object> messageDelta = new LinkedHashMap<>();
        messageDelta.put("type", "message_delta");
        messageDelta.put("delta", delta);
        messageDelta.put("usage", usageBody);
        if (!sendEvent(emitter, "message_delta", toJson(messageDelta))) {
            return false;
        }

        Map<String, Object> messageStop = new LinkedHashMap<>();
        messageStop.put("type", "message_stop");
        return sendEvent(emitter, "message_stop", toJson(messageStop));
    }

    @Override
    public boolean onError(SseEmitter emitter, String message) {
        Map<String, Object> error = new LinkedHashMap<>();
        error.put("type", "api_error");
        error.put("message", message);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("type", "error");
        body.put("error", error);
        return sendEvent(emitter, "error", toJson(body));
    }

}
