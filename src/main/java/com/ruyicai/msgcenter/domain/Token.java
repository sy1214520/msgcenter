package com.ruyicai.msgcenter.domain;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.TypedQuery;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

import com.ruyicai.msgcenter.util.Page;
import com.ruyicai.msgcenter.util.Page.Sort;
import com.ruyicai.msgcenter.util.PropertyFilter;

@RooJavaBean
@RooToString
@RooEntity(versionField = "", table = "Token")
public class Token {

	@Id
	@Column(name = "token", length = 200)
	private String token;

	@Column(name = "type", length = 50)
	private String type;

	@Column(name = "createTime")
	private Date createTime;

	@Column(name = "lastRequestTime")
	private Date lastRequestTime;

	public static Token createOrUpdate(String token, String type) {
		Token t = findToken(token);
		Date d = new Date();
		if (t == null) {
			t = new Token();
			t.setToken(token);
			t.setType(type);
			t.setCreateTime(d);
			t.setLastRequestTime(d);
			t.persist();
		} else {
			t.setType(type);
			t.setLastRequestTime(d);
			t.merge();
		}
		return t;
	}

	public static void findTokenByPage(Map<String, Object> conditionMap, Page<Token> page) {
		String sql = "SELECT o FROM Token o ";
		String countSql = "SELECT count(*) FROM Token o ";
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
			orderSql.append(" o.lastRequestTime desc ");
		}
		String tsql = sql + whereSql.toString() + orderSql.toString();
		String tCountSql = countSql + whereSql.toString();
		TypedQuery<Token> q = entityManager().createQuery(tsql, Token.class);
		TypedQuery<Long> total = entityManager().createQuery(tCountSql, Long.class);
		if (conditionMap != null && conditionMap.size() > 0) {
			PropertyFilter.setMatchValue2Query(q, pfList);
			PropertyFilter.setMatchValue2Query(total, pfList);
		}
		q.setFirstResult(page.getPageIndex()).setMaxResults(page.getMaxResult());
		List<Token> resultList = q.getResultList();
		int count = total.getSingleResult().intValue();
		page.setList(resultList);
		page.setTotalResult(count);
	}

	public static Boolean removeTokenByToken(String Token) {
		Boolean flag = false;
		String sql = "SELECT o FROM Token o WHERE o.token = :token";
		List<Token> list = entityManager().createQuery(sql, Token.class).setParameter("token", Token).getResultList();
		for (Token token : list) {
			token.remove();
			flag = true;
		}
		return flag;
	}
}
