package cn.iocoder.yudao.module.ai.dal.dataobject.knowledge;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * AI 知识库切片表
 *
 * @author 芋道源码
 */
@TableName("ai_knowledge_segment")
@KeySequence("ai_knowledge_segment_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
public class AiKnowledgeSegmentDO extends TenantBaseDO {

    /**
     * 切片编号
     */
    @TableId
    private Long id;
    /**
     * 文档编号
     *
     * 关联 {@link AiKnowledgeDocumentDO#getId()}
     */
    private Long documentId;
    /**
     * 知识库编号
     *
     * 关联 {@link AiKnowledgeDO#getId()}
     */
    private Long knowledgeId;
    /**
     * 切片内容
     */
    private String content;
    /**
     * 切片 Token 数（估算）
     */
    private Integer tokens;
    /**
     * 向量（JSON 数组）
     */
    private String vector;

}
