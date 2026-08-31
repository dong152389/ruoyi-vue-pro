package cn.iocoder.yudao.module.ai.controller.admin.medical;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.controller.admin.medical.vo.schedule.SchedulePageReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.medical.vo.schedule.ScheduleRespVO;
import cn.iocoder.yudao.module.ai.controller.admin.medical.vo.schedule.ScheduleSaveReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.medical.AiMedicalScheduleDO;
import cn.iocoder.yudao.module.ai.service.medical.AiMedicalScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 医生排班")
@RestController
@RequestMapping("/ai/medical/schedule")
@Validated
public class AiMedicalScheduleController {

    @Resource
    private AiMedicalScheduleService scheduleService;

    @PostMapping("/create")
    @Operation(summary = "创建排班")
    @PreAuthorize("@ss.hasPermission('ai:medical-schedule:create')")
    public CommonResult<Long> createSchedule(@Valid @RequestBody ScheduleSaveReqVO createReqVO) {
        return success(scheduleService.createSchedule(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "修改排班")
    @PreAuthorize("@ss.hasPermission('ai:medical-schedule:update')")
    public CommonResult<Boolean> updateSchedule(@Valid @RequestBody ScheduleSaveReqVO updateReqVO) {
        scheduleService.updateSchedule(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除排班")
    @Parameter(name = "id", description = "排班编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('ai:medical-schedule:delete')")
    public CommonResult<Boolean> deleteSchedule(@RequestParam("id") Long id) {
        scheduleService.deleteSchedule(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得排班")
    @Parameter(name = "id", description = "排班编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('ai:medical-schedule:query')")
    public CommonResult<ScheduleRespVO> getSchedule(@RequestParam("id") Long id) {
        ScheduleRespVO respVO = BeanUtils.toBean(scheduleService.getSchedule(id), ScheduleRespVO.class);
        return success(respVO);
    }

    @GetMapping("/page")
    @Operation(summary = "获得排班分页")
    @PreAuthorize("@ss.hasPermission('ai:medical-schedule:query')")
    public CommonResult<PageResult<ScheduleRespVO>> getSchedulePage(@Validated SchedulePageReqVO pageReqVO) {
        PageResult<AiMedicalScheduleDO> pageResult = scheduleService.getSchedulePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ScheduleRespVO.class));
    }

}
