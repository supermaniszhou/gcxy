package com.seeyon.apps.ext.gcxym3.dao;

import com.seeyon.ctp.util.FlipInfo;

import java.util.Map;

public interface Gcxym3Dao {

    /**
     * 办公电话
     * @param flipInfo
     * @param params
     * @return
     */
    FlipInfo getBgdhList(FlipInfo flipInfo, Map params);

    /**
     * 校内通联
     * @param flipInfo
     * @param params
     * @return
     */
    FlipInfo getXntlList(FlipInfo flipInfo, Map params);

}
