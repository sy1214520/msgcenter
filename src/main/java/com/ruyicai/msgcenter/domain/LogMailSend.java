package com.ruyicai.msgcenter.domain;

import java.util.Date;

import javax.persistence.Column;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

@RooToString
@RooJavaBean
@RooEntity(versionField = "", table = "LOGMAILSEND")
public class LogMailSend {

	@Column(name = "SENDTO")
	private String sendTo;

	@Column(name = "CONTENT", columnDefinition = "text")
	private String content;

	@Column(name = "RESULT", length = 50)
	private String result;

	@Column(name = "CREATETIME")
	private Date createTime;

	public static LogMailSend createLogMailSend(String sendTo, String content, String result) {
		LogMailSend log = new LogMailSend();
		log.setSendTo(sendTo);
		log.setContent(content);
		log.setResult(result);
		log.setCreateTime(new Date());
		log.persist();
		return log;
	}
}
