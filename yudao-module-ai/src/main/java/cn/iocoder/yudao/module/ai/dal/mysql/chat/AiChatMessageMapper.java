package cn.iocoder.yudao.module.ai.dal.mysql.chat;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.ai.dal.dataobject.chat.AiChatMessageDO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * AI 聊天消息 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface AiChatMessageMapper extends BaseMapperX<AiChatMessageDO> {

    default List<AiChatMessageDO> selectListByConversationId(Long conversationId) {
        return selectList(new LambdaQueryWrapperX<AiChatMessageDO>()
                .eq(AiChatMessageDO::getConversationId, conversationId)
                .orderByAsc(AiChatMessageDO::getId));
    }

    /**
     * 查询会话最近的 N 条消息，按消息编号正序返回
     */
    default List<AiChatMessageDO> selectListByConversationIdWithLimit(Long conversationId, int limit) {
        List<AiChatMessageDO> list = selectList(new LambdaQueryWrapper<AiChatMessageDO>()
                .eq(AiChatMessageDO::getConversationId, conversationId)
                .orderByDesc(AiChatMessageDO::getId)
                .last("LIMIT " + limit));
        List<AiChatMessageDO> result = new java.util.ArrayList<>(list);
        java.util.Collections.reverse(result);
        return result;
    }

}
