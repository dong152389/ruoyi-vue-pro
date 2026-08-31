package cn.iocoder.yudao.module.ai.controller.admin.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - AI API 密钥 Response VO")
@Data
public class ApiKeyRespVO {

    @Schema(description = "密钥编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "密钥名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "new-api 中转站")
    private String name;

    @Schema(description = "模型平台", example = "OpenAI兼容")
    private String platform;

    @Schema(description = "API 密钥", requiredMode = Schema.RequiredMode.REQUIRED, example = "sk-xxx")
    private String apiKey;

    @Schema(description = "自定义 API 地址", example = "https://your-new-api.com/v1")
    private String baseUrl;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    private Integer status;

    @Schema(description = "备注", example = "备注")
    private String remark;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
