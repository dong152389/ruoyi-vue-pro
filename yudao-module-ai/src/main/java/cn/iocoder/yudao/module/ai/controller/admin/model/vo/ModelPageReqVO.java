package cn.iocoder.yudao.module.ai.controller.admin.model.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - AI 模型分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class ModelPageReqVO extends PageParam {

    @Schema(description = "模型名称，模糊匹配", example = "DeepSeek")
    private String name;

    @Schema(description = "API 密钥编号", example = "1")
    private Long keyId;

    @Schema(description = "模型类型（1对话 2向量）", example = "1")
    private Integer type;

    @Schema(description = "状态", example = "0")
    private Integer status;

}
