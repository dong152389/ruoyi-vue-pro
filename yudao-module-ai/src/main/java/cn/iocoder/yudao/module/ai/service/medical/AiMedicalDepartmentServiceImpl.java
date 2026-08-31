package cn.iocoder.yudao.module.ai.service.medical;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.controller.admin.medical.vo.department.DepartmentPageReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.medical.vo.department.DepartmentSaveReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.medical.AiMedicalDepartmentDO;
import cn.iocoder.yudao.module.ai.dal.mysql.medical.AiMedicalDepartmentMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.ai.enums.ErrorCodeConstants.DEPARTMENT_NOT_EXISTS;

/**
 * 医疗科室 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class AiMedicalDepartmentServiceImpl implements AiMedicalDepartmentService {

    @Resource
    private AiMedicalDepartmentMapper departmentMapper;

    @Override
    public Long createDepartment(DepartmentSaveReqVO createReqVO) {
        AiMedicalDepartmentDO department = BeanUtils.toBean(createReqVO, AiMedicalDepartmentDO.class);
        departmentMapper.insert(department);
        return department.getId();
    }

    @Override
    public void updateDepartment(DepartmentSaveReqVO updateReqVO) {
        validateDepartmentExists(updateReqVO.getId());
        AiMedicalDepartmentDO updateObj = BeanUtils.toBean(updateReqVO, AiMedicalDepartmentDO.class);
        departmentMapper.updateById(updateObj);
    }

    @Override
    public void deleteDepartment(Long id) {
        validateDepartmentExists(id);
        departmentMapper.deleteById(id);
    }

    private void validateDepartmentExists(Long id) {
        if (id == null) {
            return;
        }
        if (departmentMapper.selectById(id) == null) {
            throw exception(DEPARTMENT_NOT_EXISTS);
        }
    }

    @Override
    public AiMedicalDepartmentDO getDepartment(Long id) {
        return departmentMapper.selectById(id);
    }

    @Override
    public PageResult<AiMedicalDepartmentDO> getDepartmentPage(DepartmentPageReqVO pageReqVO) {
        return departmentMapper.selectPage(pageReqVO);
    }

    @Override
    public List<AiMedicalDepartmentDO> getEnabledDepartmentList() {
        return departmentMapper.selectListByStatus(CommonStatusEnum.ENABLE.getStatus());
    }

    @Override
    public AiMedicalDepartmentDO getEnabledDepartmentByName(String name) {
        List<AiMedicalDepartmentDO> list = departmentMapper.selectListByStatus(CommonStatusEnum.ENABLE.getStatus());
        return list.stream()
                .filter(dept -> dept.getName().equals(name) || dept.getName().contains(name))
                .findFirst().orElse(null);
    }

}
