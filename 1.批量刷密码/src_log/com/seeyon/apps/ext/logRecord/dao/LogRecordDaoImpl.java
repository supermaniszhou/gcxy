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
        Map<String, Object> map = new HashMap<>();
        StringBuilder sb = new StringBuilder();
        sb.append("from LogRecord l where 1=1 ");
        String opContent = "";
        if (null != params.get("opContent")) {
            opContent = (String) params.get("opContent");
            sb.append("and opContent = :opContent");
            map.put("opContent", opContent);
        }
        sb.append(" order by l.updateDate desc");
        List<LogRecord> logRecordList = DBAgent.find(sb.toString(), map, flipInfo);
        flipInfo.setData(logRecordList);
        return flipInfo;
    }
}
