package com.ruyicai.msgcenter.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

import com.ruyicai.msgcenter.consts.SendState;

/**
 * 待发送IOS消信记录
 */
@RooToString
@RooJavaBean
@RooEntity(versionField = "", table = "SendIOSDetail")
public class SendIOSDetail {

	@Column(name = "token")
	private String token;

	@Column(name = "content", columnDefinition = "text")
	private String content;

	@Column(name = "channelName", length = 100)
	private String channelName;

	@Column(name = "sendState")
	private BigDecimal sendState;

	@Column(name = "createTime")
	private Date createTime;

	@Column(name = "sendTime")
	private Date sendTime;

	@Column(name = "sendSuccTime")
	private Date sendSuccTime;

	@Column(name = "USERNO", length = 50)
	private String userno;

	public SendIOSDetail() {
		super();
	}

	public SendIOSDetail(String token, String content, String channelName, SendState sendState, Date sendTime,
			String userno) {
		super();
		this.token = token;
		this.content = content;
		this.channelName = channelName;
		this.sendState = sendState.value();
		this.sendTime = sendTime;
		this.createTime = new Date();
		this.userno = userno;
	}

	public static List<SendIOSDetail> findDefaultReady2SendIOSDetailsBySendState(BigDecimal sendState) {
		if (sendState == null)
			throw new IllegalArgumentException("The sendState argument is required");
		EntityManager em = SendIOSDetail.entityManager();
		TypedQuery<SendIOSDetail> q = em.createQuery(
				"SELECT o FROM SendIOSDetail AS o WHERE o.sendState = :sendState AND o.sendTime <= :sendTime ",
				SendIOSDetail.class);
		q.setParameter("sendState", sendState);
		q.setParameter("sendTime", new Date());
		q.setMaxResults(5000);
		return q.getResultList();
	}
}
