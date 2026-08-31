package cn.iocoder.yudao.module.ai.controller.admin.medical.vo.department;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 医疗科室分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class DepartmentPageReqVO extends PageParam {

    @Schema(description = "科室名称，模糊匹配", example = "内科")
    private String name;

    @Schema(description = "状态", example = "0")
    private Integer status;

}
