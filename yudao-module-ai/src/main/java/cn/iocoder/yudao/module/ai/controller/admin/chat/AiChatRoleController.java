package cn.iocoder.yudao.module.ai.controller.admin.chat;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.controller.admin.chat.vo.role.ChatRolePageReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.chat.vo.role.ChatRoleRespVO;
import cn.iocoder.yudao.module.ai.controller.admin.chat.vo.role.ChatRoleSaveReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.chat.AiChatRoleDO;
import cn.iocoder.yudao.module.ai.service.chat.AiChatRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - AI 医疗角色")
@RestController
@RequestMapping("/ai/chat-role")
@Validated
public class AiChatRoleController {

    @Resource
    private AiChatRoleService chatRoleService;

    @PostMapping("/create")
    @Operation(summary = "创建医疗角色")
    @PreAuthorize("@ss.hasPermission('ai:chat-role:create')")
    public CommonResult<Long> createChatRole(@Valid @RequestBody ChatRoleSaveReqVO createReqVO) {
        return success(chatRoleService.createChatRole(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "修改医疗角色")
    @PreAuthorize("@ss.hasPermission('ai:chat-role:update')")
    public CommonResult<Boolean> updateChatRole(@Valid @RequestBody ChatRoleSaveReqVO updateReqVO) {
        chatRoleService.updateChatRole(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除医疗角色")
    @Parameter(name = "id", description = "角色编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('ai:chat-role:delete')")
    public CommonResult<Boolean> deleteChatRole(@RequestParam("id") Long id) {
        chatRoleService.deleteChatRole(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得医疗角色")
    @Parameter(name = "id", description = "角色编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('ai:chat-role:query')")
    public CommonResult<ChatRoleRespVO> getChatRole(@RequestParam("id") Long id) {
        AiChatRoleDO role = chatRoleService.getChatRole(id);
        if (role == null) {
            return success(null);
        }
        return success(convertList(List.of(role)).get(0));
    }

    @GetMapping("/page")
    @Operation(summary = "获得医疗角色分页")
    @PreAuthorize("@ss.hasPermission('ai:chat-role:query')")
    public CommonResult<PageResult<ChatRoleRespVO>> getChatRolePage(@Validated ChatRolePageReqVO pageReqVO) {
        PageResult<AiChatRoleDO> pageResult = chatRoleService.getChatRolePage(pageReqVO);
        return success(new PageResult<>(convertList(pageResult.getList()), pageResult.getTotal()));
    }

    @GetMapping("/simple-list")
    @Operation(summary = "获得开启状态的角色精简列表", description = "对话页的角色选择")
    public CommonResult<List<ChatRoleRespVO>> getSimpleChatRoleList() {
        List<AiChatRoleDO> list = chatRoleService.getChatRoleListByStatus(CommonStatusEnum.ENABLE.getStatus());
        return success(convertList(list));
    }

    /**
     * DO 的 knowledgeIds 为 JSON 字符串，转换为编号数组
     */
    private List<ChatRoleRespVO> convertList(List<AiChatRoleDO> list) {
        List<ChatRoleRespVO> result = BeanUtils.toBean(list, ChatRoleRespVO.class);
        for (int i = 0; i < list.size(); i++) {
            result.get(i).setKnowledgeIds(JsonUtils.parseArray(list.get(i).getKnowledgeIds(), Long.class));
        }
        return result;
    }

}
