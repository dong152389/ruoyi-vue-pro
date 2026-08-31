package cn.iocoder.yudao.module.ai.controller.admin.chat.vo.conversation;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - AI 聊天会话创建/修改 Request VO")
@Data
public class ConversationSaveReqVO {

    @Schema(description = "会话编号", example = "1")
    private Long id;

    @Schema(description = "角色编号（医疗角色预设）", example = "1")
    private Long roleId;

    @Schema(description = "对话模型编号", example = "1")
    private Long modelId;

    @Schema(description = "会话标题", example = "最近头痛怎么办")
    private String title;

    @Schema(description = "是否置顶", example = "true")
    private Boolean pinned;

    @Schema(description = "温度参数", example = "0.7")
    private Double temperature;

    @Schema(description = "回复最大 Token 数", example = "4096")
    private Integer maxTokens;

    @Schema(description = "上下文最大条数", example = "10")
    private Integer maxContexts;

}
