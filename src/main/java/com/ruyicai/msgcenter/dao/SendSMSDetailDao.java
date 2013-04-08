package com.ruyicai.msgcenter.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.msgcenter.consts.SendState;
import com.ruyicai.msgcenter.domain.SendSMSDetail;
import com.ruyicai.msgcenter.util.Page;
import com.ruyicai.msgcenter.util.Page.Sort;
import com.ruyicai.msgcenter.util.PropertyFilter;

@Component
public class SendSMSDetailDao {

	@PersistenceContext
	private EntityManager entityManager;

	public void createSendSMSDetail(String mobileIds, String content, String channelName, Date sendTime) {
		if (StringUtils.isNotBlank(mobileIds)) {
			String[] mobileArray = mobileIds.split("\\,");
			this.createSendSMSDetail(mobileArray, content, channelName, sendTime);
		}
	}

	@Transactional
	public void createSendSMSDetail(String[] mobileIds, String content, String channelName, Date sendTime) {
		for (String mobile : mobileIds) {
			SendSMSDetail detail = new SendSMSDetail(mobile, content, channelName, SendState.SEND_WAIT, sendTime);
			detail.setHashCode(content.hashCode());
			entityManager.persist(detail);
		}
	}

	@Transactional
	public void sendSMSSending(Set<Long> ids) {
		for (Long id : ids) {
			if (id == null) {
				continue;
			}
			SendSMSDetail detail = this.findSendSMSDetail(id);
			detail.setSendState(SendState.SEND_FAILURE.value());
			this.merge(detail);
		}
	}

	@Transactional
	public void sendSMSFailure(String[] ids) {
		for (String id : ids) {
			if (StringUtils.isBlank(id)) {
				continue;
			}
			SendSMSDetail detail = this.findSendSMSDetail(Long.parseLong(id));
			detail.setSendState(SendState.SEND_FAILURE.value());
			this.merge(detail);
		}
	}

	@Transactional
	public void sendSMSSuccess(String[] ids) {
		for (String id : ids) {
			if (StringUtils.isBlank(id)) {
				continue;
			}
			SendSMSDetail detail = this.findSendSMSDetail(Long.parseLong(id));
			detail.setSendState(SendState.SEND_SUCCESS.value());
			detail.setSendSuccTime(new Date());
			this.merge(detail);
		}
	}

	public TypedQuery<SendSMSDetail> findSendSMSDetailsBySendState(BigDecimal sendState) {
		if (sendState == null)
			throw new IllegalArgumentException("The sendState argument is required");
		TypedQuery<SendSMSDetail> q = this.entityManager.createQuery(
				"SELECT o FROM SendSMSDetail AS o WHERE o.sendState = :sendState", SendSMSDetail.class);
		q.setParameter("sendState", sendState);
		return q;
	}

	public List<SendSMSDetail> findDefaultReady2SendSMSDetailsBySendState(BigDecimal sendState, String channelName) {
		if (sendState == null)
			throw new IllegalArgumentException("The sendState argument is required");
		TypedQuery<SendSMSDetail> q = this.entityManager
				.createQuery(
						"SELECT o FROM SendSMSDetail AS o WHERE o.sendState = :sendState AND o.sendTime <= :sendTime AND o.channelName = :channelName order by o.mobileid asc",
						SendSMSDetail.class);
		q.setParameter("sendState", sendState);
		q.setParameter("sendTime", new Date());
		q.setParameter("channelName", channelName);
		q.setMaxResults(5000);
		return q.getResultList();
	}

	public SendSMSDetail findSendSMSDetail(Long id) {
		return this.entityManager.find(SendSMSDetail.class, id);
	}

	@Transactional
	public void persist(SendSMSDetail sendSMSDetail) {
		this.entityManager.persist(sendSMSDetail);
	}

	@Transactional
	public void remove(SendSMSDetail sendSMSDetail) {
		if (this.entityManager.contains(sendSMSDetail)) {
			this.entityManager.remove(sendSMSDetail);
		} else {
			SendSMSDetail attached = this.findSendSMSDetail(sendSMSDetail.getId());
			this.entityManager.remove(attached);
		}
	}

	@Transactional
	public SendSMSDetail merge(SendSMSDetail sendSMSDetail) {
		SendSMSDetail merged = entityManager.merge(sendSMSDetail);
		this.entityManager.flush();
		return merged;
	}

	public long countSendSMSDetails() {
		return this.entityManager.createQuery("SELECT COUNT(o) FROM SendSMSDetail o", Long.class).getSingleResult();
	}

	public List<SendSMSDetail> findAllSendSMSDetails() {
		return this.entityManager.createQuery("SELECT o FROM SendSMSDetail o", SendSMSDetail.class).getResultList();
	}

	public void findWaitforSend(Map<String, Object> conditionMap, Page<SendSMSDetail> page) {
		String sql = "SELECT o FROM SendSMSDetail o ";
		String countSql = "SELECT count(*) FROM SendSMSDetail o ";
		StringBuilder whereSql = new StringBuilder(" WHERE 1=1 ");
		List<PropertyFilter> pfList = null;
		if (conditionMap != null && conditionMap.size() > 0) {
			pfList = PropertyFilter.buildFromMap(conditionMap);
			String buildSql = PropertyFilter.transfer2Sql(pfList, "o");
			whereSql.append(buildSql);
		}
		List<Sort> sortList = page.fetchSort();
		StringBuilder orderSql = new StringBuilder(" ORDER BY ");
		if (page.isOrderBySetted()) {
			for (Sort sort : sortList) {
				orderSql.append(" " + sort.getProperty() + " " + sort.getDir() + ",");
			}
			orderSql.delete(orderSql.length() - 1, orderSql.length());
		} else {
			orderSql.append(" o.id desc ");
		}
		String tsql = sql + whereSql.toString() + orderSql.toString();
		String tCountSql = countSql + whereSql.toString();
		TypedQuery<SendSMSDetail> q = this.entityManager.createQuery(tsql, SendSMSDetail.class);
		TypedQuery<Long> total = this.entityManager.createQuery(tCountSql, Long.class);
		if (conditionMap != null && conditionMap.size() > 0) {
			PropertyFilter.setMatchValue2Query(q, pfList);
			PropertyFilter.setMatchValue2Query(total, pfList);
		}
		q.setFirstResult(page.getPageIndex()).setMaxResults(page.getMaxResult());
		List<SendSMSDetail> resultList = q.getResultList();
		int count = total.getSingleResult().intValue();
		page.setList(resultList);
		page.setTotalResult(count);
	}
}
