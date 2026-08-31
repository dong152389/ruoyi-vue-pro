package cn.iocoder.yudao.module.ai.controller.admin.chat.vo.message;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - AI 聊天消息发送 Request VO（SSE 流式）")
@Data
public class MessageSendReqVO {

    @Schema(description = "会话编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "会话编号不能为空")
    private Long conversationId;

    @Schema(description = "用户消息内容", requiredMode = Schema.RequiredMode.REQUIRED, example = "我最近三天一直头痛，应该挂什么科？")
    @NotBlank(message = "消息内容不能为空")
    private String content;

}
