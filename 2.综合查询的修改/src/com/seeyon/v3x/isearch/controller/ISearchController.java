package com.seeyon.v3x.isearch.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.seeyon.apps.doc.api.DocApi;
import com.seeyon.apps.doc.bo.DocLibBO;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.authenticate.domain.User;
import com.seeyon.ctp.common.constants.ApplicationCategoryEnum;
import com.seeyon.ctp.common.controller.BaseController;
import com.seeyon.ctp.util.CommonTools;
import com.seeyon.ctp.util.SQLWildcardUtil;
import com.seeyon.v3x.isearch.manager.ISearchManager;
import com.seeyon.v3x.isearch.manager.ISearchManagerInterface;
import com.seeyon.v3x.isearch.manager.ISearchManagerRegister;
import com.seeyon.v3x.isearch.model.ConditionModel;
import com.seeyon.v3x.isearch.model.ISearchAppObject;
import com.seeyon.v3x.isearch.model.ResultModel;

/**
 * 2008.03.17
 *
 * @author lihf
 * 综合查询Controller
 */
public class ISearchController extends BaseController {
    private DocApi docApi;

    public void setDocApi(DocApi docApi) {
        this.docApi = docApi;
    }

    /**
     * 进入综合查询首页
     */
    public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView ret = new ModelAndView("isearch/home");
        List<ISearchAppObject> appList = ISearchManagerRegister.getISearchAppObjectList();

        /*[工程学院综合查询，设定查询类型] 2021-03-01 zhou 修改 开始*/
        //1:协同，4：公文，78：是信息发布（新闻和公告）
        Integer[] categorys = new Integer[]{1, 4, 78};
        List<ISearchAppObject> appListNew = new ArrayList<>();
        appList.stream().forEach(is -> {
            if (null != is.getAppEnumKey()) {
                int key = is.getAppEnumKey().intValue();
                Arrays.asList(categorys).forEach(cate -> {
                    if (key == cate.intValue()) {
                        appListNew.add(is);
                    }
                });
            }
        });

        ISearchAppObject _78 = new ISearchAppObject();
        //公文
        _78.setAppEnumKey(4);
        _78.setHasPigeonholed(false);
        _78.setSortId(98);
        _78.setCustomContentType(false);
        _78.setNeedDocLibSelect(false);
        _78.setHasPigeonholed(true);
        appListNew.add(_78);
        //信息发布
        _78 = new ISearchAppObject();
        _78.setAppEnumKey(78);
        _78.setHasPigeonholed(false);
        _78.setSortId(99);
        _78.setCustomContentType(false);
        _78.setNeedDocLibSelect(false);
        _78.setHasPigeonholed(true);
        appListNew.add(_78);

        /*[工程学院综合查询，设定查询类型] 2021-03-01 zhou 修改 结束*/

        ret.addObject("appList", appListNew);
        User user = AppContext.getCurrentUser();
        if (AppContext.hasPlugin("doc")) {
            List<DocLibBO> libs = docApi.findDocLibs(user.getId(), user.getLoginAccount());
            ret.addObject("libs", libs);
        }
        ret.addObject("search", false);
        return ret;
    }

    /**
     * 综合查询主方法
     */
    public ModelAndView iSearch(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView ret = new ModelAndView("isearch/dataList");
        // 1. 返回类型
//		List<ISearchAppObject> appList = ISearchManagerRegister.getISearchAppObjectList();
//		ret.addObject("appList", appList);
//		User user = CurrentUser.get();
//		List<DocLib> libs = docLibManager.getDocLibsByUserIdNav(user.getId(), user.getLoginAccount());
//		ret.addObject("libs", libs);

        // 1. 组装条件
        ConditionModel cm = new ConditionModel();
        cm.setUser(AppContext.getCurrentUser());
        super.bind(request, cm);

        if (cm.getTitle() != null) {
            cm.setTitle(SQLWildcardUtil.escape(cm.getTitle()));
        }

        String pigeonholedFlag0 = request.getParameter("pigeonholedFlag0");
        cm.setPigeonholedFlag("1".equals(pigeonholedFlag0));

        if (cm.getEndDate() != null) {
            cm.setEndDate(new Timestamp(cm.getEndDate().getTime() + 24 * 60 * 60 * 1000 - 1));
        }

        ret.addObject("cm", cm);

//		if(cm.getBeginDate() != null)
//			ret.addObject("beginDateValue", Datetimes.formatDate(cm.getBeginDate()));
//		if(cm.getEndDate() != null)
//			ret.addObject("endDateValue", Datetimes.formatDate(cm.getEndDate()));

        ISearchAppObject appObject = ISearchManagerRegister.getAppObjByAppKey(cm.getAppKey());
        cm.setAppObj(appObject);
        ISearchManagerInterface manager = ISearchManagerRegister.getISearchManagerByAppKey(cm.getAppKey());
        if (appObject.isCustomContentType()) {
            manager = ISearchManagerRegister.getISearchManagerByAppKey(String.valueOf(ApplicationCategoryEnum.doc.key()));
        }
        //zhou:暂时注释掉
        if (cm.getPigeonholedFlag()) {//归档的单独处理
            manager = ISearchManagerRegister.getISearchManagerByAppKey(ISearchManager.ISEARCH_MANAGER_PIGEONHOLE_APPKEY);
        }
        if ("1".equals(cm.getAppKey()) && "2".equals(pigeonholedFlag0)) { //转储数据单独处理
            manager = ISearchManagerRegister.getISearchManagerByAppKey(ISearchManager.ISEARCH_MANAGER_DUMPDATA_APPKEY);
        }
        if (manager == null) {
            manager = ISearchManagerRegister.getISearchManagerByAppKey(String.valueOf(ApplicationCategoryEnum.doc.getKey()));
        }
        List<ResultModel> list = null;
        if (manager != null) {
            list = manager.iSearch(cm);
        }
        if ("11".equals(cm.getAppKey())) {//日程事件模块未分页
            list = CommonTools.pagenate(list);
        }
        //zhou:归档的单独处理
//        manager = ISearchManagerRegister.getISearchManagerByAppKey(ISearchManager.ISEARCH_MANAGER_PIGEONHOLE_APPKEY);
//		List<ResultModel> nList = null;
//		cm.setPigeonholedFlag(true);
//		if (manager != null) {
//			nList = manager.iSearch(cm);
//		}
        ret.addObject("list", list);
        // 2. 区分类型，找到争取的manager
        // 3. 调用对应manager实现分页查询
        // 4. 数据返回
        return ret;
    }

}
