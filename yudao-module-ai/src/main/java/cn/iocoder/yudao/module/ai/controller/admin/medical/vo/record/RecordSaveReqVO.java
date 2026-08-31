package cn.iocoder.yudao.module.ai.controller.admin.medical.vo.record;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "管理后台 - 预问诊病历修改 Request VO")
@Data
public class RecordSaveReqVO {

    @Schema(description = "病历编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "主诉", example = "发热 3 天，伴咳嗽")
    @Size(max = 1000, message = "主诉长度不能超过 1000 个字符")
    private String chiefComplaint;

    @Schema(description = "现病史", example = "患者 3 天前受凉后出现发热...")
    private String presentIllness;

    @Schema(description = "既往史", example = "高血压病史 5 年")
    private String pastHistory;

    @Schema(description = "过敏史", example = "青霉素过敏")
    private String allergyHistory;

    @Schema(description = "建议就诊科室", example = "内科")
    private String departmentSuggestion;

    @Schema(description = "补充说明与建议", example = "建议血常规检查")
    private String advice;

}
