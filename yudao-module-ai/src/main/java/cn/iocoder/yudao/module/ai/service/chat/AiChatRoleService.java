package cn.iocoder.yudao.module.ai.service.chat;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.ai.controller.admin.chat.vo.role.ChatRolePageReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.chat.vo.role.ChatRoleSaveReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.chat.AiChatRoleDO;

import java.util.List;

/**
 * AI 医疗角色 Service 接口
 *
 * @author 芋道源码
 */
public interface AiChatRoleService {

    Long createChatRole(ChatRoleSaveReqVO createReqVO);

    void updateChatRole(ChatRoleSaveReqVO updateReqVO);

    void deleteChatRole(Long id);

    AiChatRoleDO getChatRole(Long id);

    PageResult<AiChatRoleDO> getChatRolePage(ChatRolePageReqVO pageReqVO);

    /**
     * 获得开启状态的角色列表（对话页角色选择）
     */
    List<AiChatRoleDO> getChatRoleListByStatus(Integer status);

}
