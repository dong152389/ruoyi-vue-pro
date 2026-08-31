package cn.iocoder.yudao.module.ai.dal.dataobject.chat;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * AI 医疗角色预设表
 *
 * @author 芋道源码
 */
@TableName("ai_chat_role")
@KeySequence("ai_chat_role_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
public class AiChatRoleDO extends BaseDO {

    /**
     * 角色编号
     */
    @TableId
    private Long id;
    /**
     * 角色名称
     */
    private String name;
    /**
     * 角色头像
     */
    private String avatar;
    /**
     * 角色描述
     */
    private String description;
    /**
     * 角色系统提示词（含医疗安全护栏）
     */
    private String systemPrompt;
    /**
     * 绑定的知识库编号数组（JSON）
     */
    private String knowledgeIds;
    /**
     * 显示排序
     */
    private Integer sort;
    /**
     * 状态
     *
     * 枚举 {@link CommonStatusEnum}
     */
    private Integer status;

}
