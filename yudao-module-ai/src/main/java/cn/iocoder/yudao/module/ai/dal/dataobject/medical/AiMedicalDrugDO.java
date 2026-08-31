package cn.iocoder.yudao.module.ai.dal.dataobject.medical;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 医疗药品表
 *
 * @author 芋道源码
 */
@TableName("ai_medical_drug")
@KeySequence("ai_medical_drug_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
public class AiMedicalDrugDO extends BaseDO {

    /**
     * 药品编号
     */
    @TableId
    private Long id;
    /**
     * 药品名称
     */
    private String name;
    /**
     * 药品分类
     */
    private String category;
    /**
     * 适应症
     */
    private String indications;
    /**
     * 用法用量
     */
    private String usageDosage;
    /**
     * 禁忌
     */
    private String contraindications;
    /**
     * 药物相互作用
     */
    private String interactions;
    /**
     * 不良反应
     */
    private String sideEffects;
    /**
     * 状态
     *
     * 枚举 {@link CommonStatusEnum}
     */
    private Integer status;

}
