package com.ruyicai.msgcenter.domain;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Query;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.tostring.RooToString;

import com.ruyicai.msgcenter.util.Page;
import com.ruyicai.msgcenter.util.PropertyFilter;

@RooToString
@RooEntity(versionField = "", table = "Message", identifierField = "id")
public class Message {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private int id;

	@Column(name = "userno", length = 20)
	private String userno;

	@Column(name = "imsi", length = 100)
	private String imsi;

	@Column(name = "content")
	private String content;

	@Column(name = "detail")
	private String detail;

	@Column(name = "flag")
	private int flag;

	@Column(name = "createtime")
	private Date createtime;

	@Column(name = "reply")
	private String reply;

	/** 0：未读，1：已读 */
	@Column(name = "HASREAD")
	private int hasRead;

	/** 读取时间 */
	@Column(name = "READTIME")
	private Date readTime;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUserno() {
		return userno;
	}

	public void setUserno(String userno) {
		this.userno = userno;
	}

	public String getImsi() {
		return imsi;
	}

	public void setImsi(String imsi) {
		this.imsi = imsi;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

	public String getReply() {
		return reply;
	}

	public void setReply(String reply) {
		this.reply = reply;
	}

	public int getHasRead() {
		return hasRead;
	}

	public void setHasRead(int hasRead) {
		this.hasRead = hasRead;
	}

	public Date getReadTime() {
		return readTime;
	}

	public void setReadTime(Date readTime) {
		this.readTime = readTime;
	}

	/**
	 * 查询分页
	 * 
	 * @param page
	 *            分页集合
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Page<Message> findMsg(Map<String, Object> conditionMap, Page<Message> page) {
		EntityManager em = Message.entityManager();
		String sql = "SELECT o.* FROM Message o ";
		String countSql = "SELECT count(*) FROM Message o ";
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

		Query q = em.createNativeQuery(topSql, Message.class);
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

	/**
	 * 查询分页
	 * 
	 * @param page
	 *            分页集合
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Message findLastMsg(String userno) {
		EntityManager em = Message.entityManager();
		String sql = "SELECT o.* FROM Message o WHERE o.userno = ? ORDER BY o.createtime DESC";
		Query q = em.createNativeQuery(sql, Message.class);
		q.setParameter(1, userno);
		q.setFirstResult(0).setMaxResults(1);
		List<Message> list = q.getResultList();
		return list.size() > 0 ? list.get(0) : null;
	}

}
