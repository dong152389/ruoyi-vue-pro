package cn.iocoder.yudao.module.ai.dal.dataobject.medical;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 门诊预约表
 *
 * @author 芋道源码
 */
@TableName("ai_medical_appointment")
@KeySequence("ai_medical_appointment_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
public class AiMedicalAppointmentDO extends TenantBaseDO {

    /**
     * 预约状态 - 待就诊
     */
    public static final Integer STATUS_WAITING = 0;
    /**
     * 预约状态 - 已完成
     */
    public static final Integer STATUS_FINISHED = 1;
    /**
     * 预约状态 - 已取消
     */
    public static final Integer STATUS_CANCELED = 2;

    /**
     * 预约编号
     */
    @TableId
    private Long id;
    /**
     * 预约用户编号
     */
    private Long userId;
    /**
     * 患者姓名
     */
    private String patientName;
    /**
     * 患者手机号
     */
    private String patientPhone;
    /**
     * 排班编号
     *
     * 关联 {@link AiMedicalScheduleDO#getId()}
     */
    private Long scheduleId;
    /**
     * 科室编号
     *
     * 关联 {@link AiMedicalDepartmentDO#getId()}
     */
    private Long departmentId;
    /**
     * 科室名称（冗余）
     */
    private String departmentName;
    /**
     * 医生姓名（冗余）
     */
    private String doctorName;
    /**
     * 就诊日期
     */
    private LocalDate appointmentDate;
    /**
     * 就诊时段
     */
    private String timeSlot;
    /**
     * 预约状态（0待就诊 1已完成 2已取消）
     */
    private Integer status;
    /**
     * 备注
     */
    private String remark;

}
