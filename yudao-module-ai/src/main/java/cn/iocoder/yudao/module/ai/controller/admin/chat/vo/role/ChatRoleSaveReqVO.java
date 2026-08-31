package cn.iocoder.yudao.module.ai.controller.admin.chat.vo.role;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "管理后台 - AI 医疗角色创建/修改 Request VO")
@Data
public class ChatRoleSaveReqVO {

    @Schema(description = "角色编号", example = "1")
    private Long id;

    @Schema(description = "角色名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "智能导诊助手")
    @NotBlank(message = "角色名称不能为空")
    @Size(max = 64, message = "角色名称长度不能超过 64 个字符")
    private String name;

    @Schema(description = "角色头像", example = "https://xxx.png")
    private String avatar;

    @Schema(description = "角色描述", example = "根据症状推荐就诊科室")
    private String description;

    @Schema(description = "角色系统提示词", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "角色系统提示词不能为空")
    private String systemPrompt;

    @Schema(description = "绑定的知识库编号数组", example = "[1,2]")
    private java.util.List<Long> knowledgeIds;

    @Schema(description = "显示排序", example = "1")
    private Integer sort;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    @InEnum(value = CommonStatusEnum.class, message = "状态必须是 {value}")
    private Integer status;

}
