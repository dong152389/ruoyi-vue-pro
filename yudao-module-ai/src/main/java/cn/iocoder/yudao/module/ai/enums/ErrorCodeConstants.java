package cn.iocoder.yudao.module.ai.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

/**
 * AI 模块错误码枚举类
 *
 * ai 系统，使用 1-040-000-000 段
 *
 * @author 芋道源码
 */
public interface ErrorCodeConstants {

    // ========== 模型管理 1-040-000-000 ==========
    ErrorCode API_KEY_NOT_EXISTS = new ErrorCode(1_040_000_000, "API 密钥不存在");
    ErrorCode MODEL_NOT_EXISTS = new ErrorCode(1_040_000_001, "模型不存在");
    ErrorCode MODEL_KEY_NOT_EXISTS = new ErrorCode(1_040_000_002, "模型绑定的 API 密钥不存在");
    ErrorCode MODEL_DISABLE = new ErrorCode(1_040_000_003, "模型已停用");
    ErrorCode CHAT_MODEL_NOT_CONFIGURED = new ErrorCode(1_040_000_004, "请先在【模型配置】中启用可用的对话模型");
    ErrorCode EMBEDDING_MODEL_NOT_CONFIGURED = new ErrorCode(1_040_000_005, "请先在【模型配置】中启用可用的向量模型");

    // ========== 对话 1-040-001-000 ==========
    ErrorCode CONVERSATION_NOT_EXISTS = new ErrorCode(1_040_001_000, "会话不存在");
    ErrorCode ROLE_NOT_EXISTS = new ErrorCode(1_040_001_001, "医疗角色不存在");
    ErrorCode MESSAGE_NOT_EXISTS = new ErrorCode(1_040_001_002, "消息不存在");
    ErrorCode CHAT_PROCESS_ERROR = new ErrorCode(1_040_001_003, "AI 请求失败，原因：{}");

    // ========== 知识库 1-040-002-000 ==========
    ErrorCode KNOWLEDGE_NOT_EXISTS = new ErrorCode(1_040_002_000, "知识库不存在");
    ErrorCode DOCUMENT_NOT_EXISTS = new ErrorCode(1_040_002_001, "知识库文档不存在");
    ErrorCode SEGMENT_NOT_EXISTS = new ErrorCode(1_040_002_002, "知识库切片不存在");
    ErrorCode DOCUMENT_EMPTY_CONTENT = new ErrorCode(1_040_002_003, "文档解析失败，无法切片，原因：{}");
    ErrorCode DOCUMENT_UNSUPPORTED_TYPE = new ErrorCode(1_040_002_004, "不支持的文档类型，仅支持 txt、md、pdf、docx");

    // ========== 医疗数据 1-040-003-000 ==========
    ErrorCode DEPARTMENT_NOT_EXISTS = new ErrorCode(1_040_003_000, "科室不存在");
    ErrorCode DRUG_NOT_EXISTS = new ErrorCode(1_040_003_001, "药品不存在");
    ErrorCode SCHEDULE_NOT_EXISTS = new ErrorCode(1_040_003_002, "排班不存在");
    ErrorCode SCHEDULE_DISABLE = new ErrorCode(1_040_003_003, "该排班已停诊");
    ErrorCode SCHEDULE_NO_SLOTS = new ErrorCode(1_040_003_004, "该时段号源已约满，请选择其他时段");
    ErrorCode APPOINTMENT_NOT_EXISTS = new ErrorCode(1_040_003_005, "预约记录不存在");
    ErrorCode APPOINTMENT_STATUS_INVALID = new ErrorCode(1_040_003_006, "预约状态不正确，无法执行该操作");
    ErrorCode RECORD_NOT_EXISTS = new ErrorCode(1_040_003_007, "预问诊病历不存在");

}
