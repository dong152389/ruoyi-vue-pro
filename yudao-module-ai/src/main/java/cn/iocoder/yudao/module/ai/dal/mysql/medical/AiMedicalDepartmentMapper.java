package cn.iocoder.yudao.module.ai.dal.mysql.medical;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.ai.controller.admin.medical.vo.department.DepartmentPageReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.medical.AiMedicalDepartmentDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 医疗科室 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface AiMedicalDepartmentMapper extends BaseMapperX<AiMedicalDepartmentDO> {

    default PageResult<AiMedicalDepartmentDO> selectPage(DepartmentPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AiMedicalDepartmentDO>()
                .likeIfPresent(AiMedicalDepartmentDO::getName, reqVO.getName())
                .eqIfPresent(AiMedicalDepartmentDO::getStatus, reqVO.getStatus())
                .orderByAsc(AiMedicalDepartmentDO::getSort));
    }

    default List<AiMedicalDepartmentDO> selectListByStatus(Integer status) {
        return selectList(new LambdaQueryWrapperX<AiMedicalDepartmentDO>()
                .eqIfPresent(AiMedicalDepartmentDO::getStatus, status)
                .orderByAsc(AiMedicalDepartmentDO::getSort));
    }

}
