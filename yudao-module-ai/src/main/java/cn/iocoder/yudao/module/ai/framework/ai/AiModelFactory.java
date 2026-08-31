package cn.iocoder.yudao.module.ai.framework.ai;

import cn.iocoder.yudao.module.ai.dal.dataobject.model.AiModelDO;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.embedding.EmbeddingModel;

/**
 * AI 模型工厂接口：根据后台「模型管理」的配置（API 密钥 + 模型），构建 Spring AI 的模型实例
 *
 * 通过 OpenAI 兼容协议对接 new-api 等中转站，base_url、api_key 均来自 ai_api_key 表
 *
 * @author 芋道源码
 */
public interface AiModelFactory {

    /**
     * 获得或创建对话模型（带缓存）
     */
    ChatModel getOrCreateChatModel(AiModelDO model);

    /**
     * 获得或创建向量模型（带缓存）
     */
    EmbeddingModel getOrCreateEmbeddingModel(AiModelDO model);

}
