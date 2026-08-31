package cn.iocoder.yudao.module.ai.service.knowledge;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.ai.controller.admin.knowledge.vo.DocumentPageReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.knowledge.vo.KnowledgePageReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.knowledge.vo.KnowledgeSaveReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.knowledge.vo.SegmentPageReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.knowledge.AiKnowledgeDO;
import cn.iocoder.yudao.module.ai.dal.dataobject.knowledge.AiKnowledgeDocumentDO;
import cn.iocoder.yudao.module.ai.dal.dataobject.knowledge.AiKnowledgeSegmentDO;

import java.util.List;

/**
 * AI 医疗知识库 Service 接口
 *
 * @author 芋道源码
 */
public interface AiKnowledgeService {

    Long createKnowledge(KnowledgeSaveReqVO createReqVO);

    void updateKnowledge(KnowledgeSaveReqVO updateReqVO);

    void deleteKnowledge(Long id);

    AiKnowledgeDO getKnowledge(Long id);

    PageResult<AiKnowledgeDO> getKnowledgePage(KnowledgePageReqVO pageReqVO);

    /**
     * 上传并处理知识库文档：解析 → 切分 → 向量化 → 保存切片（同步处理）
     *
     * @param userId 上传用户
     * @param knowledgeId 知识库编号
     * @param fileName 文件名
     * @param content 文件内容
     * @return 文档编号
     */
    Long processDocument(Long userId, Long knowledgeId, String fileName, byte[] content);

    PageResult<AiKnowledgeDocumentDO> getDocumentPage(DocumentPageReqVO pageReqVO);

    void deleteDocument(Long documentId);

    PageResult<AiKnowledgeSegmentDO> getSegmentPage(SegmentPageReqVO pageReqVO);

    void deleteSegment(Long id);

    /**
     * 在指定知识库范围内检索与 query 语义最相似的切片
     *
     * 实现：向量存于 MySQL（JSON），检索时计算余弦相似度取 topK；
     * 后续如需更换为 Milvus / PGVector 等向量数据库，替换本方法即可
     *
     * @param knowledgeIds 知识库编号列表
     * @param query 查询文本
     * @param topK 返回条数
     * @return 相似切片列表（按相似度倒序）
     */
    List<AiKnowledgeSegmentDO> searchSimilarSegments(List<Long> knowledgeIds, String query, int topK);

}
