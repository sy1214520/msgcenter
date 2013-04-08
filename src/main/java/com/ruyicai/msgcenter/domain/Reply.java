package com.ruyicai.msgcenter.domain;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.Query;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

import com.ruyicai.msgcenter.util.Page;
import com.ruyicai.msgcenter.util.PropertyFilter;

@RooJavaBean
@RooToString
@RooEntity(versionField = "", table = "Reply", identifierField = "id")
public class Reply {

	@Id
	@Column(name="id")
    private int id;
    
    @Column(name="username", length = 20)
    private String username;
    
    @Column(name="bezu")
    private String bezu;
    
    @Column(name="createtime")
    private Date createtime;

    @Column(name="reply")
    private String reply;
    
    /**
	 * 查询分页 
	 * @param page 分页集合
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Page<Reply> findMsg(Map<String, Object> conditionMap, Page<Reply> page) {
		EntityManager em = Reply.entityManager();
		String sql = "SELECT o.* FROM Reply o ";
		String countSql = "SELECT count(*) FROM Reply o ";
		StringBuilder whereSql = new StringBuilder(" WHERE 1=1 ");
		List<PropertyFilter> pfList = null;
		if (conditionMap != null && conditionMap.size() > 0) {
			pfList = PropertyFilter.buildFromMap(conditionMap);
			String buildSql = PropertyFilter.transfer2Sql(pfList, "o");
			whereSql.append(buildSql);
		}
		StringBuilder topOrderSql = new StringBuilder(" ORDER BY o.createtime DESC ");
		String topSql = sql + whereSql.toString() + topOrderSql.toString();
		String topCountSql = countSql + whereSql.toString();
		
		Query q = em.createNativeQuery(topSql, Reply.class);
		Query topTotal = em.createNativeQuery(topCountSql);
		if (conditionMap != null && conditionMap.size() > 0) {
			PropertyFilter.setMatchValue2Query(q, pfList);
			PropertyFilter.setMatchValue2Query(topTotal, pfList);
		}

		q.setFirstResult(page.getPageIndex()).setMaxResults(page.getMaxResult());
		BigInteger topCount = (BigInteger) topTotal.getSingleResult();
		page.setList(q.getResultList());
		page.setTotalResult(topCount.intValue());
		return page;
	}
}
