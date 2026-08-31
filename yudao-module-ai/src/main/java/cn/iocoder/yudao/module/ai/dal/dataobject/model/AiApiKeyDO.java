package cn.iocoder.yudao.module.ai.dal.dataobject.model;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * AI API 密钥表
 *
 * 通过 OpenAI 兼容协议对接 new-api 等中转站
 *
 * @author 芋道源码
 */
@TableName("ai_api_key")
@KeySequence("ai_api_key_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
public class AiApiKeyDO extends BaseDO {

    /**
     * 密钥编号
     */
    @TableId
    private Long id;
    /**
     * 密钥名称
     */
    private String name;
    /**
     * 模型平台（OpenAI 兼容协议：new-api / one-api / 各官方中转）
     */
    private String platform;
    /**
     * API 密钥
     */
    private String apiKey;
    /**
     * 自定义 API 地址（如 new-api 的 https://xxx/v1）
     */
    private String baseUrl;
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
