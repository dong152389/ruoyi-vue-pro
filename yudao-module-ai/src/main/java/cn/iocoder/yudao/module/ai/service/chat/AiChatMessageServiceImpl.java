package cn.iocoder.yudao.module.ai.service.chat;

import cn.iocoder.yudao.module.ai.dal.dataobject.chat.AiChatMessageDO;
import cn.iocoder.yudao.module.ai.dal.mysql.chat.AiChatMessageMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * AI 聊天消息 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class AiChatMessageServiceImpl implements AiChatMessageService {

    @Resource
    private AiChatMessageMapper chatMessageMapper;

    @Override
    public AiChatMessageDO createMessage(Long conversationId, Long userId, String type, String model,
                                         String content, Integer usageTokens) {
        AiChatMessageDO message = new AiChatMessageDO();
        message.setConversationId(conversationId);
        message.setUserId(userId);
        message.setType(type);
        message.setModel(model);
        message.setContent(content);
        message.setUsageTokens(usageTokens);
        chatMessageMapper.insert(message);
        return message;
    }

    @Override
    public List<AiChatMessageDO> getMessageListByConversationId(Long conversationId) {
        return chatMessageMapper.selectListByConversationId(conversationId);
    }

    @Override
    public List<AiChatMessageDO> getMessageListByConversationIdWithLimit(Long conversationId, int limit) {
        return chatMessageMapper.selectListByConversationIdWithLimit(conversationId, limit);
    }

    @Override
    public void deleteMessageByConversationId(Long conversationId) {
        chatMessageMapper.delete(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AiChatMessageDO>()
                .eq(AiChatMessageDO::getConversationId, conversationId));
    }

}
