package cn.iocoder.yudao.module.ai.controller.admin.knowledge.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - AI 知识库文档 Response VO")
@Data
public class DocumentRespVO {

    @Schema(description = "文档编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "知识库编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long knowledgeId;

    @Schema(description = "文档名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "高血压防治指南.pdf")
    private String name;

    @Schema(description = "文档存储地址", example = "https://xxx/file.pdf")
    private String url;

    @Schema(description = "文档总 Token 数（估算）", example = "10240")
    private Integer tokens;

    @Schema(description = "切片数量", example = "18")
    private Integer segmentCount;

    @Schema(description = "切片状态（0待处理 1已完成 2失败）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer sliceStatus;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
