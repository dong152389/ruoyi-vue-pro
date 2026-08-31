package cn.iocoder.yudao.module.ai.controller.admin.medical.vo.appointment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - 门诊预约创建/修改 Request VO")
@Data
public class AppointmentSaveReqVO {

    @Schema(description = "预约编号", example = "1")
    private Long id;

    @Schema(description = "排班编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "排班编号不能为空")
    private Long scheduleId;

    @Schema(description = "患者姓名", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @NotBlank(message = "患者姓名不能为空")
    private String patientName;

    @Schema(description = "患者手机号", requiredMode = Schema.RequiredMode.REQUIRED, example = "13800138000")
    @NotBlank(message = "患者手机号不能为空")
    private String patientPhone;

    @Schema(description = "备注", example = "备注")
    private String remark;

}
