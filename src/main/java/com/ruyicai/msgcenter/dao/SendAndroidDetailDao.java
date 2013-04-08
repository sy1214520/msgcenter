package com.ruyicai.msgcenter.dao;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.msgcenter.consts.JPushReceiverType;
import com.ruyicai.msgcenter.domain.AndroidAlias;
import com.ruyicai.msgcenter.domain.SendAndroidDetail;

@Component
public class SendAndroidDetailDao {

	@PersistenceContext
	private EntityManager entityManager;

	@Transactional
	public SendAndroidDetail createAndroidDetails(Integer receiverType, String receiverValue, String pkgName,
			String msgTitle, String msgContent, Date sendTime) {
		SendAndroidDetail detail = new SendAndroidDetail(receiverType, receiverValue, pkgName, msgTitle, msgContent,
				sendTime);
		entityManager.persist(detail);
		return detail;
	}

	@Transactional
	public void createAndroidDetailsByUsernos(Collection<String> usernos, String msgTitle, String msgContent,
			Date sendTime) {
		for (String userno : usernos) {
			List<AndroidAlias> list = AndroidAlias.findAndroidAliasByUserno(userno);
			for (AndroidAlias alias : list) {
				SendAndroidDetail detail = new SendAndroidDetail(JPushReceiverType.ALIAS, alias.getId().getUserno(),
						alias.getId().getPkgName(), msgTitle, msgContent, sendTime);
				entityManager.persist(detail);
			}
		}
	}

	public SendAndroidDetail findSendAndroidDetail(Integer sendNo) {
		return entityManager.find(SendAndroidDetail.class, sendNo);
	}

	@Transactional
	public SendAndroidDetail merge(SendAndroidDetail sendAndroidDetail) {
		SendAndroidDetail merged = entityManager.merge(sendAndroidDetail);
		this.entityManager.flush();
		return merged;
	}

	public List<SendAndroidDetail> findDefaultReady2SendAndroidDetailsBySendState(BigDecimal sendState) {
		if (sendState == null)
			throw new IllegalArgumentException("The argument sendState is required");
		TypedQuery<SendAndroidDetail> q = entityManager.createQuery(
				"SELECT o FROM SendAndroidDetail AS o WHERE o.sendState = :sendState AND o.sendTime <= :sendTime ",
				SendAndroidDetail.class);
		q.setParameter("sendState", sendState);
		q.setParameter("sendTime", new Date());
		q.setMaxResults(5000);
		return q.getResultList();
	}

}
