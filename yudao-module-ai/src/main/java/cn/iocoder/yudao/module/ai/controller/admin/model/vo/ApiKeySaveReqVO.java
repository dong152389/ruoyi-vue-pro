package cn.iocoder.yudao.module.ai.controller.admin.model.vo;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "管理后台 - AI API 密钥创建/修改 Request VO")
@Data
public class ApiKeySaveReqVO {

    @Schema(description = "密钥编号", example = "1")
    private Long id;

    @Schema(description = "密钥名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "new-api 中转站")
    @NotBlank(message = "密钥名称不能为空")
    @Size(max = 64, message = "密钥名称长度不能超过 64 个字符")
    private String name;

    @Schema(description = "模型平台", example = "OpenAI兼容")
    @Size(max = 32, message = "模型平台长度不能超过 32 个字符")
    private String platform;

    @Schema(description = "API 密钥", requiredMode = Schema.RequiredMode.REQUIRED, example = "sk-xxx")
    @NotBlank(message = "API 密钥不能为空")
    @Size(max = 256, message = "API 密钥长度不能超过 256 个字符")
    private String apiKey;

    @Schema(description = "自定义 API 地址（以 /v1 结尾）", example = "https://your-new-api.com/v1")
    @Size(max = 255, message = "API 地址长度不能超过 255 个字符")
    private String baseUrl;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    @InEnum(value = CommonStatusEnum.class, message = "状态必须是 {value}")
    private Integer status;

    @Schema(description = "备注", example = "备注")
    private String remark;

}
