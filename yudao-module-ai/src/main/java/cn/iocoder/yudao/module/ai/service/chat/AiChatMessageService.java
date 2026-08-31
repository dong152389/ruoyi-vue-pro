package cn.iocoder.yudao.module.ai.service.chat;

import cn.iocoder.yudao.module.ai.dal.dataobject.chat.AiChatMessageDO;

import java.util.List;

/**
 * AI 聊天消息 Service 接口
 *
 * @author 芋道源码
 */
public interface AiChatMessageService {

    /**
     * 创建聊天消息
     */
    AiChatMessageDO createMessage(Long conversationId, Long userId, String type, String model,
                                  String content, Integer usageTokens);

    /**
     * 获得会话的消息列表（按时间正序）
     */
    List<AiChatMessageDO> getMessageListByConversationId(Long conversationId);

    /**
     * 获得会话最近 limit 条消息（按时间正序），用于构建上下文
     */
    List<AiChatMessageDO> getMessageListByConversationIdWithLimit(Long conversationId, int limit);

    /**
     * 删除会话下的所有消息
     */
    void deleteMessageByConversationId(Long conversationId);

}
