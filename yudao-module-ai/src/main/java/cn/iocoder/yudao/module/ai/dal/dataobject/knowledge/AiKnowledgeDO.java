package cn.iocoder.yudao.module.ai.dal.dataobject.knowledge;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * AI 医疗知识库表
 *
 * @author 芋道源码
 */
@TableName("ai_knowledge")
@KeySequence("ai_knowledge_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
public class AiKnowledgeDO extends TenantBaseDO {

    /**
     * 知识库编号
     */
    @TableId
    private Long id;
    /**
     * 知识库名称
     */
    private String name;
    /**
     * 知识库描述
     */
    private String description;
    /**
     * 向量模型编号
     *
     * 关联 {@link cn.iocoder.yudao.module.ai.dal.dataobject.model.AiModelDO#getId()}
     */
    private Long embeddingModelId;

}
