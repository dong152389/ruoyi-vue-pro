package cn.iocoder.yudao.module.ai.dal.dataobject.medical;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 预问诊病历表
 *
 * 由医疗 Agent 在对话中收集信息后生成
 *
 * @author 芋道源码
 */
@TableName("ai_medical_record")
@KeySequence("ai_medical_record_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
public class AiMedicalRecordDO extends TenantBaseDO {

    /**
     * 预问诊病历编号
     */
    @TableId
    private Long id;
    /**
     * 用户编号
     */
    private Long userId;
    /**
     * 产生病历的会话编号
     *
     * 关联 {@link cn.iocoder.yudao.module.ai.dal.dataobject.chat.AiChatConversationDO#getId()}
     */
    private Long conversationId;
    /**
     * 主诉
     */
    private String chiefComplaint;
    /**
     * 现病史
     */
    private String presentIllness;
    /**
     * 既往史
     */
    private String pastHistory;
    /**
     * 过敏史
     */
    private String allergyHistory;
    /**
     * 建议就诊科室
     */
    private String departmentSuggestion;
    /**
     * 补充说明与建议
     */
    private String advice;

}
