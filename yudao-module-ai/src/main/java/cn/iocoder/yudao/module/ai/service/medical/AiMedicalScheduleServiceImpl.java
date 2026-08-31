package cn.iocoder.yudao.module.ai.service.medical;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.controller.admin.medical.vo.schedule.SchedulePageReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.medical.vo.schedule.ScheduleSaveReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.medical.AiMedicalAppointmentDO;
import cn.iocoder.yudao.module.ai.dal.dataobject.medical.AiMedicalDepartmentDO;
import cn.iocoder.yudao.module.ai.dal.dataobject.medical.AiMedicalScheduleDO;
import cn.iocoder.yudao.module.ai.dal.mysql.medical.AiMedicalAppointmentMapper;
import cn.iocoder.yudao.module.ai.dal.mysql.medical.AiMedicalScheduleMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.ai.enums.ErrorCodeConstants.SCHEDULE_NOT_EXISTS;
import static cn.iocoder.yudao.module.ai.enums.ErrorCodeConstants.SCHEDULE_NO_SLOTS;

/**
 * 医生排班 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class AiMedicalScheduleServiceImpl implements AiMedicalScheduleService {

    @Resource
    private AiMedicalScheduleMapper scheduleMapper;

    @Resource
    private AiMedicalAppointmentMapper appointmentMapper;

    @Resource
    private AiMedicalDepartmentService departmentService;

    @Override
    public Long createSchedule(ScheduleSaveReqVO createReqVO) {
        AiMedicalScheduleDO schedule = BeanUtils.toBean(createReqVO, AiMedicalScheduleDO.class);
        if (schedule.getRemainingSlots() == null) {
            schedule.setRemainingSlots(schedule.getTotalSlots());
        }
        scheduleMapper.insert(schedule);
        return schedule.getId();
    }

    @Override
    public void updateSchedule(ScheduleSaveReqVO updateReqVO) {
        validateScheduleExists(updateReqVO.getId());
        AiMedicalScheduleDO updateObj = BeanUtils.toBean(updateReqVO, AiMedicalScheduleDO.class);
        scheduleMapper.updateById(updateObj);
    }

    @Override
    public void deleteSchedule(Long id) {
        validateScheduleExists(id);
        scheduleMapper.deleteById(id);
    }

    private void validateScheduleExists(Long id) {
        if (id == null) {
            return;
        }
        if (scheduleMapper.selectById(id) == null) {
            throw exception(SCHEDULE_NOT_EXISTS);
        }
    }

    @Override
    public AiMedicalScheduleDO getSchedule(Long id) {
        return scheduleMapper.selectById(id);
    }

    @Override
    public PageResult<AiMedicalScheduleDO> getSchedulePage(SchedulePageReqVO pageReqVO) {
        return scheduleMapper.selectPage(pageReqVO);
    }

    @Override
    public List<AiMedicalScheduleDO> getAvailableScheduleList(List<Long> departmentIds,
                                                              LocalDate beginDate, LocalDate endDate) {
        LocalDate begin = beginDate != null ? beginDate : LocalDate.now();
        LocalDate end = endDate != null ? endDate : begin.plusDays(7);
        return scheduleMapper.selectAvailableList(departmentIds, begin, end);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createAppointment(Long userId, Long scheduleId, String patientName, String patientPhone, String remark) {
        // 1. 校验排班存在，并通过带条件的 UPDATE 原子扣减号源（余号不足或停诊时影响行数为 0）
        AiMedicalScheduleDO schedule = scheduleMapper.selectById(scheduleId);
        if (schedule == null) {
            throw exception(SCHEDULE_NOT_EXISTS);
        }
        int updatedRows = scheduleMapper.decreaseRemainingSlots(scheduleId);
        if (updatedRows == 0) {
            throw exception(SCHEDULE_NO_SLOTS);
        }
        // 2. 创建预约记录
        AiMedicalAppointmentDO appointment = new AiMedicalAppointmentDO();
        appointment.setUserId(userId);
        appointment.setPatientName(patientName);
        appointment.setPatientPhone(patientPhone);
        appointment.setScheduleId(scheduleId);
        appointment.setDepartmentId(schedule.getDepartmentId());
        AiMedicalDepartmentDO department = departmentService.getDepartment(schedule.getDepartmentId());
        if (department != null) {
            appointment.setDepartmentName(department.getName());
        }
        appointment.setDoctorName(schedule.getDoctorName());
        appointment.setAppointmentDate(schedule.getScheduleDate());
        appointment.setTimeSlot(schedule.getTimeSlot());
        appointment.setStatus(AiMedicalAppointmentDO.STATUS_WAITING);
        appointment.setRemark(remark);
        appointmentMapper.insert(appointment);
        return appointment.getId();
    }

}
