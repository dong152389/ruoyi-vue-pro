package cn.iocoder.yudao.module.ai.dal.mysql.knowledge;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.ai.controller.admin.knowledge.vo.SegmentPageReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.knowledge.AiKnowledgeSegmentDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

/**
 * AI 知识库切片 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface AiKnowledgeSegmentMapper extends BaseMapperX<AiKnowledgeSegmentDO> {

    default List<AiKnowledgeSegmentDO> selectListByKnowledgeIds(Collection<Long> knowledgeIds) {
        return selectList(new LambdaQueryWrapperX<AiKnowledgeSegmentDO>()
                .in(AiKnowledgeSegmentDO::getKnowledgeId, knowledgeIds)
                .isNotNull(AiKnowledgeSegmentDO::getVector));
    }

    default List<AiKnowledgeSegmentDO> selectListByDocumentId(Long documentId) {
        return selectList(new LambdaQueryWrapperX<AiKnowledgeSegmentDO>()
                .eq(AiKnowledgeSegmentDO::getDocumentId, documentId)
                .orderByAsc(AiKnowledgeSegmentDO::getId));
    }

    default PageResult<AiKnowledgeSegmentDO> selectPage(SegmentPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AiKnowledgeSegmentDO>()
                .eqIfPresent(AiKnowledgeSegmentDO::getDocumentId, reqVO.getDocumentId())
                .eqIfPresent(AiKnowledgeSegmentDO::getKnowledgeId, reqVO.getKnowledgeId())
                .orderByAsc(AiKnowledgeSegmentDO::getId));
    }

}
