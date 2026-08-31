package cn.iocoder.yudao.module.ai.controller.admin.medical.vo.appointment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 门诊预约 Response VO")
@Data
public class AppointmentRespVO {

    @Schema(description = "预约编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "预约用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long userId;

    @Schema(description = "患者姓名", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    private String patientName;

    @Schema(description = "患者手机号", requiredMode = Schema.RequiredMode.REQUIRED, example = "13800138000")
    private String patientPhone;

    @Schema(description = "排班编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long scheduleId;

    @Schema(description = "科室编号", example = "1")
    private Long departmentId;

    @Schema(description = "科室名称", example = "内科")
    private String departmentName;

    @Schema(description = "医生姓名", example = "王建国")
    private String doctorName;

    @Schema(description = "就诊日期", requiredMode = Schema.RequiredMode.REQUIRED, example = "2026-09-01")
    private LocalDate appointmentDate;

    @Schema(description = "就诊时段", requiredMode = Schema.RequiredMode.REQUIRED, example = "上午")
    private String timeSlot;

    @Schema(description = "预约状态（0待就诊 1已完成 2已取消）", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    private Integer status;

    @Schema(description = "备注", example = "备注")
    private String remark;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
