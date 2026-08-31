package cn.iocoder.yudao.module.ai.controller.admin.chat;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.ai.controller.admin.chat.vo.conversation.ConversationRespVO;
import cn.iocoder.yudao.module.ai.controller.admin.chat.vo.conversation.ConversationSaveReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.chat.AiChatConversationDO;
import cn.iocoder.yudao.module.ai.service.chat.AiChatConversationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - AI 聊天会话")
@RestController
@RequestMapping("/ai/chat/conversation")
@Validated
public class AiChatConversationController {

    @Resource
    private AiChatConversationService conversationService;

    @PostMapping("/create-my")
    @Operation(summary = "创建我的会话")
    public CommonResult<Long> createConversationMy(@Valid @RequestBody(required = false) ConversationSaveReqVO createReqVO) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        if (createReqVO == null) {
            createReqVO = new ConversationSaveReqVO();
        }
        return success(conversationService.createConversationMy(userId, createReqVO));
    }

    @PutMapping("/update-my")
    @Operation(summary = "更新我的会话")
    public CommonResult<Boolean> updateConversationMy(@Valid @RequestBody ConversationSaveReqVO updateReqVO) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        conversationService.updateConversationMy(userId, updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete-my")
    @Operation(summary = "删除我的会话")
    @Parameter(name = "id", description = "会话编号", required = true, example = "1")
    public CommonResult<Boolean> deleteConversationMy(@RequestParam("id") Long id) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        conversationService.deleteConversationMy(userId, id);
        return success(true);
    }

    @GetMapping("/get-my")
    @Operation(summary = "获得我的会话")
    @Parameter(name = "id", description = "会话编号", required = true, example = "1")
    public CommonResult<ConversationRespVO> getConversationMy(@RequestParam("id") Long id) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        AiChatConversationDO conversation = conversationService.getConversationMy(userId, id);
        return success(BeanUtils.toBean(conversation, ConversationRespVO.class));
    }

    @GetMapping("/my-list")
    @Operation(summary = "获得我的会话列表", description = "置顶优先，按时间倒序")
    public CommonResult<List<ConversationRespVO>> getConversationListMy() {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        List<AiChatConversationDO> list = conversationService.getConversationListMy(userId);
        return success(BeanUtils.toBean(list, ConversationRespVO.class));
    }

}
