package cn.iocoder.yudao.module.ai.framework.chat;

import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.tenant.core.util.TenantUtils;
import cn.iocoder.yudao.module.ai.dal.dataobject.medical.AiMedicalDepartmentDO;
import cn.iocoder.yudao.module.ai.dal.dataobject.medical.AiMedicalDrugDO;
import cn.iocoder.yudao.module.ai.dal.dataobject.medical.AiMedicalScheduleDO;
import cn.iocoder.yudao.module.ai.service.medical.AiMedicalDepartmentService;
import cn.iocoder.yudao.module.ai.service.medical.AiMedicalDrugService;
import cn.iocoder.yudao.module.ai.service.medical.AiMedicalRecordService;
import cn.iocoder.yudao.module.ai.service.medical.AiMedicalScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 医疗 Agent 工具集（Function Calling）
 *
 * 每次对话请求构建一个实例，携带租户与用户上下文：
 * 流式调用时 Spring AI 会在 Reactor 线程上执行工具方法，ThreadLocal 上下文不可用，
 * 因此所有数据库操作均通过 {@link TenantUtils#execute} 显式恢复租户上下文
 *
 * @author 芋道源码
 */
@Slf4j
public class MedicalChatTools {

    private final Long tenantId;
    private final Long userId;
    private final Long conversationId;

    private final AiMedicalDepartmentService departmentService;
    private final AiMedicalDrugService drugService;
    private final AiMedicalScheduleService scheduleService;
    private final AiMedicalRecordService recordService;

    public MedicalChatTools(Long tenantId, Long userId, Long conversationId,
                            AiMedicalDepartmentService departmentService,
                            AiMedicalDrugService drugService,
                            AiMedicalScheduleService scheduleService,
                            AiMedicalRecordService recordService) {
        this.tenantId = tenantId;
        this.userId = userId;
        this.conversationId = conversationId;
        this.departmentService = departmentService;
        this.drugService = drugService;
        this.scheduleService = scheduleService;
        this.recordService = recordService;
    }

    @Tool(description = "智能导诊：根据患者的症状描述获取医院科室列表（含各科室诊疗范围与症状关键词），用于推荐就诊科室。返回结果按匹配度排序，需基于返回结果推荐科室")
    public String searchDepartments(@ToolParam(description = "患者的症状描述或关键词，例如：发热咳嗽、胃痛") String symptom) {
        return TenantUtils.execute(tenantId, () -> {
            List<AiMedicalDepartmentDO> departments = departmentService.getEnabledDepartmentList();
            List<Map<String, Object>> result = new ArrayList<>();
            for (AiMedicalDepartmentDO department : departments) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("departmentId", department.getId());
                item.put("name", department.getName());
                item.put("description", department.getDescription());
                item.put("keywords", department.getKeywords());
                item.put("location", department.getLocation());
                result.add(item);
            }
            Map<String, Object> resp = new HashMap<>();
            resp.put("symptom", symptom);
            resp.put("departments", result);
            return JsonUtils.toJsonString(resp);
        });
    }

    @Tool(description = "用药咨询：按名称或症状关键词搜索本院药品库，返回药品名称、分类与适应症。如需用法用量、禁忌等详情，请再调用 getDrugDetail")
    public String searchDrugs(@ToolParam(description = "药品名称或症状关键词，例如：布洛芬、发热") String keyword) {
        return TenantUtils.execute(tenantId, () -> {
            List<AiMedicalDrugDO> drugs = drugService.searchDrugList(keyword, 10);
            List<Map<String, Object>> result = new ArrayList<>();
            for (AiMedicalDrugDO drug : drugs) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("name", drug.getName());
                item.put("category", drug.getCategory());
                item.put("indications", drug.getIndications());
                result.add(item);
            }
            Map<String, Object> resp = new HashMap<>();
            resp.put("keyword", keyword);
            resp.put("count", result.size());
            resp.put("drugs", result);
            return JsonUtils.toJsonString(resp);
        });
    }

    @Tool(description = "用药咨询：查询指定药品的详细信息，包括适应症、用法用量、禁忌、药物相互作用、不良反应")
    public String getDrugDetail(@ToolParam(description = "药品名称，例如：布洛芬缓释胶囊") String drugName) {
        return TenantUtils.execute(tenantId, () -> {
            AiMedicalDrugDO drug = drugService.getDrugByName(drugName);
            if (drug == null) {
                Map<String, Object> resp = new HashMap<>();
                resp.put("found", false);
                resp.put("message", "药品库中未找到该药品，请确认名称后重试 searchDrugs");
                return JsonUtils.toJsonString(resp);
            }
            Map<String, Object> resp = new LinkedHashMap<>();
            resp.put("found", true);
            resp.put("name", drug.getName());
            resp.put("category", drug.getCategory());
            resp.put("indications", drug.getIndications());
            resp.put("usageDosage", drug.getUsageDosage());
            resp.put("contraindications", drug.getContraindications());
            resp.put("interactions", drug.getInteractions());
            resp.put("sideEffects", drug.getSideEffects());
            return JsonUtils.toJsonString(resp);
        });
    }

    @Tool(description = "预约挂号：查询指定科室（可指定科室名称，不限则传空字符串）在日期区间内可预约的排班号源，返回排班编号、科室、医生、日期、时段、剩余号源与挂号费")
    public String querySchedules(@ToolParam(description = "科室名称，例如：内科；不确定时传空字符串", required = false) String departmentName,
                                 @ToolParam(description = "开始日期，格式 yyyy-MM-dd，为空默认今天", required = false) String beginDate,
                                 @ToolParam(description = "结束日期，格式 yyyy-MM-dd，为空默认今天起 7 天内", required = false) String endDate) {
        return TenantUtils.execute(tenantId, () -> {
            List<Long> departmentIds = new ArrayList<>();
            if (departmentName != null && !departmentName.isBlank()) {
                AiMedicalDepartmentDO department = departmentService.getEnabledDepartmentByName(departmentName.trim());
                if (department != null) {
                    departmentIds.add(department.getId());
                }
            }
            LocalDate begin = parseDate(beginDate, LocalDate.now());
            LocalDate end = parseDate(endDate, begin.plusDays(7));
            List<AiMedicalScheduleDO> schedules = scheduleService.getAvailableScheduleList(departmentIds, begin, end);
            List<Map<String, Object>> result = new ArrayList<>();
            for (AiMedicalScheduleDO schedule : schedules) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("scheduleId", schedule.getId());
                item.put("departmentId", schedule.getDepartmentId());
                AiMedicalDepartmentDO department = departmentService.getDepartment(schedule.getDepartmentId());
                item.put("departmentName", department != null ? department.getName() : "");
                item.put("doctorName", schedule.getDoctorName());
                item.put("doctorTitle", schedule.getDoctorTitle());
                item.put("date", schedule.getScheduleDate().toString());
                item.put("timeSlot", schedule.getTimeSlot());
                item.put("timeRange", schedule.getTimeRange());
                item.put("remainingSlots", schedule.getRemainingSlots());
                item.put("fee", schedule.getFee());
                result.add(item);
            }
            Map<String, Object> resp = new HashMap<>();
            resp.put("count", result.size());
            resp.put("schedules", result);
            return JsonUtils.toJsonString(resp);
        });
    }

    @Tool(description = "预约挂号：为患者创建门诊预约。必须先通过 querySchedules 查询并获得患者确认后，再携带 scheduleId 与患者姓名、手机号调用。返回预约结果")
    public String createAppointment(@ToolParam(description = "排班编号，来自 querySchedules 的返回") Long scheduleId,
                                    @ToolParam(description = "患者姓名") String patientName,
                                    @ToolParam(description = "患者手机号") String patientPhone) {
        return TenantUtils.execute(tenantId, () -> {
            Map<String, Object> resp = new LinkedHashMap<>();
            try {
                Long appointmentId = scheduleService.createAppointment(userId, scheduleId, patientName, patientPhone, null);
                resp.put("success", true);
                resp.put("appointmentId", appointmentId);
                resp.put("message", "预约成功！请在就诊当天携带有效证件按时到院取号就诊。如需取消，请提前在「预约记录」中操作。");
            } catch (Exception ex) {
                log.warn("[createAppointment] 预约失败，scheduleId={}，原因：{}", scheduleId, ex.getMessage());
                resp.put("success", false);
                resp.put("message", ex.getMessage() != null ? ex.getMessage() : "预约失败，请稍后重试");
            }
            return JsonUtils.toJsonString(resp);
        });
    }

    @Tool(description = "预问诊：当问诊信息收集完整（主诉、现病史、既往史、过敏史）后，调用本工具保存预问诊病历，保存成功后需告知患者")
    public String saveMedicalRecord(@ToolParam(description = "主诉：最主要的不适症状及持续时间") String chiefComplaint,
                                    @ToolParam(description = "现病史：起病情况、症状演变、伴随症状、诊治经过") String presentIllness,
                                    @ToolParam(description = "既往史：既往疾病、手术、慢性病史，无则传\"无\"", required = false) String pastHistory,
                                    @ToolParam(description = "过敏史：药物或食物过敏情况，无则传\"无\"", required = false) String allergyHistory,
                                    @ToolParam(description = "建议就诊科室") String departmentSuggestion,
                                    @ToolParam(description = "补充说明与建议", required = false) String advice) {
        return TenantUtils.execute(tenantId, () -> {
            Long recordId = recordService.createRecord(userId, conversationId, chiefComplaint, presentIllness,
                    pastHistory, allergyHistory, departmentSuggestion, advice);
            Map<String, Object> resp = new LinkedHashMap<>();
            resp.put("success", true);
            resp.put("recordId", recordId);
            resp.put("message", "预问诊病历已生成");
            return JsonUtils.toJsonString(resp);
        });
    }

    private LocalDate parseDate(String date, LocalDate defaultValue) {
        if (date == null || date.isBlank()) {
            return defaultValue;
        }
        try {
            return LocalDate.parse(date.trim());
        } catch (Exception ex) {
            return defaultValue;
        }
    }

}
