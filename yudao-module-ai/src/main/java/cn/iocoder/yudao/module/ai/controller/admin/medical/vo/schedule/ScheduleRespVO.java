package cn.iocoder.yudao.module.ai.controller.admin.medical.vo.schedule;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 医生排班 Response VO")
@Data
public class ScheduleRespVO {

    @Schema(description = "排班编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "科室编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long departmentId;

    @Schema(description = "科室名称", example = "内科")
    private String departmentName;

    @Schema(description = "医生姓名", requiredMode = Schema.RequiredMode.REQUIRED, example = "王建国")
    private String doctorName;

    @Schema(description = "医生职称", example = "主任医师")
    private String doctorTitle;

    @Schema(description = "排班日期", requiredMode = Schema.RequiredMode.REQUIRED, example = "2026-09-01")
    private LocalDate scheduleDate;

    @Schema(description = "时段", requiredMode = Schema.RequiredMode.REQUIRED, example = "上午")
    private String timeSlot;

    @Schema(description = "接诊时间", example = "08:00-12:00")
    private String timeRange;

    @Schema(description = "号源总数", requiredMode = Schema.RequiredMode.REQUIRED, example = "20")
    private Integer totalSlots;

    @Schema(description = "剩余号源", requiredMode = Schema.RequiredMode.REQUIRED, example = "18")
    private Integer remainingSlots;

    @Schema(description = "挂号费（元）", example = "25.00")
    private BigDecimal fee;

    @Schema(description = "状态（0正常 1停诊）", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    private Integer status;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
