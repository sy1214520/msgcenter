package com.ruyicai.msgcenter.dao;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.msgcenter.domain.LogSMSSend;
import com.ruyicai.msgcenter.util.Page;
import com.ruyicai.msgcenter.util.PropertyFilter;
import com.ruyicai.msgcenter.util.Page.Sort;

@Component
public class LogSMSSendDao {

	@PersistenceContext
	private EntityManager entityManager;

	@Transactional
	public void updateStatusReport(String[] statusReportArray) {
		for (String statusReport : statusReportArray) {
			if (StringUtils.isBlank(statusReport)) {
				return;
			}
			String[] split = statusReport.split("\\,");
			if (split == null || split.length < 6) {
				return;
			}
			String result = split[2];
			String report = split[4] + "," + split[5];
			List<LogSMSSend> resultList = entityManager
					.createQuery("SELECT o FROM LogSMSSend o where o.result = :result", LogSMSSend.class)
					.setParameter("result", result).getResultList();
			for (LogSMSSend logSMSSend : resultList) {
				logSMSSend.setStatusReport(report);
				entityManager.merge(logSMSSend);
			}
			entityManager.flush();
		}
	}

	public void findSMSLog(Map<String, Object> conditionMap, Page<LogSMSSend> page) {
		String sql = "SELECT o FROM LogSMSSend o ";
		String countSql = "SELECT count(*) FROM LogSMSSend o ";
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
		TypedQuery<LogSMSSend> q = this.entityManager.createQuery(tsql, LogSMSSend.class);
		TypedQuery<Long> total = this.entityManager.createQuery(tCountSql, Long.class);
		if (conditionMap != null && conditionMap.size() > 0) {
			PropertyFilter.setMatchValue2Query(q, pfList);
			PropertyFilter.setMatchValue2Query(total, pfList);
		}
		q.setFirstResult(page.getPageIndex()).setMaxResults(page.getMaxResult());
		List<LogSMSSend> resultList = q.getResultList();
		int count = total.getSingleResult().intValue();
		page.setList(resultList);
		page.setTotalResult(count);
	}

}
