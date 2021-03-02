/**
 * $Author: huangfj $
 * $Rev: 155 $
 * $Date:: 2012-07-09 15:08:37#$:
 *
 * Copyright (C) 2012 Seeyon, Inc. All rights reserved.
 *
 * This software is the proprietary information of Seeyon, Inc.
 * Use is subject to license terms.
 */
package com.seeyon.apps.index.controller;

import com.seeyon.apps.index.bo.IndexInfo;
import com.seeyon.apps.index.manager.IndexEnable;
import com.seeyon.apps.index.manager.IndexInnerManager;
import com.seeyon.apps.index.util.IndexContext;
import com.seeyon.apps.index.util.IndexModule;
import com.seeyon.apps.index.util.IndexPropertiesUtil;
import com.seeyon.apps.index.util.IndexSearchHelper;
import com.seeyon.ctp.cap.api.manager.CAPFormManager;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.ModuleType;
import com.seeyon.ctp.common.config.SystemConfig;
import com.seeyon.ctp.common.constants.ApplicationCategoryEnum;
import com.seeyon.ctp.common.controller.BaseController;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.filemanager.manager.FileManager;
import com.seeyon.ctp.common.i18n.ResourceUtil;
import com.seeyon.ctp.common.microserver.MicroServiceEnum;
import com.seeyon.ctp.common.microserver.MicroServiceManager;
import com.seeyon.ctp.common.microserver.config.MicroServiceConfigManager;
import com.seeyon.ctp.common.microserver.vo.MicroCenterConfigVo;
import com.seeyon.ctp.organization.OrgConstants.Role_NAME;
import com.seeyon.ctp.util.Datetimes;
import com.seeyon.ctp.util.MicroCenterConfigUtil;
import com.seeyon.ctp.util.Strings;
import com.seeyon.ctp.util.annotation.CheckRoleAccess;
import com.seeyon.ctp.util.json.JSONUtil;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.*;

/**
 * <p>Title: 全文检索的controller</p>
 * <p>Description: 全文检索的controller</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: seeyon.com</p>
 */
public class IndexController extends BaseController {

    private static final Log  log = LogFactory.getLog(IndexController.class);
    private FileManager fileManager;
    private IndexInnerManager indexManager;
    private IndexContext indexContext;
    private IndexInnerManager realManager;
    private CAPFormManager capFormManager;
    private SystemConfig systemConfig;
    private MicroServiceConfigManager microServiceConfigManager;
    private MicroServiceManager microServiceManager;
    
    
	public void setMicroServiceManager(MicroServiceManager microServiceManager) {
		this.microServiceManager = microServiceManager;
	}

	public void setSystemConfig(SystemConfig systemConfig) {
		this.systemConfig = systemConfig;
	}

    public void setMicroServiceConfigManager(MicroServiceConfigManager microServiceConfigManager) {
		this.microServiceConfigManager = microServiceConfigManager;
	}
	/**
     * 显示全文检索配置信息
     * @param request
     * @param response
     * @return
     */
    @CheckRoleAccess(roleTypes={Role_NAME.SystemAdmin})
    public ModelAndView showIndexConfigIndex(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("index/indexConfigIndex");
    }
    /**
     * 显示全文检索配置信息
     * @param request
     * @param response
     * @return
     */
    @CheckRoleAccess(roleTypes={Role_NAME.SystemAdmin})
    public ModelAndView showIndexConfig(HttpServletRequest request, HttpServletResponse response) throws Exception{
        ModelAndView mv = new ModelAndView("index/indexConfig");
        //A6也不要本地模式直接将isA6设置为false;当前标识所有的都用es
        boolean isA6 =false;// (Boolean) (SysFlag.sys_isA6Ver.getFlag());
        if(isA6) {
        	mv.addObject("indexIp", IndexPropertiesUtil.getIndexConfigValue("indexIp"));
            mv.addObject("indexPort", IndexPropertiesUtil.getIndexConfigValue("indexPort"));
            mv.addObject("indexServiceName", IndexPropertiesUtil.getIndexConfigValue("indexServiceName"));
            mv.addObject("indexParseTimeSlice", IndexPropertiesUtil.getIndexConfigValue("indexParseTimeSlice"));
            mv.addObject("indexUpdateTimeSlice", IndexPropertiesUtil.getIndexConfigValue("indexUpdateTimeSlice"));
            mv.addObject("a8Ip", IndexPropertiesUtil.getIndexConfigValue("a8Ip"));
        }
        mv.addObject("modelName", IndexPropertiesUtil.getIndexConfigValue("modelName"));
        mv.addObject("isA6",isA6);
        //得到注册码
        String reRegistCode = microServiceManager.getRegistCode(MicroServiceEnum.INDEX_SERVICE.name());
        mv.addObject("reRegistCode",reRegistCode);
        
        boolean regist = microServiceConfigManager.isRegist(MicroServiceEnum.INDEX_SERVICE.name());
        mv.addObject("regist",regist);
        mv.addObject("serviceId", MicroServiceEnum.INDEX_SERVICE.name());
		if(!regist) {
			mv.addObject("unRegistInfo", ResourceUtil.getString("system.config.micro.unregist.info"));
			return mv;
		}
        
		List<MicroCenterConfigVo> microCenterConfigList= microServiceConfigManager.getMicroCenterConfigVo(MicroServiceEnum.INDEX_SERVICE.name());
		if(microCenterConfigList == null)
			throw new Exception("读取配置信息异常");
		microCenterConfigList = MicroCenterConfigUtil.removeReadOnlyInfo(microCenterConfigList);
		mv.addObject("fileServiceConfigList",microCenterConfigList);
		String pwd_strong_require = systemConfig.get("pwd_strong_require");
		mv.addObject("pwd_strong_require",pwd_strong_require);
		return mv;
    }

    public ModelAndView openHelp(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("index/help");
    }

    /**
     * 更新全文检索配置信息
     * @param request
     * @param response
     * @return
     */
    @CheckRoleAccess(roleTypes={Role_NAME.SystemAdmin})
    public ModelAndView updateIndexConfig(HttpServletRequest request, HttpServletResponse response)  {
    	
    	 //A6也不要本地模式直接将isA6设置为false;当前标识所有的都用es
    	boolean isA6 =false;// (Boolean) (SysFlag.sys_isA6Ver.getFlag());
    	Properties orignIndexInfo = IndexPropertiesUtil.getOrignIndexInfo();
    	String outMsg = "index.com.seeyon.v3x.index.setupSuccess";
    	String modelName = null;
    	boolean regist = microServiceConfigManager.isRegist(MicroServiceEnum.INDEX_SERVICE.name());
    	Boolean saveFlag= true;
    	if(isA6) {//a6下两种模式
    		modelName = Strings.isBlank(request.getParameter("baseModelName")) ? "" : request.getParameter("baseModelName").trim();
            String indexIp = Strings.isBlank(request.getParameter("indexIp")) ? "" : request.getParameter("indexIp").trim();
            String indexPort = Strings.isBlank(request.getParameter("indexPort")) ? "" : request.getParameter("indexPort").trim();
            String indexServiceName = Strings.isBlank(request.getParameter("indexServiceName")) ? "" : request.getParameter("indexServiceName").trim();
            String indexParseTimeSlice = Strings.isBlank(request.getParameter("indexParseTimeSlice")) ? "": request.getParameter("indexParseTimeSlice").trim();
            String indexUpdateTimeSlice = Strings.isBlank(request.getParameter("indexUpdateTimeSlice")) ? "": request.getParameter("indexUpdateTimeSlice").trim();
            String a8Ip = Strings.isBlank(request.getParameter("a8Ip")) ? "" : request.getParameter("a8Ip").trim();
            
            IndexPropertiesUtil.updateIndexConfig("modelName", modelName);
            IndexPropertiesUtil.updateIndexConfig("indexIp", indexIp);
            IndexPropertiesUtil.updateIndexConfig("indexPort", indexPort);
            IndexPropertiesUtil.updateIndexConfig("indexServiceName", indexServiceName);
            IndexPropertiesUtil.updateIndexConfig("indexParseTimeSlice", indexParseTimeSlice);
            IndexPropertiesUtil.updateIndexConfig("indexUpdateTimeSlice", indexUpdateTimeSlice);
            IndexPropertiesUtil.updateIndexConfig("a8Ip", a8Ip);
            
            if(!regist && "es".equals(modelName)) {//不改变还是用原来的模式
            	outMsg ="index.com.seeyon.v3x.index.setupFailure";
            	
            	log.info("es服务未启动。。。");
//            	prop.putAll(temp);
            	IndexPropertiesUtil.iteratorPropertiesToCtpConfig(orignIndexInfo);
            }else if(regist && "es".equals(modelName) ){
            	try {
            		microServiceConfigManager.updateConfig(request, MicroServiceEnum.INDEX_SERVICE.name());
            	}catch(Exception e){
//            		IndexPropertiesUtil.iteratorPropertiesToCtpConfig(orignIndexInfo);
            		saveFlag = false;
            		log.error(MicroServiceEnum.FILE_SERVICE.name()+"服务连接异常"+e.getMessage(),e);
            		outMsg = "menu.system.microservice.connect.fail";
            	}
            }
//            else{
//                indexManager.setRealManager(realManager);
//            }
    	}else { //a8下就es模式
    		modelName ="es"; //a8只会是es
//          boolean regist = microServiceConfigManager.isRegist(MicroServiceEnum.INDEX_SERVICE.name());
          if(!regist) {//不改变还是用原来的模式
          	outMsg ="index.com.seeyon.v3x.index.setupFailure";
          	saveFlag= false;
          	log.info("es服务未启动。。。");
//          	prop.putAll(temp);
//          	IndexPropertiesUtil.iteratorPropertiesToCtpConfig(orignIndexInfo);
          }else if(regist){
          	try {
          		microServiceConfigManager.updateConfig(request, MicroServiceEnum.INDEX_SERVICE.name());
          	}catch(Exception e){
//          		IndexPropertiesUtil.iteratorPropertiesToCtpConfig(orignIndexInfo);
          		saveFlag = false;
          		log.error(MicroServiceEnum.FILE_SERVICE.name()+"服务连接异常"+e.getMessage(),e);
          		outMsg = "menu.system.microservice.connect.fail";
          	}
          }
    	}
    	
    	if(saveFlag){         	
        	IndexContext.setModelName(modelName);
        	//确保base/index/目录下没有indexConfig.proterties
        	String baseFile = IndexContext.getBaseFolder() + File.separator + "indexConfig.properties";
        	File file = new File(baseFile);
        	if(file.exists())
        		file.delete();
        	log.info("index配置保存完毕..." );
        }
        return super.redirectModelAndView("/index/indexController.do?method=showIndexConfig&outMsg=" + outMsg);
    }

    public ModelAndView searchIndex(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("index/searchIndex");
        String authorKey = IndexSearchHelper.getAuthorKey();
        AppContext.putSessionContext("Index_AuthorKey", authorKey);
        List<IndexModule> allModules = indexContext.getIndexAllModule();
        mav.addObject("allModules", JSONUtil.toJSONString4Ajax(allModules));
        return mav;
    }

    /**
     * keyword为空，刷新页面，跳转到状态条jsp
     */
    public ModelAndView showNullList(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("index/searchResultError");
    }

    @Override
    public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return null;
    }

    public void setFileManager(FileManager fileManager) {
        this.fileManager = fileManager;
    }

	public void setIndexContext(IndexContext indexContext) {
        this.indexContext = indexContext;
    }

    public void setIndexManager(IndexInnerManager indexManager) {
        this.indexManager = indexManager;
    }

    public void setRealManager(IndexInnerManager realManager) {
        this.realManager = realManager;
    }

    public CAPFormManager getCapFormManager() {
        return capFormManager;
    }

    public void setCapFormManager(CAPFormManager capFormManager) {
        this.capFormManager = capFormManager;
    }

    public ModelAndView indexFile(HttpServletRequest request, HttpServletResponse response) {
        String[] urls = (String[]) request.getParameterValues("fileUrl");
        String[] createDates = (String[]) request.getParameterValues("fileCreateDate");
        String[] mimeTypes = (String[]) request.getParameterValues("fileMimeType");
        String[] names = (String[]) request.getParameterValues("filename");
        for (int i = 0; i < urls.length; i++) {
            Long fileId = Long.parseLong(urls[i]);

            Date createDate = null;
            createDate = Datetimes.parseDatetime(createDates[i]);
            
            try {
                File file = fileManager.getFile(fileId, createDate);
                int contentType = getContentType(mimeTypes[i]);

                IndexInfo info = new IndexInfo();
                info.setEntityID(fileId);
                info.setTitle(names[i]);
                info.setMimeContent(file);
                info.setContentType(contentType);
                info.setAppType(ApplicationCategoryEnum.doc);
                indexManager.add(info);

            } catch (BusinessException e) {
                log.error(e.getMessage(), e);
            }
        }
        return new ModelAndView("test/sucess");
    }

    private int getContentType(String type) {
        int contentType = -1;
        if ("application/msword".equals(type)) {
            contentType = IndexInfo.CONTENTTYPE_WORD;
        }
        if ("application/vnd.ms-excel".equals(type)) {
            contentType = IndexInfo.CONTENTTYPE_XLS;
        }
        if ("application/vnd.ms-powerpoint".equals(type)) {
            contentType = IndexInfo.CONTENTTYPE_PPT;
        }
        if ("text/plain".equals(type)) {
            contentType = IndexInfo.CONTENTTYPE_TXT;
        }
        if ("application/pdf".equals(type)) {
            contentType = IndexInfo.CONTENTTYPE_PDF;
        }
        return contentType;
    }

    /**
     * 获取打开URL需要的参数
     * 
     * @param categorys 各应用的类别，参见{@link ApplicationCategoryEnum}
     * @param ids       各应用的实体ID
     * @return
     * @throws Exception
     */
    public Map<String, String> getIndexParams(String categorys, String ids) throws Exception {
        int category = NumberUtils.toInt(categorys);
        Long id = NumberUtils.toLong(ids);

        Map<String, String> result = new HashMap<String, String>();
        String appType = categorys;
        String linkId = ids;
        if (ApplicationCategoryEnum.portal.getKey() == category) {
            // 门户的需要特殊处理一下
            Map<String, Object> map = IndexContext.getIndexEnableMap().get(category).findSourceInfo(id);
            result.put("appType", appType);
            result.put("linkId", linkId);
            result.put("type", MapUtils.getString(map, "type", ""));
            result.put("isMobile", MapUtils.getString(map, "isMobile", "false"));
            result.put("realSpaceId",MapUtils.getString(map, "realSpaceId", ""));
            return result;
        } else if (ApplicationCategoryEnum.menu.getKey() == category
                || ApplicationCategoryEnum.template.getKey() == category
                || ApplicationCategoryEnum.ThirdPartyIntegration.getKey() == category) {
            // 菜单的需要获取一下菜单打开地址及打开方式
            Map<String, Object> map = IndexContext.getIndexEnableMap().get(category).findSourceInfo(id);
            result.put("appType", appType);
            result.put("linkId", linkId);
            result.put("openType", MapUtils.getString(map, "openType", ""));
            result.put("openUrl", MapUtils.getString(map, "openUrl", ""));
            result.put("isMobile", MapUtils.getString(map, "isMobile", "false"));
            return result;
        }
        
        try {
            Map<String, Object> map = IndexContext.getIndexEnableMap().get(category).findSourceInfo(id);
            if (map.containsKey(IndexEnable.SOURCE_ID)) {
                linkId = String.valueOf(map.get(IndexEnable.SOURCE_ID));
                if (category == ApplicationCategoryEnum.form.getKey()) {
                    ModuleType type = (ModuleType) map.get("moduleType");
                    if (type == ModuleType.form) {// 有流程
                        appType = String.valueOf(ApplicationCategoryEnum.collaboration.getKey());
                    } else if (type == ModuleType.cap4UnflowForm) {// CAP4无流程表单
                        appType = String.valueOf(ApplicationCategoryEnum.cap4Form.getKey());
                    } else {// 无流程
                        linkId = map.get(IndexEnable.SOURCE_ID) + "|" + type.getKey() + "|" + map.get("rightId");
                        capFormManager.addRight(map.get("rightId") == null ? "" : map.get("rightId").toString());
                    }
                }
            } else {
                linkId = "-1";
            }
        } catch (Exception e) {
            logger.error("findSourceInfo() exception:", e);
        }

        result.put("appType", appType);
        result.put("linkId", linkId);
        return result;
    }

    public ModelAndView openRedirect(HttpServletRequest request, HttpServletResponse response) {
    	ModelAndView mv = new ModelAndView("index/openRedirect");
    	String keyword = request.getParameter("keyword");
    	mv.addObject("keyword", keyword);
    	return mv;
    }
}