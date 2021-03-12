package com.seeyon.apps.ext.gcxym3.dao;

import com.seeyon.ctp.util.DBAgent;
import com.seeyon.ctp.util.FlipInfo;

import java.util.Map;

public class Gcxym3DaoImpl implements Gcxym3Dao {

    @Override
    public FlipInfo getBgdhList(FlipInfo flipInfo, Map params) {
        StringBuffer sb = new StringBuffer();
        sb.append(" from Formmain0917 ");
        if (null != params) {
            if (null != params.get("input") && !"".equals(params.get("input"))) {
                sb.append(" where field0003 like :input or field0001 like :input or field0002 like :input or ");
                sb.append(" field0004 like :input or  field0005 like :input or  field0006 like :input or  field0007 like :input or field0009 like :input  ");
            }
        } else {
            sb.append(" where 1=1 ");
        }
        DBAgent.find(sb.toString(), params, flipInfo);
        return flipInfo;
    }

    @Override
    public FlipInfo getXntlList(FlipInfo flipInfo, Map params) {
        StringBuffer sb = new StringBuffer();
        sb.append(" from Formmain0918 ");
        if (null != params) {
            if (null != params.get("input") && !"".equals(params.get("input"))) {
                sb.append(" where field0001 like :input or field0002 like :input or field0003 like :input or field0004 like :input or field0005 like :input or field0006 like :input  ");
            }
        } else {
            sb.append(" where 1=1 ");
        }
        DBAgent.find(sb.toString(), params, flipInfo);
        return flipInfo;
    }
}
