package cn.iocoder.yudao.module.ai.controller.admin.model.vo;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.validation.InEnum;
import cn.iocoder.yudao.module.ai.enums.AiModelTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "管理后台 - AI 模型创建/修改 Request VO")
@Data
public class ModelSaveReqVO {

    @Schema(description = "模型编号", example = "1")
    private Long id;

    @Schema(description = "API 密钥编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "API 密钥编号不能为空")
    private Long keyId;

    @Schema(description = "模型名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "DeepSeek 对话模型")
    @NotBlank(message = "模型名称不能为空")
    @Size(max = 64, message = "模型名称长度不能超过 64 个字符")
    private String name;

    @Schema(description = "模型标识", requiredMode = Schema.RequiredMode.REQUIRED, example = "deepseek-chat")
    @NotBlank(message = "模型标识不能为空")
    @Size(max = 128, message = "模型标识长度不能超过 128 个字符")
    private String model;

    @Schema(description = "模型类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "模型类型不能为空")
    @InEnum(value = AiModelTypeEnum.class, message = "模型类型必须是 {value}")
    private Integer type;

    @Schema(description = "温度参数", example = "0.7")
    private Double temperature;

    @Schema(description = "回复最大 Token 数", example = "4096")
    private Integer maxTokens;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    @InEnum(value = CommonStatusEnum.class, message = "状态必须是 {value}")
    private Integer status;

    @Schema(description = "备注", example = "备注")
    private String remark;

}
