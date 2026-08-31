package cn.iocoder.yudao.module.ai.controller.admin.chat.vo.message;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - AI 聊天消息 Response VO")
@Data
public class MessageRespVO {

    @Schema(description = "消息编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "会话编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long conversationId;

    @Schema(description = "消息类型（user/assistant/system）", requiredMode = Schema.RequiredMode.REQUIRED, example = "user")
    private String type;

    @Schema(description = "使用的模型标识", example = "deepseek-chat")
    private String model;

    @Schema(description = "消息内容", requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;

    @Schema(description = "本次消耗 Token 数", example = "1024")
    private Integer usageTokens;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
