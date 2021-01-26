package com.seeyon.apps.ext.batchupdate.manager;

import com.seeyon.apps.ext.batchupdate.util.PropUtil;
import com.seeyon.apps.ext.logRecord.dao.LogRecordDao;
import com.seeyon.apps.ext.logRecord.po.LogRecord;
import com.seeyon.apps.ldap.config.LDAPConfig;
import com.seeyon.apps.ldap.event.OrganizationLdapEvent;
import com.seeyon.apps.ldap.util.LdapUtils;
import com.seeyon.ctp.common.appLog.AppLogAction;
import com.seeyon.ctp.common.appLog.manager.AppLogManager;
import com.seeyon.ctp.common.authenticate.domain.User;
import com.seeyon.ctp.common.security.MessageEncoder;
import com.seeyon.ctp.organization.bo.OrganizationMessage;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.bo.V3xOrgPrincipal;
import com.seeyon.ctp.organization.dao.OrgHelper;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.organization.po.OrgPrincipal;
import com.seeyon.ctp.organization.principal.PrincipalManager;
import com.seeyon.ctp.util.Strings;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.seeyon.apps.ext.batchupdate.dao.batchupdateDao;
import com.seeyon.ctp.common.AppContext;

import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class batchupdateManagerImpl implements batchupdateManager {
    private static final Log log = LogFactory.getLog(batchupdateManagerImpl.class);

    private batchupdateDao batchupdateDao = (batchupdateDao) AppContext.getBean("batchupdateDaoDemo");

    private LogRecordDao logRecordDao = (LogRecordDao) AppContext.getBean("logRecordDao");

    private OrgManager orgManager = (OrgManager) AppContext.getBean("orgManager");

    private AppLogManager appLogManager = (AppLogManager) AppContext.getBean("appLogManager");

    @Override
    public void batchUpdate() throws NoSuchAlgorithmException {
        User u = AppContext.getCurrentUser();
        PropUtil propUtil = new PropUtil();
        //白名单
        String[] arrs = {"admin1", "system", "audit-admin", "seeyon-guest"};
        List<String> wList = Arrays.asList(arrs);
        List<OrgPrincipal> list = batchupdateDao.selectAll();
        //
        PrincipalManager principalManager = (PrincipalManager) AppContext.getBean("principalManager");
        String password = propUtil.getValueByKey("init.pwd");
        list.forEach(orgPrincipal -> {
            Long createTime = orgPrincipal.getCreateTime().getTime();
            Long updateTime = orgPrincipal.getUpdateTime().getTime();
//            差值
            Long difference = updateTime - createTime;
            if (difference.longValue() == 0l) {
                if (!wList.contains(orgPrincipal.getLoginName())) {
                    try {
                        V3xOrgMember member = this.orgManager.getMemberById(orgPrincipal.getMemberId());
                        V3xOrgMember memberBeforeUpdate = new V3xOrgMember();
                        memberBeforeUpdate.setId(member.getId());
                        memberBeforeUpdate.setV3xOrgPrincipal(member.getV3xOrgPrincipal());
                        V3xOrgPrincipal newOrgPrincipal = new V3xOrgPrincipal(member.getId(), orgPrincipal.getLoginName(), password);
                        member.setV3xOrgPrincipal(newOrgPrincipal);
                        V3xOrgMember newMember = new V3xOrgMember();
                        newMember.setId(member.getId());
                        newMember.setV3xOrgPrincipal(newOrgPrincipal);
                        OrganizationMessage om = principalManager.update(newOrgPrincipal);
                        if (Strings.isNotEmpty(om.getErrorMsgs())) {
                            OrgHelper.throwBusinessExceptionTools(om);
                        }
                        this.appLogManager.insertLog(u, 1, u.getLoginName(), u.getName());

                        //记录更新了哪些
                        LogRecord logRecord = new LogRecord();
                        logRecord.setId(System.currentTimeMillis());
                        logRecord.setUpdateUser(u.getLoginName());
                        logRecord.setUpdateDate(new Date());
                        logRecord.setOpType("更新");
                        logRecord.setOpContent("用户：" + orgPrincipal.getLoginName() + "的密码被重置了！");
                        logRecord.setOpResult("成功");
                        logRecordDao.saveLogRecord(logRecord);
                        if (LdapUtils.isLdapEnabled() && LdapUtils.isBind(member.getId())) {
                            LDAPConfig config = LDAPConfig.getInstance();
                            String type = config.getSys().getProperty("ldap.ad.enabled");
                            if ("ad".equals(type) && config.getIsEnableSSL() || "ldap".equals(type)) {
                                OrganizationLdapEvent event = (OrganizationLdapEvent) AppContext.getBean("organizationLdapEvent");
                                event.changePassword(memberBeforeUpdate, newMember);
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
            }
        });

    }


}