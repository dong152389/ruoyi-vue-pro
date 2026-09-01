package cn.iocoder.yudao.module.ai.framework.chat.stream;

import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link AiChatStreamFormatter} 三种官方方言的帧序与报文形状单测
 *
 * 通过重写 send/sendEvent 捕获帧，避免依赖真实 servlet 容器
 */
class AiChatStreamFormatterTest {

    @SuppressWarnings("unchecked")
    private static Map<String, Object> parse(String json) {
        return (Map<String, Object>) JsonUtils.parseObject(json, Map.class);
    }

    private static ChatResponse textResponse(String text) {
        return new ChatResponse(List.of(new Generation(new AssistantMessage(text))));
    }

    private static ChatResponse toolCallResponse() {
        AssistantMessage message = AssistantMessage.builder()
                .content("")
                .toolCalls(List.of(new AssistantMessage.ToolCall("call_1", "function",
                        "createAppointment", "{\"departmentId\":1}")))
                .build();
        return new ChatResponse(List.of(new Generation(message)));
    }

    private static Usage usage(int prompt, int completion) {
        return new Usage() {
            @Override
            public Integer getPromptTokens() {
                return prompt;
            }

            @Override
            public Integer getCompletionTokens() {
                return completion;
            }

            @Override
            public Object getNativeUsage() {
                return null;
            }
        };
    }

    // ========== OpenAI 官方方言 ==========

    private static class CapturingOpenAi extends OpenAiChatStreamFormatter {
        final List<String> frames = new ArrayList<>();

        CapturingOpenAi(String model) {
            super(model);
        }

        @Override
        protected boolean send(SseEmitter emitter, String data) {
            frames.add(data);
            return true;
        }

        @Override
        protected boolean sendEvent(SseEmitter emitter, String eventName, String data) {
            frames.add(eventName + " | " + data);
            return true;
        }
    }

    @Test
    void openAi_frames() {
        CapturingOpenAi formatter = new CapturingOpenAi("deepseek-chat");
        SseEmitter emitter = new SseEmitter(0L);

        // 起始帧：delta.role = assistant
        assertThat(formatter.onStart(emitter)).isTrue();
        Map<String, Object> start = parse(formatter.frames.get(0));
        assertThat(start.get("object")).isEqualTo("chat.completion.chunk");
        assertThat(String.valueOf(start.get("id"))).startsWith("chatcmpl-");
        assertThat(start.get("model")).isEqualTo("deepseek-chat");
        Map<String, Object> startChoice = ((List<Map<String, Object>>) start.get("choices")).get(0);
        Map<String, Object> startDelta = (Map<String, Object>) startChoice.get("delta");
        assertThat(startDelta.get("role")).isEqualTo("assistant");

        // 内容增量帧
        assertThat(formatter.onDelta(emitter, textResponse("你好"))).isTrue();
        Map<String, Object> content = parse(formatter.frames.get(1));
        Map<String, Object> contentDelta = ((List<Map<String, Object>>) content.get("choices")).get(0)
                .get("delta") instanceof Map<?, ?> m ? (Map<String, Object>) m : null;
        assertThat(contentDelta).isNotNull();
        assertThat(contentDelta.get("content")).isEqualTo("你好");
        assertThat(content.containsKey("finish_reason")).isFalse();
        // 同一流的 id 保持一致
        assertThat(content.get("id")).isEqualTo(start.get("id"));

        // 工具调用增量帧（官方 tool_calls 形状）
        formatter.frames.clear();
        assertThat(formatter.onDelta(emitter, toolCallResponse())).isTrue();
        Map<String, Object> toolFrame = parse(formatter.frames.get(0));
        Map<String, Object> toolDelta = (Map<String, Object>) ((List<Map<String, Object>>) toolFrame.get("choices"))
                .get(0).get("delta");
        Map<String, Object> toolCall = ((List<Map<String, Object>>) toolDelta.get("tool_calls")).get(0);
        assertThat(toolCall.get("type")).isEqualTo("function");
        assertThat(toolCall.get("id")).isEqualTo("call_1");
        Map<String, Object> function = (Map<String, Object>) toolCall.get("function");
        assertThat(function.get("name")).isEqualTo("createAppointment");
        assertThat(function.get("arguments")).isEqualTo("{\"departmentId\":1}");

        // 收尾：finish_reason 帧 + usage 帧 + [DONE]
        formatter.frames.clear();
        assertThat(formatter.onComplete(emitter, usage(100, 50))).isTrue();
        assertThat(formatter.frames).hasSize(3);
        Map<String, Object> finish = parse(formatter.frames.get(0));
        Map<String, Object> finishChoice = ((List<Map<String, Object>>) finish.get("choices")).get(0);
        assertThat(finishChoice.get("finish_reason")).isEqualTo("stop");
        Map<String, Object> usageChunk = parse(formatter.frames.get(1));
        assertThat((List<?>) usageChunk.get("choices")).isEmpty();
        Map<String, Object> usageBody = (Map<String, Object>) usageChunk.get("usage");
        assertThat(usageBody.get("prompt_tokens")).isEqualTo(100);
        assertThat(usageBody.get("completion_tokens")).isEqualTo(50);
        assertThat(usageBody.get("total_tokens")).isEqualTo(150);
        assertThat(formatter.frames.get(2)).isEqualTo("[DONE]");

        // 错误帧
        formatter.frames.clear();
        assertThat(formatter.onError(emitter, "模型调用失败")).isTrue();
        Map<String, Object> errorBody = parse(formatter.frames.get(0));
        Map<String, Object> error = (Map<String, Object>) errorBody.get("error");
        assertThat(error.get("message")).isEqualTo("模型调用失败");
        assertThat(error.get("type")).isEqualTo("server_error");
    }

    // ========== Anthropic 官方方言 ==========

    private static class CapturingAnthropic extends AnthropicChatStreamFormatter {
        final List<String> frames = new ArrayList<>();

        CapturingAnthropic(String model) {
            super(model);
        }

        @Override
        protected boolean send(SseEmitter emitter, String data) {
            frames.add("data | " + data);
            return true;
        }

        @Override
        protected boolean sendEvent(SseEmitter emitter, String eventName, String data) {
            frames.add(eventName + " | " + data);
            return true;
        }
    }

    @Test
    void anthropic_events() {
        CapturingAnthropic formatter = new CapturingAnthropic("claude-sonnet-4-5");
        SseEmitter emitter = new SseEmitter(0L);

        // 起始：message_start + content_block_start
        assertThat(formatter.onStart(emitter)).isTrue();
        assertThat(formatter.frames).hasSize(2);
        assertThat(formatter.frames.get(0)).startsWith("message_start | ");
        Map<String, Object> messageStart = parse(formatter.frames.get(0).split(" \\| ", 2)[1]);
        Map<String, Object> message = (Map<String, Object>) messageStart.get("message");
        assertThat(String.valueOf(message.get("id"))).startsWith("msg_");
        assertThat(message.get("type")).isEqualTo("message");
        assertThat(message.get("role")).isEqualTo("assistant");
        assertThat(message.get("model")).isEqualTo("claude-sonnet-4-5");
        assertThat(formatter.frames.get(1)).startsWith("content_block_start | ");
        Map<String, Object> blockStart = parse(formatter.frames.get(1).split(" \\| ", 2)[1]);
        Map<String, Object> block = (Map<String, Object>) blockStart.get("content_block");
        assertThat(block.get("type")).isEqualTo("text");

        // 增量：content_block_delta(text_delta)；工具调用块不透传
        assertThat(formatter.onDelta(emitter, textResponse("建议"))).isTrue();
        Map<String, Object> deltaEvent = parse(formatter.frames.get(2).split(" \\| ", 2)[1]);
        assertThat(formatter.frames.get(2).split(" \\| ", 2)[0]).isEqualTo("content_block_delta");
        Map<String, Object> delta = (Map<String, Object>) deltaEvent.get("delta");
        assertThat(delta.get("type")).isEqualTo("text_delta");
        assertThat(delta.get("text")).isEqualTo("建议");
        formatter.frames.clear();
        assertThat(formatter.onDelta(emitter, toolCallResponse())).isTrue();
        assertThat(formatter.frames).isEmpty();

        // 收尾：content_block_stop + message_delta(end_turn) + message_stop
        assertThat(formatter.onComplete(emitter, usage(10, 20))).isTrue();
        assertThat(formatter.frames.get(0)).startsWith("content_block_stop | ");
        Map<String, Object> messageDelta = parse(formatter.frames.get(1).split(" \\| ", 2)[1]);
        assertThat(formatter.frames.get(1).split(" \\| ", 2)[0]).isEqualTo("message_delta");
        Map<String, Object> messageDeltaBody = (Map<String, Object>) messageDelta.get("delta");
        assertThat(messageDeltaBody.get("stop_reason")).isEqualTo("end_turn");
        Map<String, Object> messageDeltaUsage = (Map<String, Object>) messageDelta.get("usage");
        assertThat(messageDeltaUsage.get("output_tokens")).isEqualTo(20);
        assertThat(formatter.frames.get(2)).startsWith("message_stop | ");

        // 错误：event:error
        formatter.frames.clear();
        assertThat(formatter.onError(emitter, "密钥无效")).isTrue();
        assertThat(formatter.frames.get(0).split(" \\| ", 2)[0]).isEqualTo("error");
        Map<String, Object> errorEvent = parse(formatter.frames.get(0).split(" \\| ", 2)[1]);
        assertThat(errorEvent.get("type")).isEqualTo("error");
        Map<String, Object> error = (Map<String, Object>) errorEvent.get("error");
        assertThat(error.get("message")).isEqualTo("密钥无效");
    }

    // ========== Gemini 官方方言 ==========

    private static class CapturingGemini extends GeminiChatStreamFormatter {
        final List<String> frames = new ArrayList<>();

        CapturingGemini(String model) {
            super(model);
        }

        @Override
        protected boolean send(SseEmitter emitter, String data) {
            frames.add(data);
            return true;
        }

        @Override
        protected boolean sendEvent(SseEmitter emitter, String eventName, String data) {
            frames.add(eventName + " | " + data);
            return true;
        }
    }

    @Test
    void gemini_frames() {
        CapturingGemini formatter = new CapturingGemini("gemini-2.5-flash");
        SseEmitter emitter = new SseEmitter(0L);

        // 无起始帧
        assertThat(formatter.onStart(emitter)).isTrue();
        assertThat(formatter.frames).isEmpty();

        // 增量：candidates 块
        assertThat(formatter.onDelta(emitter, textResponse("头痛建议"))).isTrue();
        Map<String, Object> deltaBody = parse(formatter.frames.get(0));
        Map<String, Object> candidate = ((List<Map<String, Object>>) deltaBody.get("candidates")).get(0);
        Map<String, Object> content = (Map<String, Object>) candidate.get("content");
        assertThat(content.get("role")).isEqualTo("model");
        Map<String, Object> part = ((List<Map<String, Object>>) content.get("parts")).get(0);
        assertThat(part.get("text")).isEqualTo("头痛建议");
        assertThat(deltaBody.get("modelVersion")).isEqualTo("gemini-2.5-flash");

        // 收尾：finishReason + usageMetadata（无 [DONE]）
        assertThat(formatter.onComplete(emitter, usage(30, 40))).isTrue();
        Map<String, Object> finishBody = parse(formatter.frames.get(1));
        Map<String, Object> finishCandidate = ((List<Map<String, Object>>) finishBody.get("candidates")).get(0);
        assertThat(finishCandidate.get("finishReason")).isEqualTo("STOP");
        Map<String, Object> usageMetadata = (Map<String, Object>) finishBody.get("usageMetadata");
        assertThat(usageMetadata.get("promptTokenCount")).isEqualTo(30);
        assertThat(usageMetadata.get("candidatesTokenCount")).isEqualTo(40);
        assertThat(usageMetadata.get("totalTokenCount")).isEqualTo(70);

        // 无用量时省略 usageMetadata
        formatter.frames.clear();
        assertThat(formatter.onComplete(emitter, null)).isTrue();
        Map<String, Object> finishNoUsage = parse(formatter.frames.get(0));
        assertThat(((List<Map<String, Object>>) finishNoUsage.get("candidates")).get(0).get("finishReason"))
                .isEqualTo("STOP");
        assertThat(finishNoUsage.containsKey("usageMetadata")).isFalse();

        // 错误帧
        formatter.frames.clear();
        assertThat(formatter.onError(emitter, "配额不足")).isTrue();
        Map<String, Object> errorBody = parse(formatter.frames.get(0));
        Map<String, Object> error = (Map<String, Object>) errorBody.get("error");
        assertThat(error.get("code")).isEqualTo(500);
        assertThat(error.get("message")).isEqualTo("配额不足");
        assertThat(error.get("status")).isEqualTo("INTERNAL");
    }

}
