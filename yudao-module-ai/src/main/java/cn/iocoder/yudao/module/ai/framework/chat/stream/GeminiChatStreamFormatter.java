package cn.iocoder.yudao.module.ai.framework.chat.stream;

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
 * Gemini 官方流式方言：streamGenerateContent?alt=sse 的 candidates 增量块
 *
 * 帧序（与官方一致）：candidates 增量块 → 末块带 finishReason:"STOP" 与 usageMetadata（无 [DONE]）
 *
 * 工具调用块在服务端执行（Spring AI 内部处理），不对客户端透传非文本块
 *
 * @author 芋道源码
 */
public class GeminiChatStreamFormatter extends AbstractAiChatStreamFormatter {

    public GeminiChatStreamFormatter(String model) {
        super(model);
    }

    @Override
    public boolean onStart(SseEmitter emitter) {
        // 官方流式无起始帧
        return true;
    }

    @Override
    public boolean onDelta(SseEmitter emitter, ChatResponse response) {
        AssistantMessage output = AiChatStreamFormatter.getOutput(response);
        if (output == null || StrUtil.isBlank(output.getText())) {
            return true;
        }
        Map<String, Object> part = new LinkedHashMap<>();
        part.put("text", output.getText());
        Map<String, Object> content = new LinkedHashMap<>();
        content.put("parts", List.of(part));
        content.put("role", "model");
        Map<String, Object> candidate = new LinkedHashMap<>();
        candidate.put("content", content);
        candidate.put("index", 0);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("candidates", List.of(candidate));
        body.put("modelVersion", model);
        return send(emitter, toJson(body));
    }

    @Override
    public boolean onComplete(SseEmitter emitter, Usage usage) {
        Map<String, Object> content = new LinkedHashMap<>();
        content.put("role", "model");
        Map<String, Object> candidate = new LinkedHashMap<>();
        candidate.put("content", content);
        candidate.put("index", 0);
        candidate.put("finishReason", "STOP");
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("candidates", List.of(candidate));
        if (usage != null) {
            Map<String, Object> usageMetadata = new LinkedHashMap<>();
            usageMetadata.put("promptTokenCount", usage.getPromptTokens());
            usageMetadata.put("candidatesTokenCount", usage.getCompletionTokens());
            usageMetadata.put("totalTokenCount", usage.getTotalTokens());
            body.put("usageMetadata", usageMetadata);
        }
        body.put("modelVersion", model);
        return send(emitter, toJson(body));
    }

    @Override
    public boolean onError(SseEmitter emitter, String message) {
        Map<String, Object> error = new LinkedHashMap<>();
        error.put("code", 500);
        error.put("message", message);
        error.put("status", "INTERNAL");
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", error);
        return send(emitter, toJson(body));
    }

}
