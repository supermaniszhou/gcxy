package com.seeyon.apps.ext.logRecord.manager;

import com.seeyon.apps.ext.logRecord.po.LogRecord;
import com.seeyon.ctp.util.FlipInfo;

import java.util.List;
import java.util.Map;

public interface LogRecordManager {

    void saveLogRecord(LogRecord logRecord);

    void updateLogRecord(LogRecord logRecord);

    FlipInfo selectAllPage(Map<String, Object> params, FlipInfo flipInfo);
}
