package com.seeyon.apps.ext.batchupdate.manager;

import com.seeyon.apps.ext.batchupdate.po.MidUser;
import com.seeyon.ctp.organization.po.OrgPrincipal;

import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface batchupdateManager {

    void batchUpdate() throws NoSuchAlgorithmException;

    /**
     * 先把身份证信息从中间库中抽取到OA的mid_user表中
     */
    void extractData();

    /**
     * 关联获取身份证信息
     */
    List<MidUser> joinIDD();
}