package cn.iocoder.yudao.module.ai.service.model;

import cn.hutool.core.util.ObjectUtil;
import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.controller.admin.model.vo.ModelPageReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.model.vo.ModelSaveReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.model.AiApiKeyDO;
import cn.iocoder.yudao.module.ai.dal.dataobject.model.AiModelDO;
import cn.iocoder.yudao.module.ai.dal.mysql.model.AiModelMapper;
import cn.iocoder.yudao.module.ai.enums.AiModelTypeEnum;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.ai.enums.ErrorCodeConstants.MODEL_DISABLE;
import static cn.iocoder.yudao.module.ai.enums.ErrorCodeConstants.MODEL_KEY_NOT_EXISTS;
import static cn.iocoder.yudao.module.ai.enums.ErrorCodeConstants.MODEL_NOT_EXISTS;

/**
 * AI 模型 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class AiModelServiceImpl implements AiModelService {

    @Resource
    private AiModelMapper modelMapper;

    @Resource
    private AiApiKeyService apiKeyService;

    @Override
    public Long createModel(ModelSaveReqVO createReqVO) {
        AiModelDO model = BeanUtils.toBean(createReqVO, AiModelDO.class);
        modelMapper.insert(model);
        return model.getId();
    }

    @Override
    public void updateModel(ModelSaveReqVO updateReqVO) {
        validateModelExists(updateReqVO.getId());
        AiModelDO updateObj = BeanUtils.toBean(updateReqVO, AiModelDO.class);
        modelMapper.updateById(updateObj);
    }

    @Override
    public void deleteModel(Long id) {
        validateModelExists(id);
        modelMapper.deleteById(id);
    }

    private void validateModelExists(Long id) {
        if (id == null) {
            return;
        }
        if (modelMapper.selectById(id) == null) {
            throw exception(MODEL_NOT_EXISTS);
        }
    }

    @Override
    public AiModelDO getModel(Long id) {
        return modelMapper.selectById(id);
    }

    @Override
    public PageResult<AiModelDO> getModelPage(ModelPageReqVO pageReqVO) {
        return modelMapper.selectPage(pageReqVO);
    }

    @Override
    public List<AiModelDO> getModelListByTypeAndStatus(Integer type, Integer status) {
        return modelMapper.selectListByTypeAndStatus(type, status);
    }

    @Override
    public AiModelDO validateChatModel(Long id) {
        return validateModel(id, AiModelTypeEnum.CHAT.getType());
    }

    @Override
    public AiModelDO validateEmbeddingModel(Long id) {
        return validateModel(id, AiModelTypeEnum.EMBEDDING.getType());
    }

    private AiModelDO validateModel(Long id, Integer type) {
        AiModelDO model = modelMapper.selectById(id);
        if (model == null) {
            throw exception(MODEL_NOT_EXISTS);
        }
        if (CommonStatusEnum.DISABLE.getStatus().equals(model.getStatus())) {
            throw exception(MODEL_DISABLE);
        }
        if (ObjectUtil.notEqual(model.getType(), type)) {
            throw exception(MODEL_NOT_EXISTS);
        }
        AiApiKeyDO apiKey = apiKeyService.getApiKey(model.getKeyId());
        if (apiKey == null) {
            throw exception(MODEL_KEY_NOT_EXISTS);
        }
        return model;
    }

}
