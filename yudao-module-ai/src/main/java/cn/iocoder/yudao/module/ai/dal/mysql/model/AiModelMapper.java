package cn.iocoder.yudao.module.ai.dal.mysql.model;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.ai.controller.admin.model.vo.ModelPageReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.model.AiModelDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * AI 模型 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface AiModelMapper extends BaseMapperX<AiModelDO> {

    default PageResult<AiModelDO> selectPage(ModelPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AiModelDO>()
                .likeIfPresent(AiModelDO::getName, reqVO.getName())
                .eqIfPresent(AiModelDO::getKeyId, reqVO.getKeyId())
                .eqIfPresent(AiModelDO::getType, reqVO.getType())
                .eqIfPresent(AiModelDO::getStatus, reqVO.getStatus())
                .orderByDesc(AiModelDO::getId));
    }

    default List<AiModelDO> selectListByTypeAndStatus(Integer type, Integer status) {
        return selectList(new LambdaQueryWrapperX<AiModelDO>()
                .eqIfPresent(AiModelDO::getType, type)
                .eqIfPresent(AiModelDO::getStatus, status)
                .orderByAsc(AiModelDO::getId));
    }

}
