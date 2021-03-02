package com.seeyon.apps.index.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.apps.addressbook.constants.AddressbookConstants;
import com.seeyon.apps.addressbook.manager.AddressBookManager;
import com.seeyon.apps.addressbook.po.AddressBookSet;
import com.seeyon.apps.addressbook.webmodel.AddressBookMember;
import com.seeyon.apps.bbs.api.BbsApi;
import com.seeyon.apps.calendar.api.TimeViewApi;
import com.seeyon.apps.calendar.bo.TimeViewInfoBO;
import com.seeyon.apps.doc.api.DocApi;
import com.seeyon.apps.index.bo.ConfigHolder;
import com.seeyon.apps.index.bo.IndexInfo;
import com.seeyon.apps.index.bo.MultiSearchBuilder;
import com.seeyon.apps.index.util.IndexContext;
import com.seeyon.apps.index.util.IndexModule;
import com.seeyon.apps.index.util.IndexSearchHelper;
import com.seeyon.apps.index.vo.SearchResult;
import com.seeyon.apps.index.vo.SearchResultWapper;
import com.seeyon.apps.news.api.NewsApi;
import com.seeyon.apps.vreport.api.VReportApi;
import com.seeyon.apps.xiaoz.api.XiaozApi;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.affair.manager.AffairManager;
import com.seeyon.ctp.common.authenticate.domain.User;
import com.seeyon.ctp.common.constants.ApplicationCategoryEnum;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.i18n.ResourceUtil;
import com.seeyon.ctp.common.po.affair.CtpAffair;
import com.seeyon.ctp.common.taglibs.functions.Functions;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.dao.OrgHelper;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.util.CommonTools;
import com.seeyon.ctp.util.Datetimes;
import com.seeyon.ctp.util.FlipInfo;
import com.seeyon.ctp.util.Strings;
import com.seeyon.ctp.util.annotation.AjaxAccess;
import com.seeyon.ctp.util.json.JSONUtil;

public class IndexAjaxManagerImpl implements IndexAjaxManager {

    private static final Log log = LogFactory.getLog(IndexAjaxManagerImpl.class);

    private IndexInnerManager indexManager;
    private ConfigHolder configHolder;
    private IndexContext indexContext;
    private OrgManager orgManager;
    private AddressBookManager addressBookManager;
    private AffairManager affairManager;
    private VReportApi vreportApi;
    private DocApi docApi;
    private NewsApi newsApi;
    private BbsApi bbsApi;
    private TimeViewApi timeViewApi;
    private XiaozApi xiaozApi;

    public void setIndexManager(IndexInnerManager indexManager) {
        this.indexManager = indexManager;
    }

    public void setConfigHolder(ConfigHolder configHolder) {
        this.configHolder = configHolder;
    }

    public void setIndexContext(IndexContext indexContext) {
        this.indexContext = indexContext;
    }

    public void setOrgManager(OrgManager orgManager) {
        this.orgManager = orgManager;
    }

    public void setAddressBookManager(AddressBookManager addressBookManager) {
        this.addressBookManager = addressBookManager;
    }

    public void setAffairManager(AffairManager affairManager) {
        this.affairManager = affairManager;
    }

    public void setVreportApi(VReportApi vreportApi) {
        this.vreportApi = vreportApi;
    }

    public void setDocApi(DocApi docApi) {
        this.docApi = docApi;
    }

    public void setNewsApi(NewsApi newsApi) {
        this.newsApi = newsApi;
    }

    public void setBbsApi(BbsApi bbsApi) {
        this.bbsApi = bbsApi;
    }

    public void setTimeViewApi(TimeViewApi timeViewApi) {
        this.timeViewApi = timeViewApi;
    }

    public void setXiaozApi(XiaozApi xiaozApi) {
        this.xiaozApi = xiaozApi;
    }

    @AjaxAccess
    public List<String> searchHis() {
        return indexManager.findSearchHis(AppContext.currentUserId());
    }

    @AjaxAccess
    public int clearSearchHis() {
        return indexManager.clearSearchHis(AppContext.currentUserId());
    }

    @AjaxAccess
    public List<String> autoCompletion(String searchKeyword) {
        return indexManager.complete(searchKeyword);
    }

    @AjaxAccess
    public Map<String, Object> searchModule(Map<String, String> map) throws BusinessException {
        User user = AppContext.getCurrentUser();
        Map<String, Object> result = new HashMap<String, Object>();

        String searchModule = map.get("searchModule");
        if (Strings.isBlank(searchModule)) {
            searchModule = "all";
        }

        boolean isESIndex = indexManager.isESIndex();// 是否ES模式
        boolean showCondition = true;// 显示筛选条件
        List<IndexModule> allApp = new ArrayList<IndexModule>();
        List<Map<String, String>> relatedModules = new ArrayList<Map<String, String>>();

        Map<String, IndexModule> indexAllApp = indexContext.getIndexAllApp();
        if ("all".equals(searchModule)) {
            // 内容范围
            addApp(allApp, indexAllApp, "all");
            addApp(allApp, indexAllApp, ApplicationCategoryEnum.xiaoz.name());
            addApp(allApp, indexAllApp, "addressbook");
            addApp(allApp, indexAllApp, "application");
            addApp(allApp, indexAllApp, ApplicationCategoryEnum.collaboration.name());
            addApp(allApp, indexAllApp, ApplicationCategoryEnum.form.name());
            addApp(allApp, indexAllApp, ApplicationCategoryEnum.edoc.name());
            addApp(allApp, indexAllApp, ApplicationCategoryEnum.doc.name());
            addApp(allApp, indexAllApp, "video");
            addApp(allApp, indexAllApp, ApplicationCategoryEnum.news.name());
            addApp(allApp, indexAllApp, ApplicationCategoryEnum.bulletin.name());
            addApp(allApp, indexAllApp, ApplicationCategoryEnum.bbs.name());
            addApp(allApp, indexAllApp, ApplicationCategoryEnum.inquiry.name());
            addApp(allApp, indexAllApp, ApplicationCategoryEnum.leaderagenda.name());
            addApp(allApp, indexAllApp, ApplicationCategoryEnum.meeting.name());
            addApp(allApp, indexAllApp, ApplicationCategoryEnum.taskManage.name());
            addApp(allApp, indexAllApp, ApplicationCategoryEnum.calendar.name());
            addApp(allApp, indexAllApp, ApplicationCategoryEnum.plan.name());
            addApp(allApp, indexAllApp, ApplicationCategoryEnum.ThirdPartyIntegration.name());

            // 同部门的人
            Map<String, String> map42 = new HashMap<String, String>();
            map42.put("id", "deptMembers");
            map42.put("name", ResourceUtil.getString("index.module.sameDepartmentMember"));
            relatedModules.add(map42);
        } else if ("xiaoz".equals(searchModule)) {
            showCondition = false;
        } else if ("addressbook".equals(searchModule)) {
            showCondition = false;

            // 同部门的人
            Map<String, String> map42 = new HashMap<String, String>();
            map42.put("id", "deptMembers");
            map42.put("name", ResourceUtil.getString("index.module.sameDepartmentMember"));
            relatedModules.add(map42);
        } else if ("application".equals(searchModule)) {
            showCondition = false;
        } else if ("form".equals(searchModule)) {
//            if (isESIndex) {
//                // 表单模板-来自CDP
//            }
        } else if ("edoc".equals(searchModule)) {
//            if (isESIndex) {
//                // 公文模板-来自CDP
//            }
        } else if ("vreport".equals(searchModule)) {
            showCondition = false;

//            // 我关注的报表
//            Map<String, String> map41 = new HashMap<String, String>();
//            map41.put("id", "attentionReport");
//            map41.put("name", "我关注的报表");
//            relatedModules.add(map41);
//
//            List<Map<String, Object>> reports = vreportApi.findAttentionReport(user, "PC");
//            if (Strings.isNotEmpty(reports) && reports.size() > 5) {
//                reports = reports.subList(0, 5);
//            }
//            result.put("attentionReport", reports);
        } else if ("doc".equals(searchModule)) {
//            if (isESIndex) {
//                // 文档库
//                List<DocLibBO> docLibs = docApi.findDocLibs(user.getId(), user.getLoginAccount());
//                result.put("docLibs", docLibs);
//
//                // 文档格式
//                List<IndexModule> docFormats = indexContext.getIndexDocFormat();
//                result.put("docFormats", docFormats);
//            }
        } else if ("culture".equals(searchModule)) {
            // 内容范围
            allApp.add(indexAllApp.get("all"));
            addApp(allApp, indexAllApp, ApplicationCategoryEnum.news.name());
            addApp(allApp, indexAllApp, ApplicationCategoryEnum.bulletin.name());
            addApp(allApp, indexAllApp, ApplicationCategoryEnum.bbs.name());
            addApp(allApp, indexAllApp, ApplicationCategoryEnum.inquiry.name());

//            // 最新新闻
//            Map<String, String> map41 = new HashMap<String, String>();
//            map41.put("id", "latestNews");
//            map41.put("name", "最新新闻");
//            relatedModules.add(map41);
//
//            List<NewsDataBO> newsDatas = newsApi.findMyNewsDatas(0, 5);
//            result.put("latestNews", newsDatas);
//
//            // 最新发帖
//            Map<String, String> map43 = new HashMap<String, String>();
//            map43.put("id", "latestBbs");
//            map43.put("name", "最新发帖");
//            relatedModules.add(map43);
//
//            List<BbsArticleBO> bbsArticles = bbsApi.findMyBbsArticles(0, 5);
//            result.put("latestBbs", bbsArticles);
        } else if ("schedule".equals(searchModule)) {
            // 内容范围
            allApp.add(indexAllApp.get("all"));
            addApp(allApp, indexAllApp, ApplicationCategoryEnum.leaderagenda.name());
            addApp(allApp, indexAllApp, ApplicationCategoryEnum.meeting.name());
            addApp(allApp, indexAllApp, ApplicationCategoryEnum.taskManage.name());
            addApp(allApp, indexAllApp, ApplicationCategoryEnum.calendar.name());
            addApp(allApp, indexAllApp, ApplicationCategoryEnum.plan.name());
        }

        // 大家都在搜
        Map<String, String> map43 = new HashMap<String, String>();
        map43.put("id", "hotWord");
        map43.put("name", ResourceUtil.getString("index.module.everybodySearchingWord"));
        relatedModules.add(map43);

        List<String> hotWord = indexManager.searchHotWord();
        result.put("hotWord", hotWord);

        result.put("showCondition", showCondition);
        result.put("allApp", allApp);
        result.put("relatedModules", relatedModules);

        return result;
    }

    public void addApp(List<IndexModule> allApp, Map<String, IndexModule> indexAllApp, String appEnum) {
        if (indexAllApp.get(appEnum) != null) {
            if (ApplicationCategoryEnum.xiaoz.name().equals(appEnum)) {
                IndexEnable e = IndexContext.getIndexEnableMap().get(ApplicationCategoryEnum.xiaoz.key());
                if (e != null && e.isEnable()) {
                    allApp.add(indexAllApp.get(appEnum));
                }
            } else {
                allApp.add(indexAllApp.get(appEnum));
            }
        }
    }

    @AjaxAccess
    public Map<String, Object> searchXiaoz(Map<String, String> map) throws BusinessException {
        if (AppContext.hasPlugin("xiaoz")) {
            if (xiaozApi != null) {
                if (xiaozApi.checkQAPermission()) {
                    String searchKeyword = map.get("searchKeyword");
                    return xiaozApi.indexSearchQa(searchKeyword);
                }
            }
        }
        return new HashMap<String, Object>();
    }

    @AjaxAccess
    public Map<String, Object> searchAddressbook(Map<String, String> map) throws BusinessException {
        User user = AppContext.getCurrentUser();
        Map<String, Object> result = new HashMap<String, Object>();

        String searchKeyword = map.get("searchKeyword");
        String currentTab = map.get("currentTab");

        String pageStr = map.get("page");
        if (Strings.isBlank(pageStr)) {
            pageStr = "1";
        }
        int page = Integer.parseInt(pageStr);
        int pageSize = NumberUtils.toInt(map.get("pageSize"), 15);

        FlipInfo fi = new FlipInfo();
        if ("all".equals(currentTab)) {// 全部页签all
            fi.setPage(1);
            fi.setSize(6);
        } else {// 通讯录页签addressbook
            fi.setPage(page);
            fi.setSize(pageSize);
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("key", searchKeyword);
        params.put("type", "Name,Telnum");
        params.put("accId", "-1");
        List<AddressBookMember> list = new ArrayList<AddressBookMember>();
        List<AddressBookMember> temp = addressBookManager.searchMembers(params, fi);
        for (AddressBookMember a : temp) {
            AddressBookSet accountAddressBookSet = addressBookManager.getAddressbookSetByAccountId(a.getAId());
            if (a.getBy().equals("Telnum")) {// 通过手机号码查出来的
                if (accountAddressBookSet.getDisplayColumn().indexOf("memberMobile") == -1) {// 不允许显示手机号码字段
                    continue;
                }
            } else {// 通过姓名或其他方式查出来的查出来的
                if (accountAddressBookSet.getDisplayColumn().indexOf("memberMobile") == -1) {// 不允许显示手机号码字段
                    a.settNm("");
                } else if (!addressBookManager.checkPhone(AppContext.currentUserId(), a.getI(), a.getAId(), accountAddressBookSet)) {
                    // 手机号码隐藏了
                    a.settNm(AddressbookConstants.ADDRESSBOOK_INFO_REPLACE);
                }

            }

            if (accountAddressBookSet.getDisplayColumn().indexOf("memberPost") == -1) {// 不允许显示岗位
                a.setPN("");
            }

            list.add(a);
        }

        if (Strings.isNotEmpty(list)) {
            result.put("memberList", list);

            result.put("totalCount", fi.getTotal());
            result.put("pageSize", pageSize);
            result.put("totalPage", fi.getPages());
            result.put("currentPage", page);

            if (list.size() == 1) {
                AddressBookMember member = list.get(0);

                // XXX同部门的人
                List<AddressBookMember> deptMembers = new ArrayList<AddressBookMember>();
                List<V3xOrgMember> list1 = orgManager.getMembersByDepartment(member.getDId(), true);
                for (V3xOrgMember v3xOrgMember : list1) {
                    if (!v3xOrgMember.getId().equals(member.getI())) {
                        AddressBookMember addressBookMember = new AddressBookMember();
                        addressBookMember.setI(v3xOrgMember.getId());
                        addressBookMember.setN(v3xOrgMember.getName());
                        addressBookMember.setDId(v3xOrgMember.getOrgDepartmentId());
                        addressBookMember.setImg(OrgHelper.getAvatarImageUrl(v3xOrgMember.getId()));
                        deptMembers.add(addressBookMember);
                    }
                }
                result.put("deptMembers", deptMembers);

                List<CtpAffair> receiveList = this.getReceiveOrSend(member.getI(), user.getId(), true, 5);
                List<CtpAffair> sendList = this.getReceiveOrSend(user.getId(), member.getI(), false, 5);

                // XXX发/转给我的协同
                result.put("receiveList", receiveList);
                // 我发/转给XXX的协同
                result.put("sendList", sendList);

                // XXX的日程
                if (timeViewApi != null) {
                    List<Integer> status = new ArrayList<Integer>();
                    status.add(0);
                    status.add(1);
                    List<ApplicationCategoryEnum> apps = new ArrayList<ApplicationCategoryEnum>();
                    apps.add(ApplicationCategoryEnum.taskManage);
                    apps.add(ApplicationCategoryEnum.plan);
                    apps.add(ApplicationCategoryEnum.calendar);
                    apps.add(ApplicationCategoryEnum.meeting);
                    Date date = new Date();
                    Date startDate = Datetimes.getFirstDayInWeek(date);
                    Date endDate = Datetimes.getLastDayInWeek(date);
                    List<TimeViewInfoBO> list2 = timeViewApi.findTimeViewInfo4Index(member.getI(), startDate, endDate, status, apps);
                    result.put("timeList", list2);
                }
            }
        }

        return result;
    }

    private List<CtpAffair> getReceiveOrSend(Long memberId, Long relatedId, boolean isReceive, int size) throws BusinessException {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("senderId", memberId);
        map.put("memberId", relatedId);
        map.put("isReceive", isReceive);
        if (isReceive) {
            map.put("delete", "0");
        }
        FlipInfo flipInfo = null;
        if (size > 0) {
            flipInfo = new FlipInfo();
            flipInfo.setPage(1);
            flipInfo.setSize(size);
            flipInfo.setNeedTotal(false);
        }

        List<CtpAffair> list = affairManager.getWorkflowRelatedAffairs(flipInfo, map);
        return list;
    }

    @AjaxAccess
    public Map<String, Object> searchMultiApp(Map<String, String> map) throws BusinessException {
        User user = AppContext.getCurrentUser();
        Map<String, Object> rv = new HashMap<String, Object>();
        try {
            // 内容范围
            String searchApp = map.get("searchApp");
            String[] libs = searchApp.split(",");
            if (libs.length < 1) {
                return rv;
            }

            String key = map.get("searchKeyword");
            String regex = "(^(\\s|\\*)*)|(\\s*$)";// 与前端的正则保持一致
            if (StringUtils.isBlank(key) || StringUtils.isBlank(key.replaceAll(regex, ""))) {
                key = "";
            }
            if (StringUtils.isBlank(key)) {
                return rv;
            }
            String originalKey = key;
            key = IndexSearchHelper.replaceSearchKey(key);

            // 搜索域
            String searchArea = map.get("searchArea");
            if (Strings.isBlank(searchArea)) {
                searchArea = "all";
            }
            boolean isAll = "all".equals(searchArea);// 全部
            boolean isTitle = "title".equals(searchArea);// 标题
            boolean isAccessory = "accessory".equals(searchArea);// 附件

            // 搜索时间
            String searchTime = map.get("searchTime");
            if (Strings.isBlank(searchTime)) {
                searchTime = "all";
            }
            String startTime = "";
            String endTime = "";
            Date date1 = new Date();
            Date date2 = Datetimes.getTodayLastTime();
            if ("all".equals(searchTime)) {

            } else if ("custom".equals(searchTime)) {// 自定义
                startTime = map.get("startTime");
                endTime = map.get("endTime");
            } else {
                if ("oneweek".equals(searchTime)) {// 一周内
                    date1 = Datetimes.addDate(date2, -7);
                } else if ("onemonth".equals(searchTime)) {// 一个月内
                    date1 = Datetimes.addMonth(date2, -1);
                } else if ("threemonth".equals(searchTime)) {// 三个月内
                    date1 = Datetimes.addMonth(date2, -3);
                } else if ("halfyear".equals(searchTime)) {// 半年内
                    date1 = Datetimes.addMonth(date2, -6);
                } else if ("oneyear".equals(searchTime)) {// 一年内
                    date1 = Datetimes.addYear(date2, -1);
                }
                startTime = Datetimes.formatDate(date1);
                endTime = Datetimes.formatDate(date2);
            }

            // 发起人
            String author = map.get(IndexInfo.APP_AUTHOR);

            String authorKey = (String) AppContext.getSessionContext("Index_AuthorKey");
            if (Strings.isBlank(authorKey)) {
                authorKey = IndexSearchHelper.getAuthorKey();
            }

            MultiSearchBuilder multiSearchBuilder = new MultiSearchBuilder();
            for (int i = 0; i < libs.length; i++) {
                Map<String, String> extendProperties = new HashMap<String, String>();
                String lib = libs[i];
                String[] newLibs = new String[] { lib };
                if ("portal".equals(lib)) {// 门户空间
                    List<String> apps = new ArrayList<String>();
                    apps.add(ApplicationCategoryEnum.cap4business.name());
                    apps.add(ApplicationCategoryEnum.portal.name());
                    newLibs = apps.toArray(new String[apps.size()]);
                } else if ("video".equals(lib)) {// 视频
                    List<String> apps = new ArrayList<String>();
                    apps.add(ApplicationCategoryEnum.doc.name());
                    newLibs = apps.toArray(new String[apps.size()]);
                    extendProperties.put("docType", "131");
                }

                Map<String, Object> keyMap = new HashMap<String, Object>();// 用于放置查询关键字的Map
                keyMap.put(IndexInfo.PARAMETER_KEYWORD_ORIGINAL, originalKey);// 用于存储历史检索数据
                if (isAll) {
                    keyMap.put(IndexInfo.PARAMETER_KEYWORD, key);
                }
                if (isTitle) {
                    keyMap.put(IndexInfo.TITLE, key);
                }
                if (isAccessory) {
                    keyMap.put(IndexInfo.ACCES_NAME, key);
                }
                keyMap.put(IndexInfo.PARAMETER_SEARCHDATE_BEGIN, startTime);
                keyMap.put(IndexInfo.PARAMETER_SEARCHDATE_END, endTime);
                if (Strings.isNotBlank(author)) {
                    keyMap.put(IndexInfo.APP_AUTHOR, author);
                }
                keyMap.put("extendProperties", extendProperties);
                keyMap.put(IndexInfo.SORT_TYPE, "2");// 1为时间,2为相关度
                keyMap.put("currentUserId", String.valueOf(user.getId()));
                String currentTab = map.get("currentTab");
                if ("all".equals(currentTab)) {// 全部页签下最多6条，多查1条是为了显示更多按钮
                    keyMap.put("pageSize", "7");
                } else {
                    keyMap.put("pageSize", "1000");
                }

                multiSearchBuilder.add(authorKey, keyMap, newLibs, 0);
            }

            List<SearchResultWapper> list = indexManager.multiSearch(multiSearchBuilder);
            for (int j = 0; j < libs.length; j++) {
                SearchResult[] results = null;
                if (list != null) {
                    SearchResultWapper searchResultWapper = list.get(j);
                    if (searchResultWapper != null) {
                        results = searchResultWapper.getSearchResults();
                        if (ArrayUtils.isNotEmpty(results)) {
                            buildSearchResult(results);
                        }
                    }
                }
                rv.put(libs[j], CommonTools.newHashMap("searchResults", results));
            }
        } catch (Exception e) {
            log.error("", e);
        }

        return rv;
    }

    @AjaxAccess
    public Map<String, Object> searchApp(Map<String, String> map) throws BusinessException {
        User user = AppContext.getCurrentUser();
        Map<String, Object> rv = new HashMap<String, Object>();
        try {
            Map<String, String> extendProperties = new HashMap<String, String>();
            // 内容范围
            String searchApp = map.get("searchApp");
            String[] libs = null;
            if (Strings.isBlank(searchApp) || "all".equals(searchApp)) {
                List<String> allApp = new ArrayList<String>();
                List<String> indexNames = indexContext.getIndexAllAppName();
                for (String string : indexNames) {
                    if (ApplicationCategoryEnum.xiaoz.name().equals(string) 
                            || ApplicationCategoryEnum.cap4business.name().equals(string) 
                            || ApplicationCategoryEnum.portal.name().equals(string)
                            || ApplicationCategoryEnum.template.name().equals(string) 
                            || ApplicationCategoryEnum.vreport.name().equals(string)) {
                        continue;
                    }
                    allApp.add(string);
                }
                libs = allApp.toArray(new String[allApp.size()]);
            } else if ("application".equals(searchApp)) {// 应用
                List<String> allApp = new ArrayList<String>();
                allApp.add(ApplicationCategoryEnum.cap4business.name());
                allApp.add(ApplicationCategoryEnum.portal.name());
                allApp.add(ApplicationCategoryEnum.template.name());
                allApp.add(ApplicationCategoryEnum.vreport.name());
                libs = allApp.toArray(new String[allApp.size()]);
            } else if ("video".equals(searchApp)) {// 视频
                List<String> allApp = new ArrayList<String>();
                allApp.add(ApplicationCategoryEnum.doc.name());
                libs = allApp.toArray(new String[allApp.size()]);
                extendProperties.put("docType", "131");
            } else {
                libs = searchApp.split(",");
            }

            // 搜索域
            String searchArea = map.get("searchArea");
            if (Strings.isBlank(searchArea)) {
                searchArea = "all";
            }
            boolean isAll = "all".equals(searchArea);// 全部
            boolean isTitle = "title".equals(searchArea);// 标题
            boolean isAccessory = "accessory".equals(searchArea);// 附件

            // 搜索时间
            String searchTime = map.get("searchTime");
            if (Strings.isBlank(searchTime)) {
                searchTime = "all";
            }
            String startTime = "";
            String endTime = "";
            Date date1 = new Date();
            Date date2 = Datetimes.getTodayLastTime();
            if ("all".equals(searchTime)) {

            } else if ("custom".equals(searchTime)) {// 自定义
                startTime = map.get("startTime");
                endTime = map.get("endTime");
            } else {
                if ("oneweek".equals(searchTime)) {// 一周内
                    date1 = Datetimes.addDate(date2, -7);
                } else if ("onemonth".equals(searchTime)) {// 一个月内
                    date1 = Datetimes.addMonth(date2, -1);
                } else if ("threemonth".equals(searchTime)) {// 三个月内
                    date1 = Datetimes.addMonth(date2, -3);
                } else if ("halfyear".equals(searchTime)) {// 半年内
                    date1 = Datetimes.addMonth(date2, -6);
                } else if ("oneyear".equals(searchTime)) {// 一年内
                    date1 = Datetimes.addYear(date2, -1);
                }
                startTime = Datetimes.formatDate(date1);
                endTime = Datetimes.formatDate(date2);
            }

            // 发起人
            String author = map.get(IndexInfo.APP_AUTHOR);

            String pageStr = map.get("page");
            if (Strings.isBlank(pageStr)) {
                pageStr = "1";
            }
            int page = Integer.parseInt(pageStr);
            int pageSize = NumberUtils.toInt(map.get("pageSize"), configHolder.getSizeOfPage());
            if ("application".equals(searchApp)) {// 应用
                pageSize = 100;
            }
            int firstResult = (page - 1) * pageSize;

            String key = map.get("searchKeyword");
            String regex = "(^(\\s|\\*)*)|(\\s*$)";// 与前端的正则保持一致
            if (StringUtils.isBlank(key) || StringUtils.isBlank(key.replaceAll(regex, ""))) {
                key = "";
            }
            if (StringUtils.isBlank(key)) {
                return rv;
            }

            String originalKey = key;
            Map<String, Object> keyMap = new HashMap<String, Object>();// 用于放置查询关键字的Map
            String currentTab = map.get("currentTab");
            if ("form".equals(currentTab)) {// 表单页签下才搜索无流程表单
                keyMap.put("__unflow", "true");
            }
            keyMap.put(IndexInfo.PARAMETER_KEYWORD_ORIGINAL, originalKey);// 用于存储历史检索数据
            key = IndexSearchHelper.replaceSearchKey(key);
            if (isAll) {
                keyMap.put(IndexInfo.PARAMETER_KEYWORD, key);
            }
            if (isTitle) {
                keyMap.put(IndexInfo.TITLE, key);
            }
            if (isAccessory) {
                keyMap.put(IndexInfo.ACCES_NAME, key);
            }
            keyMap.put(IndexInfo.PARAMETER_SEARCHDATE_BEGIN, startTime);
            keyMap.put(IndexInfo.PARAMETER_SEARCHDATE_END, endTime);
            if (Strings.isNotBlank(author)) {
                keyMap.put(IndexInfo.APP_AUTHOR, author);
            }
            keyMap.put("extendProperties", extendProperties);
            keyMap.put(IndexInfo.SORT_TYPE, "2");// 1为时间,2为相关度
            keyMap.put("currentUserId", String.valueOf(user.getId()));
            keyMap.put("pageSize", pageSize + "");
            String authorKey = (String) AppContext.getSessionContext("Index_AuthorKey");
            if (Strings.isBlank(authorKey)) {
                authorKey = IndexSearchHelper.getAuthorKey();
            }
            SearchResultWapper resultWapper = indexManager.search(authorKey, keyMap, libs, firstResult);
            SearchResult[] results = resultWapper.getSearchResults();
            int totalCount = resultWapper.getResultCount();
            pageCountHandle(rv, page, pageSize, totalCount, results);
            Boolean hasResult = false;
            if (ArrayUtils.isNotEmpty(results)) {
                hasResult = true;
                buildSearchResult(results);
            }
            rv.put("hasResult", hasResult);
            rv.put("searchResults", results);
        } catch (Exception e) {
            log.error("", e);
        }

        return rv;
    }

    public void buildSearchResult(SearchResult[] results) {
        // 避免前台summary字段出现null
        // 避免summary过长(单字母或数字中间没有空格时,会造成页面拉伸)
        if (ArrayUtils.isNotEmpty(results)) {
            for (SearchResult result : results) {
                result.setSummary(replaceSpecial(result.getSummary()));
                result.setOpinionSummary(replaceSpecial(result.getOpinionSummary()));
                result.setCommentSummary(replaceSpecial(result.getCommentSummary()));
                result.setAccessorySummary(replaceSpecial(result.getAccessorySummary()));
                result.setAccessoryNames(replaceSpecial(result.getAccessoryNames()));

                Integer appInt = Integer.parseInt(result.getAppType());
                result.setAppTypeStr(ApplicationCategoryEnum.valueOf(appInt).name());

                String docType = result.getExtendProperties().get("docType");
                if (appInt == ApplicationCategoryEnum.doc.key() && Strings.isNotBlank(docType)) {
                    result.setIcon(docApi.getDocIconFont(Long.parseLong(docType)));
                } else {
                    result.setIcon(result.getAppTypeStr());
                }

                try {
                    boolean isShowIndexSummary = IndexContext.getIndexEnableMap().get(appInt).isShowIndexSummary(Long.parseLong(result.getId()), result.getExtendProperties());
                    if (!isShowIndexSummary) { // 不显示摘要：无流程表单
                        result.setSummary("");
                        result.setOpinionSummary("");
                        result.setCommentSummary("");
                        result.setAccessorySummary("");
                        result.setAccessoryNames("");
                    }
                    result.setShowIndexSummary(isShowIndexSummary);
                } catch (Exception e1) {
                    log.error("isShowIndexSummary:" + appInt, e1);
                }

                result.setShowMember("false");
                if (result.getStartMemberId() != null) {
                    if ("-1".equals(result.getStartMemberId())) {
                        result.setStartMember(ResourceUtil.getString("message.sender.anonymous"));
                    } else {
                        Long memberId = Long.valueOf(result.getStartMemberId());
                        try {
                            V3xOrgMember member = orgManager.getMemberById(memberId);
                            if (member != null && member.isValid()) {
                                result.setShowMember("true");
                                result.setStartMember(member.getName());// 重置一下防止应用串改
                            }
                        } catch (Exception e) {
                            log.error("", e);
                        }
                    }
                }

                String showMember = result.getExtendProperties().get("showMember");
                if (Strings.isNotBlank(showMember)) {
                    List<Map<String, String>> showMemberList = new ArrayList<Map<String, String>>();
                    String[] showMembers = showMember.split(",");
                    for (String member : showMembers) {
                        Map<String, String> lMap = new HashMap<String, String>();
                        lMap.put("id", member);
                        lMap.put("name", Functions.showMemberNameOnly(Long.parseLong(member)));
                        showMemberList.add(lMap);
                    }
                    result.getExtendProperties().put("showMemberList", JSONUtil.toJSONString4Ajax(showMemberList));
                }

                if (Strings.isNotBlank(result.getCreateDate())) {
                    String createDate = Datetimes.format(Datetimes.parseNoTimeZone(result.getCreateDate(), Datetimes.datetimeStyle), Datetimes.datetimeWithoutSecondStyle);
                    result.setCreateDate(createDate);
                    result.setCreateDateFile(StringUtils.substring(createDate, 0, 10));
                }

                if (Strings.isNotBlank(result.getStartTime())) {
                    result.setStartTime(Datetimes.format(Datetimes.parseNoTimeZone(result.getStartTime(), Datetimes.datetimeStyle), Datetimes.datetimeWithoutSecondStyle));
                }
                if (Strings.isNotBlank(result.getEndTime())) {
                    result.setEndTime(Datetimes.format(Datetimes.parseNoTimeZone(result.getEndTime(), Datetimes.datetimeStyle), Datetimes.datetimeWithoutSecondStyle));
                }

                if (appInt == ApplicationCategoryEnum.doc.getKey()) {
                    try {
                        Map<String, Object> map1 = IndexContext.getIndexEnableMap().get(appInt).findSourceInfo(Long.parseLong(result.getId()));
                        result.setFolderId(String.valueOf(map1.get(IndexEnable.FOLDER_ID)));
                        if (Strings.isBlank(result.getDocPath())) {
                            result.setDocPath(String.valueOf(map1.get(IndexEnable.SOURCE_PATH)));
                        }
                    } catch (Exception e2) {
                        log.error("", e2);
                    }
                }

                result.setSourceName(ResourceUtil.getString("index.application." + appInt + ".label"));
                result.setSourceUrl("");
                if (appInt == ApplicationCategoryEnum.news.key()) {
                    if (Strings.isNotBlank(result.getTypeName())) {
                        result.setSourceName(result.getTypeName());
                    }
                    result.setSourceUrl("/newsData.do?method=newsIndex&boardId=" + result.getTypeId());
                } else if (appInt == ApplicationCategoryEnum.bulletin.key()) {
                    if (Strings.isNotBlank(result.getTypeName())) {
                        result.setSourceName(result.getTypeName());
                    }
                    result.setSourceUrl("/bulData.do?method=bulIndex&typeId=" + result.getTypeId());
                } else if (appInt == ApplicationCategoryEnum.bbs.key()) {
                    if (Strings.isNotBlank(result.getTypeName())) {
                        result.setSourceName(result.getTypeName());
                    }
                    
                    String spaceType = result.getExtendProperties().get("spaceType");
                    if (!"12".equals(spaceType)) {
                        result.setSourceUrl("/bbs.do?method=bbsIndex&boardId=" + result.getTypeId());
                    }
                } else if (appInt == ApplicationCategoryEnum.inquiry.key()) {
                    if (Strings.isNotBlank(result.getTypeName())) {
                        result.setSourceName(result.getTypeName());
                    }
                    result.setSourceUrl("/inquiryData.do?method=inquiryBoardIndex&boardId=" + result.getTypeId());
                } else if (appInt == ApplicationCategoryEnum.doc.key()) {
                    if (Strings.isNotBlank(result.getDocPath())) {
                        result.setSourceName(result.getDocPath());
                    }
                    result.setSourceUrl("message.link.doc.folder.open|" + result.getFolderId());
                } else if (appInt == ApplicationCategoryEnum.leaderagenda.key()) {
                    result.setSourceUrl("/leaderAgenda.do?method=agendaIndex");
                } else if (appInt == ApplicationCategoryEnum.meeting.key()) {
                    result.setSourceUrl("/meeting.do?method=home&list=pending");
                } else if (appInt == ApplicationCategoryEnum.taskManage.key()) {
                    result.setSourceUrl("/projectandtask.do?method=projectAndTaskIndex&pageType=task");
                } else if (appInt == ApplicationCategoryEnum.calendar.key()) {
                    result.setSourceUrl("/calendar/calEvent.do?method=calEventIndex");
                } else if (appInt == ApplicationCategoryEnum.plan.key()) {
                    result.setSourceUrl("/plan/plan.do?method=planListHome");
                }
            }
        }
    }

    public String replaceSpecial(String content) {
        if (Strings.isNotBlank(content)) {
            content = content.replaceAll("<br/>", "");
        }
        if (StringUtils.isBlank(content) || StringUtils.equals("null", content)) {
            content = "";
        } else if (!StringUtils.contains(content, "&nbsp") && !StringUtils.contains(content, " ")) {
            content = StringUtils.abbreviate(content, 40) + "...";
        }
        return content;
    }

    /*
     * @page:当前要求页
     * 
     * @resultWapper:搜索的结果集
     * 
     * @mv:返回的MV 在此进行分页计算，总页数，是否显现上一页、下一页
     */
    private void pageCountHandle(Map<String, Object> rv, int page, int pageSize, int totalCount, SearchResult[] results) {
        if (totalCount > 10000) {
            totalCount = 10000;
        }
        Boolean showPrePage = false;
        Boolean showNextPage = false;
        boolean firstPage = false;
        boolean lastPage = false;

        int prePage = 0;
        int nextPage = 0;
        if (pageSize <= 0 || pageSize > 999) {
            pageSize = configHolder.getSizeOfPage();
        }
        if (totalCount < (page - 1) * pageSize) {
            page = 1;
        }
        int lastResult = (page - 1) * pageSize + (results != null ? results.length : 0);

        int totalPage = totalCount % pageSize == 0 ? totalCount / pageSize : totalCount / pageSize + 1;
        if ((page - 1) >= 1) {
            showPrePage = true;
            prePage = page - 1;
        }
        if ((page + 1) <= totalPage) {
            showNextPage = true;
            nextPage = page + 1;
        }

        if (totalPage != 1 && page != 1) {
            firstPage = true;
        }
        if (totalPage != 0 && totalPage != 1 && page != totalPage) {
            lastPage = true;
        }
        rv.put("prePage", prePage);
        rv.put("nextPage", nextPage);
        rv.put("showPrePage", showPrePage);
        rv.put("showNextPage", showNextPage);
        rv.put("currentPage", page);
        rv.put("totalPage", totalPage);
        rv.put("pageSize", pageSize);
        rv.put("totalCount", totalCount);
        rv.put("firstResult", (page - 1) * pageSize + 1);
        rv.put("lastResult", lastResult);
        rv.put("firstPage", firstPage);
        rv.put("lastPage", lastPage);
    }

}
