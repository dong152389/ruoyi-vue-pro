package cn.iocoder.yudao.module.ai.controller.admin.model;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.controller.admin.model.vo.ApiKeyPageReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.model.vo.ApiKeyRespVO;
import cn.iocoder.yudao.module.ai.controller.admin.model.vo.ApiKeySaveReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.model.AiApiKeyDO;
import cn.iocoder.yudao.module.ai.service.model.AiApiKeyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.common.enums.CommonStatusEnum.ENABLE;

@Tag(name = "管理后台 - AI API 密钥")
@RestController
@RequestMapping("/ai/api-key")
@Validated
public class AiApiKeyController {

    @Resource
    private AiApiKeyService apiKeyService;

    @PostMapping("/create")
    @Operation(summary = "创建 API 密钥")
    @PreAuthorize("@ss.hasPermission('ai:api-key:create')")
    public CommonResult<Long> createApiKey(@Valid @RequestBody ApiKeySaveReqVO createReqVO) {
        return success(apiKeyService.createApiKey(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "修改 API 密钥")
    @PreAuthorize("@ss.hasPermission('ai:api-key:update')")
    public CommonResult<Boolean> updateApiKey(@Valid @RequestBody ApiKeySaveReqVO updateReqVO) {
        apiKeyService.updateApiKey(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除 API 密钥")
    @Parameter(name = "id", description = "密钥编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('ai:api-key:delete')")
    public CommonResult<Boolean> deleteApiKey(@RequestParam("id") Long id) {
        apiKeyService.deleteApiKey(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得 API 密钥")
    @Parameter(name = "id", description = "密钥编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('ai:api-key:query')")
    public CommonResult<ApiKeyRespVO> getApiKey(@RequestParam("id") Long id) {
        return success(BeanUtils.toBean(apiKeyService.getApiKey(id), ApiKeyRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得 API 密钥分页")
    @PreAuthorize("@ss.hasPermission('ai:api-key:query')")
    public CommonResult<PageResult<ApiKeyRespVO>> getApiKeyPage(@Validated ApiKeyPageReqVO pageReqVO) {
        PageResult<AiApiKeyDO> pageResult = apiKeyService.getApiKeyPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ApiKeyRespVO.class));
    }

    @GetMapping("/simple-list")
    @Operation(summary = "获得开启状态的 API 密钥精简列表", description = "用于前端下拉选项")
    public CommonResult<List<ApiKeyRespVO>> getSimpleApiKeyList() {
        List<AiApiKeyDO> list = apiKeyService.getApiKeyListByStatus(ENABLE.getStatus());
        return success(BeanUtils.toBean(list, ApiKeyRespVO.class));
    }

}
