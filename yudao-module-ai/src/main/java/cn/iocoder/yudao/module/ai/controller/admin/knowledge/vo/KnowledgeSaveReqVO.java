package cn.iocoder.yudao.module.ai.controller.admin.knowledge.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "管理后台 - AI 医疗知识库创建/修改 Request VO")
@Data
public class KnowledgeSaveReqVO {

    @Schema(description = "知识库编号", example = "1")
    private Long id;

    @Schema(description = "知识库名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "高血压诊疗指南")
    @NotBlank(message = "知识库名称不能为空")
    @Size(max = 64, message = "知识库名称长度不能超过 64 个字符")
    private String name;

    @Schema(description = "知识库描述", example = "收录高血压相关诊疗规范")
    private String description;

    @Schema(description = "向量模型编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "向量模型编号不能为空")
    private Long embeddingModelId;

}
