package cn.iocoder.yudao.module.ai.service.knowledge;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.controller.admin.knowledge.vo.DocumentPageReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.knowledge.vo.KnowledgePageReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.knowledge.vo.KnowledgeSaveReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.knowledge.vo.SegmentPageReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.knowledge.AiKnowledgeDO;
import cn.iocoder.yudao.module.ai.dal.dataobject.knowledge.AiKnowledgeDocumentDO;
import cn.iocoder.yudao.module.ai.dal.dataobject.knowledge.AiKnowledgeSegmentDO;
import cn.iocoder.yudao.module.ai.dal.dataobject.model.AiModelDO;
import cn.iocoder.yudao.module.ai.dal.mysql.knowledge.AiKnowledgeDocumentMapper;
import cn.iocoder.yudao.module.ai.dal.mysql.knowledge.AiKnowledgeMapper;
import cn.iocoder.yudao.module.ai.dal.mysql.knowledge.AiKnowledgeSegmentMapper;
import cn.iocoder.yudao.module.ai.framework.ai.AiModelFactory;
import cn.iocoder.yudao.module.ai.framework.rag.DocumentParser;
import cn.iocoder.yudao.module.ai.framework.rag.TextChunker;
import cn.iocoder.yudao.module.ai.service.model.AiModelService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.ai.enums.ErrorCodeConstants.DOCUMENT_EMPTY_CONTENT;
import static cn.iocoder.yudao.module.ai.enums.ErrorCodeConstants.DOCUMENT_NOT_EXISTS;
import static cn.iocoder.yudao.module.ai.enums.ErrorCodeConstants.DOCUMENT_UNSUPPORTED_TYPE;
import static cn.iocoder.yudao.module.ai.enums.ErrorCodeConstants.KNOWLEDGE_NOT_EXISTS;
import static cn.iocoder.yudao.module.ai.enums.ErrorCodeConstants.SEGMENT_NOT_EXISTS;

/**
 * AI 医疗知识库 Service 实现类
 *
 * @author 芋道源码
 */
@Slf4j
@Service
@Validated
public class AiKnowledgeServiceImpl implements AiKnowledgeService {

    @Resource
    private AiKnowledgeMapper knowledgeMapper;
    @Resource
    private AiKnowledgeDocumentMapper documentMapper;
    @Resource
    private AiKnowledgeSegmentMapper segmentMapper;
    @Resource
    private AiModelService modelService;
    @Resource
    private AiModelFactory modelFactory;

    @Override
    public Long createKnowledge(KnowledgeSaveReqVO createReqVO) {
        AiKnowledgeDO knowledge = BeanUtils.toBean(createReqVO, AiKnowledgeDO.class);
        knowledgeMapper.insert(knowledge);
        return knowledge.getId();
    }

    @Override
    public void updateKnowledge(KnowledgeSaveReqVO updateReqVO) {
        validateKnowledgeExists(updateReqVO.getId());
        AiKnowledgeDO updateObj = BeanUtils.toBean(updateReqVO, AiKnowledgeDO.class);
        knowledgeMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteKnowledge(Long id) {
        validateKnowledgeExists(id);
        knowledgeMapper.deleteById(id);
        // 级联删除文档与切片
        documentMapper.delete(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<cn.iocoder.yudao.module.ai.dal.dataobject.knowledge.AiKnowledgeDocumentDO>()
                .eq(AiKnowledgeDocumentDO::getKnowledgeId, id));
        segmentMapper.delete(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<cn.iocoder.yudao.module.ai.dal.dataobject.knowledge.AiKnowledgeSegmentDO>()
                .eq(AiKnowledgeSegmentDO::getKnowledgeId, id));
    }

    private void validateKnowledgeExists(Long id) {
        if (id == null) {
            return;
        }
        if (knowledgeMapper.selectById(id) == null) {
            throw exception(KNOWLEDGE_NOT_EXISTS);
        }
    }

    @Override
    public AiKnowledgeDO getKnowledge(Long id) {
        return knowledgeMapper.selectById(id);
    }

    @Override
    public PageResult<AiKnowledgeDO> getKnowledgePage(KnowledgePageReqVO pageReqVO) {
        return knowledgeMapper.selectPage(pageReqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long processDocument(Long userId, Long knowledgeId, String fileName, byte[] content) {
        // 1. 校验知识库与文件类型
        AiKnowledgeDO knowledge = knowledgeMapper.selectById(knowledgeId);
        if (knowledge == null) {
            throw exception(KNOWLEDGE_NOT_EXISTS);
        }
        if (!DocumentParser.isSupported(fileName)) {
            throw exception(DOCUMENT_UNSUPPORTED_TYPE);
        }
        // 2. 解析文本并切分
        String text = DocumentParser.parse(fileName, content);
        if (StrUtil.isBlank(text)) {
            throw exception(DOCUMENT_EMPTY_CONTENT);
        }
        List<String> chunks = TextChunker.chunk(text);
        if (chunks.isEmpty()) {
            throw exception(DOCUMENT_EMPTY_CONTENT);
        }
        // 3. 创建文档记录
        AiKnowledgeDocumentDO document = new AiKnowledgeDocumentDO();
        document.setKnowledgeId(knowledgeId);
        document.setName(fileName);
        document.setTokens(text.length());
        document.setSegmentCount(chunks.size());
        document.setSliceStatus(AiKnowledgeDocumentDO.SLICE_STATUS_SUCCESS);
        documentMapper.insert(document);
        // 4. 向量化并保存切片
        embeddingAndSaveSegments(knowledge, document, chunks);
        return document.getId();
    }

    /**
     * 调用向量模型批量向量化切片并保存；失败时更新文档状态为失败，并抛出异常回滚
     */
    private void embeddingAndSaveSegments(AiKnowledgeDO knowledge, AiKnowledgeDocumentDO document, List<String> chunks) {
        try {
            EmbeddingModel embeddingModel = getEmbeddingModel(knowledge.getEmbeddingModelId());
            List<float[]> vectors = embeddingModel.embed(chunks);
            List<AiKnowledgeSegmentDO> segments = new ArrayList<>(chunks.size());
            for (int i = 0; i < chunks.size(); i++) {
                AiKnowledgeSegmentDO segment = new AiKnowledgeSegmentDO();
                segment.setDocumentId(document.getId());
                segment.setKnowledgeId(knowledge.getId());
                segment.setContent(chunks.get(i));
                segment.setTokens(chunks.get(i).length());
                segment.setVector(JsonUtils.toJsonString(vectors.get(i)));
                segments.add(segment);
            }
            segmentMapper.insertBatch(segments);
        } catch (Exception ex) {
            document.setSliceStatus(AiKnowledgeDocumentDO.SLICE_STATUS_FAIL);
            documentMapper.updateById(document);
            log.error("[embeddingAndSaveSegments] 文档向量化失败，documentId={}", document.getId(), ex);
            throw exception(DOCUMENT_EMPTY_CONTENT, "向量模型调用失败：" + ex.getMessage());
        }
    }

    private EmbeddingModel getEmbeddingModel(Long embeddingModelId) {
        AiModelDO model = modelService.validateEmbeddingModel(embeddingModelId);
        return modelFactory.getOrCreateEmbeddingModel(model);
    }

    @Override
    public PageResult<AiKnowledgeDocumentDO> getDocumentPage(DocumentPageReqVO pageReqVO) {
        return documentMapper.selectPage(pageReqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDocument(Long documentId) {
        AiKnowledgeDocumentDO document = documentMapper.selectById(documentId);
        if (document == null) {
            throw exception(DOCUMENT_NOT_EXISTS);
        }
        documentMapper.deleteById(documentId);
        segmentMapper.delete(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AiKnowledgeSegmentDO>()
                .eq(AiKnowledgeSegmentDO::getDocumentId, documentId));
    }

    @Override
    public PageResult<AiKnowledgeSegmentDO> getSegmentPage(SegmentPageReqVO pageReqVO) {
        return segmentMapper.selectPage(pageReqVO);
    }

    @Override
    public void deleteSegment(Long id) {
        if (segmentMapper.selectById(id) == null) {
            throw exception(SEGMENT_NOT_EXISTS);
        }
        segmentMapper.deleteById(id);
    }

    @Override
    public List<AiKnowledgeSegmentDO> searchSimilarSegments(List<Long> knowledgeIds, String query, int topK) {
        if (CollUtil.isEmpty(knowledgeIds) || StrUtil.isBlank(query)) {
            return List.of();
        }
        // 1. 加载知识库与切片
        List<AiKnowledgeDO> knowledges = knowledgeMapper.selectByIds(knowledgeIds);
        if (CollUtil.isEmpty(knowledges)) {
            return List.of();
        }
        List<AiKnowledgeSegmentDO> segments = segmentMapper.selectListByKnowledgeIds(knowledgeIds);
        if (CollUtil.isEmpty(segments)) {
            return List.of();
        }
        // 2. 按向量模型分组：不同知识库可能配置不同的向量模型
        Map<Long, AiKnowledgeDO> knowledgeMap = new HashMap<>();
        knowledges.forEach(kb -> knowledgeMap.put(kb.getId(), kb));
        List<AiKnowledgeSegmentDO> result = new ArrayList<>();
        Map<Long, float[]> queryVectorMap = new HashMap<>();
        // 3. 逐切片计算余弦相似度，取 topK
        List<double[]> similarityList = new ArrayList<>();
        for (AiKnowledgeSegmentDO segment : segments) {
            AiKnowledgeDO knowledge = knowledgeMap.get(segment.getKnowledgeId());
            if (knowledge == null) {
                continue;
            }
            float[] queryVector = queryVectorMap.computeIfAbsent(knowledge.getEmbeddingModelId(),
                    modelId -> embedQuery(modelId, query));
            float[] segmentVector = parseVector(segment.getVector());
            if (segmentVector == null) {
                continue;
            }
            double similarity = cosineSimilarity(queryVector, segmentVector);
            similarityList.add(new double[]{result.size(), similarity});
            result.add(segment);
        }
        similarityList.sort((a, b) -> Double.compare(b[1], a[1]));
        List<AiKnowledgeSegmentDO> topKSegments = new ArrayList<>(Math.min(topK, result.size()));
        for (int i = 0; i < Math.min(topK, similarityList.size()); i++) {
            topKSegments.add(result.get((int) similarityList.get(i)[0]));
        }
        return topKSegments;
    }

    private float[] embedQuery(Long embeddingModelId, String query) {
        EmbeddingModel embeddingModel = getEmbeddingModel(embeddingModelId);
        return embeddingModel.embed(query);
    }

    private float[] parseVector(String vectorJson) {
        try {
            return JsonUtils.parseObject(vectorJson, float[].class);
        } catch (Exception ex) {
            log.warn("[parseVector] 向量解析失败：{}", ex.getMessage());
            return null;
        }
    }

    private double cosineSimilarity(float[] vectorA, float[] vectorB) {
        if (vectorA == null || vectorB == null || vectorA.length != vectorB.length) {
            return 0;
        }
        double dotProduct = 0;
        double normA = 0;
        double normB = 0;
        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            normA += vectorA[i] * vectorA[i];
            normB += vectorB[i] * vectorB[i];
        }
        if (normA == 0 || normB == 0) {
            return 0;
        }
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

}
