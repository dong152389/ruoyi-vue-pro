package cn.iocoder.yudao.module.ai.controller.admin.medical;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.controller.admin.medical.vo.appointment.AppointmentPageReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.medical.vo.appointment.AppointmentRespVO;
import cn.iocoder.yudao.module.ai.controller.admin.medical.vo.appointment.AppointmentSaveReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.medical.AiMedicalAppointmentDO;
import cn.iocoder.yudao.module.ai.service.medical.AiMedicalAppointmentService;
import cn.iocoder.yudao.module.ai.service.medical.AiMedicalScheduleService;
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

@Tag(name = "管理后台 - 门诊预约")
@RestController
@RequestMapping("/ai/medical/appointment")
@Validated
public class AiMedicalAppointmentController {

    @Resource
    private AiMedicalAppointmentService appointmentService;

    @Resource
    private AiMedicalScheduleService scheduleService;

    @PostMapping("/create")
    @Operation(summary = "创建预约", description = "管理端直接代患者挂号")
    @PreAuthorize("@ss.hasPermission('ai:medical-appointment:create')")
    public CommonResult<Long> createAppointment(@Valid @RequestBody AppointmentSaveReqVO createReqVO) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        Long appointmentId = scheduleService.createAppointment(userId, createReqVO.getScheduleId(),
                createReqVO.getPatientName(), createReqVO.getPatientPhone(), createReqVO.getRemark());
        return success(appointmentId);
    }

    @PutMapping("/update")
    @Operation(summary = "修改预约的患者信息与备注")
    @PreAuthorize("@ss.hasPermission('ai:medical-appointment:update')")
    public CommonResult<Boolean> updateAppointment(@Valid @RequestBody AppointmentSaveReqVO updateReqVO) {
        appointmentService.updateAppointment(updateReqVO);
        return success(true);
    }

    @PutMapping("/cancel")
    @Operation(summary = "取消预约", description = "仅待就诊状态可取消，并归还号源")
    @Parameter(name = "id", description = "预约编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('ai:medical-appointment:update')")
    public CommonResult<Boolean> cancelAppointment(@RequestParam("id") Long id) {
        appointmentService.cancelAppointment(id);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除预约")
    @Parameter(name = "id", description = "预约编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('ai:medical-appointment:delete')")
    public CommonResult<Boolean> deleteAppointment(@RequestParam("id") Long id) {
        appointmentService.deleteAppointment(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得预约")
    @Parameter(name = "id", description = "预约编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('ai:medical-appointment:query')")
    public CommonResult<AppointmentRespVO> getAppointment(@RequestParam("id") Long id) {
        return success(BeanUtils.toBean(appointmentService.getAppointment(id), AppointmentRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得预约分页")
    @PreAuthorize("@ss.hasPermission('ai:medical-appointment:query')")
    public CommonResult<PageResult<AppointmentRespVO>> getAppointmentPage(@Validated AppointmentPageReqVO pageReqVO) {
        PageResult<AiMedicalAppointmentDO> pageResult = appointmentService.getAppointmentPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, AppointmentRespVO.class));
    }

    @GetMapping("/my-list")
    @Operation(summary = "获得我的预约列表", description = "供对话页右侧面板使用")
    public CommonResult<List<AppointmentRespVO>> getMyAppointmentList() {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        List<AiMedicalAppointmentDO> list = appointmentService.getAppointmentListByUserId(userId);
        return success(BeanUtils.toBean(list, AppointmentRespVO.class));
    }

}
