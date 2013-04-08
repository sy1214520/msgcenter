package com.ruyicai.msgcenter.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.msgcenter.domain.Message;

@Component
public class MessageDao {

	@PersistenceContext
	private EntityManager entityManager;
	
	@Transactional
	public  List<Message> update2ReadBatch(String id) {
		if (StringUtils.isBlank(id)) {
			throw new IllegalArgumentException("the argument id is require");
		}
		String[] idArray = id.split(",");
		List<Message> resultList = new ArrayList<Message>();
		for (String iditem : idArray) {
			resultList.add(update2Read(Integer.parseInt(iditem)));
		}
		return resultList;
	}
	@Transactional
	public  Message update2Read(int id) {
		Message message  = findMessage(id, true);
		message.setHasRead(1);
		message.setReadTime(new Date());
		return entityManager.merge(message);
	}
	public  Message findMessage(int id, boolean lock) {
		Message message = entityManager.find(Message.class, id,
				lock ? LockModeType.PESSIMISTIC_WRITE : LockModeType.NONE);
		return message;
	}
	
	public int findCountUnread(String userno) {
		String sql = "SELECT count(*) FROM Message o  WHERE hasRead = '0' and o.userno = ? ";
		TypedQuery<Long> q = entityManager.createQuery(sql, Long.class);
		q.setParameter(1, userno);
		int count = q.getSingleResult().intValue();
		return count;
	}
}
