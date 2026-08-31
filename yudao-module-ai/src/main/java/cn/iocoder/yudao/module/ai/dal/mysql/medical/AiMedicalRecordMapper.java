package cn.iocoder.yudao.module.ai.dal.mysql.medical;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.ai.controller.admin.medical.vo.record.RecordPageReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.medical.AiMedicalRecordDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 预问诊病历 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface AiMedicalRecordMapper extends BaseMapperX<AiMedicalRecordDO> {

    default PageResult<AiMedicalRecordDO> selectPage(RecordPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AiMedicalRecordDO>()
                .eqIfPresent(AiMedicalRecordDO::getUserId, reqVO.getUserId())
                .likeIfPresent(AiMedicalRecordDO::getChiefComplaint, reqVO.getChiefComplaint())
                .orderByDesc(AiMedicalRecordDO::getId));
    }

    default List<AiMedicalRecordDO> selectListByUserId(Long userId) {
        return selectList(new LambdaQueryWrapperX<AiMedicalRecordDO>()
                .eq(AiMedicalRecordDO::getUserId, userId)
                .orderByDesc(AiMedicalRecordDO::getId));
    }

}
