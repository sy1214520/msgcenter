// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.ruyicai.msgcenter.domain;

import java.lang.Integer;
import java.lang.String;
import java.util.Date;

privileged aspect LogSMSSend_Roo_JavaBean {
    
    public String LogSMSSend.getChannelName() {
        return this.channelName;
    }
    
    public void LogSMSSend.setChannelName(String channelName) {
        this.channelName = channelName;
    }
    
    public String LogSMSSend.getMobileid() {
        return this.mobileid;
    }
    
    public void LogSMSSend.setMobileid(String mobileid) {
        this.mobileid = mobileid;
    }
    
    public String LogSMSSend.getContent() {
        return this.content;
    }
    
    public void LogSMSSend.setContent(String content) {
        this.content = content;
    }
    
    public String LogSMSSend.getResult() {
        return this.result;
    }
    
    public void LogSMSSend.setResult(String result) {
        this.result = result;
    }
    
    public String LogSMSSend.getStatusReport() {
        return this.statusReport;
    }
    
    public void LogSMSSend.setStatusReport(String statusReport) {
        this.statusReport = statusReport;
    }
    
    public Date LogSMSSend.getCreateTime() {
        return this.createTime;
    }
    
    public void LogSMSSend.setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    
    public Integer LogSMSSend.getSmscount() {
        return this.smscount;
    }
    
    public void LogSMSSend.setSmscount(Integer smscount) {
        this.smscount = smscount;
    }
    
}