package cn.iocoder.yudao.module.ai.service.model;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.ai.controller.admin.model.vo.ApiKeyPageReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.model.vo.ApiKeySaveReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.model.AiApiKeyDO;

import java.util.List;

/**
 * AI API 密钥 Service 接口
 *
 * @author 芋道源码
 */
public interface AiApiKeyService {

    Long createApiKey(ApiKeySaveReqVO createReqVO);

    void updateApiKey(ApiKeySaveReqVO updateReqVO);

    void deleteApiKey(Long id);

    AiApiKeyDO getApiKey(Long id);

    PageResult<AiApiKeyDO> getApiKeyPage(ApiKeyPageReqVO pageReqVO);

    /**
     * 获得开启状态的 API 密钥列表
     */
    List<AiApiKeyDO> getApiKeyListByStatus(Integer status);

    /**
     * 校验 API 密钥是否存在且开启，返回密钥信息
     */
    AiApiKeyDO validateApiKey(Long id);

}
