package cn.iocoder.yudao.module.ai.service.medical;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.ai.controller.admin.medical.vo.drug.DrugPageReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.medical.vo.drug.DrugSaveReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.medical.AiMedicalDrugDO;

import java.util.List;

/**
 * 医疗药品 Service 接口
 *
 * @author 芋道源码
 */
public interface AiMedicalDrugService {

    Long createDrug(DrugSaveReqVO createReqVO);

    void updateDrug(DrugSaveReqVO updateReqVO);

    void deleteDrug(Long id);

    AiMedicalDrugDO getDrug(Long id);

    PageResult<AiMedicalDrugDO> getDrugPage(DrugPageReqVO pageReqVO);

    /**
     * 按关键词模糊搜索开启状态的药品（名称、分类、适应症），用于用药咨询工具
     */
    List<AiMedicalDrugDO> searchDrugList(String keyword, int limit);

    /**
     * 按名称模糊匹配单个开启状态的药品，用于查询药品详情
     */
    AiMedicalDrugDO getDrugByName(String name);

}
