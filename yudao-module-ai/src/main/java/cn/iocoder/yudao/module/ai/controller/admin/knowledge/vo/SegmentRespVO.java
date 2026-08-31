package cn.iocoder.yudao.module.ai.controller.admin.knowledge.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - AI 知识库切片 Response VO")
@Data
public class SegmentRespVO {

    @Schema(description = "切片编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "文档编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long documentId;

    @Schema(description = "知识库编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long knowledgeId;

    @Schema(description = "切片内容", requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;

    @Schema(description = "切片 Token 数（估算）", example = "512")
    private Integer tokens;

    @Schema(description = "是否已向量化", example = "true")
    private Boolean hasVector;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
