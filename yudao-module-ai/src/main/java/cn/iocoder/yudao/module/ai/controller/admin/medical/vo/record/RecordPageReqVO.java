package cn.iocoder.yudao.module.ai.controller.admin.medical.vo.record;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 预问诊病历分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class RecordPageReqVO extends PageParam {

    @Schema(description = "用户编号", example = "1")
    private Long userId;

    @Schema(description = "主诉，模糊匹配", example = "发热")
    private String chiefComplaint;

}
