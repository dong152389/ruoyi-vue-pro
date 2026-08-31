package cn.iocoder.yudao.module.ai.dal.dataobject.chat;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * AI 聊天会话表
 *
 * @author 芋道源码
 */
@TableName("ai_chat_conversation")
@KeySequence("ai_chat_conversation_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
public class AiChatConversationDO extends TenantBaseDO {

    /**
     * 会话编号
     */
    @TableId
    private Long id;
    /**
     * 用户编号
     */
    private Long userId;
    /**
     * 角色编号
     *
     * 关联 {@link AiChatRoleDO#getId()}
     */
    private Long roleId;
    /**
     * 对话模型编号
     *
     * 关联 {@link cn.iocoder.yudao.module.ai.dal.dataobject.model.AiModelDO#getId()}
     */
    private Long modelId;
    /**
     * 会话标题
     */
    private String title;
    /**
     * 是否置顶
     */
    private Boolean pinned;
    /**
     * 温度参数
     */
    private Double temperature;
    /**
     * 回复最大 Token 数
     */
    private Integer maxTokens;
    /**
     * 上下文最大条数
     */
    private Integer maxContexts;

}
