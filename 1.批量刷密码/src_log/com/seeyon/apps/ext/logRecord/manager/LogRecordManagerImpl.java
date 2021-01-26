package com.seeyon.apps.ext.logRecord.manager;

import com.seeyon.apps.ext.logRecord.dao.LogRecordDao;
import com.seeyon.apps.ext.logRecord.dao.LogRecordDaoImpl;
import com.seeyon.apps.ext.logRecord.po.LogRecord;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.util.FlipInfo;
import com.seeyon.ctp.util.annotation.AjaxAccess;

import java.util.List;
import java.util.Map;

public class LogRecordManagerImpl implements LogRecordManager {

    private LogRecordDao logRecordDao = (LogRecordDao) AppContext.getBean("logRecordDao");

    @Override
    public void saveLogRecord(LogRecord logRecord) {
        logRecordDao.saveLogRecord(logRecord);
    }

    @Override
    public void updateLogRecord(LogRecord logRecord) {
        logRecordDao.updateLogRecord(logRecord);
    }

    @AjaxAccess
    @Override
    public FlipInfo selectAllPage(FlipInfo flipInfo,Map<String, Object> params) {
        return logRecordDao.selectAllPage(flipInfo, params);
    }
}
