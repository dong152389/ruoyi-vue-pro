package cn.iocoder.yudao.module.ai.controller.admin.medical.vo.department;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "管理后台 - 医疗科室创建/修改 Request VO")
@Data
public class DepartmentSaveReqVO {

    @Schema(description = "科室编号", example = "1")
    private Long id;

    @Schema(description = "科室名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "内科")
    @NotBlank(message = "科室名称不能为空")
    @Size(max = 64, message = "科室名称长度不能超过 64 个字符")
    private String name;

    @Schema(description = "科室介绍（诊疗范围）", example = "诊治内科常见病、多发病")
    private String description;

    @Schema(description = "症状关键词，逗号分隔", example = "发热,咳嗽,头痛")
    @Size(max = 500, message = "症状关键词长度不能超过 500 个字符")
    private String keywords;

    @Schema(description = "门诊位置", example = "门诊楼2层")
    @Size(max = 255, message = "门诊位置长度不能超过 255 个字符")
    private String location;

    @Schema(description = "显示排序", example = "1")
    private Integer sort;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    @InEnum(value = CommonStatusEnum.class, message = "状态必须是 {value}")
    private Integer status;

}
