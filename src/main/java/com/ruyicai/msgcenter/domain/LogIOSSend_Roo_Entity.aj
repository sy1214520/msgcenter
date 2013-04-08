// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.ruyicai.msgcenter.domain;

import com.ruyicai.msgcenter.domain.LogIOSSend;
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

privileged aspect LogIOSSend_Roo_Entity {
    
    declare @type: LogIOSSend: @Entity;
    
    declare @type: LogIOSSend: @Table(name = "LOGIOSSEND");
    
    @PersistenceContext
    transient EntityManager LogIOSSend.entityManager;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long LogIOSSend.id;
    
    public Long LogIOSSend.getId() {
        return this.id;
    }
    
    public void LogIOSSend.setId(Long id) {
        this.id = id;
    }
    
    @Transactional
    public void LogIOSSend.persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional
    public void LogIOSSend.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            LogIOSSend attached = LogIOSSend.findLogIOSSend(this.id);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional
    public void LogIOSSend.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional
    public void LogIOSSend.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    @Transactional
    public LogIOSSend LogIOSSend.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        LogIOSSend merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
    public static final EntityManager LogIOSSend.entityManager() {
        EntityManager em = new LogIOSSend().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static long LogIOSSend.countLogIOSSends() {
        return entityManager().createQuery("SELECT COUNT(o) FROM LogIOSSend o", Long.class).getSingleResult();
    }
    
    public static List<LogIOSSend> LogIOSSend.findAllLogIOSSends() {
        return entityManager().createQuery("SELECT o FROM LogIOSSend o", LogIOSSend.class).getResultList();
    }
    
    public static LogIOSSend LogIOSSend.findLogIOSSend(Long id) {
        if (id == null) return null;
        return entityManager().find(LogIOSSend.class, id);
    }
    
    public static List<LogIOSSend> LogIOSSend.findLogIOSSendEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM LogIOSSend o", LogIOSSend.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
}
