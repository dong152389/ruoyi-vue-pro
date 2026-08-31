package cn.iocoder.yudao.module.ai.service.medical;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.controller.admin.medical.vo.drug.DrugPageReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.medical.vo.drug.DrugSaveReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.medical.AiMedicalDrugDO;
import cn.iocoder.yudao.module.ai.dal.mysql.medical.AiMedicalDrugMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.ai.enums.ErrorCodeConstants.DRUG_NOT_EXISTS;

/**
 * 医疗药品 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class AiMedicalDrugServiceImpl implements AiMedicalDrugService {

    @Resource
    private AiMedicalDrugMapper drugMapper;

    @Override
    public Long createDrug(DrugSaveReqVO createReqVO) {
        AiMedicalDrugDO drug = BeanUtils.toBean(createReqVO, AiMedicalDrugDO.class);
        drugMapper.insert(drug);
        return drug.getId();
    }

    @Override
    public void updateDrug(DrugSaveReqVO updateReqVO) {
        validateDrugExists(updateReqVO.getId());
        AiMedicalDrugDO updateObj = BeanUtils.toBean(updateReqVO, AiMedicalDrugDO.class);
        drugMapper.updateById(updateObj);
    }

    @Override
    public void deleteDrug(Long id) {
        validateDrugExists(id);
        drugMapper.deleteById(id);
    }

    private void validateDrugExists(Long id) {
        if (id == null) {
            return;
        }
        if (drugMapper.selectById(id) == null) {
            throw exception(DRUG_NOT_EXISTS);
        }
    }

    @Override
    public AiMedicalDrugDO getDrug(Long id) {
        return drugMapper.selectById(id);
    }

    @Override
    public PageResult<AiMedicalDrugDO> getDrugPage(DrugPageReqVO pageReqVO) {
        return drugMapper.selectPage(pageReqVO);
    }

    @Override
    public List<AiMedicalDrugDO> searchDrugList(String keyword, int limit) {
        return drugMapper.selectListByKeyword(keyword, limit);
    }

    @Override
    public AiMedicalDrugDO getDrugByName(String name) {
        return drugMapper.selectByName(name);
    }

}
