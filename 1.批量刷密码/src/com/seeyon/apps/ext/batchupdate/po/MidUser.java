package com.seeyon.apps.ext.batchupdate.po;

import java.math.BigDecimal;
import java.util.Date;

public class MidUser {
    private BigDecimal id;
    private Date createTime;
    private Date updateTime;
    private String idd;
    private String loginName;
    private BigDecimal memberId;

    public BigDecimal getMemberId() {
        return memberId;
    }

    public void setMemberId(BigDecimal memberId) {
        this.memberId = memberId;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getIdd() {
        return idd;
    }

    public void setIdd(String idd) {
        this.idd = idd;
    }
}
