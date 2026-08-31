package cn.iocoder.yudao.module.ai.service.medical;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.ai.controller.admin.medical.vo.department.DepartmentPageReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.medical.vo.department.DepartmentSaveReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.medical.AiMedicalDepartmentDO;

import java.util.List;

/**
 * 医疗科室 Service 接口
 *
 * @author 芋道源码
 */
public interface AiMedicalDepartmentService {

    Long createDepartment(DepartmentSaveReqVO createReqVO);

    void updateDepartment(DepartmentSaveReqVO updateReqVO);

    void deleteDepartment(Long id);

    AiMedicalDepartmentDO getDepartment(Long id);

    PageResult<AiMedicalDepartmentDO> getDepartmentPage(DepartmentPageReqVO pageReqVO);

    /**
     * 获得开启状态的科室列表（按 sort 排序），用于导诊工具
     */
    List<AiMedicalDepartmentDO> getEnabledDepartmentList();

    /**
     * 根据科室名称精确匹配开启状态的科室（预约工具定位科室用）
     */
    AiMedicalDepartmentDO getEnabledDepartmentByName(String name);

}
