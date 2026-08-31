package cn.iocoder.yudao.module.ai.controller.admin.medical.vo.appointment;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 门诊预约分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class AppointmentPageReqVO extends PageParam {

    @Schema(description = "科室编号", example = "1")
    private Long departmentId;

    @Schema(description = "患者姓名，模糊匹配", example = "张三")
    private String patientName;

    @Schema(description = "预约状态（0待就诊 1已完成 2已取消）", example = "0")
    private Integer status;

}
