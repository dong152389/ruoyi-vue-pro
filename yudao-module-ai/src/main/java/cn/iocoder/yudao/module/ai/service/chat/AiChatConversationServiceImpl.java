package cn.iocoder.yudao.module.ai.service.chat;

import cn.hutool.core.bean.BeanUtil;
import cn.iocoder.yudao.module.ai.controller.admin.chat.vo.conversation.ConversationSaveReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.chat.AiChatConversationDO;
import cn.iocoder.yudao.module.ai.dal.dataobject.model.AiModelDO;
import cn.iocoder.yudao.module.ai.dal.mysql.chat.AiChatConversationMapper;
import cn.iocoder.yudao.module.ai.enums.AiModelTypeEnum;
import cn.iocoder.yudao.module.ai.service.model.AiModelService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.ai.enums.ErrorCodeConstants.CHAT_MODEL_NOT_CONFIGURED;
import static cn.iocoder.yudao.module.ai.enums.ErrorCodeConstants.CONVERSATION_NOT_EXISTS;

/**
 * AI 聊天会话 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class AiChatConversationServiceImpl implements AiChatConversationService {

    /**
     * 默认温度参数
     */
    private static final Double DEFAULT_TEMPERATURE = 0.7D;
    /**
     * 默认回复最大 Token 数
     */
    private static final Integer DEFAULT_MAX_TOKENS = 4096;
    /**
     * 默认上下文最大条数
     */
    private static final Integer DEFAULT_MAX_CONTEXTS = 10;

    @Resource
    private AiChatConversationMapper conversationMapper;

    @Resource
    private AiChatMessageService chatMessageService;

    @Resource
    private AiModelService modelService;

    @Override
    public Long createConversationMy(Long userId, ConversationSaveReqVO createReqVO) {
        AiChatConversationDO conversation = BeanUtil.toBean(createReqVO, AiChatConversationDO.class);
        conversation.setId(null);
        conversation.setUserId(userId);
        conversation.setTitle(createReqVO.getTitle() != null ? createReqVO.getTitle() : "新对话");
        conversation.setPinned(createReqVO.getPinned() != null ? createReqVO.getPinned() : false);
        conversation.setTemperature(createReqVO.getTemperature() != null ? createReqVO.getTemperature() : DEFAULT_TEMPERATURE);
        conversation.setMaxTokens(createReqVO.getMaxTokens() != null ? createReqVO.getMaxTokens() : DEFAULT_MAX_TOKENS);
        conversation.setMaxContexts(createReqVO.getMaxContexts() != null ? createReqVO.getMaxContexts() : DEFAULT_MAX_CONTEXTS);
        conversation.setModelId(resolveModelId(createReqVO.getModelId()));
        conversationMapper.insert(conversation);
        return conversation.getId();
    }

    private Long resolveModelId(Long modelId) {
        if (modelId != null) {
            return modelId;
        }
        // 未指定模型时，选择默认开启的第一个对话模型
        List<AiModelDO> models = modelService.getModelListByTypeAndStatus(
                AiModelTypeEnum.CHAT.getType(), cn.iocoder.yudao.framework.common.enums.CommonStatusEnum.ENABLE.getStatus());
        if (models.isEmpty()) {
            throw exception(CHAT_MODEL_NOT_CONFIGURED);
        }
        return models.get(0).getId();
    }

    @Override
    public void updateConversationMy(Long userId, ConversationSaveReqVO updateReqVO) {
        getConversationMy(userId, updateReqVO.getId());
        AiChatConversationDO updateObj = BeanUtil.toBean(updateReqVO, AiChatConversationDO.class);
        updateObj.setUserId(userId);
        conversationMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteConversationMy(Long userId, Long id) {
        getConversationMy(userId, id);
        conversationMapper.deleteById(id);
        chatMessageService.deleteMessageByConversationId(id);
    }

    @Override
    public AiChatConversationDO getConversationMy(Long userId, Long id) {
        AiChatConversationDO conversation = conversationMapper.selectById(id);
        if (conversation == null || !conversation.getUserId().equals(userId)) {
            throw exception(CONVERSATION_NOT_EXISTS);
        }
        return conversation;
    }

    @Override
    public List<AiChatConversationDO> getConversationListMy(Long userId) {
        return conversationMapper.selectListByUserId(userId);
    }

}
