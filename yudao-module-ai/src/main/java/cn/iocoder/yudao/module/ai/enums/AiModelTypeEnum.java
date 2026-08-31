package cn.iocoder.yudao.module.ai.enums;

import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * AI 模型类型枚举
 *
 * @author 芋道源码
 */
@Getter
@AllArgsConstructor
public enum AiModelTypeEnum implements ArrayValuable<Integer> {

    CHAT(1, "对话模型"),
    EMBEDDING(2, "向量模型");

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(AiModelTypeEnum::getType).toArray(Integer[]::new);

    private final Integer type;
    private final String name;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

}
