package cn.iocoder.yudao.module.ai.controller.admin.knowledge.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - AI 医疗知识库分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class KnowledgePageReqVO extends PageParam {

    @Schema(description = "知识库名称，模糊匹配", example = "高血压")
    private String name;

}
