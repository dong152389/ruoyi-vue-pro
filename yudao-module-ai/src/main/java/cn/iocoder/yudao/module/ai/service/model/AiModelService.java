package cn.iocoder.yudao.module.ai.service.model;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.ai.controller.admin.model.vo.ModelPageReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.model.vo.ModelSaveReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.model.AiApiKeyDO;
import cn.iocoder.yudao.module.ai.dal.dataobject.model.AiModelDO;
import cn.iocoder.yudao.module.ai.enums.AiModelTypeEnum;

import java.util.List;

/**
 * AI 模型 Service 接口
 *
 * @author 芋道源码
 */
public interface AiModelService {

    Long createModel(ModelSaveReqVO createReqVO);

    void updateModel(ModelSaveReqVO updateReqVO);

    void deleteModel(Long id);

    AiModelDO getModel(Long id);

    PageResult<AiModelDO> getModelPage(ModelPageReqVO pageReqVO);

    /**
     * 获得指定类型、开启状态的模型列表
     */
    List<AiModelDO> getModelListByTypeAndStatus(Integer type, Integer status);

    /**
     * 校验模型存在且开启，并校验其绑定的 API 密钥可用，返回模型信息
     */
    AiModelDO validateChatModel(Long id);

    /**
     * 校验向量模型存在且开启，并校验其绑定的 API 密钥可用，返回模型信息
     */
    AiModelDO validateEmbeddingModel(Long id);

}
