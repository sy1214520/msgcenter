package com.ruyicai.msgcenter.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.msgcenter.domain.Letter;
import com.ruyicai.msgcenter.util.Page;
import com.ruyicai.msgcenter.util.PropertyFilter;
import com.ruyicai.msgcenter.util.Page.Sort;

@Component
public class LetterDao {

	@PersistenceContext
	private EntityManager entityManager;
	
	
	public  Letter findLetter(String id, boolean lock) {
		Letter letter = entityManager.find(Letter.class, id,
				lock ? LockModeType.PESSIMISTIC_WRITE : LockModeType.NONE);
		return letter;
	}

	@Transactional
	public  Letter createLetter(String fromUserno, String toUserno, Integer letterType, String title,
			String content) {
		if (StringUtils.isBlank(fromUserno)) {
			throw new IllegalArgumentException("the argument fromUserno is require");
		}
		if (StringUtils.isBlank(toUserno)) {
			throw new IllegalArgumentException("the argument toUserno is require");
		}
		if (StringUtils.isBlank(title)) {
			throw new IllegalArgumentException("the argument title is require");
		}
		Letter letter = new Letter();
		letter.setFromUserno(fromUserno);
		letter.setToUserno(toUserno);
		letter.setLetterType(letterType == null ? 0 : letterType);
		letter.setTitle(title);
		letter.setContent(content);
		letter.setHasRead(0);
		letter.setHasDel(0);
		letter.setCreateTime(new Date());
		letter.setReadTime(null);
		letter.setDelTime(null);
		entityManager.persist(letter);
		return letter;
	}

	@Transactional
	public  List<Letter> createLetterBatch(String fromUserno, String toUserno, Integer letterType, String title,
			String content) {
		if (StringUtils.isBlank(fromUserno)) {
			throw new IllegalArgumentException("the argument fromUserno is require");
		}
		if (StringUtils.isBlank(toUserno)) {
			throw new IllegalArgumentException("the argument toUserno is require");
		}
		if (StringUtils.isBlank(title)) {
			throw new IllegalArgumentException("the argument title is require");
		}
		String[] toUsernoArray = toUserno.split(",");
		List<Letter> resultList = new ArrayList<Letter>();
		Set<String> usernoSet = new HashSet<String>();
		for (String userno : toUsernoArray) {
			usernoSet.add(userno);
		}
		for (String userno : usernoSet) {
			resultList.add(createLetter(fromUserno, userno, letterType, title, content));
		}
		return resultList;
	}

	@Transactional
	public  Letter update2Read(String id) {
		if (StringUtils.isBlank(id)) {
			throw new IllegalArgumentException("the argument id is require");
		}
		Letter letter = findLetter(id, true);
		letter.setHasRead(1);
		letter.setReadTime(new Date());
		return entityManager.merge(letter);
	}

	@Transactional
	public  List<Letter> update2ReadBatch(String id) {
		if (StringUtils.isBlank(id)) {
			throw new IllegalArgumentException("the argument id is require");
		}
		String[] idArray = id.split(",");
		List<Letter> resultList = new ArrayList<Letter>();
		for (String iditem : idArray) {
			resultList.add(update2Read(iditem));
		}
		return resultList;
	}

	@Transactional
	public  Letter update2Del(String id) {
		if (StringUtils.isBlank(id)) {
			throw new IllegalArgumentException("the argument id is require");
		}
		Letter letter = findLetter(id, true);
		letter.setHasDel(1);
		letter.setDelTime(new Date());
		return entityManager.merge(letter);
	}

	@Transactional
	public  List<Letter> update2DelBatch(String id) {
		if (StringUtils.isBlank(id)) {
			throw new IllegalArgumentException("the argument id is require");
		}
		String[] idArray = id.split(",");
		List<Letter> resultList = new ArrayList<Letter>();
		for (String iditem : idArray) {
			resultList.add(update2Del(iditem));
		}
		return resultList;
	}

	public  void findLetterByPage(Map<String, Object> conditionMap, Page<Letter> page) {
		String sql = "SELECT o FROM Letter o ";
		String countSql = "SELECT count(*) FROM Letter o ";
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
		TypedQuery<Letter> q = entityManager.createQuery(tsql, Letter.class);
		TypedQuery<Long> total = entityManager.createQuery(tCountSql, Long.class);
		if (conditionMap != null && conditionMap.size() > 0) {
			PropertyFilter.setMatchValue2Query(q, pfList);
			PropertyFilter.setMatchValue2Query(total, pfList);
		}
		q.setFirstResult(page.getPageIndex()).setMaxResults(page.getMaxResult());
		List<Letter> resultList = q.getResultList();
		int count = total.getSingleResult().intValue();
		page.setList(resultList);
		page.setTotalResult(count);
	}

	public  int findCountUnread(String userno) {
		String sql = "SELECT count(*) FROM Letter o  WHERE hasRead = '0' and toUserno = :toUserno ";
		TypedQuery<Long> q = entityManager.createQuery(sql, Long.class);
		q.setParameter("toUserno", userno);
		int count = q.getSingleResult().intValue();
		return count;
	}
	
	
	/*///////////////////////////////
	
	@Transactional
    public void Letter.persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional
    public void Letter.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            Letter attached = Letter.findLetter(this.id);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional
    public void Letter.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional
    public void Letter.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    @Transactional
    public Letter Letter.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        Letter merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
    public static final EntityManager Letter.entityManager() {
        EntityManager em = new Letter().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static long Letter.countLetters() {
        return entityManager().createQuery("SELECT COUNT(o) FROM Letter o", Long.class).getSingleResult();
    }
    
    public static List<Letter> Letter.findAllLetters() {
        return entityManager().createQuery("SELECT o FROM Letter o", Letter.class).getResultList();
    }
    
    public static Letter Letter.findLetter(String id) {
        if (id == null || id.length() == 0) return null;
        return entityManager().find(Letter.class, id);
    }
    
    public static List<Letter> Letter.findLetterEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM Letter o", Letter.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }*/
}
