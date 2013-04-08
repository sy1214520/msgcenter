package com.ruyicai.msgcenter.domain;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Column;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJson
@RooEntity(versionField = "", table = "TSMS")
public class Tsms {

	@Column(name = "lotno", length = 100)
	private String lotno;

	@Column(name = "userno", length = 100)
	private String userno;

	@Column(name = "type")
	private BigDecimal type;

	@Column(name = "state")
	private BigDecimal state;

	public static List<Tsms> findBytype(BigDecimal type) {
		return Tsms.entityManager().createQuery("select o from Tsms o where type=? and state = 1", Tsms.class)
				.setParameter(1, type).getResultList();
	}
}
