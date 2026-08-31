package cn.iocoder.yudao.module.ai.controller.admin.knowledge.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - AI 知识库切片分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class SegmentPageReqVO extends PageParam {

    @Schema(description = "文档编号", example = "1")
    private Long documentId;

    @Schema(description = "知识库编号", example = "1")
    private Long knowledgeId;

}
