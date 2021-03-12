package com.seeyon.apps.ext.gcxym3.manager;

import com.seeyon.apps.ext.gcxym3.dao.Gcxym3Dao;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.util.FlipInfo;

import java.util.Map;

public class Gcxym3ManagerImpl implements Gcxym3Manager {

    private Gcxym3Dao dao = (Gcxym3Dao) AppContext.getBean("gcxym3Dao");

    @Override
    public FlipInfo getBgdhList(FlipInfo flipInfo, Map params) {
        return dao.getBgdhList(flipInfo, params);
    }

    @Override
    public FlipInfo getXntlList(FlipInfo flipInfo, Map params) {
        return dao.getXntlList(flipInfo, params);
    }
}
