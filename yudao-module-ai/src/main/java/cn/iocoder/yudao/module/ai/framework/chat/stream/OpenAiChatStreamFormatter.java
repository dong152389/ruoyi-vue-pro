package cn.iocoder.yudao.module.ai.framework.chat.stream;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * OpenAI 官方流式方言：chat.completion.chunk 数据帧
 *
 * 帧序（与官方 /v1/chat/completions stream 一致）：
 * 1. delta:{role:"assistant"} 起始帧
 * 2. delta:{content:"..."} 增量帧（tool_calls 增量按官方形状透传，供标准客户端解析）
 * 3. delta:{} + finish_reason:"stop" 收尾帧
 * 4. usage 帧（choices 为空数组，仅在获得用量时发送）
 * 5. data: [DONE]
 *
 * @author 芋道源码
 */
public class OpenAiChatStreamFormatter extends AbstractAiChatStreamFormatter {

    /**
     * 官方流式终止符
     */
    private static final String DONE = "[DONE]";

    private final String id;
    private final long created;
    private boolean roleSent = false;

    public OpenAiChatStreamFormatter(String model) {
        super(model);
        this.id = "chatcmpl-" + IdUtil.fastSimpleUUID();
        this.created = System.currentTimeMillis() / 1000;
    }

    @Override
    public boolean onStart(SseEmitter emitter) {
        roleSent = true;
        return send(emitter, chunkJson(Map.of("role", "assistant"), null));
    }

    @Override
    public boolean onDelta(SseEmitter emitter, ChatResponse response) {
        AssistantMessage output = AiChatStreamFormatter.getOutput(response);
        if (output == null) {
            return true;
        }
        if (output.hasToolCalls()) {
            List<Map<String, Object>> toolCalls = new ArrayList<>();
            int index = 0;
            for (AssistantMessage.ToolCall toolCall : output.getToolCalls()) {
                Map<String, Object> function = new LinkedHashMap<>();
                function.put("name", toolCall.name());
                function.put("arguments", toolCall.arguments());
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("index", index++);
                item.put("id", toolCall.id());
                item.put("type", "function");
                item.put("function", function);
                toolCalls.add(item);
            }
            return send(emitter, chunkJson(Map.of("tool_calls", toolCalls), null));
        }
        if (StrUtil.isBlank(output.getText())) {
            return true;
        }
        if (!roleSent) {
            roleSent = true;
            if (!send(emitter, chunkJson(Map.of("role", "assistant"), null))) {
                return false;
            }
        }
        return send(emitter, chunkJson(Map.of("content", output.getText()), null));
    }

    @Override
    public boolean onComplete(SseEmitter emitter, Usage usage) {
        if (!send(emitter, chunkJson(new LinkedHashMap<>(), "stop"))) {
            return false;
        }
        if (usage != null) {
            Map<String, Object> usageBody = new LinkedHashMap<>();
            usageBody.put("prompt_tokens", usage.getPromptTokens());
            usageBody.put("completion_tokens", usage.getCompletionTokens());
            usageBody.put("total_tokens", usage.getTotalTokens());
            Map<String, Object> usageChunk = new LinkedHashMap<>();
            usageChunk.put("id", id);
            usageChunk.put("object", "chat.completion.chunk");
            usageChunk.put("created", created);
            usageChunk.put("model", model);
            usageChunk.put("choices", List.of());
            usageChunk.put("usage", usageBody);
            if (!send(emitter, toJson(usageChunk))) {
                return false;
            }
        }
        return send(emitter, DONE);
    }

    @Override
    public boolean onError(SseEmitter emitter, String message) {
        Map<String, Object> error = new LinkedHashMap<>();
        error.put("message", message);
        error.put("type", "server_error");
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", error);
        return send(emitter, toJson(body));
    }

    private String chunkJson(Map<String, Object> delta, String finishReason) {
        Map<String, Object> choice = new LinkedHashMap<>();
        choice.put("index", 0);
        choice.put("delta", delta);
        choice.put("finish_reason", finishReason);
        Map<String, Object> chunk = new LinkedHashMap<>();
        chunk.put("id", id);
        chunk.put("object", "chat.completion.chunk");
        chunk.put("created", created);
        chunk.put("model", model);
        chunk.put("choices", List.of(choice));
        return toJson(chunk);
    }

}
