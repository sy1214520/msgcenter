package com.ruyicai.msgcenter.domain;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.TypedQuery;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

import com.ruyicai.msgcenter.util.Page;
import com.ruyicai.msgcenter.util.PropertyFilter;
import com.ruyicai.msgcenter.util.Page.Sort;

@RooToString
@RooJavaBean
@RooEntity(versionField = "", table = "LOGIOSSEND")
public class LogIOSSend {

	@Column(name = "TOKEN", columnDefinition = "text")
	private String token;

	@Column(name = "TYPE", length = 50)
	private String type;

	@Column(name = "CONTENT", columnDefinition = "text")
	private String content;

	@Column(name = "RESULT", length = 50)
	private String result;

	@Column(name = "CREATETIME")
	private Date createTime;

	@Column(name = "USERNO", length = 50)
	private String userno;

	public static LogIOSSend createLogIOSSend(String token, String type, String content, String result, String userno) {
		LogIOSSend log = new LogIOSSend();
		log.setToken(token);
		log.setType(type);
		log.setContent(content);
		log.setResult(result);
		log.setUserno(userno);
		log.setCreateTime(new Date());
		log.persist();
		return log;
	}

	public static void findSMSLog(Map<String, Object> conditionMap, Page<LogIOSSend> page) {
		String sql = "SELECT o FROM LogIOSSend o ";
		String countSql = "SELECT count(*) FROM LogIOSSend o ";
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
		TypedQuery<LogIOSSend> q = entityManager().createQuery(tsql, LogIOSSend.class);
		TypedQuery<Long> total = entityManager().createQuery(tCountSql, Long.class);
		if (conditionMap != null && conditionMap.size() > 0) {
			PropertyFilter.setMatchValue2Query(q, pfList);
			PropertyFilter.setMatchValue2Query(total, pfList);
		}
		q.setFirstResult(page.getPageIndex()).setMaxResults(page.getMaxResult());
		List<LogIOSSend> resultList = q.getResultList();
		int count = total.getSingleResult().intValue();
		page.setList(resultList);
		page.setTotalResult(count);
	}
}
