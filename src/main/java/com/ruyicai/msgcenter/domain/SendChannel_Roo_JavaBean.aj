// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.ruyicai.msgcenter.domain;

import java.lang.Integer;
import java.lang.Long;
import java.util.Date;

privileged aspect SendChannel_Roo_JavaBean {
    
    public Long SendChannel.getUserSMStimingId() {
        return this.userSMStimingId;
    }
    
    public void SendChannel.setUserSMStimingId(Long userSMStimingId) {
        this.userSMStimingId = userSMStimingId;
    }
    
    public Integer SendChannel.getNeedToSend() {
        return this.needToSend;
    }
    
    public void SendChannel.setNeedToSend(Integer needToSend) {
        this.needToSend = needToSend;
    }
    
    public Integer SendChannel.getSendtype() {
        return this.sendtype;
    }
    
    public void SendChannel.setSendtype(Integer sendtype) {
        this.sendtype = sendtype;
    }
    
    public Date SendChannel.getModifyTime() {
        return this.modifyTime;
    }
    
    public void SendChannel.setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }
    
}
