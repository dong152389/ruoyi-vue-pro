package cn.iocoder.yudao.module.ai.framework.ai;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.module.ai.dal.dataobject.model.AiApiKeyDO;
import cn.iocoder.yudao.module.ai.dal.dataobject.model.AiModelDO;
import cn.iocoder.yudao.module.ai.enums.AiPlatformEnum;
import cn.iocoder.yudao.module.ai.service.model.AiApiKeyService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.anthropic.AnthropicChatOptions;
import org.springframework.ai.anthropic.api.AnthropicApi;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.ai.google.genai.GoogleGenAiChatOptions;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AI 模型工厂实现类
 *
 * 按 ai_api_key.platform 构建对应官方模型实例：
 * - OpenAI兼容（默认兜底）：通过 {@link OpenAiApi} 访问 new-api 等中转站，
 *   中转站地址以 /v1 结尾时自动剥离，交由 OpenAiApi 默认的 /v1/chat/completions、/v1/embeddings 路径拼接
 * - Anthropic：{@link AnthropicApi} 直连官方（baseUrl 可选覆盖）
 * - Gemini：官方 genai SDK 的 Gemini Developer API（API Key 模式，不支持自定义 baseUrl）
 *
 * @author 芋道源码
 */
@Slf4j
@Component
public class AiModelFactoryImpl implements AiModelFactory {

    /**
     * Anthropic 官方 API 必填 max_tokens，未配置时的兜底值
     */
    private static final int DEFAULT_ANTHROPIC_MAX_TOKENS = 4096;

    /**
     * 模型实例缓存：key = 平台 + 类型 + 密钥编号 + 模型标识 + 采样参数
     * 后台修改模型配置后，key 变化会重建实例，旧实例少量驻留可接受
     */
    private final Map<String, ChatModel> chatModelCache = new ConcurrentHashMap<>();
    private final Map<String, EmbeddingModel> embeddingModelCache = new ConcurrentHashMap<>();

    @Resource
    private AiApiKeyService apiKeyService;

    @Override
    public ChatModel getOrCreateChatModel(AiModelDO model) {
        AiPlatformEnum platform = getChatPlatform(model);
        AiApiKeyDO apiKey = getApiKey(model);
        String cacheKey = String.format("chat:%s:%d:%s:%s:%s", platform, apiKey.getId(), model.getModel(),
                model.getTemperature(), model.getMaxTokens());
        return chatModelCache.computeIfAbsent(cacheKey, k -> buildChatModel(platform, model, apiKey));
    }

    @Override
    public ChatOptions buildChatOptions(AiModelDO model, Double temperature, Integer maxTokens) {
        AiPlatformEnum platform = getChatPlatform(model);
        return switch (platform) {
            case ANTHROPIC -> {
                AnthropicChatOptions.Builder builder = AnthropicChatOptions.builder().model(model.getModel());
                if (temperature != null) {
                    builder.temperature(temperature);
                }
                // Anthropic 官方 API 必填 max_tokens
                builder.maxTokens(maxTokens != null ? maxTokens : DEFAULT_ANTHROPIC_MAX_TOKENS);
                yield builder.build();
            }
            case GEMINI -> {
                GoogleGenAiChatOptions.Builder builder = GoogleGenAiChatOptions.builder().model(model.getModel());
                if (temperature != null) {
                    builder.temperature(temperature);
                }
                if (maxTokens != null) {
                    builder.maxOutputTokens(maxTokens);
                }
                yield builder.build();
            }
            default -> {
                // streamUsage：流式响应也回传 token 用量，用于落库 usageTokens
                OpenAiChatOptions.Builder builder = OpenAiChatOptions.builder().model(model.getModel()).streamUsage(true);
                if (temperature != null) {
                    builder.temperature(temperature);
                }
                if (maxTokens != null) {
                    builder.maxTokens(maxTokens);
                }
                yield builder.build();
            }
        };
    }

    @Override
    public AiPlatformEnum getChatPlatform(AiModelDO model) {
        return AiPlatformEnum.of(getApiKey(model).getPlatform());
    }

    @Override
    public EmbeddingModel getOrCreateEmbeddingModel(AiModelDO model) {
        AiApiKeyDO apiKey = getApiKey(model);
        String cacheKey = String.format("embedding:%d:%s", apiKey.getId(), model.getModel());
        return embeddingModelCache.computeIfAbsent(cacheKey, k -> buildEmbeddingModel(model, apiKey));
    }

    private ChatModel buildChatModel(AiPlatformEnum platform, AiModelDO model, AiApiKeyDO apiKey) {
        return switch (platform) {
            case ANTHROPIC -> buildAnthropicChatModel(model, apiKey);
            case GEMINI -> buildGeminiChatModel(model, apiKey);
            default -> buildOpenAiChatModel(model, apiKey);
        };
    }

    private ChatModel buildOpenAiChatModel(AiModelDO model, AiApiKeyDO apiKey) {
        OpenAiApi api = buildOpenAiApi(apiKey);
        OpenAiChatOptions.Builder optionsBuilder = OpenAiChatOptions.builder().model(model.getModel());
        if (model.getTemperature() != null) {
            optionsBuilder.temperature(model.getTemperature());
        }
        if (model.getMaxTokens() != null) {
            optionsBuilder.maxTokens(model.getMaxTokens());
        }
        return OpenAiChatModel.builder()
                .openAiApi(api)
                .defaultOptions(optionsBuilder.build())
                .build();
    }

    private ChatModel buildAnthropicChatModel(AiModelDO model, AiApiKeyDO apiKey) {
        AnthropicApi.Builder apiBuilder = AnthropicApi.builder().apiKey(apiKey.getApiKey());
        String baseUrl = normalizeBaseUrl(apiKey.getBaseUrl());
        if (baseUrl != null) {
            apiBuilder.baseUrl(baseUrl);
        }
        AnthropicChatOptions.Builder optionsBuilder = AnthropicChatOptions.builder().model(model.getModel());
        if (model.getTemperature() != null) {
            optionsBuilder.temperature(model.getTemperature());
        }
        // Anthropic 官方 API 必填 max_tokens
        optionsBuilder.maxTokens(model.getMaxTokens() != null ? model.getMaxTokens() : DEFAULT_ANTHROPIC_MAX_TOKENS);
        return AnthropicChatModel.builder()
                .anthropicApi(apiBuilder.build())
                .defaultOptions(optionsBuilder.build())
                .build();
    }

    private ChatModel buildGeminiChatModel(AiModelDO model, AiApiKeyDO apiKey) {
        // Gemini Developer API（API Key 模式）直连官方端点，不支持自定义 baseUrl
        com.google.genai.Client genAiClient = com.google.genai.Client.builder()
                .apiKey(apiKey.getApiKey())
                .build();
        GoogleGenAiChatOptions.Builder optionsBuilder = GoogleGenAiChatOptions.builder().model(model.getModel());
        if (model.getTemperature() != null) {
            optionsBuilder.temperature(model.getTemperature());
        }
        if (model.getMaxTokens() != null) {
            optionsBuilder.maxOutputTokens(model.getMaxTokens());
        }
        return GoogleGenAiChatModel.builder()
                .genAiClient(genAiClient)
                .defaultOptions(optionsBuilder.build())
                .build();
    }

    private EmbeddingModel buildEmbeddingModel(AiModelDO model, AiApiKeyDO apiKey) {
        OpenAiApi api = buildOpenAiApi(apiKey);
        return new OpenAiEmbeddingModel(api, MetadataMode.EMBED,
                OpenAiEmbeddingOptions.builder().model(model.getModel()).build());
    }

    private OpenAiApi buildOpenAiApi(AiApiKeyDO apiKey) {
        OpenAiApi.Builder builder = OpenAiApi.builder().apiKey(apiKey.getApiKey());
        String baseUrl = normalizeBaseUrl(apiKey.getBaseUrl());
        if (baseUrl != null) {
            builder.baseUrl(baseUrl);
        }
        return builder.build();
    }

    private AiApiKeyDO getApiKey(AiModelDO model) {
        AiApiKeyDO apiKey = apiKeyService.getApiKey(model.getKeyId());
        Assert.notNull(apiKey, "模型 [{}] 绑定的 API 密钥不存在", model.getId());
        return apiKey;
    }

    /**
     * 归一化中转站地址：去掉末尾斜杠与 /v1 后缀（OpenAiApi 默认路径已包含 /v1 前缀）
     */
    private String normalizeBaseUrl(String baseUrl) {
        if (StrUtil.isBlank(baseUrl)) {
            return null;
        }
        String url = StrUtil.trim(baseUrl);
        while (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        if (url.endsWith("/v1")) {
            url = url.substring(0, url.length() - 3);
        }
        return url;
    }

}
