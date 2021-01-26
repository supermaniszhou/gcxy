package com.seeyon.apps.ext.batchupdate.dao;

import com.seeyon.ctp.organization.po.OrgPrincipal;

import java.util.List;

public interface batchupdateDao {

    public static boolean Debugger = true;

    List<OrgPrincipal> selectAll();

    void updateAll(List<OrgPrincipal> list);
}