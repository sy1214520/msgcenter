// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.ruyicai.msgcenter.domain;

import com.ruyicai.msgcenter.domain.Tsms;
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

privileged aspect Tsms_Roo_Entity {
    
    declare @type: Tsms: @Entity;
    
    declare @type: Tsms: @Table(name = "TSMS");
    
    @PersistenceContext
    transient EntityManager Tsms.entityManager;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long Tsms.id;
    
    public Long Tsms.getId() {
        return this.id;
    }
    
    public void Tsms.setId(Long id) {
        this.id = id;
    }
    
    @Transactional
    public void Tsms.persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional
    public void Tsms.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            Tsms attached = Tsms.findTsms(this.id);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional
    public void Tsms.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional
    public void Tsms.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    @Transactional
    public Tsms Tsms.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        Tsms merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
    public static final EntityManager Tsms.entityManager() {
        EntityManager em = new Tsms().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static long Tsms.countTsmses() {
        return entityManager().createQuery("SELECT COUNT(o) FROM Tsms o", Long.class).getSingleResult();
    }
    
    public static List<Tsms> Tsms.findAllTsmses() {
        return entityManager().createQuery("SELECT o FROM Tsms o", Tsms.class).getResultList();
    }
    
    public static Tsms Tsms.findTsms(Long id) {
        if (id == null) return null;
        return entityManager().find(Tsms.class, id);
    }
    
    public static List<Tsms> Tsms.findTsmsEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM Tsms o", Tsms.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
}
