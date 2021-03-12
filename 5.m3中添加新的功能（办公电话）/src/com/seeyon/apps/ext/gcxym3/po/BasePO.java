package com.seeyon.apps.ext.gcxym3.po;

import java.sql.Timestamp;

public class BasePO {
    private Long id;
    private Long state;
    private String startMemberId;
    private Timestamp startDate;
    private String approveMemberId;
    private Timestamp approveDate;
    private Long finishedflag;
    private Long ratifyflag;
    private String ratifyMemberId;
    private Timestamp ratifyDate;
    private Long sort;
    private String modifyMemberId;
    private Timestamp modifyDate;

    public BasePO() {
    }

    public BasePO(Long id, Long state, String startMemberId, Timestamp startDate, String approveMemberId, Timestamp approveDate, Long finishedflag, Long ratifyflag, String ratifyMemberId, Timestamp ratifyDate, Long sort, String modifyMemberId, Timestamp modifyDate) {
        this.id = id;
        this.state = state;
        this.startMemberId = startMemberId;
        this.startDate = startDate;
        this.approveMemberId = approveMemberId;
        this.approveDate = approveDate;
        this.finishedflag = finishedflag;
        this.ratifyflag = ratifyflag;
        this.ratifyMemberId = ratifyMemberId;
        this.ratifyDate = ratifyDate;
        this.sort = sort;
        this.modifyMemberId = modifyMemberId;
        this.modifyDate = modifyDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getState() {
        return state;
    }

    public void setState(Long state) {
        this.state = state;
    }

    public String getStartMemberId() {
        return startMemberId;
    }

    public void setStartMemberId(String startMemberId) {
        this.startMemberId = startMemberId;
    }

    public Timestamp getStartDate() {
        return startDate;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    public String getApproveMemberId() {
        return approveMemberId;
    }

    public void setApproveMemberId(String approveMemberId) {
        this.approveMemberId = approveMemberId;
    }

    public Timestamp getApproveDate() {
        return approveDate;
    }

    public void setApproveDate(Timestamp approveDate) {
        this.approveDate = approveDate;
    }

    public Long getFinishedflag() {
        return finishedflag;
    }

    public void setFinishedflag(Long finishedflag) {
        this.finishedflag = finishedflag;
    }

    public Long getRatifyflag() {
        return ratifyflag;
    }

    public void setRatifyflag(Long ratifyflag) {
        this.ratifyflag = ratifyflag;
    }

    public String getRatifyMemberId() {
        return ratifyMemberId;
    }

    public void setRatifyMemberId(String ratifyMemberId) {
        this.ratifyMemberId = ratifyMemberId;
    }

    public Timestamp getRatifyDate() {
        return ratifyDate;
    }

    public void setRatifyDate(Timestamp ratifyDate) {
        this.ratifyDate = ratifyDate;
    }

    public Long getSort() {
        return sort;
    }

    public void setSort(Long sort) {
        this.sort = sort;
    }

    public String getModifyMemberId() {
        return modifyMemberId;
    }

    public void setModifyMemberId(String modifyMemberId) {
        this.modifyMemberId = modifyMemberId;
    }

    public Timestamp getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(Timestamp modifyDate) {
        this.modifyDate = modifyDate;
    }
}
