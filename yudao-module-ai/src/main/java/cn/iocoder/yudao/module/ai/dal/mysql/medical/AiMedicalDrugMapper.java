package cn.iocoder.yudao.module.ai.dal.mysql.medical;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.ai.controller.admin.medical.vo.drug.DrugPageReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.medical.AiMedicalDrugDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 医疗药品 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface AiMedicalDrugMapper extends BaseMapperX<AiMedicalDrugDO> {

    default PageResult<AiMedicalDrugDO> selectPage(DrugPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AiMedicalDrugDO>()
                .likeIfPresent(AiMedicalDrugDO::getName, reqVO.getName())
                .likeIfPresent(AiMedicalDrugDO::getCategory, reqVO.getCategory())
                .eqIfPresent(AiMedicalDrugDO::getStatus, reqVO.getStatus())
                .orderByAsc(AiMedicalDrugDO::getId));
    }

    /**
     * 按关键词模糊搜索药品（名称、分类、适应症）
     */
    default List<AiMedicalDrugDO> selectListByKeyword(String keyword, int limit) {
        return selectList(new LambdaQueryWrapperX<AiMedicalDrugDO>()
                .eq(AiMedicalDrugDO::getStatus, 0)
                .and(w -> w.like(AiMedicalDrugDO::getName, keyword)
                        .or().like(AiMedicalDrugDO::getCategory, keyword)
                        .or().like(AiMedicalDrugDO::getIndications, keyword))
                .last("LIMIT " + limit));
    }

    default AiMedicalDrugDO selectByName(String name) {
        return selectOne(new LambdaQueryWrapperX<AiMedicalDrugDO>()
                .eq(AiMedicalDrugDO::getStatus, 0)
                .like(AiMedicalDrugDO::getName, name)
                .last("LIMIT 1"));
    }

}
