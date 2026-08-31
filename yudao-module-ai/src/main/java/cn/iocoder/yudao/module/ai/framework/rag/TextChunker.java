package cn.iocoder.yudao.module.ai.framework.rag;

import java.util.ArrayList;
import java.util.List;

/**
 * 知识库文本切分器
 *
 * 按段落聚合、超长段落硬切分（带重叠）的固定长度切分策略：
 * 中文场景下 1 个汉字约等于 1 个 Token，故按字符数近似 Token 数
 *
 * @author 芋道源码
 */
public class TextChunker {

    /**
     * 切片最大长度（字符，近似 Token）
     */
    private static final int CHUNK_SIZE = 600;
    /**
     * 相邻切片的重叠长度（字符）
     */
    private static final int CHUNK_OVERLAP = 80;

    private TextChunker() {
    }

    public static List<String> chunk(String text) {
        return chunk(text, CHUNK_SIZE, CHUNK_OVERLAP);
    }

    public static List<String> chunk(String text, int chunkSize, int overlap) {
        List<String> chunks = new ArrayList<>();
        if (text == null || text.isBlank()) {
            return chunks;
        }
        // 按空行/换行拆分段落
        String[] paragraphs = text.replaceAll("\r\n", "\n").split("\n+");
        StringBuilder buffer = new StringBuilder();
        for (String paragraph : paragraphs) {
            String trimmed = paragraph.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            // 超长段落：先硬切分再入缓冲
            if (trimmed.length() > chunkSize) {
                if (buffer.length() > 0) {
                    chunks.add(buffer.toString());
                    buffer.setLength(0);
                }
                chunks.addAll(splitLongParagraph(trimmed, chunkSize, overlap));
                continue;
            }
            // 聚合段落，超过切片长度则先输出
            if (buffer.length() + trimmed.length() + 1 > chunkSize) {
                if (buffer.length() > 0) {
                    chunks.add(buffer.toString());
                    buffer.setLength(0);
                }
            }
            if (buffer.length() > 0) {
                buffer.append('\n');
            }
            buffer.append(trimmed);
        }
        if (buffer.length() > 0) {
            chunks.add(buffer.toString());
        }
        return chunks;
    }

    private static List<String> splitLongParagraph(String paragraph, int chunkSize, int overlap) {
        List<String> chunks = new ArrayList<>();
        int start = 0;
        while (start < paragraph.length()) {
            int end = Math.min(start + chunkSize, paragraph.length());
            chunks.add(paragraph.substring(start, end));
            if (end >= paragraph.length()) {
                break;
            }
            start = end - overlap;
        }
        return chunks;
    }

}
