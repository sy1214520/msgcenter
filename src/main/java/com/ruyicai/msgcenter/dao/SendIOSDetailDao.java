package com.ruyicai.msgcenter.dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.msgcenter.consts.SendState;
import com.ruyicai.msgcenter.domain.DeviceToken;
import com.ruyicai.msgcenter.domain.SendIOSDetail;
import com.ruyicai.msgcenter.domain.Token;
import com.ruyicai.msgcenter.util.Page;
import com.ruyicai.msgcenter.util.Page.Sort;
import com.ruyicai.msgcenter.util.PropertyFilter;

@Component
public class SendIOSDetailDao {

	@PersistenceContext
	private EntityManager entityManager;

	@Transactional
	public void createIOSDetails(Collection<DeviceToken> collection, String text, Date sendTimeDate) {
		for (DeviceToken token : collection) {
			SendIOSDetail detail = new SendIOSDetail(token.getId().getToken(), text, token.getId().getType(),
					SendState.SEND_WAIT, sendTimeDate, token.getId().getUserno());
			entityManager.persist(detail);
		}
	}

	@Transactional
	public void createIOSDetailsByToken(Collection<Token> collection, String text, Date sendTimeDate) {
		for (Token token : collection) {
			SendIOSDetail detail = new SendIOSDetail(token.getToken(), text, token.getType(), SendState.SEND_WAIT,
					sendTimeDate, null);
			entityManager.persist(detail);
		}
	}

	public void findWaitforSend(Map<String, Object> conditionMap, Page<SendIOSDetail> page) {
		String sql = "SELECT o FROM SendIOSDetail o ";
		String countSql = "SELECT count(*) FROM SendIOSDetail o ";
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
		TypedQuery<SendIOSDetail> q = this.entityManager.createQuery(tsql, SendIOSDetail.class);
		TypedQuery<Long> total = this.entityManager.createQuery(tCountSql, Long.class);
		if (conditionMap != null && conditionMap.size() > 0) {
			PropertyFilter.setMatchValue2Query(q, pfList);
			PropertyFilter.setMatchValue2Query(total, pfList);
		}
		q.setFirstResult(page.getPageIndex()).setMaxResults(page.getMaxResult());
		List<SendIOSDetail> resultList = q.getResultList();
		int count = total.getSingleResult().intValue();
		page.setList(resultList);
		page.setTotalResult(count);
	}

}
