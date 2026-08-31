package cn.iocoder.yudao.module.ai.controller.admin.knowledge;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.ai.controller.admin.knowledge.vo.DocumentPageReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.knowledge.vo.DocumentRespVO;
import cn.iocoder.yudao.module.ai.controller.admin.knowledge.vo.KnowledgePageReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.knowledge.vo.KnowledgeRespVO;
import cn.iocoder.yudao.module.ai.controller.admin.knowledge.vo.KnowledgeSaveReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.knowledge.vo.SegmentPageReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.knowledge.vo.SegmentRespVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.knowledge.AiKnowledgeDO;
import cn.iocoder.yudao.module.ai.dal.dataobject.knowledge.AiKnowledgeDocumentDO;
import cn.iocoder.yudao.module.ai.dal.dataobject.knowledge.AiKnowledgeSegmentDO;
import cn.iocoder.yudao.module.ai.service.knowledge.AiKnowledgeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.module.ai.enums.ErrorCodeConstants.DOCUMENT_EMPTY_CONTENT;
import static cn.iocoder.yudao.module.ai.enums.ErrorCodeConstants.DOCUMENT_UNSUPPORTED_TYPE;

@Tag(name = "管理后台 - AI 医疗知识库")
@RestController
@RequestMapping("/ai/knowledge")
@Validated
public class AiKnowledgeController {

    @Resource
    private AiKnowledgeService knowledgeService;

    // ==================== 知识库 ====================

    @PostMapping("/create")
    @Operation(summary = "创建知识库")
    @PreAuthorize("@ss.hasPermission('ai:knowledge:create')")
    public CommonResult<Long> createKnowledge(@Valid @RequestBody KnowledgeSaveReqVO createReqVO) {
        return success(knowledgeService.createKnowledge(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "修改知识库")
    @PreAuthorize("@ss.hasPermission('ai:knowledge:update')")
    public CommonResult<Boolean> updateKnowledge(@Valid @RequestBody KnowledgeSaveReqVO updateReqVO) {
        knowledgeService.updateKnowledge(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除知识库", description = "同时删除库下文档与切片")
    @Parameter(name = "id", description = "知识库编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('ai:knowledge:delete')")
    public CommonResult<Boolean> deleteKnowledge(@RequestParam("id") Long id) {
        knowledgeService.deleteKnowledge(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得知识库")
    @Parameter(name = "id", description = "知识库编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('ai:knowledge:query')")
    public CommonResult<KnowledgeRespVO> getKnowledge(@RequestParam("id") Long id) {
        return success(BeanUtils.toBean(knowledgeService.getKnowledge(id), KnowledgeRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得知识库分页")
    @PreAuthorize("@ss.hasPermission('ai:knowledge:query')")
    public CommonResult<PageResult<KnowledgeRespVO>> getKnowledgePage(@Validated KnowledgePageReqVO pageReqVO) {
        PageResult<AiKnowledgeDO> pageResult = knowledgeService.getKnowledgePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, KnowledgeRespVO.class));
    }

    // ==================== 文档 ====================

    @PostMapping(value = "/document/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "上传知识库文档", description = "解析、切分并向量化，支持 txt / md / pdf / docx，同步返回处理结果")
    @PreAuthorize("@ss.hasPermission('ai:knowledge:create')")
    public CommonResult<Long> uploadDocument(@RequestParam("knowledgeId") Long knowledgeId,
                                             @RequestParam("file") MultipartFile file) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        String fileName = file.getOriginalFilename();
        byte[] content;
        try {
            content = file.getBytes();
        } catch (Exception ex) {
            throw exception(DOCUMENT_EMPTY_CONTENT, "文件读取失败");
        }
        if (content.length == 0) {
            throw exception(DOCUMENT_EMPTY_CONTENT, "文件为空");
        }
        if (!cn.iocoder.yudao.module.ai.framework.rag.DocumentParser.isSupported(fileName)) {
            throw exception(DOCUMENT_UNSUPPORTED_TYPE);
        }
        return success(knowledgeService.processDocument(userId, knowledgeId, fileName, content));
    }

    @GetMapping("/document/page")
    @Operation(summary = "获得知识库文档分页")
    @PreAuthorize("@ss.hasPermission('ai:knowledge:query')")
    public CommonResult<PageResult<DocumentRespVO>> getDocumentPage(@Validated DocumentPageReqVO pageReqVO) {
        PageResult<AiKnowledgeDocumentDO> pageResult = knowledgeService.getDocumentPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, DocumentRespVO.class));
    }

    @DeleteMapping("/document/delete")
    @Operation(summary = "删除知识库文档", description = "同时删除文档下的切片")
    @Parameter(name = "id", description = "文档编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('ai:knowledge:delete')")
    public CommonResult<Boolean> deleteDocument(@RequestParam("id") Long id) {
        knowledgeService.deleteDocument(id);
        return success(true);
    }

    // ==================== 切片 ====================

    @GetMapping("/segment/page")
    @Operation(summary = "获得知识库切片分页")
    @PreAuthorize("@ss.hasPermission('ai:knowledge:query')")
    public CommonResult<PageResult<SegmentRespVO>> getSegmentPage(@Validated SegmentPageReqVO pageReqVO) {
        PageResult<AiKnowledgeSegmentDO> pageResult = knowledgeService.getSegmentPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, SegmentRespVO.class));
    }

    @DeleteMapping("/segment/delete")
    @Operation(summary = "删除知识库切片")
    @Parameter(name = "id", description = "切片编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('ai:knowledge:delete')")
    public CommonResult<Boolean> deleteSegment(@RequestParam("id") Long id) {
        knowledgeService.deleteSegment(id);
        return success(true);
    }

    @GetMapping("/segment/search")
    @Operation(summary = "检索切片", description = "调试用：输入问题查看 RAG 检索命中结果")
    @Parameter(name = "knowledgeIds", description = "知识库编号列表", required = true)
    @Parameter(name = "query", description = "查询文本", required = true)
    public CommonResult<List<SegmentRespVO>> searchSegment(@RequestParam("knowledgeIds") List<Long> knowledgeIds,
                                                           @RequestParam("query") String query) {
        List<AiKnowledgeSegmentDO> segments = knowledgeService.searchSimilarSegments(knowledgeIds, query, 5);
        return success(BeanUtils.toBean(segments, SegmentRespVO.class));
    }

}
