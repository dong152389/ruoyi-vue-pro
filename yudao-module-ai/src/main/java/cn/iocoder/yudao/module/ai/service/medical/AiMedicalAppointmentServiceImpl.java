package cn.iocoder.yudao.module.ai.service.medical;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.controller.admin.medical.vo.appointment.AppointmentPageReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.medical.vo.appointment.AppointmentSaveReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.medical.AiMedicalAppointmentDO;
import cn.iocoder.yudao.module.ai.dal.mysql.medical.AiMedicalAppointmentMapper;
import cn.iocoder.yudao.module.ai.dal.mysql.medical.AiMedicalScheduleMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.ai.enums.ErrorCodeConstants.APPOINTMENT_NOT_EXISTS;
import static cn.iocoder.yudao.module.ai.enums.ErrorCodeConstants.APPOINTMENT_STATUS_INVALID;

/**
 * 门诊预约 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class AiMedicalAppointmentServiceImpl implements AiMedicalAppointmentService {

    @Resource
    private AiMedicalAppointmentMapper appointmentMapper;

    @Resource
    private AiMedicalScheduleMapper scheduleMapper;

    @Override
    public void updateAppointment(AppointmentSaveReqVO updateReqVO) {
        validateAppointmentExists(updateReqVO.getId());
        AiMedicalAppointmentDO updateObj = new AiMedicalAppointmentDO();
        updateObj.setId(updateReqVO.getId());
        updateObj.setPatientName(updateReqVO.getPatientName());
        updateObj.setPatientPhone(updateReqVO.getPatientPhone());
        updateObj.setRemark(updateReqVO.getRemark());
        appointmentMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelAppointment(Long id) {
        AiMedicalAppointmentDO appointment = appointmentMapper.selectById(id);
        if (appointment == null) {
            throw exception(APPOINTMENT_NOT_EXISTS);
        }
        if (!AiMedicalAppointmentDO.STATUS_WAITING.equals(appointment.getStatus())) {
            throw exception(APPOINTMENT_STATUS_INVALID);
        }
        // 更新预约状态，并归还号源
        AiMedicalAppointmentDO updateObj = new AiMedicalAppointmentDO();
        updateObj.setId(id);
        updateObj.setStatus(AiMedicalAppointmentDO.STATUS_CANCELED);
        appointmentMapper.updateById(updateObj);
        scheduleMapper.increaseRemainingSlots(appointment.getScheduleId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAppointment(Long id) {
        AiMedicalAppointmentDO appointment = appointmentMapper.selectById(id);
        if (appointment == null) {
            throw exception(APPOINTMENT_NOT_EXISTS);
        }
        // 待就诊的预约删除时归还号源
        if (AiMedicalAppointmentDO.STATUS_WAITING.equals(appointment.getStatus())) {
            scheduleMapper.increaseRemainingSlots(appointment.getScheduleId());
        }
        appointmentMapper.deleteById(id);
    }

    private void validateAppointmentExists(Long id) {
        if (id == null) {
            return;
        }
        if (appointmentMapper.selectById(id) == null) {
            throw exception(APPOINTMENT_NOT_EXISTS);
        }
    }

    @Override
    public AiMedicalAppointmentDO getAppointment(Long id) {
        return appointmentMapper.selectById(id);
    }

    @Override
    public PageResult<AiMedicalAppointmentDO> getAppointmentPage(AppointmentPageReqVO pageReqVO) {
        return appointmentMapper.selectPage(pageReqVO);
    }

    @Override
    public List<AiMedicalAppointmentDO> getAppointmentListByUserId(Long userId) {
        return appointmentMapper.selectListByUserId(userId);
    }

}
