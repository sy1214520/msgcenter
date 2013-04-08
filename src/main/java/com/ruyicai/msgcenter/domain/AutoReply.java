package com.ruyicai.msgcenter.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Id;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

import com.ruyicai.msgcenter.service.MemcachedService;

/**
 * 自动回复短信设置
 */
@RooToString
@RooJavaBean
@RooJson
@RooEntity(versionField = "", table = "AUTOREPLY")
public class AutoReply implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 接收到的短信标识 */
	@Id
	@Column(name = "RECEIVEKEY", length = 50)
	private String receiveKey;

	/** 自动回复的短信内容 */
	@Column(name = "REPLYCONTENT", columnDefinition = "text")
	private String replyContent;

	@Autowired
	transient MemcachedService<AutoReply> memcachedService;

	public static AutoReply createOrMergeAutoReply(String receiveKey, String replyContent) {
		AutoReply autoReply = AutoReply.findAutoReply(receiveKey);
		if (autoReply != null) {
			autoReply.setReplyContent(replyContent);
			autoReply.merge();
		} else {
			autoReply = new AutoReply();
			autoReply.setReceiveKey(receiveKey);
			autoReply.setReplyContent(replyContent);
			autoReply.persist();
		}
		new AutoReply().memcachedService.set("AutoReply" + receiveKey, autoReply);
		return autoReply;
	}

	public static AutoReply findByReceiveKey(String receiveKey) {
		AutoReply autoReply = new AutoReply().memcachedService.get("AutoReply" + receiveKey);
		if (autoReply != null) {
			return autoReply;
		} else {
			autoReply = AutoReply.findAutoReply(receiveKey);
			if (autoReply == null) {
				return null;
			}
			new AutoReply().memcachedService.checkToSet("AutoReply" + receiveKey, autoReply);
			return autoReply;
		}
	}
}
