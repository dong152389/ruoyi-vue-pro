package cn.iocoder.yudao.module.ai.dal.mysql.medical;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.ai.controller.admin.medical.vo.schedule.SchedulePageReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.medical.AiMedicalScheduleDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDate;
import java.util.List;

/**
 * 医生排班 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface AiMedicalScheduleMapper extends BaseMapperX<AiMedicalScheduleDO> {

    default PageResult<AiMedicalScheduleDO> selectPage(SchedulePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AiMedicalScheduleDO>()
                .eqIfPresent(AiMedicalScheduleDO::getDepartmentId, reqVO.getDepartmentId())
                .likeIfPresent(AiMedicalScheduleDO::getDoctorName, reqVO.getDoctorName())
                .betweenIfPresent(AiMedicalScheduleDO::getScheduleDate, reqVO.getBeginScheduleDate(), reqVO.getEndScheduleDate())
                .eqIfPresent(AiMedicalScheduleDO::getStatus, reqVO.getStatus())
                .orderByAsc(AiMedicalScheduleDO::getScheduleDate)
                .orderByAsc(AiMedicalScheduleDO::getId));
    }

    /**
     * 查询可预约（正常停诊状态、有余号）的排班
     */
    default List<AiMedicalScheduleDO> selectAvailableList(List<Long> departmentIds, LocalDate beginDate, LocalDate endDate) {
        return selectList(new LambdaQueryWrapperX<AiMedicalScheduleDO>()
                .inIfPresent(AiMedicalScheduleDO::getDepartmentId, departmentIds)
                .ge(AiMedicalScheduleDO::getScheduleDate, beginDate)
                .le(AiMedicalScheduleDO::getScheduleDate, endDate)
                .eq(AiMedicalScheduleDO::getStatus, 0)
                .gt(AiMedicalScheduleDO::getRemainingSlots, 0)
                .orderByAsc(AiMedicalScheduleDO::getScheduleDate)
                .orderByAsc(AiMedicalScheduleDO::getId));
    }

    /**
     * 扣减剩余号源，带余号校验，防止并发超约
     *
     * @return 影响行数，0 表示号源不足或已停诊
     */
    @Update("UPDATE ai_medical_schedule SET remaining_slots = remaining_slots - 1, update_time = NOW() "
            + "WHERE id = #{id} AND deleted = 0 AND status = 0 AND remaining_slots > 0")
    int decreaseRemainingSlots(@Param("id") Long id);

    /**
     * 归还剩余号源（取消预约时使用）
     */
    @Update("UPDATE ai_medical_schedule SET remaining_slots = remaining_slots + 1, update_time = NOW() "
            + "WHERE id = #{id} AND deleted = 0")
    int increaseRemainingSlots(@Param("id") Long id);

}
