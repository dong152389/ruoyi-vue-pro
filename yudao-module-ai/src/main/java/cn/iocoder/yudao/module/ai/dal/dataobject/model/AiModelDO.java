package cn.iocoder.yudao.module.ai.dal.dataobject.model;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.module.ai.enums.AiModelTypeEnum;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * AI 模型表
 *
 * @author 芋道源码
 */
@TableName("ai_model")
@KeySequence("ai_model_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
public class AiModelDO extends BaseDO {

    /**
     * 模型编号
     */
    @TableId
    private Long id;
    /**
     * API 密钥编号
     *
     * 关联 {@link AiApiKeyDO#getId()}
     */
    private Long keyId;
    /**
     * 模型名称（显示用）
     */
    private String name;
    /**
     * 模型标识（如 gpt-4o-mini / deepseek-chat / text-embedding-3-small）
     */
    private String model;
    /**
     * 模型类型
     *
     * 枚举 {@link AiModelTypeEnum}
     */
    private Integer type;
    /**
     * 温度参数（仅对话模型）
     */
    private Double temperature;
    /**
     * 回复最大 Token 数（仅对话模型）
     */
    private Integer maxTokens;
    /**
     * 状态
     *
     * 枚举 {@link CommonStatusEnum}
     */
    private Integer status;
    /**
     * 备注
     */
    private String remark;

}
