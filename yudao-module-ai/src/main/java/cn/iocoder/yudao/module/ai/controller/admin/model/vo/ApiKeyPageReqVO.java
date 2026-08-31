package cn.iocoder.yudao.module.ai.controller.admin.model.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - AI API 密钥分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiKeyPageReqVO extends PageParam {

    @Schema(description = "密钥名称，模糊匹配", example = "new-api")
    private String name;

    @Schema(description = "状态", example = "0")
    private Integer status;

}
