package com.ruyicai.msgcenter.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.TypedQuery;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

import com.ruyicai.msgcenter.service.MemcachedService;

@RooToString
@RooJavaBean
@RooJson
@RooEntity(versionField = "", table = "JPUSHKEY")
public class JPushKey implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "PKGNAME", length = 100)
	private String pkgName;

	@Column(name = "MASTERSECRET", length = 100)
	private String masterSecret;

	@Column(name = "APPKEY", length = 100)
	private String appKey;

	@Column(name = "STATE")
	private Integer state;

	@Autowired
	transient MemcachedService<JPushKey> memcachedService;

	public static JPushKey findByPkgName(String pkgName) {
		JPushKey jPushKey = null;
		jPushKey = new JPushKey().memcachedService.get("JPushKey" + pkgName);
		if (null != jPushKey) {
			return jPushKey;
		} else {
			jPushKey = JPushKey.findByPkgNameFromDB(pkgName);
			if (null == jPushKey) {
				return null;
			}
			new JPushKey().memcachedService.checkToSet("JPushKey" + pkgName, jPushKey);
			return jPushKey;
		}
	}

	private static JPushKey findByPkgNameFromDB(String pkgName) {
		if (StringUtils.isBlank(pkgName)) {
			return null;
		}
		EntityManager em = entityManager();
		TypedQuery<JPushKey> q = em.createQuery("SELECT o FROM JPushKey AS o WHERE o.pkgName = :pkgName",
				JPushKey.class).setParameter("pkgName", pkgName);
		List<JPushKey> list = q.getResultList();
		if (list != null && list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}

	public static JPushKey saveOrUpdate(String pkgName, String masterSecret, String appKey, Integer state) {
		if (StringUtils.isBlank(pkgName)) {
			throw new IllegalArgumentException("the argument pkgName is required");
		}
		if (StringUtils.isBlank(masterSecret)) {
			throw new IllegalArgumentException("the masterSecret name is required");
		}
		if (StringUtils.isBlank(appKey)) {
			throw new IllegalArgumentException("the appKey name is required");
		}
		if (state == null) {
			state = 1;
		}
		JPushKey jPushKey = JPushKey.findByPkgName(pkgName);
		if (jPushKey == null) {
			jPushKey = new JPushKey();
			jPushKey.setPkgName(pkgName);
			jPushKey.setMasterSecret(masterSecret);
			jPushKey.setAppKey(appKey);
			jPushKey.setState(state);
			jPushKey.persist();
			jPushKey.flush();
		} else {
			jPushKey.setMasterSecret(masterSecret);
			jPushKey.setAppKey(appKey);
			jPushKey.setState(state);
			jPushKey.merge();
		}
		new JPushKey().memcachedService.set("JPushKey" + pkgName, jPushKey);
		return jPushKey;
	}

}
