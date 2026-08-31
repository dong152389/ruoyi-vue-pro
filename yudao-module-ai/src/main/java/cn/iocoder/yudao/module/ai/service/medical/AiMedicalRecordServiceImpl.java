package cn.iocoder.yudao.module.ai.service.medical;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.controller.admin.medical.vo.record.RecordPageReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.medical.vo.record.RecordSaveReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.medical.AiMedicalRecordDO;
import cn.iocoder.yudao.module.ai.dal.mysql.medical.AiMedicalRecordMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.ai.enums.ErrorCodeConstants.RECORD_NOT_EXISTS;

/**
 * 预问诊病历 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class AiMedicalRecordServiceImpl implements AiMedicalRecordService {

    @Resource
    private AiMedicalRecordMapper recordMapper;

    @Override
    public Long createRecord(Long userId, Long conversationId, String chiefComplaint, String presentIllness,
                             String pastHistory, String allergyHistory, String departmentSuggestion, String advice) {
        AiMedicalRecordDO record = new AiMedicalRecordDO();
        record.setUserId(userId);
        record.setConversationId(conversationId);
        record.setChiefComplaint(chiefComplaint);
        record.setPresentIllness(presentIllness);
        record.setPastHistory(pastHistory);
        record.setAllergyHistory(allergyHistory);
        record.setDepartmentSuggestion(departmentSuggestion);
        record.setAdvice(advice);
        recordMapper.insert(record);
        return record.getId();
    }

    @Override
    public void updateRecord(RecordSaveReqVO updateReqVO) {
        validateRecordExists(updateReqVO.getId());
        AiMedicalRecordDO updateObj = BeanUtils.toBean(updateReqVO, AiMedicalRecordDO.class);
        recordMapper.updateById(updateObj);
    }

    @Override
    public void deleteRecord(Long id) {
        validateRecordExists(id);
        recordMapper.deleteById(id);
    }

    private void validateRecordExists(Long id) {
        if (id == null) {
            return;
        }
        if (recordMapper.selectById(id) == null) {
            throw exception(RECORD_NOT_EXISTS);
        }
    }

    @Override
    public AiMedicalRecordDO getRecord(Long id) {
        return recordMapper.selectById(id);
    }

    @Override
    public PageResult<AiMedicalRecordDO> getRecordPage(RecordPageReqVO pageReqVO) {
        return recordMapper.selectPage(pageReqVO);
    }

    @Override
    public List<AiMedicalRecordDO> getRecordListByUserId(Long userId) {
        return recordMapper.selectListByUserId(userId);
    }

}
