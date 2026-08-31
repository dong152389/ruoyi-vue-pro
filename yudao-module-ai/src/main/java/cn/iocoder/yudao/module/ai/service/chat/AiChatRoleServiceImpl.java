package cn.iocoder.yudao.module.ai.service.chat;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.module.ai.controller.admin.chat.vo.role.ChatRolePageReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.chat.vo.role.ChatRoleSaveReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.chat.AiChatRoleDO;
import cn.iocoder.yudao.module.ai.dal.mysql.chat.AiChatRoleMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.ai.enums.ErrorCodeConstants.ROLE_NOT_EXISTS;

/**
 * AI 医疗角色 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class AiChatRoleServiceImpl implements AiChatRoleService {

    @Resource
    private AiChatRoleMapper chatRoleMapper;

    @Override
    public Long createChatRole(ChatRoleSaveReqVO createReqVO) {
        AiChatRoleDO role = BeanUtils.toBean(createReqVO, AiChatRoleDO.class);
        role.setKnowledgeIds(JsonUtils.toJsonString(createReqVO.getKnowledgeIds() != null
                ? createReqVO.getKnowledgeIds() : List.of()));
        chatRoleMapper.insert(role);
        return role.getId();
    }

    @Override
    public void updateChatRole(ChatRoleSaveReqVO updateReqVO) {
        validateChatRoleExists(updateReqVO.getId());
        AiChatRoleDO updateObj = BeanUtils.toBean(updateReqVO, AiChatRoleDO.class);
        updateObj.setKnowledgeIds(JsonUtils.toJsonString(updateReqVO.getKnowledgeIds() != null
                ? updateReqVO.getKnowledgeIds() : List.of()));
        chatRoleMapper.updateById(updateObj);
    }

    @Override
    public void deleteChatRole(Long id) {
        validateChatRoleExists(id);
        chatRoleMapper.deleteById(id);
    }

    private void validateChatRoleExists(Long id) {
        if (id == null) {
            return;
        }
        if (chatRoleMapper.selectById(id) == null) {
            throw exception(ROLE_NOT_EXISTS);
        }
    }

    @Override
    public AiChatRoleDO getChatRole(Long id) {
        return id != null ? chatRoleMapper.selectById(id) : null;
    }

    @Override
    public PageResult<AiChatRoleDO> getChatRolePage(ChatRolePageReqVO pageReqVO) {
        return chatRoleMapper.selectPage(pageReqVO);
    }

    @Override
    public List<AiChatRoleDO> getChatRoleListByStatus(Integer status) {
        return chatRoleMapper.selectListByStatus(status);
    }

}
