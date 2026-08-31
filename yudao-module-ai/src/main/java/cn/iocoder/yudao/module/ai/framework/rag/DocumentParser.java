package cn.iocoder.yudao.module.ai.framework.rag;

import cn.iocoder.yudao.module.ai.enums.ErrorCodeConstants;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;

/**
 * 知识库文档解析器：从上传的文档中抽取纯文本
 *
 * 支持 txt、md、pdf（PDFBox）、docx（POI）
 *
 * @author 芋道源码
 */
public class DocumentParser {

    private DocumentParser() {
    }

    public static boolean isSupported(String fileName) {
        String lower = fileName == null ? "" : fileName.toLowerCase();
        return lower.endsWith(".txt") || lower.endsWith(".md") || lower.endsWith(".pdf") || lower.endsWith(".docx");
    }

    public static String parse(String fileName, byte[] content) {
        String lower = fileName == null ? "" : fileName.toLowerCase();
        if (lower.endsWith(".pdf")) {
            try (PDDocument document = Loader.loadPDF(content)) {
                return new PDFTextStripper().getText(document);
            } catch (Exception ex) {
                throw exception(ErrorCodeConstants.DOCUMENT_EMPTY_CONTENT, "PDF 解析失败");
            }
        }
        if (lower.endsWith(".docx")) {
            try (XWPFDocument document = new XWPFDocument(new ByteArrayInputStream(content));
                 XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
                return extractor.getText();
            } catch (Exception ex) {
                throw exception(ErrorCodeConstants.DOCUMENT_EMPTY_CONTENT, "DOCX 解析失败");
            }
        }
        if (lower.endsWith(".txt") || lower.endsWith(".md")) {
            return new String(content, StandardCharsets.UTF_8);
        }
        throw exception(ErrorCodeConstants.DOCUMENT_UNSUPPORTED_TYPE);
    }

}
