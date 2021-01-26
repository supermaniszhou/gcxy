package com.seeyon.apps.ext.batchupdate.manager;

import com.seeyon.apps.ext.batchupdate.util.PropUtil;
import com.seeyon.apps.ext.logRecord.dao.LogRecordDao;
import com.seeyon.apps.ext.logRecord.po.LogRecord;
import com.seeyon.ctp.common.authenticate.domain.User;
import com.seeyon.ctp.common.security.MessageEncoder;
import com.seeyon.ctp.organization.po.OrgPrincipal;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.seeyon.apps.ext.batchupdate.dao.batchupdateDao;
import com.seeyon.ctp.common.AppContext;

import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class batchupdateManagerImpl implements batchupdateManager {
    private static final Log log = LogFactory.getLog(batchupdateManagerImpl.class);

    private batchupdateDao batchupdateDao = (batchupdateDao) AppContext.getBean("batchupdateDaoDemo");

    private LogRecordDao logRecordDao = (LogRecordDao) AppContext.getBean("logRecordDao");

    @Override
    public void batchUpdate() throws NoSuchAlgorithmException {
        PropUtil propUtil = new PropUtil();
        User user = AppContext.getCurrentUser();
        //白名单
        String[] arrs = {"admin1", "system", "audit-admin", "seeyon-guest"};
        List<String> wList = Arrays.asList(arrs);
        MessageEncoder encoder = new MessageEncoder();
        List<OrgPrincipal> list = batchupdateDao.selectAll();
        List<OrgPrincipal> orgList = new ArrayList<>();
        list.forEach(orgPrincipal -> {
            Long createTime = orgPrincipal.getCreateTime().getTime();
            Long updateTime = orgPrincipal.getUpdateTime().getTime();
//            差值
            Long difference = updateTime - createTime;
            if (difference.longValue() != 0l) {
                if (!wList.contains(orgPrincipal.getLoginName())) {
                    String pwd = encoder.encode(orgPrincipal.getLoginName(), propUtil.getValueByKey("init.pwd"));
                    orgPrincipal.setCredentialValue(pwd);
                    orgPrincipal.setUpdateTime(new Date());
                    orgList.add(orgPrincipal);
                    //记录更新了哪些
                    LogRecord logRecord = new LogRecord();
                    logRecord.setId(System.currentTimeMillis());
                    logRecord.setUpdateUser(user.getLoginName());
                    logRecord.setUpdateDate(new Date());
                    logRecord.setOpType("更新");
                    logRecord.setOpContent("用户：" + orgPrincipal.getLoginName() + "的密码被重置了！");
                    logRecord.setOpResult("成功");
                    logRecordDao.saveLogRecord(logRecord);
                }
            }
        });
        System.out.println(orgList.size());
        batchupdateDao.updateAll(orgList);

    }


}