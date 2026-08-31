package cn.iocoder.yudao.module.ai.controller.admin.medical;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.controller.admin.medical.vo.department.DepartmentPageReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.medical.vo.department.DepartmentRespVO;
import cn.iocoder.yudao.module.ai.controller.admin.medical.vo.department.DepartmentSaveReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.medical.AiMedicalDepartmentDO;
import cn.iocoder.yudao.module.ai.service.medical.AiMedicalDepartmentService;
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

@Tag(name = "管理后台 - 医疗科室")
@RestController
@RequestMapping("/ai/medical/department")
@Validated
public class AiMedicalDepartmentController {

    @Resource
    private AiMedicalDepartmentService departmentService;

    @PostMapping("/create")
    @Operation(summary = "创建科室")
    @PreAuthorize("@ss.hasPermission('ai:medical-department:create')")
    public CommonResult<Long> createDepartment(@Valid @RequestBody DepartmentSaveReqVO createReqVO) {
        return success(departmentService.createDepartment(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "修改科室")
    @PreAuthorize("@ss.hasPermission('ai:medical-department:update')")
    public CommonResult<Boolean> updateDepartment(@Valid @RequestBody DepartmentSaveReqVO updateReqVO) {
        departmentService.updateDepartment(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除科室")
    @Parameter(name = "id", description = "科室编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('ai:medical-department:delete')")
    public CommonResult<Boolean> deleteDepartment(@RequestParam("id") Long id) {
        departmentService.deleteDepartment(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得科室")
    @Parameter(name = "id", description = "科室编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('ai:medical-department:query')")
    public CommonResult<DepartmentRespVO> getDepartment(@RequestParam("id") Long id) {
        return success(BeanUtils.toBean(departmentService.getDepartment(id), DepartmentRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得科室分页")
    @PreAuthorize("@ss.hasPermission('ai:medical-department:query')")
    public CommonResult<PageResult<DepartmentRespVO>> getDepartmentPage(@Validated DepartmentPageReqVO pageReqVO) {
        PageResult<AiMedicalDepartmentDO> pageResult = departmentService.getDepartmentPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, DepartmentRespVO.class));
    }

    @GetMapping("/simple-list")
    @Operation(summary = "获得开启状态的科室列表", description = "用于前端下拉选项")
    public CommonResult<List<DepartmentRespVO>> getSimpleDepartmentList() {
        List<AiMedicalDepartmentDO> list = departmentService.getEnabledDepartmentList();
        return success(BeanUtils.toBean(list, DepartmentRespVO.class));
    }

}
