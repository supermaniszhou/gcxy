package com.seeyon.ctp.common.taglibs.functions;

import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.SystemEnvironment;
import com.seeyon.ctp.common.authenticate.domain.User;
import com.seeyon.ctp.common.authenticate.domain.UserHelper;
import com.seeyon.ctp.common.config.IConfigPublicKey;
import com.seeyon.ctp.common.config.SystemConfig;
import com.seeyon.ctp.common.config.manager.ConfigManager;
import com.seeyon.ctp.common.constants.ApplicationCategoryEnum;
import com.seeyon.ctp.common.constants.ApplicationSubCategoryEnum;
import com.seeyon.ctp.common.constants.ProductEditionEnum;
import com.seeyon.ctp.common.constants.ProductVersionEnum;
import com.seeyon.ctp.common.constants.SystemProperties;
import com.seeyon.ctp.common.customize.manager.CustomizeManager;
import com.seeyon.ctp.common.dao.paginate.Pagination;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.flag.BrowserFlag;
import com.seeyon.ctp.common.flag.SysFlag;
import com.seeyon.ctp.common.formula.FormulaUtil;
import com.seeyon.ctp.common.i18n.LocaleContext;
import com.seeyon.ctp.common.i18n.ResourceBundleUtil;
import com.seeyon.ctp.common.i18n.ResourceUtil;
import com.seeyon.ctp.common.i18n.expand.I18nResource;
import com.seeyon.ctp.common.i18n.manager.I18nResourceCacheHolder;
import com.seeyon.ctp.common.init.MclclzUtil;
import com.seeyon.ctp.common.log.CtpLogFactory;
import com.seeyon.ctp.common.po.config.ConfigItem;
import com.seeyon.ctp.common.web.util.WebUtil;
import com.seeyon.ctp.login.online.OnlineRecorder;
import com.seeyon.ctp.login.online.OnlineUser;
import com.seeyon.ctp.organization.bo.MemberPost;
import com.seeyon.ctp.organization.bo.V3xOrgAccount;
import com.seeyon.ctp.organization.bo.V3xOrgDepartment;
import com.seeyon.ctp.organization.bo.V3xOrgEntity;
import com.seeyon.ctp.organization.bo.V3xOrgLevel;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.bo.V3xOrgPost;
import com.seeyon.ctp.organization.bo.V3xOrgTeam;
import com.seeyon.ctp.organization.dao.OrgHelper;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.util.Cookies;
import com.seeyon.ctp.util.Datetimes;
import com.seeyon.ctp.util.JSObject;
import com.seeyon.ctp.util.LightWeightEncoder;
import com.seeyon.ctp.util.Strings;
import com.seeyon.ctp.util.XMLCoder;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;

import javax.script.ScriptException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.jstl.fmt.LocalizationContext;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


/**
 * V3X Functions
 *
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2007-1-8
 */
public class Functions {
    protected static Log log = CtpLogFactory.getLog(Functions.class);
    /**
     * 类似于out.println(condition ? y : n);
     *
     * @param statment
     * @param y
     * @param n
     * @return
     */
    public static String outConditionExpression(Boolean statment, String y,
            String n) {
        if (BooleanUtils.isTrue(statment)) {
            return y;
        }

        return n;
    }

    /**
     * 检测List中是否包含指定object
     *
     * @param <E>
     * @param list
     * @param object
     * @return
     */
    public static <E> boolean containInCollection(Collection<E> list, E object) {
        if (list == null || list.isEmpty() || object == null) {
            return false;
        }

        return list.contains(object);
    }

    /**
     * 检测List中是否包含指定Integer
     *
     * @param <E>
     * @param list
     * @param object
     * @return
     */
    public static boolean isIntegerInCollection(Collection<Integer> list, Integer obj) {
        if (list == null || list.isEmpty() || obj == null) {
            return false;
        }

        return list.contains(obj);
    }
    
    /**
     * 获取是否关闭密码自动填写禁用开关
     * @return
     */
    public static String autoCompleteIsOpen() {
    	//致信报空指针
    	if(configManager == null) {
    		 configManager = (ConfigManager)AppContext.getBean("configManager");
    	}
    	ConfigItem autoCompleteIsOpenItem = configManager.getConfigItem(SystemConfig.SWITCH, IConfigPublicKey.AUTO_COMPLETE_ENABLE);
        String autoCompleteIsOpen = autoCompleteIsOpenItem != null ? autoCompleteIsOpenItem.getConfigValue():"";
        if("enable".equals(autoCompleteIsOpen))
        	return "off";
    	return "on";
    } 
    
    /**
     * 将字符串转换成HTML,不包括 \n
     * @param text
     * @return
     */
    public static String toHTMLAlt(String text) {
        return Strings.toHTMLAlt(text);
    }

    /**
     * 将字符串转换成HTML，将对\r \n < > & 空格进行转换
     *
     * @param text
     * @return
     */
    public static String toHTML(String text) {
        return Strings.toHTML(text);
    }

    /**
     * 将字符串转换成HTML，将对\r \n < > & 空格不进行转换
     *
     * @param text
     * @return
     */
    public static String toHTMLWithoutSpace(String text) {
        return Strings.toHTML(text, false);
    }

    public static String toHTMLescapeRN(String text) {
        return Strings.toHTMLescapeRN(text, false);
    }
    
    public static String getServerName(HttpServletRequest request) {
        return Strings.getServerName(request);
    }

    /**
     * <pre>
     * 将字符串转换成HTML，空格不进行转换，<b>单引号不转成<code>"&amp;#039;"</code>，而是转为"\'"</b>
     * 使用场景，作为js参数传入时，需要转移，比如：
     * <c:set value="OnMouseUp(new DocResource('${docsList.docResource.id}','${<b>v3x:toHTMLWithoutSpaceEscapeQuote</b>(docsList.docResource.frName)}',...
     * 如使用toHTMLWithoutSpace则会因为单引号被转成<code>"&amp;#039;"</code>而报js错
     * </pre>
     */
    public static String toHTMLWithoutSpaceEscapeQuote(String text) {
        return toHTMLWithoutSpace(text).replaceAll("&#039;", "\\\\'");
    }

    /**
     * 将字符串转换成Javascript，将对\r \n < > & 空格进行转换
     *
     * @param text
     * @return
     */
    public static String escapeJavascript(String str) {
        return Strings.escapeJavascript(str);
    }
    public static String escapeQuot(String str) {
        return Strings.escapeQuot(str);
    }
    /**
     * 国际化,不支持参数
     *
     * @param pageContext
     * @param key
     * @return
     */
    public static String _(PageContext pageContext, String key) {
        return OrgHelper._(pageContext, key);
    }

    /**
     *
     * @param baseName
     * @param key
     * @return
     */
    public static String messageFromResource(String baseName, String key) {
        return ResourceBundleUtil.getString(baseName, key);
    }

    public static String messageFromBundle(LocalizationContext locCtxt, String key) {
        String val = ResourceBundleUtil.getString(locCtxt, key);
        return val == null ? key : val;
    }

    /**
     * 国际化，参数被序列化成XML
     *
     * @param pageContext
     * @param key
     * @param paramXML
     * @return
     */
    public static String messageOfParameterXML(PageContext pageContext, String key, String paramXML) {
        Object[] params = null;
        if(Strings.isNotBlank(paramXML)){
            params = (Object[])XMLCoder.decoder(paramXML);
        }

        return ResourceBundleUtil.getString(pageContext, key, params);
    }

    /**
     * 得到当前的语言(字符串zh-cn),用在JS中
     *
     * @param request
     * @return 如zh-cn
     */
    public static String getLanguage(HttpServletRequest request) {
    	User user = AppContext.getCurrentUser();
    	if(user==null){
    		AppContext.initSystemEnvironmentContext(request, null,false);
    	}
       return LocaleContext.getLanguage(request);
    }
    
    /**
     * 得到指定语言的国际化文字
     */
    public static String getShowLanguage(String localCode) {
       if(Strings.isBlank(localCode)){
    	   return "";
       }
       return ResourceUtil.getString("localeselector.locale."+localCode);
    }

    /**
     * 得到当前的语言(Locale)
     *
     * @param request
     * @return
     */
    public static Locale getLocale(HttpServletRequest request) {
        return LocaleContext.getLocale(request);
    }

    /**
     * 把集合中的元素的某个属性值分隔符连接起来
     *
     * <pre>
     * <code>
     *     class Member{
     *          private long id;
     *          private String name;
     *          private Department department;
     *     }
     *     class Department{
     *          private long id;
     *          private String name;
     *     }
     *
     *     join(list&lt;Member&gt;, &quot;name&quot;, &quot;,&quot;)    = 人名字的字符串
     *     join(list&lt;Member&gt;, &quot;department.name&quot;, &quot;,&quot;) = 部门名字的字符串
     * </code>
     * </pre>
     *
     * @param list
     * @param property
     *            支持多级属性,用.分隔
     * @param pageContext
     *            为了实现分隔符的国际化
     * @return
     */
    public static String join(Collection<? extends Object> list,
            String properties, PageContext pageContext) {
        if (list == null || list.isEmpty() || properties == null
                || pageContext == null) {
            return null;
        }

        String separator = getOrgEntitiesSeparator(pageContext);

        return join(list, properties, separator);
    }

    /**
     * 与<code>join(Collection, String, PageContext)</code>雷同，只是分隔符在调用放指定，常用于id的分割
     *
     * @param list
     * @param properties
     * @param separator
     *            分隔符，如果要实现国际化，请用<code>join(Collection, String, PageContext)</code>
     * @return
     */
    public static String join(Collection<? extends Object> list,
            String properties, String separator) {
        if (list == null || list.isEmpty() || properties == null) {
            return null;
        }

        List<Object> objects = new ArrayList<Object>();

        String[] props = properties.split("[.]");
        for (Object object : list) {
            Object o = object;
            for (int i = 0; i < props.length; i++) {
                String property = props[i];
                if(o == null){
                    log.warn("", new Exception("Collection中的数据有null"));
                    break;
                }

                try {
                    o = PropertyUtils.getProperty(object, property);
                }catch (Exception e) {
                    log.error("从[" + object + "]中获取属性'" + property + "'错误", e);
                }
            }

            objects.add(o);
        }

        return StringUtils.join(objects.iterator(), separator);
    }

    /**
     * 将集合连接起来，分隔符采用系统默认
     *
     * @param list
     * @param pageContext
     * @return
     */
    public static String join(Collection<? extends Object> list, PageContext pageContext){
        String separator = getOrgEntitiesSeparator(pageContext);
        return join(list, separator);
    }

    /**
     * 将集合连接起来
     * @param list
     * @param separator 分隔符
     * @return
     */
    public static String join(Collection<? extends Object> list, String separator){
        if (list == null || list.isEmpty()) {
            return null;
        }

        return StringUtils.join(list.iterator(), separator);
    }

    /**
     * 显示异常，在抛出异常时，需要制定resource key 和对应的Parameter。如：<br>
     *
     * <pre>
     *   Manager: throw new BusinessException(&quot;fileupload.exception.MaxSize&quot;, maxSize);
     *
     *    Controller:
     *    try {
     *    }
     *    catch (BusinessException e) {
     *      modelAndView.addObject(&quot;e&quot;, e);
     *    }
     *
     *    JSP :
     *    &lt;c:if test=&quot;${e ne null}&quot;&gt;
     *    &lt;script type=&quot;text/javascript&quot;&gt;
     *    alert("${v3x:showException(e, pageContext)}");
     *    &lt;/script&gt;
     *    &lt;/c:if&gt;
     * </pre>
     *
     * 在该方法中直接alert(异常内容)，自动完成国际化
     *
     * @param e
     *
     */
    public static String showException(BusinessException e,
            PageContext pageContext) {
        if (e == null) {
            return null;
        }

/*        String key = e..getErrorCode();

        String message = "";

        if (StringUtils.isNotBlank(key)) {
            Object[] parameters = e.getErrorArgs();
            message = ResourceBundleUtil.getString(pageContext, key, parameters);
        } else {
            message = e.toString();
        }

        if (StringUtils.isNotBlank(message)) {
            return (escapeJavascript(message));
        }

        return null;*/
        //TODO::老方法已经不能使用，确认新方式是否已经国际化
       return e.getMessage();
    }

    /**
     * 显示长度
     *
     * @param content
     * @param len
     * @param symbol
     * @return
     */
    public static String getLimitLengthString(String content, Integer len,
            String symbol){
        return Strings.getLimitLengthString(content, len, symbol);
    }

    public static String getSafeLimitLengthString(String content, Integer len,
            String symbol){
        return Strings.getSafeLimitLengthString(content, len, symbol);
    }

    /**
     * 获取按照指定长度截取之后的字符串内容，按照UTF-8编码<br>
     * 上传文件时，文件名中的空格，在提交之后会转为UTF-8编码空格，而默认的截取字符串内容<br>
     * 在不设定字符集时，是采用GBK编码，为兼容，增加此方法，指定字符集为UTF-8<br>
     */
    public static String getLimitLengthStringUTF8(String content, Integer len,
            String symbol){
        if(len < 0){
            return content;
        }

        try {
            return Strings.getLimitLengthString(content, "UTF-8", len, symbol);
        }
        catch (UnsupportedEncodingException e) {
            return content;
        }
    }

    /**
     * 显示常用语调用
     *
     * <pre>
     * &lt;script type=&quot;text/javascript&quot; src=&quot;&lt;c:url value=&quot;/apps_res/v3xmain/js/phrase.js&quot; /&gt;&quot;&gt;&lt;/script&gt;
     * ${v3x:showCommonPhrase(pageContext)}
     * &lt;a href=&quot;javascript:showPhrase()&quot;&gt;Phrase&lt;/a&gt;
     * </pre>
     *
     * @param pageContext
     * @return
     */
    public static String showCommonPhrase(PageContext pageContext){
        StringBuilder str = new StringBuilder();

        try {
//            String url = com.seeyon.v3x.portlets.bridge.spring.taglibs.LinkTag.calculateURL("/phrase.do?method=list", pageContext);
            String url = SystemEnvironment.getContextPath() + "/phrase.do?method=list";

            str.append("<script type=\"text/javascript\">\n")
               .append("var phraseURL = '" + url + "';\n")
               .append("</script>\n");

            str.append("<div oncontextmenu=\"return false\"\n")
               .append("    class=\"border-tree\" style=\"position:absolute; right:20px; top:100px; width:260px; height:160px; z-index:2; background-color: #ffffff;display:none;overflow:no;\"\n")
               .append("     id=\"divPhrase\" onMouseOver=\"showPhrase()\" onMouseOut=\"hiddenPhrase()\">\n")
               .append("    <IFRAME width=\"100%\" id=\"phraseFrame\" name=\"phraseFrame\" height=\"100%\" frameborder=\"0\" align=\"middle\" scrolling=\"no\"\n")
               .append("            marginheight=\"0\" marginwidth=\"0\"></IFRAME>\n")
               .append("</div>\n");
        }
        catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
        }

        return str.toString();
    }

    /**
     * 获取系统配置
     *
     * @param key
     * @return
     */
    public static String getSystemProperty(String key){
        return SystemProperties.getInstance().getProperty(key);
    }

    /********************************** 组织模型相关 *****************************************/
    public static User currentUser(){
        return AppContext.getCurrentUser();
    }

    private static OrgManager orgManager = null;
    private static CustomizeManager customizeManager = null;
    public static CustomizeManager getCustomizeManager() {
        if(customizeManager == null){
            customizeManager = (CustomizeManager)AppContext.getBean("customizeManager");
        }

        return customizeManager;
    }

    protected static OrgManager getOrgManager(){
        if(orgManager == null){
            orgManager = (OrgManager)AppContext.getBean("orgManager");
        }

        return orgManager;
    }

    private static SystemConfig systemConfig = null;
    private static SystemConfig getSystemConfig(){
        if(systemConfig == null){
            systemConfig = (SystemConfig)AppContext.getBean("systemConfig");
        }
        return systemConfig;
    }

    private static ConfigManager configManager = null;
    private static ConfigManager getConfigManager(){
        if(configManager == null){
            configManager = (ConfigManager)AppContext.getBean("configManager");
        }
        return configManager;
    }

    public static V3xOrgEntity getEntity(String entityType, Long entityId) {
        if(entityId == null || entityId == -1){
            return null;
        }

        try {
            return getOrgManager().getGlobalEntity(entityType, entityId);
        }
        catch (Exception e) {
            log.warn("", e);
            return null;
        }
    }

    public static V3xOrgMember getMember(Long memberId) {
        return OrgHelper.getMember(memberId);
    }

    /**
     * 显示人员名字，不带单位简称
     *
     * @param memberId
     * @return
     */
    public static String showMemberNameOnly(Long memberId){
        return OrgHelper.showMemberNameOnly(memberId);
    }


    /**
     * 显示人员名字，如果不是一个单位的，则显示单位简称
     *
     * @param memberId
     * @return
     */
    public static String showMemberName(Long memberId){
    	String memberName= OrgHelper.showMemberName(memberId);
    	return memberName;
//    	OA-218322 OA-218156 OA-218128
//    	if(Strings.isEmpty(memberName)){
//    		return memberName;
//    	}
//        return toHTML(memberName);
    }

    /**
     * 显示部门名称，如果不是一个单位的，则显示单位简称
     * @param deptId
     * @return
     */
    public static String showDepartmentName(Long deptId) {
        return OrgHelper.showDepartmentName(deptId);
    }

    /**
     * 人员所在单位的名称
     * @param memberId
     * @return
     */
    public static String showOrgAccountNameByMemberid(Long memberId){
    	return OrgHelper.showOrgAccountNameByMemberid(memberId);
    }

    public static String showOrgAccountName(Long accountId){
    	return OrgHelper.showOrgAccountName(accountId);
    }
    /**
     * 人员的岗位名称
     * @param memberId
     * @return
     */
    public static String showOrgPostNameByMemberid(Long memberId){
    	return OrgHelper.showOrgPostNameByMemberid(memberId);
    }

    public static String showOrgPostName(Long postId){
        return OrgHelper.showOrgPostName(postId);
    }

    /**
     * 人员的职务级别的名称
     * @param memberId
     * @return
     */
    public static String showMemberLeave(Long memberId){
        if(memberId == 1 || memberId == 0){
            return "-" ;
        }

        V3xOrgMember m = getMember(memberId);

        if(m  == null){
            log.error("获取的人员为空") ;
            return null ;
        }

        return showOrgLeaveName(m) ;
    }

    public static String showOrgLeaveName(V3xOrgMember m){

        if(m.getIsAdmin()){
            return "-" ;
        }

        V3xOrgLevel level =  getLeave(m.getOrgLevelId()) ;
        if(level == null){
            log.error("获取的职务级别为空") ;
            return null ;
        }
        return level.getName() ;
    }

    public static String showOrgLeaveName(V3xOrgLevel level){
        if(level == null){
            log.error("获取的职务级别为空") ;
            return null ;
        }
        return level.getName() ;
    }


    public static V3xOrgLevel getLeave(Long leaveId){
        if(leaveId == -1){
            return null;
        }
        try {
            return getOrgManager().getLevelById(leaveId);
        }
        catch (Exception e) {
            log.warn("", e);
            return null;
        }
    }

    /**
     * 显示人员名字，永远不显示单位简称
     *
     * @param member
     * @return
     */
    public static String showMemberNameOnly(V3xOrgMember member){
        return OrgHelper.showMemberNameOnly(member);
    }

    /**
     * 显示人员名字，如果不是一个单位的，则显示单位简称
     *
     * @param member
     * @return
     */
    public static String showMemberName(V3xOrgMember member){
        return OrgHelper.showMemberName(member);
    }


    /**
     * 显示人员的基础信息
     *
     * @param memberId
     * @return
     */
    public static String showMemberAlt(Long memberId){
        V3xOrgMember m = getMember(memberId);
        if(m == null){
            return null;
        }
        return showMemberAlt(m);
    }

    /**
     * 显示人员的基础信息
     *
     * @param member
     * @return
     */
    public static String showMemberAlt(V3xOrgMember member){
        return showMemberAlt(member, false);
    }

    /**
     * 显示人员的基础信息，部门显示全称
     *
     * @param memberId
     * @return
     */
    public static String showMemberAltWithFullDeptPath(Long memberId){
        V3xOrgMember m = getMember(memberId);
        if(m == null){
            return null;
        }
        return showMemberAltWithFullDeptPath(m);
    }

    /**
     * 显示人员的基础信息，部门显示全称
     *
     * @param member
     * @return
     */
    public static String showMemberAltWithFullDeptPath(V3xOrgMember member){
        return showMemberAlt(member, true);
    }

    private static String showMemberAlt(V3xOrgMember member, boolean isShowFullDepartmentPath) {
    	String alt= showMemberAlt(member, isShowFullDepartmentPath, member.getOrgDepartmentId());
        return alt;
    }
    
    public static String showMemberAlt(V3xOrgMember member, boolean isShowFullDepartmentPath,Long departmentId) {
        if (member == null || Boolean.TRUE.equals(member.getIsAdmin())) {
            return null;
        }

        StringBuilder sb = new StringBuilder();

        sb.append(Strings.toHTMLescapeRN(showMemberName(member), false));

        String departName = null;
        if (isShowFullDepartmentPath) {
            departName = showDepartmentFullPath(departmentId);
        } else {
            V3xOrgDepartment dept = getDepartment(departmentId);
            if (dept != null) {
                departName = dept.getName();
            }
        }

        if (Strings.isNotBlank(departName)) {
            sb.append("\n");
            sb.append(ResourceUtil.getString("org.department.label")).append(" : ").append(departName);
        }

        V3xOrgPost post = null; 
        try{
        	OrgManager orgManager = (OrgManager)AppContext.getBean("orgManager");
        	List<MemberPost> postList= orgManager.getMemberPosts(member.getOrgAccountId(), member.getId());
        	for (MemberPost memberPost2 : postList) {
				if(memberPost2.getDepId().equals(departmentId)){ 
					post= orgManager.getPostById(memberPost2.getPostId());
					break;
				}
			}
        	if(null==post){
        		post = getPost(member.getOrgPostId());
        	}
        }catch(Exception e){
            log.error("", e);
        }
        if (post != null) {
            sb.append("\n");
            sb.append(ResourceUtil.getString("org.post.label")).append(" : ").append(post.getName());
        }

        sb.append("\n");
        sb.append(ResourceUtil.getString("org.telNumber.label")).append(" : ").append(member.getOfficeNum() == null ? "" : member.getOfficeNum());

        sb.append("\n");
        sb.append(ResourceUtil.getString("member.mobile")).append(" : ").append(member.getTelNumber() == null ? "" : member.getTelNumber());

        sb.append("\n");
        sb.append(ResourceUtil.getString("member.email")).append(" : ").append(member.getEmailAddress() == null ? "" : member.getEmailAddress());

        return sb.toString();
    }

    public static String showDepartmentFullPath(Long departmentId){
        return OrgHelper.showDepartmentFullPath(departmentId);
    }
    
    public static String showDepartmentFullPathById(Long memberId,Long accountId){
        return OrgHelper.showDepartmentFullPath(memberId,accountId);
    }
    
    public static String showDepartmentFullPathByMemberId(Long memberId,Long departmentId){
        return OrgHelper.showDepartmentFullPathByMemberId(memberId,departmentId);
    }
    
    /**
     * 显示关联人员的基础信息
     * @param memberId
     */
    public static String showRelateMemberAlt(Long memberId){
        V3xOrgMember member = getMember(memberId);
        if(member == null){
            return null;
        }

        StringBuilder sb = new StringBuilder();

        V3xOrgDepartment dept = getDepartment(member.getOrgDepartmentId());
        if(dept != null){
            sb.append(ResourceUtil.getString("common.toolbar.department.label")).append(" : ").append(dept.getName());
            sb.append("\n");
        }

        V3xOrgPost post = getPost(member.getOrgPostId());
        if(post != null){
            sb.append(ResourceUtil.getString("common.toolbar.post.label")).append(" : ").append(post.getName());
            sb.append("\n");
        }

        String telNumber = (String)member.getProperty("officeNum");
        if(Strings.isBlank(telNumber)){
            telNumber = member.getTelNumber();;
        }
        if(Strings.isNotBlank(telNumber)){
            sb.append(ResourceUtil.getString("org.telNumber.label")).append(" : ").append(telNumber);
        }

        return sb.toString();
    }

    public static V3xOrgPost getPost(Long postId) {
       return OrgHelper.getPost(postId);
    }

    public static V3xOrgDepartment getDepartment(Long departmentId) {
        return OrgHelper.getDepartment(departmentId);
    }

    public static V3xOrgLevel getLevel(Long levelId) {
        try {
            return getOrgManager().getLevelById(levelId);
        }
        catch (Exception e) {
            log.warn("", e);
            return null;
        }
    }

    public static V3xOrgAccount getAccount(Long accountId) {
        return OrgHelper.getAccount(accountId);
    }

    public static String getAccountShortName(Long accountId) {
        try {
            V3xOrgAccount a = getAccount(accountId);
            if(a != null){
                return a.getShortName();
            }
        }
        catch (Exception e) {
            log.warn("", e);
        }

        return null;
    }

    public static V3xOrgTeam getTeam(Long teamId) {
        try {
            return getOrgManager().getTeamById(teamId);
        }
        catch (Exception e) {
            log.warn(e.getLocalizedMessage(), e);
            return null;
        }
    }

    public static String getTeamName(Long teamId) {
        try {
            V3xOrgTeam team = getTeam(teamId);
			return team==null ? null : team.getName();
        }
        catch (Exception e) {
            log.warn(e.getLocalizedMessage(), e);
            return null;
        }
    }

    /**
     * 我的单位，包括兼职单位
     *
     * @return
     */
    public static List<V3xOrgAccount> concurrentAccount(){
        User user = AppContext.getCurrentUser();
        try {
            List<V3xOrgAccount> a = getOrgManager().concurrentAccount(user.getId());
            if(a != null){
                return a;
            }

            return new ArrayList<V3xOrgAccount>();
        }
        catch (Exception e) {
            return new ArrayList<V3xOrgAccount>();
        }
    }

    /**
     * 我能访问的单位
     *
     * @return
     */
    public static List<V3xOrgAccount> accessableAccounts(){
        User user = AppContext.getCurrentUser();
        try {
            return getOrgManager().accessableAccounts(user.getId());
        }
        catch (BusinessException e) {
            return null;
        }
    }
    /**
     * 我能否访问集团
     *
     * @return
     */
    public static boolean isGroupAccessable(Long accountId){
        try {
           return getOrgManager().isAccessGroup(accountId);
        }
        catch (Exception e) {
            return false;
        }
    }
    /**
     * 
     * @方法名称: hasRoleName
     * @功能描述: 我是否拥有该角色
     * @参数 ：@param roleName
     * @参数 ：@param memberId
     * @参数 ：@param accountId
     * @参数 ：@return
     * @返回类型：Boolean
     * @创建时间 ：2016年1月23日 下午12:25:38
     * @修改时间 ：2016年10月08日 下午18:29:38
     */
    public static Boolean hasRoleName(String roleName){
    	try{
	    	User user=AppContext.getCurrentUser();
	    	if(user!=null){
	    		String roleNames[]=roleName.split(",");
	    		Set<String> userRoles = (Set<String>) user.getProperty(UserHelper.USERROLES);
	    		for(String rn :roleNames){
//	    			if(getOrgManager().hasSpecificRole(user.getId(), user.getLoginAccount(), rn)){
//	    				return true;
//	    			}
	    			
	    			if(userRoles.contains(rn)){
	    				return true;
	    			}
	    			
	    		}
	    	}
	    	return false;
    	}catch (Exception e) {
    		log.error("", e);
			return false;
		}
    }
    /**
     * 将对象人员/部门/单位等数据链接起来，显示方式为： (致远)开发中心、(股份)U8事业本部、(金融)赵大伟
     *
     * 自动根据
     *
     * <pre>
     * public class TempleteAuth extends BaseModel implements Serializable {
     *  private Long authId;
     *  private String authType;
     *  private Integer sort;
     *  private long templeteId;
     *
     *  //setter / getter
     * }
     * </pre>
     *
     * ${v3x:showOrgEntities(List<TempleteAuth>, "authId", "authType", pageContext)}
     *
     * @param list 数据集合
     * @param idProperty V3xOrgEntity的id
     * @param typeProperty V3xOrgEntity的type
     * @param pageContext
     * @return
     */
    public static String showOrgEntities(Collection<? extends Object> list, String idProperty, String typeProperty, PageContext pageContext){
        return OrgHelper.showOrgEntities(list, idProperty, typeProperty, pageContext);
    }

    /**
     * 将EntityType|EntityId,EntityType|EntityId转换成名称字符串
     *
     * @param typeAndIds Member|13241234,Department|23452345234
     * @return (致远)开发中心、(股份)U8事业本部、(金融)赵大伟
     */
    public static String showOrgEntities(String typeAndIds, PageContext pageContext) {
        return OrgHelper.showOrgEntities(typeAndIds, pageContext);
    }

    /**
     * 将EntityType|EntityId,EntityType|EntityId转换成名称字符串
     * * @param separator 分隔符
     * @param typeAndIds Member|13241234,Department|23452345234
     * @return (致远)开发中心、(股份)U8事业本部、(金融)赵大伟
     */
    public static String showOrgEntities(String typeAndIds, String separator) {
        return OrgHelper.showOrgEntities(typeAndIds, separator);
    }

    /**
     * 将格式为EntityId,EntityId的数据转换成Element[]
     *
     * @param ids 1234123,234534563
     * @param type 指定类型
     * @param separator 显示内容的间隔符号
     * @return
     */
    public static String showOrgEntities(String ids, String type, String separator) {
        return OrgHelper.showOrgEntities(ids, type, separator);
    }

    /**
     * 将格式为EntityId,EntityId的数据转换成Element[]
     *
     * @param ids 1234123,234534563
     * @param type 指定类型
     * @param pageContext
     * @return
     */
    public static String showOrgEntities(String ids, String type, PageContext pageContext) {
        return OrgHelper.showOrgEntities(ids, type, pageContext);
    }

    private static String getOrgEntitiesSeparator(PageContext pageContext) {
        String key = "common.separator.label";
        String separator = ResourceUtil.getString(key);
        if(key.equals(separator) && pageContext!=null)separator = _(pageContext, key);
        if(key.equals(separator)){
        	separator = "、";
        }
        return separator;
    }

    public static String showOrgEntities(List<Object[]> entities, String separator){
    	return OrgHelper.showOrgEntities(entities, separator);
    }


    /**
     * 将授权、发布范围等信息连接成elements 格式EntityType|EntityId|EntityName|AccountId<br>
     * 注意：id或者type为null，以及id=-1的将被过滤掉
     *
     * <pre>
     * public class TempleteAuth extends BaseModel implements Serializable {
     *  private Long authId;
     *  private String authType;
     *  private Integer sort;
     *  private long templeteId;
     *
     *  //setter / getter
     * }
     *
     * 转换
     * parseElements(List<TempleteAuth>, "authId", "authType")
     *
     * 结果
     * Member|1234123|谭敏锋|34561234,Department|234534563|开发中心|34561234
     * </pre>
     *
     * @param list
     *            发布范围、授权集合
     * @param idProperty
     *            组织模型实体的Id字段的属性
     * @param typeProperty
     *            组织模型实体的类型字段的属性
     * @param accountType
     *            组织模型实体的所属单位字段的属性
     * @return
     */
    public static String parseElements(Collection<? extends Object> list,
            String idProperty, String typeProperty) {
    	return OrgHelper.parseElements(list, idProperty, typeProperty);
    }

    /**
     * 将格式为EntityType|EntityId,EntityType|EntityId的数据转换成Element[]
     *
     * @param typeAndIds Member|1234123,Department|234534563
     * @return
     */
    public static String parseElements(String typeAndIds) {
        return OrgHelper.parseElements(typeAndIds);
    }
    
    /**
     * 将格式为EntityId,EntityId的数据转换成Element[]
     *
     * @param ids 1234123,234534563
     * @param type 指定类型
     * @return
     */
    public static String parseElements(String ids, String type) {
        return OrgHelper.parseElements(ids, type);
    }

    /**
     * 与当前登录者比较是否是同一个单位的（包括兼职单位）
     *
     * @param memberId 被检测对象
     * @return true-是同一个单位的
     */
    public static boolean isSameAccount(Long memberId){
        try {
            List<V3xOrgAccount> myAccounts = concurrentAccount();
            long aId = getMember(memberId).getOrgAccountId();

            for (V3xOrgAccount account : myAccounts) {
                if(account.getId().equals(aId)){
                    return true;
                }
            }
        }
        catch (Exception e1) {
            log.error("", e1);
        }

        return false;
    }

    /**
     * 该单位是否是我的单位（包含兼职）
     *
     * @param accountId
     * @return
     */
    public static boolean isMyAccount(Long accountId){
        List<V3xOrgAccount> myAccounts = concurrentAccount();
        for (V3xOrgAccount account : myAccounts) {
            if(account.getId().equals(accountId)){
                return true;
            }
        }

        return false;
    }

    public static String toString(Object o){
        return String.valueOf(o);
    }

    public static Set<Object> keys(Map<Object, Object> map){
        if(map != null){
            return map.keySet();
        }

        return null;
    }
    public static Collection<Object> mapValues(Map<Object,Object> map){
        if(map != null){
            return map.values();
        }
        return null;
    }
    @SuppressWarnings("deprecation")
    public static String encodeURI(String p){
        try {
            return java.net.URLEncoder.encode(p,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            return java.net.URLEncoder.encode(p);
        }
    }

    public static ApplicationCategoryEnum getApplicationCategoryEnum(int key){
        return ApplicationCategoryEnum.valueOf(key);
    }

    public static ApplicationSubCategoryEnum getApplicationSubCategoryEnum(int key){
        return ApplicationSubCategoryEnum.valueOf(key);
    }

    /**
     *
     * @param list
     * @param o
     * @return
     */
    public static List<Object> addToList(List<Object> list, Object o){
    	List<Object> l = list ==null ? new ArrayList<Object>():list;

        l.add(o);

        return l;
    }

    /**
     * 读取系统标志
     *
     * @param flagName 标志名称
     * @return
     */
    public static Object getSysFlag(String flagName){
    	return OrgHelper.getSysFlag(flagName);
    }

    /**
     * 读取系统标志
     *
     * @param sysFlag
     * @return
     */
    public static Object getSysFlag(SysFlag sysFlag){
        return OrgHelper.getSysFlag(sysFlag);
    }
    
    /**
     * 判断是否为多组织版
     * @return
     */
    public static boolean isGroupVer(){
    	return (Boolean) (SysFlag.sys_isGroupVer.getFlag());
    }

    /**
     * 根据当前用户判断浏览器差异
     * @param flagName
     * @param user
     * @return
     */
    public static Object getBrowserFlag(String flagName, User user){
        BrowserFlag browserFlag = BrowserFlag.valueOf(flagName);
        return browserFlag.getFlag(user);
    }

    /**
     * 根据request请求判断浏览器差异
     * @param flagName
     * @param request
     * @return
     */
    public static Object getBrowserFlag(String flagName, HttpServletRequest request){
        BrowserFlag browserFlag = BrowserFlag.valueOf(flagName);
        return browserFlag.getFlag(request);
    }

    public static String bodyTypeSelector(String v3xjsObj){
        boolean hasPluginOffice=isOfficeOcxEnable();//SystemEnvironment.hasPlugin("officeOcx");
        boolean hasPluginPdf=SystemEnvironment.hasPlugin("pdf");
        return "createOfficeMenu("+v3xjsObj+","+hasPluginOffice+","+hasPluginPdf+")";
    }

    /**
     * 是否安装了Office控件
     * @return
     */
    public static boolean isOfficeOcxEnable(){
        return SystemEnvironment.hasPlugin("officeOcx");
    }

    public static String urlEncoder(String s) {
        try {
            return java.net.URLEncoder.encode(s, "UTF-8");
        }
        catch (Exception e) {
        }

        return null;
    }

    public static String urlDecoder(String s) {
        try {
            return java.net.URLDecoder.decode(s, "UTF-8");
        }
        catch (Exception e) {
        }

        return null;
    }

    /**
     * 对字符串进行轻度加密
     * @param s
     * @return
     */
    public static String encodeStr(String s) {
        try {
            if(Strings.isNotBlank(s))
                return LightWeightEncoder.encodeString(s);
        }
        catch (Exception e) {
        }

        return null;
    }

    /**
     * 对轻度加密过的字符串进行解密
     * @param s
     * @return
     */
    public static String decodeStr(String s) {
        try {
            if(Strings.isNotBlank(s))
                return LightWeightEncoder.decodeString(s);
        }
        catch (Exception e) {
        }

        return null;
    }

    /**
     * 得到应用类别的显示文本，支持插件，需要在插件定义文件中配置applicationCategory属性
     *
     * @param applicationCategory
     * @param pageContext
     * @return
     */
    public static String getApplicationCategoryName(Integer applicationCategory, PageContext pageContext){
        if (applicationCategory == null) {
            return "";
        }
		if (applicationCategory > 2000) {
			applicationCategory = ApplicationCategoryEnum.ThirdPartyIntegration.getKey();
		}
        if(applicationCategory < 100||applicationCategory==101||applicationCategory==12111||applicationCategory==105 || applicationCategory==106){
            if(pageContext == null){
                return ResourceUtil.getString("application." + applicationCategory + ".label");
            } else {
                return ResourceBundleUtil.getString(pageContext, "application." + applicationCategory + ".label");
            }
        }
        else{
            try {
                Method mainMethod = clazz1.getMethod("getInstance");
                Object obj = mainMethod.invoke(null, null);
                Method initMethod = clazz1.getMethod("getPluginApplicationCategoryName", new Class[] { int.class });
                return (String) initMethod.invoke(obj, new Object[] { applicationCategory });
            }catch(Exception e) {
                log.error("Error while plugin system not loaded", e);
                return "Error while plugin system not loaded";
            }
        }
    }

    /**
     * 判断当前单位是否是集团下的单位
     * @throws BusinessException
     *
     */
    public static boolean isAccountInGroup(Long accountId) {
        try {
            return getOrgManager().isAccountInGroupTree(accountId);
        }
        catch (Exception e) {
            return false;
        }
    }


    /**
     * 用Integer作为Map的key时，JSTL的表达式 ${map[1]}是不能返回值的，故提供该方法
     *
     * @param map
     * @param key
     * @return
     */
    public static Object getMapValueOfIntegerKey(Map<Integer, ? extends Object> map, Integer key){
        if(map == null){
            return null;
        }

        return map.get(key);
    }

    /**
     * 判断当前在线用户是否在其主单位而非兼职单位
     * @param memberId
     * @param accountId
     * @return
     */
    public static boolean isUsersMainAccount(Long memberId, Long accountId){
        V3xOrgMember m = getMember(memberId);
        if(m != null){
            return m.getOrgAccountId().equals(accountId);
        }
        return false;
    }

    /**
     * 取得用户的兼职部门岗位等信息
     * @return
     */
    public static Map<String, String> getPluralityInfo4User(Long memberId, Long accountId){
        Map<String, String> result = new HashMap<String, String>();
        try {
        	List<MemberPost> memberPosts = getOrgManager().getMemberPosts(accountId, memberId);
        	if(!CollectionUtils.isEmpty(memberPosts)){
        		MemberPost mp = memberPosts.get(0);
                V3xOrgDepartment dept = getOrgManager().getDepartmentById(mp.getDepId());
                V3xOrgPost post =  getOrgManager().getPostById(mp.getPostId());
                result.put("departmentSimpleName", dept.getName());
                result.put("departmentPath", dept.getPath());
                result.put("departmentId", dept.getId().toString());
                result.put("postName", post.getName());        		
        	}
        }
        catch (BusinessException e) {
            log.error(e.getLocalizedMessage(), e);
        }
        return result;
    }
    /**
     * 内部人员访问外部人员，同时检查这个外部人员的工作范围
     * @param memberId
     * @param outerId
     * @return
     * @throws BusinessException
     */
    public static boolean canReadOuter(Long memberId, Long outerId) throws BusinessException {
        List<V3xOrgMember> canReadList = orgManager.getMemberWorkScopeForExternal(memberId,false);
        for (V3xOrgMember m : canReadList) {
            if(Strings.equals(m.getId(), outerId)) {
                return true;
            }
        }
        return false;
    }
    /**
     * 检测工作范围
     *
     * @param currentMemberId 当前登录者
     * @param memberId 被访问人
     * @return true:当前登录者可以访问被访问人
     */
    public static boolean checkLevelScope(Long currentMemberId, Long memberId){
    	return OrgHelper.checkLevelScope(currentMemberId, memberId);
    }

  //映射集团职务   by wusb 2010-09-25
    public static int mappingLevelSortId(V3xOrgMember member, V3xOrgMember currentMember) throws BusinessException{

        int currentMemberLevelSortId=0;
        V3xOrgLevel level = null;
        User user = AppContext.getCurrentUser();
        boolean isNeedCheckLevelScope=true;
        if(user.isAdministrator() || user.isGroupAdmin() || user.isSystemAdmin()){ //管理员默认不限制
            isNeedCheckLevelScope = false;
        }
        if (isNeedCheckLevelScope) {
            Map<Long, List<MemberPost>> concurrentPostMap = orgManager.getConcurentPostsByMemberId(
                    member.getOrgAccountId(), currentMember.getId());
            if (concurrentPostMap != null && !concurrentPostMap.isEmpty()) { //我在当前单位兼职
                Iterator<List<MemberPost>> it = concurrentPostMap.values().iterator();
                boolean isExist = false;
                while (it.hasNext()) {
                    List<MemberPost> cnPostList = it.next();
                    for (MemberPost cnPost : cnPostList) {
                        if (cnPost != null) {
                            if(cnPost.getDepId().equals(member.getOrgDepartmentId())) {
                                return 0;
                            }
                            Long cnLevelId = cnPost.getLevelId();
                            if (cnLevelId != null) {
                                V3xOrgLevel cnLevel = orgManager.getLevelById(cnLevelId);
                                if (cnLevel != null) {
                                    currentMemberLevelSortId = cnLevel.getLevelId();
                                    isExist = true;
                                    break;
                                } else {
                                    level = getOrgManager().getLowestLevel(member.getOrgAccountId());
                                    currentMemberLevelSortId = level != null ? level.getLevelId().intValue() : 0;
                                }
                            }
                        }
                    }
                    if (isExist) {
                        break;
                    }
                }
                return currentMemberLevelSortId;
            }
            //当前人员查看兼职到自己单位的人
            Map<Long, List<MemberPost>> concurrentPostMap2 = orgManager.getConcurentPostsByMemberId(
                    currentMember.getOrgAccountId(), member.getId());
            if(concurrentPostMap2 != null && !concurrentPostMap2.isEmpty()) {
                Iterator<List<MemberPost>> it = concurrentPostMap2.values().iterator();
                while (it.hasNext()) {
                    List<MemberPost> cnPostList = it.next();
                    for (MemberPost memberPost : cnPostList) {
                        Long cnLevelId = memberPost.getLevelId();
                        V3xOrgLevel cnLevel = orgManager.getLevelById(cnLevelId);
                        if(null == cnLevel) {
                            level = getOrgManager().getLowestLevel(currentMember.getOrgAccountId());
                            return level != null ? level.getLevelId().intValue() : 0;
                        } else {
                            return cnLevel.getLevelId();
                        }
                    }
                }
            }

            Long mappingGroupId = orgManager.getLevelById(currentMember.getOrgLevelId()).getGroupLevelId();
            Long levelIdOfGroup = (!currentMember.getOrgLevelId().equals(-1L)) ? mappingGroupId : Long.valueOf(-1); //当前登录者对应集团的职务级别id
            //切换单位的所有职务级别
            List<V3xOrgLevel> levels = getOrgManager().getAllLevels(member.getOrgAccountId());
            for (V3xOrgLevel lvl : levels) {
                if (levelIdOfGroup != null && levelIdOfGroup.equals(lvl.getGroupLevelId())) {
				    level = lvl;
				    break;
				}
            }
            if (level == null) {
                level = getOrgManager().getLowestLevel(member.getOrgAccountId()); //最低职务级别
            }

            if (level != null) {
                currentMemberLevelSortId = level.getLevelId();
            }
        }
        return currentMemberLevelSortId;
    }

    /**
     * 添加Cookie
     *
     * @param response
     * @param name
     * @param value
     * @param isForever 是否永久有效
     * @param isEncode 是否加密
     */
    public static void addCookie(HttpServletResponse response, String name,
            String value, boolean isForever, boolean isEncode){
        int expires = 0;
        if(isForever){
            expires = Cookies.COOKIE_EXPIRES_FOREVER;
        }

        Cookies.add(response, name, value, expires, isEncode);
    }

    /**
     * 读取Cookie值
     *
     * @param request
     * @param name
     * @param isEncode
     * @return
     */
    public static String getCookie(HttpServletRequest request, String name, boolean isEncode){
        return Cookies.get(request, name, isEncode);
    }



    /**
     * 取得皮肤Path , 格式如 "/common/skin/default4GOV"
     */
    public static String getSkin(){
        User currentUser = currentUser();
		return "/common/skin/"+(currentUser==null?"default":currentUser.getSkin());//+ com.seeyon.v3x.skin.Constants.getUserSkinSuffix();
    }
    public static String skin(){
        return "<link href=\"" +SystemEnvironment.getContextPath()+ getSkin() + "/skin.css" + resSuffix() + "\" type=\"text/css\" rel=\"stylesheet\">"
                +"<script type=\"text/javascript\">var skinType = '" +SystemEnvironment.getContextPath()+ getSkin() + "';</script>" ;
        }

    public static String getXUA(){
        if((Boolean)BrowserFlag.XUA.getFlag(WebUtil.getRequest())){
            return "<meta http-equiv=X-UA-Compatible content=IE=EmulateIE9>";
        }else{
            return "";
        }
    }
    /**
     *
     */
    public static String showAgentMemberName(Long memberId, Integer appKey){
        String agentName = "";
        try{
            if( !currentUser().getId().equals(memberId)){
                V3xOrgMember member = getMember(memberId);
                if(member == null){
                    return agentName;
                }
                agentName = member.getName();
            }else{
            	Integer key = appKey;
                if(appKey.equals(ApplicationCategoryEnum.edocRec.key())
                        || appKey.equals(ApplicationCategoryEnum.edocRegister.key())
                        || appKey.equals(ApplicationCategoryEnum.edocSend.key())
                        || appKey.equals(ApplicationCategoryEnum.edocSign.key())
                        || appKey.equals(ApplicationCategoryEnum.exSign.key())
                        || appKey.equals(ApplicationCategoryEnum.exSend.key())){
                    key = ApplicationCategoryEnum.edoc.key();
                }
                // TODO
/*                Long agentToId = MemberAgentBean.getInstance().getAgentMemberId(key, currentUser().getId());
                if(agentToId != null){
                    V3xOrgMember member = getMember(agentToId);
                    agentName = member.getName();
                }*/
            }
        }catch(Exception e){
            log.error("", e);
        }
        return agentName;
    }

    /**
     * 是否启用公文
     * @return
     */
    public static boolean isEnableEdoc(){
        boolean isEnable = true;
        String enableEdocConfig = getSystemConfig().get(IConfigPublicKey.EDOC_ENABLE);
        if(enableEdocConfig != null){
            isEnable = "enable".equals(enableEdocConfig);
        }
        return isEnable;
    }

    /**
     * 是否启用系统开关
     * @param key 系统开关关键字
     * @return
     */
    public static boolean isEnableSwitch(String key){
        boolean isEnable = false;
        String enableSwitchConfig = null;
        if(IConfigPublicKey.EDOC_ENABLE.equals(key) || "Edoc".equals(key)){
            enableSwitchConfig = getSystemConfig().get(IConfigPublicKey.EDOC_ENABLE);
        }
        else if(IConfigPublicKey.READ_STATE_ENABLE.equals(key) || "ReadState".equals(key)){
            enableSwitchConfig = getSystemConfig().get(IConfigPublicKey.READ_STATE_ENABLE);
        }
        //TODO 其他添加在这里

        if(enableSwitchConfig != null){
            isEnable = "enable".equals(enableSwitchConfig);
        }
        return isEnable;
    }

    /**
     * 国际化Label的后缀， 用于支持政务版的key.<br>
     * 政务版与集团版的Label不同时，需要增加一个以.GOV区分的后缀，引用该key后附加这个<br>
     * 如：<fmt:message key='menu.group.info.set${v3x:suffix()}'/>
     * 集团版引用key：menu.group.info.set<br>
     * 政务版key为：menu.group.info.set.GOV
     * @return
     */
    public static String suffix(){
       return OrgHelper.suffix();
    }
    
    public static String csrfSuffix(){
    	HttpSession session = AppContext.getRawSession();
		if(session==null){
    		return "";
    	}
    	try {
			Object token = session.getAttribute("CSRFTOKEN");
			return token==null ? "" : "&CSRFTOKEN=" + token;
		} catch (Exception e) {
			return "";
		}
    }

    public static String oemSuffix(){
        return (String)SysFlag.NCSuffix.getFlag();
    }
    public static String oemSuffixInJS(){
        String suffix = (String)SysFlag.NCSuffix.getFlag().toString();
        return suffix.replace(".", "_");
    }
    /**
     * JS国际化Label的后缀， 用于支持政务版的key.<br>
     * 政务版与集团版的Label不同时，需要增加一个以_GOV区分的后缀，引用该key后附加这个<br>
     * 集团版引用key：group_info_set<br>
     * 政务版key为：group_info_set_GOV<br>
     * JSP调用:var msgLabel = "group_info_label" + "${v3x:suffixInJS()}");
     * @return
     */
    public static String suffixInJS(){
       String suffix = (String)SysFlag.EditionSuffix.getFlag().toString();
       return suffix.replace(".", "_");
    }

    private static String resSuffix = null;
    
    private static String resSuffixWithoutQuestionMark = null;
    /**
     * 静态资源文件的后缀.（每次发版需要更新该值）<br>
     * 用于解决客户端缓存问题导致的CSS、JS、SWF等的加载异常问题
     * @return ?V=320_20100
     */
    public static String resSuffix(){
        if(resSuffix == null){
            String date = SystemProperties.getInstance().getProperty("product.build.date");
            if(Strings.isNotEmpty(date)) {
            	date = date.replaceAll("-", "");
            	date = date.substring(2);
            }
			resSuffix = "?V=" + ProductVersionEnum.getCurrentVersion().name() + "_" + date + "_" + SystemEnvironment.getProductBuildVersion();//SystemEnvironment.getPluginCRC();
        }
        ConfigManager configManager = (ConfigManager)AppContext.getBean("configManager");
		ConfigItem jsCacheRest= configManager.getConfigItem(SystemConfig.SWITCH,IConfigPublicKey.JS_CACHE_REST);
		String tag = jsCacheRest != null?jsCacheRest.getConfigValue():"0";
        return resSuffix+tag;
    }

    /**
     * 国际化js资源文件的后缀,重启和管理员更改词条记录更新该值<br>
     * 用于解决客户端缓存问题导致的国际化值加载异常问题
     * @return ?timestamp=320_20100
     */
    public static String i18nSuffix(){
        //环境启动后生成的国际化时间戳 + 管理员更新国际化生成的时间戳
        return "&timestamp=" + I18nResource.getI18ntimestamp() + "_" + I18nResourceCacheHolder.getI18nTimestamp();
    }
    
    /**
     * 静态资源文件的后缀.（每次发版需要更新该值）<br>
     * 用于解决客户端缓存问题导致的CSS、JS、SWF等的加载异常问题
     * @return ?V=320_20100
     */
    public static String resSuffixWithoutQuestionMark(){ 
        if(resSuffixWithoutQuestionMark == null){
        	resSuffixWithoutQuestionMark = "&V=" + ProductVersionEnum.getCurrentVersion().name() + "_" + SystemProperties.getInstance().getProperty("product.build.date") + "_" +SystemEnvironment.getPluginCRC();
        }
        	ConfigManager configManager = (ConfigManager)AppContext.getBean("configManager");
    		ConfigItem jsCacheRest= configManager.getConfigItem(SystemConfig.SWITCH,IConfigPublicKey.JS_CACHE_REST);
    		String tag = jsCacheRest != null?jsCacheRest.getConfigValue():"0";
           
        return resSuffixWithoutQuestionMark+tag;
    }
    
    //临时调试用的
    public static void setResSuffix(String _resSuffix){
        resSuffix = _resSuffix;
    }

    /**
     * 得到被代理人的ID（我给<谁>干活）
     */
    public static Long getMyAgentId(Integer appEnum){
    	Long agentId = -1L;
    	// TODO
/*        List<AgentModel> agentModelList = MemberAgentBean.getInstance().getAgentModelList(currentUser().getId());
        if(agentModelList != null && !agentModelList.isEmpty()){
            for(AgentModel agentModel : agentModelList){
                String agentOptionStr = agentModel.getAgentOption();
                if(agentOptionStr.indexOf(String.valueOf(appEnum)) != -1){
                    agentId = agentModel.getAgentToId();
                    break;
                }
            }
        }*/
        return agentId;
    }

    /**
     * 根据类型和ID得到所有人员Id，包括兼职<br>
     * <ul>应用场景：
     * <li>1、模板推送时根据授权类型获取所有人员ID，（orgManager.getMembersByType接口不能返回兼职人员）</li>
     * @param type
     * @param id
     * @return
     */
    public static Set<Long> getAllMembersId(String type, Long id){
        Set<Long> memberIds = new HashSet<Long>();
        if(V3xOrgEntity.ORGENT_TYPE_MEMBER.equals(type)){
            memberIds.add(id);
            return memberIds;
        }

        List<V3xOrgMember> memberList = new ArrayList<V3xOrgMember>();
        if(V3xOrgEntity.ORGENT_TYPE_ACCOUNT.equals(type)){
            try{
                memberList = getOrgManager().getAllMembers(id);

                Map<Long, List<V3xOrgMember>> parttimeMap = getOrgManager().getConcurentPostByAccount(id); //兼职
                if(parttimeMap != null && !parttimeMap.isEmpty()){
                    Set<Map.Entry<Long, List<V3xOrgMember>>> enities = parttimeMap.entrySet();
                    for (Map.Entry<Long, List<V3xOrgMember>> entry : enities) {
                        for(V3xOrgMember m : entry.getValue()){
                            memberIds.add(m.getId()); //直接放入结果，不追加到List再转换
                        }
                    }
                }
            }
            catch (BusinessException e) {
                log.error("", e);
            }
        }
        else if(V3xOrgEntity.ORGENT_TYPE_DEPARTMENT.equals(type)){
            try{
                V3xOrgDepartment department = getOrgManager().getDepartmentById(id);
                if(department != null && department.getIsInternal()){
                    memberList = getOrgManager().getMembersByDepartment(id, false);
                }else{
                    memberList = getOrgManager().getExtMembersByDepartment(id, false);
                }
            }
            catch (BusinessException e) {
                log.error("", e);
            }
        }
        else if(V3xOrgEntity.ORGENT_TYPE_LEVEL.equals(type)){
            try{
                memberList = getOrgManager().getMembersByLevel(id);
            }
            catch (BusinessException e) {
                log.error("", e);
            }
        }
        else if(V3xOrgEntity.ORGENT_TYPE_POST.equals(type)){
            try{
                memberList = getOrgManager().getMembersByPost(id);
            }
            catch (BusinessException e) {
                log.error("", e);
            }
        }
        else if(V3xOrgEntity.ORGENT_TYPE_TEAM.equals(type)){

            try {
                memberList = getOrgManager().getMembersByTeam(id);
            } catch (BusinessException e) {
                log.error("", e);
            }
        }

        if(memberList != null && !memberList.isEmpty()){
            for (V3xOrgMember m : memberList) {
                memberIds.add(m.getId());
            }
        }
        return memberIds;
    }

    public static V3xOrgAccount getGroup(){
        try{
            return getOrgManager().getRootAccount();
        }catch(Exception e){
            log.error("",e);
        }
        return null;
    }

    public static String getVersion(){
    	if (ProductEditionEnum.isNCOEM()||ProductEditionEnum.isU8OEM()) {
    		return ProductVersionEnum.getCurrentVersion().getOEMVersion(ProductEditionEnum.isNCOEM(), ProductEditionEnum.isU8OEM());
    	} else {
    	    return ProductVersionEnum.getCurrentVersion().getCanonicalVersion();
    	}
    }

    public static String getPageTitle(){
        ConfigItem configItemLogin = getConfigManager().getConfigItem("System_Login_Title", "loginTitleName");
        String title = null;
        if(configItemLogin != null){
            title = configItemLogin.getConfigValue();
        }
        else{
            title = ResourceUtil.getString("common.page.title" + oemSuffix() + suffix());
        }

        title += " " + getVersion();

        User user = AppContext.getCurrentUser();
		if(user != null && !user.isGuest()){
            title += ", " + ResourceUtil.getString("welcome.label", user.getName());
        }

        return title;
    }

    /**
     * 判断此人是否在这个单位是该角色
     * @param roleName 角色名称
     * @param user     人员对象
     * @return
     */
    public static boolean isRole(String roleName, User user) {
        try {
            return getOrgManager().isRole(user.getId(), user.getLoginAccount(), roleName);
        } catch (BusinessException e) {
           log.error(e.getLocalizedMessage(),e);
        }
        return false;
    }
    
    public static boolean isRoleByCode(String roleName) {
        return isRole(roleName,currentUser());
    }

    public static OnlineUser getOnlineUser(Long memberId) {
        V3xOrgMember member = getMember(memberId);
        if (member == null) {
            return null;
        }

        return OnlineRecorder.getOnlineUser(member.getLoginName());
    }

    /**
     * 显示完成率或进度等百分比数字时，将百分比数字只显示为整数
     */
    public static String showRate(Float f) {
        return showRate(f, false);
    }

    /**
     * 显示完成率或进度等百分比数字时，将百分比数字只显示为整数
     * 视选择加上百分号
     * @param f             浮点数
     * @param showPercent   是否显示百分比
     */
    public static String showRate(Float f, Boolean showPercent) {
        String percent = showPercent ? "%" : "";
        String ret = "0";
        if(f != null && f > 0.0f) {
            String rate = String.valueOf(f);
            if(rate.indexOf('.') != -1) {
                ret = rate.substring(0, rate.indexOf('.'));
            }
        }
        return ret + percent;
    }
    /**
     * 将小数显示为百分比的数字,保留两个小数
     * 例如 0.3933 显示为39.33%
     * @param d : 浮点数
     * @return
     */
    public static String showNumber2Percent(Number d){
        if(d==null)
            return "0.00%";
        NumberFormat num = NumberFormat.getPercentInstance();
        num.setMaximumIntegerDigits(10); //小数点前面最多显示几位的
        num.setMaximumFractionDigits(2); //小数点后面最多显示几位
        num.setMinimumFractionDigits(2);
        return num.format(d);
    }



    public static void selectTree(Collection<? extends Object> list, String idProperty, String parentProperty, String textProperty, PageContext pageContext) {
        if (CollectionUtils.isNotEmpty(list)) {
            try {
                JspWriter out = pageContext.getOut();
                StringBuilder sb = new StringBuilder();
                for (Object object : list) {
                    String nodeId = String.valueOf(PropertyUtils.getProperty(object, idProperty));
                    String nodeParentId = String.valueOf(PropertyUtils.getProperty(object, parentProperty));
                    String nodeText = String.valueOf(PropertyUtils.getProperty(object, textProperty));
                    sb.append("{" +
                                    "id:'" + nodeId + "'," +
                                    "pId:'" + nodeParentId + "'," +
                                    "name:'" + nodeText + "'," +
                                    "iconSkin:'" + ("-1".equals(nodeParentId) ? "nodeRoot" : "nodeChildren") + "'" +
                                    ("-1".equals(nodeParentId) ? ",'open':'true'" : "")
                            + "},");
                }
                if (sb.length() > 0) {
                    out.print(sb.substring(0, sb.length() - 1));
                }
            } catch (Exception e) {
                log.error("", e);
            }
        }
    }



    /**
     * 获取系统开关配置值
     *
     * @param name
     * @return
     */
    public static String getSystemSwitch(String name){
    	return OrgHelper.getSystemSwitch(name);
    }

    /**
     * 检查请求IP,是否一定应用CA登录
     * @param request
     * @return
     */
    public static String caCheckUserIP(HttpServletRequest request) {
        final String configItemMustCheckCA = "MustCheckCA";
        final String configCategory = "IdentificationValidateCA";
        String showLoginWay = "";
        ConfigItem configItem = configManager.getConfigItem(configCategory,
                configItemMustCheckCA);
        if ( configItem != null && "true".equals(configItem.getConfigValue())) {
            String ipString = configItem.getExtConfigValue();
            showLoginWay = ipString.indexOf(Strings.getRemoteAddr(request)) > -1?"IPisIncluding":"IPisNotIncluding";
        }else{
            showLoginWay = "unCheckIP";
        }
        return showLoginWay;
    }




    /**
     * 得到枚举的顺序号
     *
     * @param e
     * @return
     */
    public static int getEnumOrdinal(Enum e){
        return e.ordinal();
    }
    /**
     * 得到产品升级的时间
     * 将安装时间与2012-04-30进行比较，取最靠后的时间。
     * eg. 安装时间为2011-01-01.则这个函数返回2012-04-30,  如果安装时间为2012-09-30,则返回2012-09-30
     */
    public static String getProductInstallDate4WF(){
        Date d = SystemEnvironment.getProductInstallDate();
        Calendar cal = Calendar.getInstance();
        //2012-04-30 : V3.5发版，流程效率分析模块开始运行
        cal.set(Calendar.YEAR, 2012);
        cal.set(Calendar.MONTH, 3);
        cal.set(Calendar.DAY_OF_MONTH,30);
        Date sDate = cal.getTime();
        String date = "";

//      if(d.before(sDate)){
//          d = sDate;
//      }

        if(d!=null){
            date = Datetimes.formatDate(d);
        }
        return date;
    }

    /**
     * 得到分页组件的条数
     * @return
     */
    public static int getPaginationRowCount(){
        return Pagination.getRowCount(false);
    }

    /**
     * 暂实现Map
     * @param o
     * @return
     */
    public static String toJson(Object o){
        try {
            if(o instanceof Map){
                JSObject js = new JSObject();
                for (Iterator<Map.Entry<?, ?>> iterator = ((Map)o).entrySet().iterator(); iterator.hasNext();) {
                    Map.Entry<?, ?> entry = iterator.next();
                    js.put(String.valueOf(entry.getKey()), entry.getValue());
                }

                return js.toString();
            }
        }
        catch (Exception e) {
        }

        return null;
    }

    /**
     * 流程期限换算成时间点
     * @param createTime 创建时间
     * @param deadline 流程期限，单位是分钟
     * @return 创建时间+流程期限（分钟）换算出来流程期限时间点
     */
    public static String  showDeadlineTime(String createTime,Long deadline){
        if(deadline==null || deadline<=0){
            return null;
        }
        Date date=Datetimes.parseDatetimeWithoutSecond(createTime);
        Date afterDate = new Date(date.getTime() +deadline*60*1000);
        return Datetimes.formatDatetimeWithoutSecond(afterDate);

    }
    
    /**
     * 取得指定人员头像图片地址。
     * @param memberId 人员Id。
     * @return 头像的url，包括上下文，形如http://192.168.0.1:8080/seeyon/fileUpload.do...
     */
    public static String getAvatarImageUrl(Long memberId){
        return OrgHelper.getAvatarImageUrl(memberId);
    }
    /**
     * 取得指定人员头像图片地址。
     * @param memberId 人员Id。
     * @return 头像的url，不包括上下文，形如/fileUpload.do...。上下文由contextPath参数指定。
     */
    public static String getAvatarImageUrl(Long memberId, String contextPath) {
        return OrgHelper.getAvatarImageUrl(memberId, contextPath);
    }
    public static String trimXSSHtml(String html){
    	
    	return Strings.trimXSSHtml(html);
    }
    
    public static String evalFormulaString(String text) throws ScriptException, BusinessException{
    	if(Strings.isEmpty(text)){
    		return text;
    	}
    	if(text.indexOf("$")<0){
    		return text;
    	}
    	try {
    		return FormulaUtil.evalString(text, new HashMap());
		} catch (Throwable e) {
			log.error("表达式计算错误:", e);
			return text;
		}
    }
    private static final Class clazz1 = MclclzUtil.ioiekc("com.seeyon.ctp.common.plugin.PluginSystemInit");

}