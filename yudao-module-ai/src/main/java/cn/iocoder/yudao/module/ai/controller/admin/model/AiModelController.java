package cn.iocoder.yudao.module.ai.controller.admin.model;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.controller.admin.model.vo.ModelPageReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.model.vo.ModelRespVO;
import cn.iocoder.yudao.module.ai.controller.admin.model.vo.ModelSaveReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.model.vo.ModelSimpleRespVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.model.AiApiKeyDO;
import cn.iocoder.yudao.module.ai.dal.dataobject.model.AiModelDO;
import cn.iocoder.yudao.module.ai.service.model.AiApiKeyService;
import cn.iocoder.yudao.module.ai.service.model.AiModelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - AI 模型")
@RestController
@RequestMapping("/ai/model")
@Validated
public class AiModelController {

    @Resource
    private AiModelService modelService;

    @Resource
    private AiApiKeyService apiKeyService;

    @PostMapping("/create")
    @Operation(summary = "创建模型")
    @PreAuthorize("@ss.hasPermission('ai:model:create')")
    public CommonResult<Long> createModel(@Valid @RequestBody ModelSaveReqVO createReqVO) {
        return success(modelService.createModel(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "修改模型")
    @PreAuthorize("@ss.hasPermission('ai:model:update')")
    public CommonResult<Boolean> updateModel(@Valid @RequestBody ModelSaveReqVO updateReqVO) {
        modelService.updateModel(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除模型")
    @Parameter(name = "id", description = "模型编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('ai:model:delete')")
    public CommonResult<Boolean> deleteModel(@RequestParam("id") Long id) {
        modelService.deleteModel(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得模型")
    @Parameter(name = "id", description = "模型编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('ai:model:query')")
    public CommonResult<ModelRespVO> getModel(@RequestParam("id") Long id) {
        return success(BeanUtils.toBean(modelService.getModel(id), ModelRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得模型分页")
    @PreAuthorize("@ss.hasPermission('ai:model:query')")
    public CommonResult<PageResult<ModelRespVO>> getModelPage(@Validated ModelPageReqVO pageReqVO) {
        PageResult<AiModelDO> pageResult = modelService.getModelPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ModelRespVO.class));
    }

    @GetMapping("/simple-list")
    @Operation(summary = "获得开启状态的模型精简列表", description = "用于前端下拉选择，type 不传时返回全部类型，附带绑定密钥的平台")
    @Parameter(name = "type", description = "模型类型（1对话 2向量）", example = "1")
    public CommonResult<List<ModelSimpleRespVO>> getSimpleModelList(
            @RequestParam(value = "type", required = false) Integer type) {
        List<AiModelDO> list = modelService.getModelListByTypeAndStatus(type, CommonStatusEnum.ENABLE.getStatus());
        // 批量补齐平台：前端据此决定解析哪种官方流式协议（openai / anthropic / gemini）
        Map<Long, AiApiKeyDO> apiKeyMap = apiKeyService.getApiKeyListByStatus(CommonStatusEnum.ENABLE.getStatus())
                .stream().collect(Collectors.toMap(AiApiKeyDO::getId, Function.identity()));
        List<ModelSimpleRespVO> result = BeanUtils.toBean(list, ModelSimpleRespVO.class);
        for (int i = 0; i < result.size(); i++) {
            AiApiKeyDO apiKey = apiKeyMap.get(list.get(i).getKeyId());
            result.get(i).setPlatform(apiKey != null ? apiKey.getPlatform() : null);
        }
        return success(result);
    }

}
