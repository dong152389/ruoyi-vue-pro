package cn.iocoder.yudao.module.ai.framework.ai;

import cn.iocoder.yudao.module.ai.dal.dataobject.model.AiModelDO;
import cn.iocoder.yudao.module.ai.enums.AiPlatformEnum;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.embedding.EmbeddingModel;

/**
 * AI 模型工厂接口：根据后台「模型管理」的配置（API 密钥 + 模型），构建 Spring AI 的模型实例
 * <p>
 * 按 ai_api_key.platform 构建对应官方 ChatModel：
 * - OpenAI兼容（默认）：OpenAI 兼容协议对接 new-api 等中转站；
 * - Anthropic：Anthropic 官方 API（Claude）；
 * - Gemini：Gemini Developer API（API Key 模式，直连官方端点）
 *
 * @author 芋道源码
 */
public interface AiModelFactory {

    /**
     * 获得或创建对话模型（带缓存）
     */
    ChatModel getOrCreateChatModel(AiModelDO model);

    /**
     * 构建单次请求的对话 Options（按平台返回对应子类）
     *
     * temperature / maxTokens 由会话级配置覆盖，为空时仅携带 model 标识（其余走模型默认配置）
     */
    ChatOptions buildChatOptions(AiModelDO model, Double temperature, Integer maxTokens);

    /**
     * 解析模型所属平台（根据其绑定密钥的 platform 字段）
     */
    AiPlatformEnum getChatPlatform(AiModelDO model);

    /**
     * 获得或创建向量模型（带缓存，仅支持 OpenAI 兼容协议）
     */
    EmbeddingModel getOrCreateEmbeddingModel(AiModelDO model);

}
