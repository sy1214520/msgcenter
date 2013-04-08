package com.ruyicai.msgcenter.domain;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.TypedQuery;

import org.apache.commons.lang.StringUtils;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

import com.ruyicai.msgcenter.util.Page;
import com.ruyicai.msgcenter.util.Page.Sort;
import com.ruyicai.msgcenter.util.PropertyFilter;

@RooJavaBean
@RooToString
@RooEntity(versionField = "", table = "ANDROIDALIAS", identifierField = "id")
public class AndroidAlias {

	@EmbeddedId
	private AndroidAliasPK id;

	@Column(name = "CREATETIME")
	private Date createTime;

	public static AndroidAlias createIfNotExist(String userno, String pkgName) {
		if (StringUtils.isBlank(userno)) {
			throw new IllegalArgumentException("the arguments userno is require");
		}
		if (StringUtils.isBlank(pkgName)) {
			throw new IllegalArgumentException("the arguments pkgName is require");
		}
		AndroidAliasPK id = new AndroidAliasPK(userno, pkgName);
		AndroidAlias androidAlias = findAndroidAlias(id);
		if (androidAlias == null) {
			androidAlias = new AndroidAlias();
			androidAlias.setId(id);
			androidAlias.setCreateTime(new Date());
			androidAlias.persist();
		}
		return androidAlias;
	}

	public static List<AndroidAlias> findAndroidAliasByUserno(String userno) {
		return entityManager()
				.createQuery("SELECT o FROM AndroidAlias o where o.id.userno = :userno", AndroidAlias.class)
				.setParameter("userno", userno).getResultList();
	}

	public static void findAndroidAliasPage(Map<String, Object> conditionMap, Page<AndroidAlias> page) {
		String sql = "SELECT o FROM AndroidAlias o ";
		String countSql = "SELECT count(*) FROM AndroidAlias o ";
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
			orderSql.append(" o.createTime desc ");
		}
		String tsql = sql + whereSql.toString() + orderSql.toString();
		String tCountSql = countSql + whereSql.toString();
		TypedQuery<AndroidAlias> q = entityManager().createQuery(tsql, AndroidAlias.class);
		TypedQuery<Long> total = entityManager().createQuery(tCountSql, Long.class);
		if (conditionMap != null && conditionMap.size() > 0) {
			PropertyFilter.setMatchValue2Query(q, pfList);
			PropertyFilter.setMatchValue2Query(total, pfList);
		}
		q.setFirstResult(page.getPageIndex()).setMaxResults(page.getMaxResult());
		List<AndroidAlias> resultList = q.getResultList();
		int count = total.getSingleResult().intValue();
		page.setList(resultList);
		page.setTotalResult(count);
	}

}
