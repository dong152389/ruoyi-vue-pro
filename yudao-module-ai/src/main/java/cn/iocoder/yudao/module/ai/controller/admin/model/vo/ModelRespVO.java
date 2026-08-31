package cn.iocoder.yudao.module.ai.controller.admin.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - AI 模型 Response VO")
@Data
public class ModelRespVO {

    @Schema(description = "模型编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "API 密钥编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long keyId;

    @Schema(description = "模型名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "DeepSeek 对话模型")
    private String name;

    @Schema(description = "模型标识", requiredMode = Schema.RequiredMode.REQUIRED, example = "deepseek-chat")
    private String model;

    @Schema(description = "模型类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer type;

    @Schema(description = "温度参数", example = "0.7")
    private Double temperature;

    @Schema(description = "回复最大 Token 数", example = "4096")
    private Integer maxTokens;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    private Integer status;

    @Schema(description = "备注", example = "备注")
    private String remark;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
