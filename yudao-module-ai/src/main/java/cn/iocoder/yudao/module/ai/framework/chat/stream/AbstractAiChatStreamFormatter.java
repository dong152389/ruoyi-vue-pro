package cn.iocoder.yudao.module.ai.framework.chat.stream;

import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 流式格式化器基类：封装 SSE 发送与客户端断开时的异常兜底
 *
 * @author 芋道源码
 */
@Slf4j
public abstract class AbstractAiChatStreamFormatter implements AiChatStreamFormatter {

    protected final String model;

    protected AbstractAiChatStreamFormatter(String model) {
        this.model = model;
    }

    /**
     * 发送无事件名的 data 帧（OpenAI / Gemini 官方格式）
     */
    protected boolean send(SseEmitter emitter, String data) {
        try {
            emitter.send(SseEmitter.event().data(data));
            return true;
        } catch (Exception ex) {
            log.warn("[send] SSE 推送失败，客户端可能已断开：{}", ex.getMessage());
            return false;
        }
    }

    /**
     * 发送带事件名的 SSE 帧（Anthropic 官方格式）
     */
    protected boolean sendEvent(SseEmitter emitter, String eventName, String data) {
        try {
            emitter.send(SseEmitter.event().name(eventName).data(data));
            return true;
        } catch (Exception ex) {
            log.warn("[sendEvent] SSE 推送失败，客户端可能已断开：{}", ex.getMessage());
            return false;
        }
    }

    protected String toJson(Object object) {
        return JsonUtils.toJsonString(object);
    }

}
