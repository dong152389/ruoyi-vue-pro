package cn.iocoder.yudao.module.ai.service.medical;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.ai.controller.admin.medical.vo.appointment.AppointmentPageReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.medical.vo.appointment.AppointmentSaveReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.medical.AiMedicalAppointmentDO;

import java.util.List;

/**
 * 门诊预约 Service 接口
 *
 * @author 芋道源码
 */
public interface AiMedicalAppointmentService {

    /**
     * 管理端修改患者信息与备注
     */
    void updateAppointment(AppointmentSaveReqVO updateReqVO);

    /**
     * 取消预约：仅待就诊状态可取消，并归还号源
     */
    void cancelAppointment(Long id);

    void deleteAppointment(Long id);

    AiMedicalAppointmentDO getAppointment(Long id);

    PageResult<AiMedicalAppointmentDO> getAppointmentPage(AppointmentPageReqVO pageReqVO);

    /**
     * 获得指定用户的预约列表
     */
    List<AiMedicalAppointmentDO> getAppointmentListByUserId(Long userId);

}
