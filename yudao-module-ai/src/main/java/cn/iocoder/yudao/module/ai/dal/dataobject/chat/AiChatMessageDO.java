package cn.iocoder.yudao.module.ai.dal.dataobject.chat;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * AI 聊天消息表
 *
 * @author 芋道源码
 */
@TableName("ai_chat_message")
@KeySequence("ai_chat_message_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
public class AiChatMessageDO extends TenantBaseDO {

    /**
     * 消息编号
     */
    @TableId
    private Long id;
    /**
     * 会话编号
     *
     * 关联 {@link AiChatConversationDO#getId()}
     */
    private Long conversationId;
    /**
     * 用户编号
     */
    private Long userId;
    /**
     * 消息类型
     *
     * 枚举 {@link cn.iocoder.yudao.module.ai.enums.AiMessageTypeEnum}
     */
    private String type;
    /**
     * 使用的模型标识
     */
    private String model;
    /**
     * 消息内容
     */
    private String content;
    /**
     * 本次消耗 Token 数
     */
    private Integer usageTokens;

}
