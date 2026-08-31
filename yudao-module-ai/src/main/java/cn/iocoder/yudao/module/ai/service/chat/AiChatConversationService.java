package cn.iocoder.yudao.module.ai.service.chat;

import cn.iocoder.yudao.module.ai.controller.admin.chat.vo.conversation.ConversationSaveReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.chat.AiChatConversationDO;

import java.util.List;

/**
 * AI 聊天会话 Service 接口
 *
 * @author 芋道源码
 */
public interface AiChatConversationService {

    /**
     * 创建我的会话：未指定模型时，自动选择默认开启的对话模型
     */
    Long createConversationMy(Long userId, ConversationSaveReqVO createReqVO);

    /**
     * 更新我的会话（校验归属）
     */
    void updateConversationMy(Long userId, ConversationSaveReqVO updateReqVO);

    /**
     * 删除我的会话（同时删除会话下的消息）
     */
    void deleteConversationMy(Long userId, Long id);

    /**
     * 获得我的会话（校验归属）
     */
    AiChatConversationDO getConversationMy(Long userId, Long id);

    /**
     * 获得我的会话列表
     */
    List<AiChatConversationDO> getConversationListMy(Long userId);

}
