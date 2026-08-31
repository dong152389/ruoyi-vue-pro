package cn.iocoder.yudao.module.ai.controller.admin.chat.vo.role;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - AI 医疗角色 Response VO")
@Data
public class ChatRoleRespVO {

    @Schema(description = "角色编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "角色名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "智能导诊助手")
    private String name;

    @Schema(description = "角色头像", example = "https://xxx.png")
    private String avatar;

    @Schema(description = "角色描述", example = "根据症状推荐就诊科室")
    private String description;

    @Schema(description = "角色系统提示词", requiredMode = Schema.RequiredMode.REQUIRED)
    private String systemPrompt;

    @Schema(description = "绑定的知识库编号数组", example = "[1,2]")
    private List<Long> knowledgeIds;

    @Schema(description = "显示排序", example = "1")
    private Integer sort;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    private Integer status;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
