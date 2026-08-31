package cn.iocoder.yudao.module.ai.controller.admin.medical.vo.drug;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "管理后台 - 医疗药品创建/修改 Request VO")
@Data
public class DrugSaveReqVO {

    @Schema(description = "药品编号", example = "1")
    private Long id;

    @Schema(description = "药品名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "布洛芬缓释胶囊")
    @NotBlank(message = "药品名称不能为空")
    @Size(max = 128, message = "药品名称长度不能超过 128 个字符")
    private String name;

    @Schema(description = "药品分类", example = "解热镇痛药")
    @Size(max = 64, message = "药品分类长度不能超过 64 个字符")
    private String category;

    @Schema(description = "适应症", example = "用于缓解轻至中度疼痛及发热")
    private String indications;

    @Schema(description = "用法用量", example = "成人一次1粒，一日2次")
    private String usageDosage;

    @Schema(description = "禁忌", example = "消化道溃疡出血史者禁用")
    private String contraindications;

    @Schema(description = "药物相互作用", example = "与抗凝药合用增加出血风险")
    private String interactions;

    @Schema(description = "不良反应", example = "常见胃肠道不适")
    private String sideEffects;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    @InEnum(value = CommonStatusEnum.class, message = "状态必须是 {value}")
    private Integer status;

}
