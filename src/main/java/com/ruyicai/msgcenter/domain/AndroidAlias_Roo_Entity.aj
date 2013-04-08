// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.ruyicai.msgcenter.domain;

import com.ruyicai.msgcenter.domain.AndroidAlias;
import com.ruyicai.msgcenter.domain.AndroidAliasPK;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Table;
import org.springframework.transaction.annotation.Transactional;

privileged aspect AndroidAlias_Roo_Entity {
    
    declare @type: AndroidAlias: @Entity;
    
    declare @type: AndroidAlias: @Table(name = "ANDROIDALIAS");
    
    @PersistenceContext
    transient EntityManager AndroidAlias.entityManager;
    
    @Transactional
    public void AndroidAlias.persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional
    public void AndroidAlias.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            AndroidAlias attached = AndroidAlias.findAndroidAlias(this.id);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional
    public void AndroidAlias.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional
    public void AndroidAlias.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    @Transactional
    public AndroidAlias AndroidAlias.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        AndroidAlias merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
    public static final EntityManager AndroidAlias.entityManager() {
        EntityManager em = new AndroidAlias().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static long AndroidAlias.countAndroidAliases() {
        return entityManager().createQuery("SELECT COUNT(o) FROM AndroidAlias o", Long.class).getSingleResult();
    }
    
    public static List<AndroidAlias> AndroidAlias.findAllAndroidAliases() {
        return entityManager().createQuery("SELECT o FROM AndroidAlias o", AndroidAlias.class).getResultList();
    }
    
    public static AndroidAlias AndroidAlias.findAndroidAlias(AndroidAliasPK id) {
        if (id == null) return null;
        return entityManager().find(AndroidAlias.class, id);
    }
    
    public static List<AndroidAlias> AndroidAlias.findAndroidAliasEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM AndroidAlias o", AndroidAlias.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
}