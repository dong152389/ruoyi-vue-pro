package cn.iocoder.yudao.module.ai.dal.mysql.knowledge;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.ai.controller.admin.knowledge.vo.KnowledgePageReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.knowledge.AiKnowledgeDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * AI 医疗知识库 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface AiKnowledgeMapper extends BaseMapperX<AiKnowledgeDO> {

    default PageResult<AiKnowledgeDO> selectPage(KnowledgePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AiKnowledgeDO>()
                .likeIfPresent(AiKnowledgeDO::getName, reqVO.getName())
                .orderByDesc(AiKnowledgeDO::getId));
    }

}
