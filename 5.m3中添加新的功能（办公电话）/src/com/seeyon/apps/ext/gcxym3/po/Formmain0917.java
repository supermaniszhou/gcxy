package com.seeyon.apps.ext.gcxym3.po;

import java.sql.Timestamp;

public class Formmain0917 extends BasePO {
    private String field0003;
    private String field0001;
    private String field0002;
    private String field0004;
    private String field0005;
    private String field0006;
    private String field0007;
    private String field0008;
    private String field0009;
    private String receiveUnit;

    public Formmain0917() {
    }

    public Formmain0917(String field0003, String field0001, String field0002, String field0004, String field0005, String field0006, String field0007, String field0008, String field0009, String receiveUnit) {
        this.field0003 = field0003;
        this.field0001 = field0001;
        this.field0002 = field0002;
        this.field0004 = field0004;
        this.field0005 = field0005;
        this.field0006 = field0006;
        this.field0007 = field0007;
        this.field0008 = field0008;
        this.field0009 = field0009;
        this.receiveUnit = receiveUnit;
    }

    public Formmain0917(Long id, Long state, String startMemberId, Timestamp startDate, String approveMemberId, Timestamp approveDate, Long finishedflag, Long ratifyflag, String ratifyMemberId, Timestamp ratifyDate, Long sort, String modifyMemberId, Timestamp modifyDate, String field0003, String field0001, String field0002, String field0004, String field0005, String field0006, String field0007, String field0008, String field0009, String receiveUnit) {
        super(id, state, startMemberId, startDate, approveMemberId, approveDate, finishedflag, ratifyflag, ratifyMemberId, ratifyDate, sort, modifyMemberId, modifyDate);
        this.field0003 = field0003;
        this.field0001 = field0001;
        this.field0002 = field0002;
        this.field0004 = field0004;
        this.field0005 = field0005;
        this.field0006 = field0006;
        this.field0007 = field0007;
        this.field0008 = field0008;
        this.field0009 = field0009;
        this.receiveUnit = receiveUnit;
    }

    public String getField0003() {
        return field0003;
    }

    public void setField0003(String field0003) {
        this.field0003 = field0003;
    }

    public String getField0001() {
        return field0001;
    }

    public void setField0001(String field0001) {
        this.field0001 = field0001;
    }

    public String getField0002() {
        return field0002;
    }

    public void setField0002(String field0002) {
        this.field0002 = field0002;
    }

    public String getField0004() {
        return field0004;
    }

    public void setField0004(String field0004) {
        this.field0004 = field0004;
    }

    public String getField0005() {
        return field0005;
    }

    public void setField0005(String field0005) {
        this.field0005 = field0005;
    }

    public String getField0006() {
        return field0006;
    }

    public void setField0006(String field0006) {
        this.field0006 = field0006;
    }

    public String getField0007() {
        return field0007;
    }

    public void setField0007(String field0007) {
        this.field0007 = field0007;
    }

    public String getField0008() {
        return field0008;
    }

    public void setField0008(String field0008) {
        this.field0008 = field0008;
    }

    public String getField0009() {
        return field0009;
    }

    public void setField0009(String field0009) {
        this.field0009 = field0009;
    }

    public String getReceiveUnit() {
        return receiveUnit;
    }

    public void setReceiveUnit(String receiveUnit) {
        this.receiveUnit = receiveUnit;
    }
}
