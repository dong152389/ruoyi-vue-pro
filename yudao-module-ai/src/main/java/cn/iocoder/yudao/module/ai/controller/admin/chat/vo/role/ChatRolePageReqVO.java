package cn.iocoder.yudao.module.ai.controller.admin.chat.vo.role;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - AI 医疗角色分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class ChatRolePageReqVO extends PageParam {

    @Schema(description = "角色名称，模糊匹配", example = "导诊")
    private String name;

    @Schema(description = "状态", example = "0")
    private Integer status;

}
