package cn.iocoder.yudao.module.ai.controller.admin.chat.vo.conversation;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - AI 聊天会话 Response VO")
@Data
public class ConversationRespVO {

    @Schema(description = "会话编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "角色编号", example = "1")
    private Long roleId;

    @Schema(description = "对话模型编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long modelId;

    @Schema(description = "会话标题", requiredMode = Schema.RequiredMode.REQUIRED, example = "最近头痛怎么办")
    private String title;

    @Schema(description = "是否置顶", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    private Boolean pinned;

    @Schema(description = "温度参数", example = "0.7")
    private Double temperature;

    @Schema(description = "回复最大 Token 数", example = "4096")
    private Integer maxTokens;

    @Schema(description = "上下文最大条数", example = "10")
    private Integer maxContexts;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
