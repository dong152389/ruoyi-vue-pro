package cn.iocoder.yudao.module.ai.dal.mysql.chat;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.ai.controller.admin.chat.vo.role.ChatRolePageReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.chat.AiChatRoleDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * AI 医疗角色 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface AiChatRoleMapper extends BaseMapperX<AiChatRoleDO> {

    default PageResult<AiChatRoleDO> selectPage(ChatRolePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AiChatRoleDO>()
                .likeIfPresent(AiChatRoleDO::getName, reqVO.getName())
                .eqIfPresent(AiChatRoleDO::getStatus, reqVO.getStatus())
                .orderByAsc(AiChatRoleDO::getSort));
    }

    default List<AiChatRoleDO> selectListByStatus(Integer status) {
        return selectList(new LambdaQueryWrapperX<AiChatRoleDO>()
                .eqIfPresent(AiChatRoleDO::getStatus, status)
                .orderByAsc(AiChatRoleDO::getSort));
    }

}
