package cn.iocoder.yudao.module.ai.dal.dataobject.knowledge;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * AI 知识库文档表
 *
 * @author 芋道源码
 */
@TableName("ai_knowledge_document")
@KeySequence("ai_knowledge_document_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
public class AiKnowledgeDocumentDO extends TenantBaseDO {

    /**
     * 切片状态 - 待处理
     */
    public static final Integer SLICE_STATUS_PENDING = 0;
    /**
     * 切片状态 - 已完成
     */
    public static final Integer SLICE_STATUS_SUCCESS = 1;
    /**
     * 切片状态 - 失败
     */
    public static final Integer SLICE_STATUS_FAIL = 2;

    /**
     * 文档编号
     */
    @TableId
    private Long id;
    /**
     * 知识库编号
     *
     * 关联 {@link AiKnowledgeDO#getId()}
     */
    private Long knowledgeId;
    /**
     * 文档名称
     */
    private String name;
    /**
     * 文档存储地址
     */
    private String url;
    /**
     * 文档总 Token 数（估算）
     */
    private Integer tokens;
    /**
     * 切片数量
     */
    private Integer segmentCount;
    /**
     * 切片状态（0待处理 1已完成 2失败）
     */
    private Integer sliceStatus;

}
