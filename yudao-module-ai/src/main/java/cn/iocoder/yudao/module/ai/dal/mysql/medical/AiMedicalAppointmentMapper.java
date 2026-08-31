package cn.iocoder.yudao.module.ai.dal.mysql.medical;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.ai.controller.admin.medical.vo.appointment.AppointmentPageReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.medical.AiMedicalAppointmentDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 门诊预约 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface AiMedicalAppointmentMapper extends BaseMapperX<AiMedicalAppointmentDO> {

    default PageResult<AiMedicalAppointmentDO> selectPage(AppointmentPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AiMedicalAppointmentDO>()
                .eqIfPresent(AiMedicalAppointmentDO::getDepartmentId, reqVO.getDepartmentId())
                .likeIfPresent(AiMedicalAppointmentDO::getPatientName, reqVO.getPatientName())
                .eqIfPresent(AiMedicalAppointmentDO::getStatus, reqVO.getStatus())
                .orderByDesc(AiMedicalAppointmentDO::getId));
    }

    default List<AiMedicalAppointmentDO> selectListByUserId(Long userId) {
        return selectList(new LambdaQueryWrapperX<AiMedicalAppointmentDO>()
                .eq(AiMedicalAppointmentDO::getUserId, userId)
                .orderByDesc(AiMedicalAppointmentDO::getId));
    }

}
