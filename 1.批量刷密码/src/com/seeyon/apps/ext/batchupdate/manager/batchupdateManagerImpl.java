package com.seeyon.apps.ext.batchupdate.manager;

import com.seeyon.apps.ext.batchupdate.po.MidUser;
import com.seeyon.apps.ext.batchupdate.util.JdbcTool;
import com.seeyon.apps.ext.batchupdate.util.PropUtil;
import com.seeyon.apps.ext.logRecord.dao.LogRecordDao;
import com.seeyon.apps.ext.logRecord.po.LogRecord;
import com.seeyon.apps.ldap.config.LDAPConfig;
import com.seeyon.apps.ldap.event.OrganizationLdapEvent;
import com.seeyon.apps.ldap.util.LdapUtils;
import com.seeyon.ctp.common.appLog.manager.AppLogManager;
import com.seeyon.ctp.common.authenticate.domain.User;
import com.seeyon.ctp.organization.bo.OrganizationMessage;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.bo.V3xOrgPrincipal;
import com.seeyon.ctp.organization.dao.OrgHelper;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.organization.principal.PrincipalManager;
import com.seeyon.ctp.util.JDBCAgent;
import com.seeyon.ctp.util.Strings;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.seeyon.apps.ext.batchupdate.dao.batchupdateDao;
import com.seeyon.ctp.common.AppContext;

import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;


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
//        List<OrgPrincipal> list = batchupdateDao.selectAll();
        List<MidUser> list = this.joinIDD();
        //
        PrincipalManager principalManager = (PrincipalManager) AppContext.getBean("principalManager");
//        String password = propUtil.getValueByKey("init.pwd");
        AtomicReference<String> password = new AtomicReference<>("");
        list.forEach(orgPrincipal -> {
            password.set(orgPrincipal.getIdd().substring(12));
            Long createTime = orgPrincipal.getCreateTime().getTime();
            Long updateTime = orgPrincipal.getUpdateTime().getTime();
//            差值
            Long difference = updateTime - createTime;
            if (difference.longValue() == 0l) {
                if (!wList.contains(orgPrincipal.getLoginName())) {
                    try {
                        V3xOrgMember member = this.orgManager.getMemberById(orgPrincipal.getMemberId().longValue());
                        V3xOrgMember memberBeforeUpdate = new V3xOrgMember();
                        memberBeforeUpdate.setId(member.getId());
                        memberBeforeUpdate.setV3xOrgPrincipal(member.getV3xOrgPrincipal());

                        V3xOrgPrincipal newOrgPrincipal = new V3xOrgPrincipal(member.getId(), orgPrincipal.getLoginName(), password.get());
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
                        long rand = Math.round(Math.random() * 100);
                        logRecord.setId(System.currentTimeMillis() + rand);
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

    public void extractData() {
        String sql = "select oa_id,p_id from mid_user where oa_id is not null and p_id is not null";
        String inSql = "insert into mid_user(oa_id,idd) values (?,?)";
        String deleteSql = "delete from mid_user";
        ResultSet rs = null;
        try (Connection connection = JdbcTool.getMidConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             Connection oaConn = JDBCAgent.getRawConnection();
             Connection oaConnDe = JDBCAgent.getRawConnection();
             PreparedStatement oaPs = oaConn.prepareStatement(inSql);
             PreparedStatement oaPsDele = oaConnDe.prepareStatement(deleteSql);
        ) {
            oaPsDele.executeUpdate();

            oaConn.setAutoCommit(false);
            rs = ps.executeQuery();
            while (rs.next()) {
                if (null != rs.getBigDecimal("oa_id")) {
                    oaPs.setBigDecimal(1, rs.getBigDecimal("oa_id"));
                    if (null != rs.getString("p_id") && !"".equals(rs.getString("p_id"))) {
                        oaPs.setString(2, rs.getString("p_id"));
                    }
                }
                oaPs.addBatch();
            }
            oaPs.executeBatch();
            oaConn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (null != rs) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public List<MidUser> joinIDD() {
        String sql = "select p.id,p.login_name,p.create_time,p.update_time,p.member_id,m.idd from ORG_PRINCIPAL p,mid_user m where p.MEMBER_ID=m.oa_id";
        ResultSet rs = null;
        List<MidUser> list = new ArrayList<>();
        try (Connection connection = JDBCAgent.getRawConnection();
             PreparedStatement ps = connection.prepareStatement(sql);) {
            rs = ps.executeQuery();
            MidUser midUser = null;
            while (rs.next()) {
                midUser = new MidUser();
                midUser.setId(rs.getBigDecimal("id"));
                midUser.setLoginName(rs.getString("login_name"));
                midUser.setCreateTime(rs.getTimestamp("create_time"));
                midUser.setUpdateTime(rs.getTimestamp("update_time"));
                midUser.setIdd(rs.getString("idd"));
                midUser.setMemberId(rs.getBigDecimal("member_id"));
                list.add(midUser);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}