package com.ruyicai.msgcenter.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.msgcenter.service.MemcachedService;

@RooToString
@RooJavaBean
@RooEntity(versionField = "", table = "SendChannel")
public class SendChannel implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "userSMStimingId")
	private Long userSMStimingId;

	/** 是否有效 0:不发送,1:发送 */
	@Column(name = "needToSend")
	private Integer needToSend;

	/** 发送类型 0短信，1邮件，2IPhone */
	@Column(name = "sendtype")
	private Integer sendtype;

	/** 最后修改时间 */
	@Column(name = "modifyTime")
	private Date modifyTime;

	@Autowired
	public transient MemcachedService<SendChannel> memcachedService;

	@Transactional
	public static SendChannel createOrMergeSendChannel(Long userSMStimingId, Integer needToSend, Integer sendtype) {
		if (userSMStimingId == null) {
			throw new IllegalArgumentException("the argument userSMStimingId is required");
		}
		if (sendtype == null) {
			throw new IllegalArgumentException("the argument sendtype is required");
		}
		SendChannel sendChannel = SendChannel.findByUserSMStimingIdAndSendtypeFromDB(userSMStimingId, sendtype);
		if (sendChannel == null) {
			sendChannel = SendChannel.createSendChannel(userSMStimingId, needToSend, sendtype);
		} else {
			sendChannel.setNeedToSend(needToSend);
			sendChannel.setModifyTime(new Date());
			sendChannel.merge();
		}
		new SendChannel().memcachedService.set("SendChannel" + userSMStimingId + sendtype, sendChannel);
		return sendChannel;
	}

	@Transactional
	public static SendChannel createSendChannel(Long userSMStimingId, Integer needToSend, Integer sendtype) {
		SendChannel sendChannel = new SendChannel();
		sendChannel.setUserSMStimingId(userSMStimingId);
		sendChannel.setNeedToSend(needToSend);
		sendChannel.setSendtype(sendtype);
		sendChannel.setModifyTime(new Date());
		sendChannel.persist();
		new SendChannel().memcachedService.set("SendChannel" + userSMStimingId + sendtype, sendChannel);
		return sendChannel;
	}

	public static List<SendChannel> findAllSendChannelsByUserSMStimingId(long userSMStimingId) {
		return entityManager()
				.createQuery("SELECT o FROM SendChannel o where o.userSMStimingId = :userSMStimingId",
						SendChannel.class).setParameter("userSMStimingId", userSMStimingId).getResultList();
	}

	public static SendChannel findByUserSMStimingIdAndSendtypeFromDB(long userSMStimingId, int sendtype) {
		List<SendChannel> resultList = entityManager()
				.createQuery(
						"SELECT o FROM SendChannel o where o.userSMStimingId = :userSMStimingId and o.sendtype=:sendtype",
						SendChannel.class).setParameter("userSMStimingId", userSMStimingId)
				.setParameter("sendtype", sendtype).getResultList();
		if (resultList != null && resultList.size() > 0) {
			return resultList.get(0);
		} else {
			return null;
		}
	}

	public static SendChannel findByUserSMStimingIdAndSendtypeFromCache(long userSMStimingId, int sendtype) {
		SendChannel sendChannel = new SendChannel().memcachedService.get("SendChannel" + userSMStimingId + sendtype);
		if (sendChannel != null) {
			return sendChannel;
		} else {
			sendChannel = findByUserSMStimingIdAndSendtypeFromDB(userSMStimingId, sendtype);
			if (sendChannel == null) {
				return null;
			} else {
				new SendChannel().memcachedService.set("SendChannel" + userSMStimingId + sendtype, sendChannel);
				return sendChannel;
			}
		}
	}

	@Transactional
	public void updateNeedToSend(Integer needToSend) {
		this.setNeedToSend(needToSend);
		this.setModifyTime(new Date());
		this.merge();
		new SendChannel().memcachedService.set("SendChannel" + this.getUserSMStimingId() + this.getSendtype(), this);
	}
}
