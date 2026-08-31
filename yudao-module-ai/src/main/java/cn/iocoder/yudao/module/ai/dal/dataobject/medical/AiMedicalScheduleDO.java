package cn.iocoder.yudao.module.ai.dal.dataobject.medical;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 医生排班表
 *
 * @author 芋道源码
 */
@TableName("ai_medical_schedule")
@KeySequence("ai_medical_schedule_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
public class AiMedicalScheduleDO extends BaseDO {

    /**
     * 排班编号
     */
    @TableId
    private Long id;
    /**
     * 科室编号
     *
     * 关联 {@link AiMedicalDepartmentDO#getId()}
     */
    private Long departmentId;
    /**
     * 医生姓名
     */
    private String doctorName;
    /**
     * 医生职称
     */
    private String doctorTitle;
    /**
     * 排班日期
     */
    private LocalDate scheduleDate;
    /**
     * 时段（上午/下午/晚间）
     */
    private String timeSlot;
    /**
     * 接诊时间（如 08:00-12:00）
     */
    private String timeRange;
    /**
     * 号源总数
     */
    private Integer totalSlots;
    /**
     * 剩余号源
     */
    private Integer remainingSlots;
    /**
     * 挂号费（元）
     */
    private BigDecimal fee;
    /**
     * 状态（0正常 1停诊）
     */
    private Integer status;

}
