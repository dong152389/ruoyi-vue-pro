package cn.iocoder.yudao.module.ai.controller.admin.medical.vo.schedule;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY;

@Schema(description = "管理后台 - 医生排班分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class SchedulePageReqVO extends PageParam {

    @Schema(description = "科室编号", example = "1")
    private Long departmentId;

    @Schema(description = "医生姓名，模糊匹配", example = "王建国")
    private String doctorName;

    @Schema(description = "排班日期-开始", example = "2026-09-01")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate beginScheduleDate;

    @Schema(description = "排班日期-结束", example = "2026-09-07")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate endScheduleDate;

    @Schema(description = "状态（0正常 1停诊）", example = "0")
    private Integer status;

}
