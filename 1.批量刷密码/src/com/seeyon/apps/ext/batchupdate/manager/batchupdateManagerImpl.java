package com.seeyon.apps.ext.batchupdate.manager;

import com.seeyon.ctp.common.security.MessageEncoder;
import com.seeyon.ctp.organization.po.OrgPrincipal;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.seeyon.apps.ext.batchupdate.dao.batchupdateDao;
import com.seeyon.ctp.common.AppContext;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class batchupdateManagerImpl implements batchupdateManager {
    private static final Log log = LogFactory.getLog(batchupdateManagerImpl.class);

    private batchupdateDao batchupdateDao = (batchupdateDao) AppContext.getBean("batchupdateDaoDemo");

    @Override
    public void batchUpdate() throws NoSuchAlgorithmException {
        MessageEncoder encoder = new MessageEncoder();
        List<OrgPrincipal> list = batchupdateDao.selectAll();
        List<OrgPrincipal> orgList = new ArrayList<>();
        list.forEach(orgPrincipal -> {
            Long createTime = orgPrincipal.getCreateTime().getTime();
            Long updateTime = orgPrincipal.getUpdateTime().getTime();
//            差值
            Long difference = updateTime - createTime;
            if (difference.longValue() != 0l) {
                String pwd = encoder.encode(orgPrincipal.getLoginName(), "666666");
                orgPrincipal.setCredentialValue(pwd);
                orgPrincipal.setUpdateTime(new Date());
                orgList.add(orgPrincipal);
            }
        });
        System.out.println(orgList.size());
//        batchupdateDao.updateAll(orgList);

    }


}