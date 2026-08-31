package cn.iocoder.yudao.module.ai.controller.admin.knowledge.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - AI 知识库文档分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class DocumentPageReqVO extends PageParam {

    @Schema(description = "知识库编号", example = "1")
    private Long knowledgeId;

    @Schema(description = "文档名称，模糊匹配", example = "指南")
    private String name;

}
