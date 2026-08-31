package cn.iocoder.yudao.module.ai.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * AI 聊天消息类型枚举
 *
 * @author 芋道源码
 */
@Getter
@AllArgsConstructor
public enum AiMessageTypeEnum {

    USER("user", "用户消息"),
    ASSISTANT("assistant", "AI 回复"),
    SYSTEM("system", "系统消息");

    private final String type;
    private final String name;

}
