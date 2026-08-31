package cn.iocoder.yudao.module.ai.controller.admin.medical;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.controller.admin.medical.vo.drug.DrugPageReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.medical.vo.drug.DrugRespVO;
import cn.iocoder.yudao.module.ai.controller.admin.medical.vo.drug.DrugSaveReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.medical.AiMedicalDrugDO;
import cn.iocoder.yudao.module.ai.service.medical.AiMedicalDrugService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 医疗药品")
@RestController
@RequestMapping("/ai/medical/drug")
@Validated
public class AiMedicalDrugController {

    @Resource
    private AiMedicalDrugService drugService;

    @PostMapping("/create")
    @Operation(summary = "创建药品")
    @PreAuthorize("@ss.hasPermission('ai:medical-drug:create')")
    public CommonResult<Long> createDrug(@Valid @RequestBody DrugSaveReqVO createReqVO) {
        return success(drugService.createDrug(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "修改药品")
    @PreAuthorize("@ss.hasPermission('ai:medical-drug:update')")
    public CommonResult<Boolean> updateDrug(@Valid @RequestBody DrugSaveReqVO updateReqVO) {
        drugService.updateDrug(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除药品")
    @Parameter(name = "id", description = "药品编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('ai:medical-drug:delete')")
    public CommonResult<Boolean> deleteDrug(@RequestParam("id") Long id) {
        drugService.deleteDrug(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得药品")
    @Parameter(name = "id", description = "药品编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('ai:medical-drug:query')")
    public CommonResult<DrugRespVO> getDrug(@RequestParam("id") Long id) {
        return success(BeanUtils.toBean(drugService.getDrug(id), DrugRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得药品分页")
    @PreAuthorize("@ss.hasPermission('ai:medical-drug:query')")
    public CommonResult<PageResult<DrugRespVO>> getDrugPage(@Validated DrugPageReqVO pageReqVO) {
        PageResult<AiMedicalDrugDO> pageResult = drugService.getDrugPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, DrugRespVO.class));
    }

}
