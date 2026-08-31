package cn.iocoder.yudao.module.ai.controller.admin.medical.vo.department;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 医疗科室 Response VO")
@Data
public class DepartmentRespVO {

    @Schema(description = "科室编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "科室名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "内科")
    private String name;

    @Schema(description = "科室介绍", example = "诊治内科常见病、多发病")
    private String description;

    @Schema(description = "症状关键词", example = "发热,咳嗽,头痛")
    private String keywords;

    @Schema(description = "门诊位置", example = "门诊楼2层")
    private String location;

    @Schema(description = "显示排序", example = "1")
    private Integer sort;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    private Integer status;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
