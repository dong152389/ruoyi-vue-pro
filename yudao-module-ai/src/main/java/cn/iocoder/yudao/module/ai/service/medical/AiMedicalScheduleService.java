package cn.iocoder.yudao.module.ai.service.medical;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.ai.controller.admin.medical.vo.schedule.SchedulePageReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.medical.vo.schedule.ScheduleSaveReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.medical.AiMedicalScheduleDO;

import java.time.LocalDate;
import java.util.List;

/**
 * 医生排班 Service 接口
 *
 * @author 芋道源码
 */
public interface AiMedicalScheduleService {

    Long createSchedule(ScheduleSaveReqVO createReqVO);

    void updateSchedule(ScheduleSaveReqVO updateReqVO);

    void deleteSchedule(Long id);

    AiMedicalScheduleDO getSchedule(Long id);

    PageResult<AiMedicalScheduleDO> getSchedulePage(SchedulePageReqVO pageReqVO);

    /**
     * 查询指定科室（可空）在日期区间内、有余号且未停诊的排班，用于预约挂号工具
     */
    List<AiMedicalScheduleDO> getAvailableScheduleList(List<Long> departmentIds, LocalDate beginDate, LocalDate endDate);

    /**
     * 预约挂号：扣减号源并创建预约记录（事务），供工具与管理端共用
     *
     * @param userId 预约用户
     * @param scheduleId 排班编号
     * @param patientName 患者姓名
     * @param patientPhone 患者手机号
     * @param remark 备注
     * @return 预约编号
     */
    Long createAppointment(Long userId, Long scheduleId, String patientName, String patientPhone, String remark);

}
