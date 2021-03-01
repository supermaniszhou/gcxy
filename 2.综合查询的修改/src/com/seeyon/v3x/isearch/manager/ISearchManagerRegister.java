package com.seeyon.v3x.isearch.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;

import com.seeyon.apps.doc.api.DocApi;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.SystemInitializer;
import com.seeyon.ctp.common.constants.ApplicationCategoryEnum;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.log.CtpLogFactory;
import com.seeyon.ctp.dubbo.RefreshInterfacesAfterUpdate;
import com.seeyon.ctp.util.Strings;
import com.seeyon.v3x.isearch.model.ISearchAppObject;

public class ISearchManagerRegister implements SystemInitializer {
    private static final Log log = CtpLogFactory.getLog(ISearchManagerRegister.class);
    // 综合查询类型map Map<typeName, ISearchManager实现>
    private static Map<String, ISearchManagerInterface> isearchTypesManagerMap = new ConcurrentHashMap<String, ISearchManagerInterface>();

    @Override
    @RefreshInterfacesAfterUpdate(inface = ISearchManagerInterface.class)
    public void initialize() {
        long start = System.currentTimeMillis();
        Map<String, ISearchManagerInterface> beans = AppContext.getBeansOfType(ISearchManagerInterface.class);
        Set<Map.Entry<String, ISearchManagerInterface>> enities = beans.entrySet();

        for (Map.Entry<String, ISearchManagerInterface> entry : enities) {
            ISearchManagerInterface search = entry.getValue();
            try {
                Integer appEnum = search.getAppEnumKey();
                isearchTypesManagerMap.put(appEnum != null ? appEnum.toString() : search.getAppShowName(), search);
            } catch (Exception e) {
                log.error("", e);
            }

        }
        log.info("加载所有综合查询信息ISearchManager: " + isearchTypesManagerMap.size() + ". 耗时: " + (System.currentTimeMillis() - start) + " MS");
    }

    private static Map<String, ISearchAppObject> getIsearchTypesMap() {
        Map<String, ISearchAppObject> isearchTypesMap = new HashMap<String, ISearchAppObject>();
        Map<String, ISearchManagerInterface> beans = AppContext.getBeansOfType(ISearchManagerInterface.class);
        Set<Map.Entry<String, ISearchManagerInterface>> enities = beans.entrySet();

        for (Map.Entry<String, ISearchManagerInterface> entry : enities) {
            ISearchManagerInterface search = entry.getValue();
            try {
                if (search.isEnabled()) {
                    Integer appEnum = search.getAppEnumKey();
                    ISearchAppObject appObj = new ISearchAppObject(appEnum, search.getAppShowName(), search.getSortId(), null, false);
                    setPigFlag(appObj);
                    String appKey = appObj.getAppEnumKey() != null ? appObj.getAppEnumKey().toString() : appObj.getAppShowName();
                    isearchTypesMap.put(appKey, appObj);
                }

                List<ISearchAppObject> list = search.getOtherISearchAppObject();
                if (Strings.isNotEmpty(list)) {
                    for (ISearchAppObject object : list) {
                        setPigFlag(object);
                        String appKey1 = object.getAppEnumKey() != null ? object.getAppEnumKey().toString() : object.getAppShowName();
                        isearchTypesMap.put(appKey1, object);
                    }
                }
            } catch (Exception e) {
                log.error("", e);
            }
        }
        return isearchTypesMap;
    }

    private static void setPigFlag(ISearchAppObject appObj) {
        appObj.setHasPigeonholed(false);
        if (appObj.getAppEnumKey() != null && AppContext.hasPlugin("doc")) {
            DocApi docApi = (DocApi) AppContext.getBean("docApi");
            try {
                appObj.setHasPigeonholed(docApi.canPigeonhole(appObj.getAppEnumKey()));
            } catch (BusinessException e) {
                log.error("", e);
            }
        }
    }

    /**
     * 返回综合查询类型
     */
    public static List<ISearchAppObject> getISearchAppObjectList() {
        Map<String, ISearchAppObject> isearchTypesMap = getIsearchTypesMap();
        boolean edocEnabled = AppContext.hasPlugin("edoc");
        List<ISearchAppObject> ret = new ArrayList<ISearchAppObject>();
        for (ISearchAppObject isao : isearchTypesMap.values()) {
            String appKey = isao.getAppEnumKey() != null ? isao.getAppEnumKey().toString() : isao.getAppShowName();
            if (ISearchManager.ISEARCH_MANAGER_PIGEONHOLE_APPKEY.equals(appKey)) {
                continue;
            }
            if (ISearchManager.ISEARCH_MANAGER_DUMPDATA_APPKEY.equals(appKey)) {
                continue;
            }

            if ((!edocEnabled && (isao.getAppEnumKey() == null || isao.getAppEnumKey() != ApplicationCategoryEnum.edoc.key())) || edocEnabled) {
                ret.add(isao);
            }
        }

        Collections.sort(ret);

        return ret;
    }

    /**
     * 得到对应的ISearchManager
     */
    public static ISearchManagerInterface getISearchManagerByAppKey(String appKey) {
        return isearchTypesManagerMap.get(appKey);
    }

    /**
     * 得到对应的ISearchAppObj
     */
    public static ISearchAppObject getAppObjByAppKey(String appKey) {
        Map<String, ISearchAppObject> isearchTypesMap = getIsearchTypesMap();
        return isearchTypesMap.get(appKey);
    }

    @Override
    public void destroy() {
        log.info("destory");
    }
}
