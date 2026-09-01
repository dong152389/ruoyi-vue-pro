package cn.iocoder.yudao.module.ai.enums;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * AI 平台枚举
 *
 * 决定两件事：
 * 1. {@link cn.iocoder.yudao.module.ai.framework.ai.AiModelFactoryImpl} 用哪个官方 ChatModel 接入模型；
 * 2. SSE 流式输出采用哪种官方协议（方言）：
 *    - OPENAI：chat.completion.chunk + [DONE]
 *    - ANTHROPIC：官方 messages 流（message_start / content_block_delta / message_stop ...）
 *    - GEMINI：官方 streamGenerateContent 流（candidates 增量块）
 *
 * @author 芋道源码
 */
@Getter
@RequiredArgsConstructor
public enum AiPlatformEnum {

    /**
     * OpenAI 兼容协议（new-api / one-api / 官方中转等，默认兜底）
     */
    OPENAI("OpenAI兼容"),

    /**
     * Anthropic 官方 API（Claude）
     */
    ANTHROPIC("Anthropic"),

    /**
     * Gemini 官方 API（Google Developer API，API Key 模式）
     */
    GEMINI("Gemini");

    /**
     * 平台名称（与 ai_api_key.platform 列的存储值保持一致）
     */
    private final String name;

    /**
     * 将密钥表中的自由文本 platform 归一化为枚举
     *
     * 未识别的值兜底为 OPENAI，保证历史数据（默认 'OpenAI兼容'）继续走原链路
     */
    public static AiPlatformEnum of(String platform) {
        if (StrUtil.isBlank(platform)) {
            return OPENAI;
        }
        String value = platform.toLowerCase();
        if (value.contains("anthropic") || value.contains("claude")) {
            return ANTHROPIC;
        }
        if (value.contains("gemini") || value.contains("google") || value.contains("vertex")) {
            return GEMINI;
        }
        return OPENAI;
    }

}
