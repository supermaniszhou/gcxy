package com.seeyon.apps.ext.logRecord.po;

import java.sql.Timestamp;
import java.util.Date;

public class LogRecord {

    private Long id;
    private String updateUser;//操作者
    private Date updateDate;//操作时间
    private String opContent;//操作内容
    private String opUserIp;//操作者的ip
    private String opType;//操作类型
    private String opModule;//操作模块或功能
    private String opResult;//操作结果 成功或失败
    private String extAttr1;
    private String extAttr2;
    private String extAttr3;
    private String extAttr4;
    private String extAttr5;
    private String extAttr6;
    private String extAttr7;
    private String extAttr8;

    public LogRecord() {
    }

    public LogRecord(Long id, String updateUser, Date updateDate, String opContent, String opUserIp, String opType, String opModule, String opResult, String extAttr1, String extAttr2, String extAttr3, String extAttr4, String extAttr5, String extAttr6, String extAttr7, String extAttr8) {
        this.id = id;
        this.updateUser = updateUser;
        this.updateDate = updateDate;
        this.opContent = opContent;
        this.opUserIp = opUserIp;
        this.opType = opType;
        this.opModule = opModule;
        this.opResult = opResult;
        this.extAttr1 = extAttr1;
        this.extAttr2 = extAttr2;
        this.extAttr3 = extAttr3;
        this.extAttr4 = extAttr4;
        this.extAttr5 = extAttr5;
        this.extAttr6 = extAttr6;
        this.extAttr7 = extAttr7;
        this.extAttr8 = extAttr8;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getOpContent() {
        return opContent;
    }

    public void setOpContent(String opContent) {
        this.opContent = opContent;
    }

    public String getOpUserIp() {
        return opUserIp;
    }

    public void setOpUserIp(String opUserIp) {
        this.opUserIp = opUserIp;
    }

    public String getOpType() {
        return opType;
    }

    public void setOpType(String opType) {
        this.opType = opType;
    }

    public String getOpModule() {
        return opModule;
    }

    public void setOpModule(String opModule) {
        this.opModule = opModule;
    }

    public String getOpResult() {
        return opResult;
    }

    public void setOpResult(String opResult) {
        this.opResult = opResult;
    }

    public String getExtAttr1() {
        return extAttr1;
    }

    public void setExtAttr1(String extAttr1) {
        this.extAttr1 = extAttr1;
    }

    public String getExtAttr2() {
        return extAttr2;
    }

    public void setExtAttr2(String extAttr2) {
        this.extAttr2 = extAttr2;
    }

    public String getExtAttr3() {
        return extAttr3;
    }

    public void setExtAttr3(String extAttr3) {
        this.extAttr3 = extAttr3;
    }

    public String getExtAttr4() {
        return extAttr4;
    }

    public void setExtAttr4(String extAttr4) {
        this.extAttr4 = extAttr4;
    }

    public String getExtAttr5() {
        return extAttr5;
    }

    public void setExtAttr5(String extAttr5) {
        this.extAttr5 = extAttr5;
    }

    public String getExtAttr6() {
        return extAttr6;
    }

    public void setExtAttr6(String extAttr6) {
        this.extAttr6 = extAttr6;
    }

    public String getExtAttr7() {
        return extAttr7;
    }

    public void setExtAttr7(String extAttr7) {
        this.extAttr7 = extAttr7;
    }

    public String getExtAttr8() {
        return extAttr8;
    }

    public void setExtAttr8(String extAttr8) {
        this.extAttr8 = extAttr8;
    }
}
