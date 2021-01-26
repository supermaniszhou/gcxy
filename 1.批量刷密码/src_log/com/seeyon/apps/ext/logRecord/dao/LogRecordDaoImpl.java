package com.seeyon.apps.ext.logRecord.dao;

import com.seeyon.apps.ext.logRecord.po.LogRecord;
import com.seeyon.ctp.util.DBAgent;
import com.seeyon.ctp.util.FlipInfo;

import java.util.HashMap;
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
        StringBuilder sb = new StringBuilder();
        sb.append("from LogRecord l where 1=1 ");
        if (null != params.get("opContent")) {
            sb.append(" and l.opContent like :opContent ");
        }
        sb.append(" order by l.updateDate desc");
        List<LogRecord> logRecordList = DBAgent.find(sb.toString(), params, flipInfo);
        flipInfo.setData(logRecordList);
        return flipInfo;
    }
}
