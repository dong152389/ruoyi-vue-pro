# yudao-module-ai —— 医疗方向 AI Agent 模块

基于 **Spring AI**（OpenAI 兼容协议）实现的医疗方向 Agent 模块，通过 **new-api** 等中转站接入大模型，提供智能导诊、用药咨询、预问诊病历生成、预约挂号四个医疗工具，并内置医疗知识库 RAG 问答。

> ⚠️ 免责声明：本模块为医疗辅助/演示场景设计，AI 输出仅供参考，不能替代执业医师的诊断与治疗。

## 一、功能总览

| 功能 | 说明 | 实现方式 |
|---|---|---|
| 医疗对话 | 多会话管理、SSE 流式输出、上下文记忆 | Spring AI `ChatClient.stream()` + `SseEmitter` |
| 智能导诊 | 症状 → 推荐科室与紧急程度 | `searchDepartments` 工具（Function Calling） |
| 用药咨询 | 药品用途/用法用量/禁忌/相互作用 | `searchDrugs` / `getDrugDetail` 工具 |
| 预约挂号 | 查询号源 → 确认 → 占用号源落库 | `querySchedules` / `createAppointment` 工具（带余号原子扣减） |
| 预问诊病历 | 对话中收集主诉/现病史等并结构化落库 | `saveMedicalRecord` 工具 |
| 知识库 RAG | 文档上传 → 解析 → 切分 → 向量化 → 检索注入 | PDFBox/POI 解析 + 内存余弦检索 |
| 医疗角色预设 | 导诊/用药/预问诊/全科 4 个内置角色 | `ai_chat_role` 系统提示词（含安全护栏） |

**安全护栏**：所有角色提示词强制要求「不做确定性诊断、不开处方、急症引导拨打 120/急诊、回复附免责声明」。

## 二、技术选型说明

- **Spring AI（非 LangChain4j、非 Python）**：Spring 官方维护，与 yudao 的 starter 体系一致；RAG、工具调用、流式均为标准能力；上游 yudao 官方完整版 AI 模块同样迁移到了 Spring AI，未来可对齐。
- **new-api 中转站**：OpenAI 兼容协议，`AiModelFactoryImpl` 按 `ai_api_key`（base_url + api_key）动态构建并缓存 `OpenAiChatModel` / `OpenAiEmbeddingModel`，模型切换零代码。
- **向量存储**：向量存 MySQL（JSON），检索时内存余弦计算，万级切片内性能足够；已抽象在 `AiKnowledgeService#searchSimilarSegments`，后续换 Milvus/PGVector 只需替换该方法。
- **SSE**：Spring MVC 原生 `SseEmitter`，未引入 WebFlux；异步线程通过 `TenantUtils.execute` 显式恢复租户上下文。

## 三、快速开始

### 1. 执行 SQL

`sql/mysql/ruoyi-vue-pro.sql` 末尾追加了 AI 模块块（以「AI 医疗助手模块」注释开头），包含：
- 13 张 `ai_*` 建表语句
- 种子数据：12 个科室、20 种常用药品、2026-09-01~05 示例排班、4 个医疗角色、1 条示例 API 密钥
- 菜单（ID 2500~2550：目录「AI 医疗助手」+ 8 个页面 + 按钮权限）

对已有库，可只执行该块（从注释标记截取到文件末尾）。

### 2. 配置 new-api 中转站

登录管理后台 → **AI 医疗助手 → 模型配置 → API 密钥**，编辑种子记录：
1. `API 地址`：你的 new-api 地址，以 `/v1` 结尾，如 `https://your-relay.com/v1`
2. `API 密钥`：中转站令牌 `sk-xxx`
3. 状态改为 **开启**

再到 **模型管理** 标签页，按渠道实际支持的模型名修改两条种子模型并开启：
- 对话模型（如 `deepseek-chat` / `glm-4-flash` / `gpt-4o-mini`）
- 向量模型（如 `text-embedding-3-small`，需中转站支持 `/v1/embeddings`；不支持时可为向量单独配一条密钥指向其它平台）

### 3. 重启后端

`yudao-server` 的 pom 已引入 `yudao-module-ai`，直接启动即可（首次启动无需任何 AI 相关 yaml 配置）。

### 4. 使用

- **医疗对话**：菜单「AI 医疗助手 → 医疗对话」，选角色 → 描述症状 → 助手会调用工具完成导诊/用药/挂号/病历
- **管理页**：科室 / 药品 / 排班 / 预约 / 病历 / 知识库 / 模型配置，均为标准 CRUD
- **知识库**：新建知识库（选向量模型）→ 文档管理上传文档（txt/md/pdf/docx，同步完成向量化）→ 「切片检索」可调试 RAG 命中效果 → 在角色编辑中把知识库绑定给角色，对话即自动检索注入

## 四、SSE 事件格式

`POST /admin-api/ai/chat/message/send-stream`，`data` 为 JSON：

```
{"type":"content","content":"增量文本"}   # 逐片输出
{"type":"done","messageId":123}          # 完成，消息已落库
{"type":"error","message":"失败原因"}     # 失败（已生成部分会保留）
```

前端使用 `@microsoft/fetch-event-source` 调用，见 `src/api/ai/medical/chat.ts`。

## 五、代码地图（后端）

```
yudao-module-ai
├── controller/admin/model      # AiApiKeyController、AiModelController（模型管理）
├── controller/admin/chat       # 会话/消息（含 send-stream SSE）/医疗角色
├── controller/admin/knowledge  # 知识库/文档/切片（含上传与检索调试）
├── controller/admin/medical    # 科室/药品/排班/预约/病历
├── framework/ai                # AiModelFactory：new-api（OpenAI 兼容）模型构建与缓存
├── framework/chat              # MedicalChatTools：5 个医疗 @Tool 工具
├── framework/rag               # DocumentParser（pdf/docx/txt/md）+ TextChunker
├── service/chat                # AiAgentServiceImpl：Agent 编排核心
├── service/knowledge           # 向量化与余弦相似检索
└── enums/ErrorCodeConstants    # 错误码 1-040 段
```

## 六、常见问题

- **对话报「请先在模型配置中启用可用的对话模型」**：种子密钥/模型默认是停用状态，先完成第二章配置。
- **知识库向量化失败**：检查向量模型对应渠道是否支持 embeddings 接口；查看后端日志中的具体报错。
- **工具没有按预期调用**：与模型能力有关，建议使用支持 function calling 的模型（deepseek-chat、gpt-4o-mini 等）。
- **预约显示约满**：号源扣减为原子操作（`remaining_slots > 0` 条件更新），余号 0 时工具会返回失败提示，属正常。
