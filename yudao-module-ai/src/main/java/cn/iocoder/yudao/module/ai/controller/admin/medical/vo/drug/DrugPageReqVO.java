package cn.iocoder.yudao.module.ai.controller.admin.medical.vo.drug;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 医疗药品分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class DrugPageReqVO extends PageParam {

    @Schema(description = "药品名称，模糊匹配", example = "布洛芬")
    private String name;

    @Schema(description = "药品分类，模糊匹配", example = "解热镇痛")
    private String category;

    @Schema(description = "状态", example = "0")
    private Integer status;

}
