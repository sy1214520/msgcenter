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
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.msgcenter.util.Page;
import com.ruyicai.msgcenter.util.Page.Sort;
import com.ruyicai.msgcenter.util.PropertyFilter;

@RooJavaBean
@RooToString
@RooEntity(versionField = "", table = "DEVICETOKEN", identifierField = "id")
public class DeviceToken {
	
	@EmbeddedId
	private DeviceTokenPK id;

	@Column(name = "machine", length = 50)
	private String machine;

	/** 是否有效 0:不发送,1:发送 */
	@Column(name = "needToSend")
	private int needToSend;

	@Column(name = "createTime")
	private Date createTime;

	public static DeviceToken createIfNotExist(String userno, String token, String type, String machine, int needToSend) {
		if (StringUtils.isBlank(userno)) {
			throw new IllegalArgumentException("the arguments userno is require");
		}
		if (StringUtils.isBlank(token)) {
			throw new IllegalArgumentException("the arguments token is require");
		}
		if (StringUtils.isBlank(type)) {
			throw new IllegalArgumentException("the arguments type is require");
		}
		DeviceToken deviceToken = findDeviceToken(new DeviceTokenPK(userno, token, type));
		if (deviceToken == null) {
			deviceToken = new DeviceToken();
			deviceToken.setId(new DeviceTokenPK(userno, token, type));
			deviceToken.setMachine(machine);
			deviceToken.setNeedToSend(needToSend);
			deviceToken.setCreateTime(new Date());
			deviceToken.persist();
		}
		return deviceToken;
	}

	public static List<DeviceToken> findAllDeviceTokensByUserno(String userno) {
		return entityManager()
				.createQuery("SELECT o FROM DeviceToken o where o.id.userno = :userno", DeviceToken.class)
				.setParameter("userno", userno).getResultList();
	}

	public static List<DeviceToken> findAllDeviceTokensByType(String type) {
		return entityManager().createQuery("SELECT o FROM DeviceToken o where o.id.type = :type", DeviceToken.class)
				.setParameter("type", type).getResultList();
	}

	public static List<DeviceToken> findDeviceTokensByUsernoAndToken(String userno, String token) {
		return entityManager()
				.createQuery("SELECT o FROM DeviceToken o where o.id.userno = :userno and o.id.token = :token",
						DeviceToken.class).setParameter("userno", userno).setParameter("token", token).getResultList();
	}

	public static List<DeviceToken> findSendDeviceTokensByUserno(String userno) {
		return entityManager()
				.createQuery("SELECT o FROM DeviceToken o where o.id.userno = :userno and o.needToSend = 1",
						DeviceToken.class).setParameter("userno", userno).getResultList();
	}

	public static void findDeviceTokenByPage(Map<String, Object> conditionMap, Page<DeviceToken> page) {
		String sql = "SELECT o FROM DeviceToken o ";
		String countSql = "SELECT count(*) FROM DeviceToken o ";
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
		TypedQuery<DeviceToken> q = entityManager().createQuery(tsql, DeviceToken.class);
		TypedQuery<Long> total = entityManager().createQuery(tCountSql, Long.class);
		if (conditionMap != null && conditionMap.size() > 0) {
			PropertyFilter.setMatchValue2Query(q, pfList);
			PropertyFilter.setMatchValue2Query(total, pfList);
		}
		q.setFirstResult(page.getPageIndex()).setMaxResults(page.getMaxResult());
		List<DeviceToken> resultList = q.getResultList();
		int count = total.getSingleResult().intValue();
		page.setList(resultList);
		page.setTotalResult(count);
	}

	@Transactional
	public static Integer removeDeviceTokenByToken(String deviceToken) {
		Integer count = 0;
		String sql = "SELECT o FROM DeviceToken o WHERE o.id.token = :token";
		List<DeviceToken> list = entityManager().createQuery(sql, DeviceToken.class).setParameter("token", deviceToken)
				.getResultList();
		for (DeviceToken token : list) {
			token.remove();
			count++;
		}
		return count;
	}
}
