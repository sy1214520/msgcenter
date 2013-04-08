package com.ruyicai.msgcenter.domain;

import java.io.Serializable;
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

import com.ruyicai.msgcenter.service.MemcachedService;

@RooToString
@RooJavaBean
@RooEntity(versionField = "", table = "LotConfig")
public class LotConfig implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 彩种编号 */
	@Column(name = "lotno", length = 50)
	private String lotno;

	/** 彩种名称 */
	@Column(name = "name", length = 50)
	private String name;

	/** 是否发送短信 */
	@Column(name = "issendsms")
	private Boolean issendsms = true;

	/** 创建时间 */
	@Column(name = "createTime")
	private Date createTime;

	/** 最近修改时间 */
	@Column(name = "lastModifyTime")
	private Date lastModifyTime;

	@Autowired
	transient MemcachedService<LotConfig> memcachedService;

	public static LotConfig findByLotno(String lotno) {
		LotConfig lotConfig = null;
		lotConfig = new LotConfig().memcachedService.get("LotConfig" + lotno);
		if (null != lotConfig) {
			return lotConfig;
		} else {
			lotConfig = LotConfig.findByLotnoFromDB(lotno);
			if (null == lotConfig) {
				return null;
			}
			new LotConfig().memcachedService.checkToSet("LotConfig" + lotno, lotConfig);
			return lotConfig;
		}
	}

	private static LotConfig findByLotnoFromDB(String lotno) {
		if (StringUtils.isBlank(lotno)) {
			return null;
		}
		EntityManager em = LotConfig.entityManager();
		TypedQuery<LotConfig> q = em
				.createQuery("SELECT o FROM LotConfig AS o WHERE o.lotno = :lotno", LotConfig.class).setParameter(
						"lotno", lotno);
		List<LotConfig> list = q.getResultList();
		if (list != null && list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}

	public static LotConfig saveOrUpdate(String lotno, String name, Boolean issendsms) {
		if (StringUtils.isBlank(lotno)) {
			throw new IllegalArgumentException("the argument lotno is required");
		}
		if (StringUtils.isBlank(name)) {
			throw new IllegalArgumentException("the argument name is required");
		}
		if (issendsms == null) {
			throw new IllegalArgumentException("the argument issendsms is required");
		}
		LotConfig lotConfig = LotConfig.findByLotno(lotno);
		if (lotConfig == null) {
			lotConfig = new LotConfig();
			lotConfig.setLotno(lotno);
			lotConfig.setName(name);
			lotConfig.setIssendsms(issendsms);
			lotConfig.setCreateTime(new Date());
			lotConfig.persist();
			lotConfig.flush();
		} else {
			lotConfig.setLotno(lotno);
			lotConfig.setName(name);
			lotConfig.setIssendsms(issendsms);
			lotConfig.setLastModifyTime(new Date());
			lotConfig.merge();
		}
		new LotConfig().memcachedService.set("LotConfig" + lotno, lotConfig);
		return lotConfig;
	}

}
