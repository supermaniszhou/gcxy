package com.seeyon.apps.ext.batchupdate.dao;

import java.util.List;

import com.seeyon.ctp.organization.po.OrgPrincipal;
import com.seeyon.ctp.util.DBAgent;

public class batchupdateDaoImpl implements batchupdateDao {

    @Override
    public List<OrgPrincipal> selectAll() {
        List<OrgPrincipal> list = DBAgent.loadAll(OrgPrincipal.class);
        return list;
    }

    @Override
    public void updateAll(List<OrgPrincipal> list) {
        DBAgent.updateAll(list);
    }
}