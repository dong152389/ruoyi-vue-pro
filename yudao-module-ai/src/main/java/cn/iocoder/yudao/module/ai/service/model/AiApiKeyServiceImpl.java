package cn.iocoder.yudao.module.ai.service.model;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.controller.admin.model.vo.ApiKeyPageReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.model.vo.ApiKeySaveReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.model.AiApiKeyDO;
import cn.iocoder.yudao.module.ai.dal.mysql.model.AiApiKeyMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.ai.enums.ErrorCodeConstants.API_KEY_NOT_EXISTS;
import static cn.iocoder.yudao.module.ai.enums.ErrorCodeConstants.MODEL_DISABLE;

/**
 * AI API 密钥 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class AiApiKeyServiceImpl implements AiApiKeyService {

    @Resource
    private AiApiKeyMapper apiKeyMapper;

    @Override
    public Long createApiKey(ApiKeySaveReqVO createReqVO) {
        AiApiKeyDO apiKey = BeanUtils.toBean(createReqVO, AiApiKeyDO.class);
        apiKeyMapper.insert(apiKey);
        return apiKey.getId();
    }

    @Override
    public void updateApiKey(ApiKeySaveReqVO updateReqVO) {
        validateApiKeyExists(updateReqVO.getId());
        AiApiKeyDO updateObj = BeanUtils.toBean(updateReqVO, AiApiKeyDO.class);
        apiKeyMapper.updateById(updateObj);
    }

    @Override
    public void deleteApiKey(Long id) {
        validateApiKeyExists(id);
        apiKeyMapper.deleteById(id);
    }

    private void validateApiKeyExists(Long id) {
        if (id == null) {
            return;
        }
        if (apiKeyMapper.selectById(id) == null) {
            throw exception(API_KEY_NOT_EXISTS);
        }
    }

    @Override
    public AiApiKeyDO getApiKey(Long id) {
        return apiKeyMapper.selectById(id);
    }

    @Override
    public PageResult<AiApiKeyDO> getApiKeyPage(ApiKeyPageReqVO pageReqVO) {
        return apiKeyMapper.selectPage(pageReqVO);
    }

    @Override
    public List<AiApiKeyDO> getApiKeyListByStatus(Integer status) {
        return apiKeyMapper.selectListByStatus(status);
    }

    @Override
    public AiApiKeyDO validateApiKey(Long id) {
        AiApiKeyDO apiKey = apiKeyMapper.selectById(id);
        if (apiKey == null) {
            throw exception(API_KEY_NOT_EXISTS);
        }
        if (CommonStatusEnum.DISABLE.getStatus().equals(apiKey.getStatus())) {
            throw exception(MODEL_DISABLE);
        }
        return apiKey;
    }

}
