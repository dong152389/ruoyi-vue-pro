package cn.iocoder.yudao.module.ai.controller.admin.medical;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.ai.controller.admin.medical.vo.record.RecordPageReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.medical.vo.record.RecordRespVO;
import cn.iocoder.yudao.module.ai.controller.admin.medical.vo.record.RecordSaveReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.medical.AiMedicalRecordDO;
import cn.iocoder.yudao.module.ai.service.medical.AiMedicalRecordService;
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

@Tag(name = "管理后台 - 预问诊病历")
@RestController
@RequestMapping("/ai/medical/record")
@Validated
public class AiMedicalRecordController {

    @Resource
    private AiMedicalRecordService recordService;

    @PutMapping("/update")
    @Operation(summary = "修改预问诊病历")
    @PreAuthorize("@ss.hasPermission('ai:medical-record:update')")
    public CommonResult<Boolean> updateRecord(@Valid @RequestBody RecordSaveReqVO updateReqVO) {
        recordService.updateRecord(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除预问诊病历")
    @Parameter(name = "id", description = "病历编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('ai:medical-record:delete')")
    public CommonResult<Boolean> deleteRecord(@RequestParam("id") Long id) {
        recordService.deleteRecord(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得预问诊病历")
    @Parameter(name = "id", description = "病历编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('ai:medical-record:query')")
    public CommonResult<RecordRespVO> getRecord(@RequestParam("id") Long id) {
        return success(BeanUtils.toBean(recordService.getRecord(id), RecordRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得预问诊病历分页")
    @PreAuthorize("@ss.hasPermission('ai:medical-record:query')")
    public CommonResult<PageResult<RecordRespVO>> getRecordPage(@Validated RecordPageReqVO pageReqVO) {
        PageResult<AiMedicalRecordDO> pageResult = recordService.getRecordPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, RecordRespVO.class));
    }

    @GetMapping("/my-list")
    @Operation(summary = "获得我的预问诊病历列表")
    public CommonResult<List<RecordRespVO>> getMyRecordList() {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        List<AiMedicalRecordDO> list = recordService.getRecordListByUserId(userId);
        return success(BeanUtils.toBean(list, RecordRespVO.class));
    }

}
