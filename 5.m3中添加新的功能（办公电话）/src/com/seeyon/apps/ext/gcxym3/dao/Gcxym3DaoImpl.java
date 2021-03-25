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
            String key = (String) params.get("field0001");
            if (null != params.get("field0001") && !"".equals(key)) {
                sb.append(" where field0003 like '%'||:field0001||'%' or field0001 like '%'||:field0001||'%' or field0002 like '%'||:field0001||'%' or ");
                sb.append(" field0004 like '%'||:field0001||'%' or  field0005 like '%'||:field0001||'%' or  field0006 like '%'||:field0001||'%' or  field0007 like '%'||:field0001||'%' or field0009 like '%'||:field0001||'%'  ");
                DBAgent.find(sb.toString(), params, flipInfo);
            } else {
                sb.append(" where 1=2 ");
                DBAgent.find(sb.toString(), null, flipInfo);
            }
        } else {
            sb.append(" where 1=2 ");
            DBAgent.find(sb.toString(), null, flipInfo);
        }
        return flipInfo;
    }

    @Override
    public FlipInfo getXntlList(FlipInfo flipInfo, Map params) {
        StringBuffer sb = new StringBuffer();
        sb.append(" from Formmain0918 ");
        if (null != params) {
            String key = (String) params.get("field0001");
            if (null != params.get("field0001") && !"".equals(key)) {
                sb.append(" where field0001 like '%'||:field0001||'%' or field0002 like '%'||:field0001||'%' or field0003 like '%'||:field0001||'%' or field0004 like '%'||:field0001||'%' or field0005 like '%'||:field0001||'%' or field0006 like '%'||:field0001||'%'  ");
                DBAgent.find(sb.toString(), params, flipInfo);
            } else {
                sb.append(" where 1=2 ");
                DBAgent.find(sb.toString(), null, flipInfo);
            }
        } else {
            sb.append(" where 1=2 ");
            DBAgent.find(sb.toString(), params, flipInfo);
        }
        return flipInfo;
    }
}
