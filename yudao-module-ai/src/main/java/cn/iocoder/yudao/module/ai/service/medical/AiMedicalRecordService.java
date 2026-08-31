package cn.iocoder.yudao.module.ai.service.medical;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.ai.controller.admin.medical.vo.record.RecordPageReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.medical.vo.record.RecordSaveReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.medical.AiMedicalRecordDO;

import java.util.List;

/**
 * 预问诊病历 Service 接口
 *
 * @author 芋道源码
 */
public interface AiMedicalRecordService {

    /**
     * 创建预问诊病历（由医疗 Agent 的 saveMedicalRecord 工具调用）
     */
    Long createRecord(Long userId, Long conversationId, String chiefComplaint, String presentIllness,
                      String pastHistory, String allergyHistory, String departmentSuggestion, String advice);

    void updateRecord(RecordSaveReqVO updateReqVO);

    void deleteRecord(Long id);

    AiMedicalRecordDO getRecord(Long id);

    PageResult<AiMedicalRecordDO> getRecordPage(RecordPageReqVO pageReqVO);

    /**
     * 获得指定用户的预问诊病历列表
     */
    List<AiMedicalRecordDO> getRecordListByUserId(Long userId);

}
