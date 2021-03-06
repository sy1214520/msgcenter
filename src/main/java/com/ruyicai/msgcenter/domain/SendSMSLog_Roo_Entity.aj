// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.ruyicai.msgcenter.domain;

import com.ruyicai.msgcenter.domain.SendSMSLog;
import java.lang.Long;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PersistenceContext;
import javax.persistence.Table;
import org.springframework.transaction.annotation.Transactional;

privileged aspect SendSMSLog_Roo_Entity {
    
    declare @type: SendSMSLog: @Entity;
    
    declare @type: SendSMSLog: @Table(name = "SENDSMSLOG");
    
    @PersistenceContext
    transient EntityManager SendSMSLog.entityManager;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long SendSMSLog.id;
    
    public Long SendSMSLog.getId() {
        return this.id;
    }
    
    public void SendSMSLog.setId(Long id) {
        this.id = id;
    }
    
    @Transactional
    public void SendSMSLog.persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional
    public void SendSMSLog.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            SendSMSLog attached = SendSMSLog.findSendSMSLog(this.id);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional
    public void SendSMSLog.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional
    public void SendSMSLog.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    @Transactional
    public SendSMSLog SendSMSLog.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        SendSMSLog merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
    public static final EntityManager SendSMSLog.entityManager() {
        EntityManager em = new SendSMSLog().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static long SendSMSLog.countSendSMSLogs() {
        return entityManager().createQuery("SELECT COUNT(o) FROM SendSMSLog o", Long.class).getSingleResult();
    }
    
    public static List<SendSMSLog> SendSMSLog.findAllSendSMSLogs() {
        return entityManager().createQuery("SELECT o FROM SendSMSLog o", SendSMSLog.class).getResultList();
    }
    
    public static SendSMSLog SendSMSLog.findSendSMSLog(Long id) {
        if (id == null) return null;
        return entityManager().find(SendSMSLog.class, id);
    }
    
    public static List<SendSMSLog> SendSMSLog.findSendSMSLogEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM SendSMSLog o", SendSMSLog.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
}
