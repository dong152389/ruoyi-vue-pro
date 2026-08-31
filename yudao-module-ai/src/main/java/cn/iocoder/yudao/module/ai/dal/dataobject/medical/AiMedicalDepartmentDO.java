package cn.iocoder.yudao.module.ai.dal.dataobject.medical;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 医疗科室表
 *
 * @author 芋道源码
 */
@TableName("ai_medical_department")
@KeySequence("ai_medical_department_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
public class AiMedicalDepartmentDO extends BaseDO {

    /**
     * 科室编号
     */
    @TableId
    private Long id;
    /**
     * 科室名称
     */
    private String name;
    /**
     * 科室介绍（诊疗范围）
     */
    private String description;
    /**
     * 症状关键词（逗号分隔，用于导诊匹配）
     */
    private String keywords;
    /**
     * 门诊位置
     */
    private String location;
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
