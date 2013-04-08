package com.ruyicai.msgcenter.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.msgcenter.service.MemcachedService;

/**
 * 用户短信发送时间设置
 */
@RooToString
@RooJavaBean
@RooEntity(versionField = "", table = "USERSMSTIMING")
public class UserSMSTiming implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 用户编号 */
	@Column(name = "userno", length = 50)
	private String userno;

	/** 是否有效 0:不发送,1:发送 */
	@Column(name = "needToSend")
	private Integer needToSend;

	/** 创建时间 */
	@Column(name = "createTime")
	private Date createTime;

	/** 最近修改时间 */
	@Column(name = "lastModifyTime")
	private Date lastModifyTime;

	/** 短信类型 */
	// TODO 1,开奖，2，中奖，需要再细分
	@Column(name = "smstype")
	private BigDecimal smstype;

	@Autowired
	transient MemcachedService<UserSMSTiming> memcachedService;

	public UserSMSTiming() {
		super();
	}

	public UserSMSTiming(String userno, BigDecimal smstype, Integer needToSend) {
		super();
		this.userno = userno;
		this.smstype = smstype;
		this.needToSend = needToSend;
		this.createTime = new Date();
	}

	/**
	 * 创建用户短信发送时间设置
	 * 
	 * @param userno
	 *            用户编号
	 * @param sendTime
	 *            发送时间
	 * @param sendTimeType
	 *            发送类型
	 * @return UserSMSTiming
	 */
	@Transactional
	public static UserSMSTiming createOrMergeUserSMSTiming(String userno, BigDecimal smstype, int needToSend) {
		if (StringUtils.isBlank(userno)) {
			throw new IllegalArgumentException("the argument userno is required");
		}
		UserSMSTiming timing = UserSMSTiming.findByUsernoAndSmstypeFromDB(userno, smstype);
		if (timing == null) {
			timing = new UserSMSTiming(userno, smstype, needToSend);
			timing.persist();
		} else {
			timing.setNeedToSend(needToSend);
			timing.setLastModifyTime(new Date());
			timing.merge();
		}
		new UserSMSTiming().memcachedService.set("UserSMSTiming" + userno + smstype, timing);
		return timing;
	}

	@Transactional
	public static UserSMSTiming createUserSMSTiming(String userno, BigDecimal smstype, Integer needToSend) {
		if (StringUtils.isBlank(userno)) {
			throw new IllegalArgumentException("the argument userno is required");
		}
		if (smstype == null) {
			throw new IllegalArgumentException("the argument smstype is required");
		}
		UserSMSTiming timing = new UserSMSTiming(userno, smstype, needToSend);
		timing.persist();
		new UserSMSTiming().memcachedService.set("UserSMSTiming" + userno + smstype, timing);
		return timing;
	}

	public static UserSMSTiming findByUsernoAndSmstypeFromCache(String userno, BigDecimal smstype) {
		if (StringUtils.isBlank(userno)) {
			throw new IllegalArgumentException("the argument userno is required");
		}
		if (smstype == null) {
			throw new IllegalArgumentException("the argument smstype is required");
		}
		UserSMSTiming userSMSTiming = new UserSMSTiming().memcachedService.get("UserSMSTiming" + userno + smstype);
		if (userSMSTiming != null) {
			return userSMSTiming;
		} else {
			userSMSTiming = findByUsernoAndSmstypeFromDB(userno, smstype);
			if (userSMSTiming == null) {
				return null;
			} else {
				new UserSMSTiming().memcachedService.checkToSet("UserSMSTiming" + userno + smstype, userSMSTiming);
				return userSMSTiming;
			}
		}
	}

	public static UserSMSTiming findByUsernoAndSmstypeFromDB(String userno, BigDecimal smstype) {
		if (StringUtils.isBlank(userno)) {
			throw new IllegalArgumentException("the argument userno is required");
		}
		if (smstype == null) {
			throw new IllegalArgumentException("the argument smstype is required");
		}
		EntityManager em = UserSMSTiming.entityManager();
		TypedQuery<UserSMSTiming> q = em.createQuery(
				"SELECT o FROM UserSMSTiming AS o WHERE o.userno = :userno AND o.smstype = :smstype",
				UserSMSTiming.class);
		q.setParameter("userno", userno);
		q.setParameter("smstype", smstype);
		List<UserSMSTiming> list = q.getResultList();
		if (list != null && list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@Transactional
	public void updateNeedToSend(Integer needToSend) {
		this.setNeedToSend(needToSend);
		this.setLastModifyTime(new Date());
		this.merge();
		new UserSMSTiming().memcachedService.set("UserSMSTiming" + this.getUserno() + this.getSmstype(), this);
	}
}
