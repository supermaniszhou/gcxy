package com.seeyon.apps.ext.logRecord.dao;

import com.seeyon.apps.ext.logRecord.po.LogRecord;
import com.seeyon.ctp.util.DBAgent;
import com.seeyon.ctp.util.FlipInfo;

import java.util.List;
import java.util.Map;

public class LogRecordDaoImpl implements LogRecordDao {

    @Override
    public void saveLogRecord(LogRecord logRecord) {
        DBAgent.save(logRecord);
    }

    @Override
    public void updateLogRecord(LogRecord logRecord) {
        DBAgent.update(logRecord);
    }

    @Override
    public FlipInfo selectAllPage(FlipInfo flipInfo, Map<String, Object> params) {
        String sql = "from LogRecord where opContent = :opContent and updateDate = :updateDate";
        List<LogRecord> logRecordList = DBAgent.find(sql, params, flipInfo);
        flipInfo.setData(logRecordList);
        return flipInfo;
    }
}
