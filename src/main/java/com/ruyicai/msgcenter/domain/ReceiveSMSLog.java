package com.ruyicai.msgcenter.domain;

import java.util.Date;

import javax.persistence.Column;

import org.apache.commons.lang.StringUtils;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

import com.ruyicai.msgcenter.util.DateUtil;

/**
 * 上行短信记录
 */
@RooToString
@RooJavaBean
@RooJson
@RooEntity(versionField = "", table = "RECEIVESMSLOG")
public class ReceiveSMSLog {

	@Column(name = "mobileid", length = 50)
	private String mobileid;

	@Column(name = "content", columnDefinition = "text")
	private String content;

	@Column(name = "sendTime")
	private Date sendTime;

	@Column(name = "createTime")
	private Date createTime;

	@Column(name = "channelName", length = 50)
	private String channelName;

	public static ReceiveSMSLog createSendSMSLog(String message, String channelName) {
		String[] split = message.split("\\,");
		if (split.length >= 6) {
			String date = StringUtils.substringBefore(message, ",");
			message = StringUtils.substringAfter(message, ",");
			String time = StringUtils.substringBefore(message, ",");
			message = StringUtils.substringAfter(message, ",");
			String mobileid = StringUtils.substringBefore(message, ",");
			message = StringUtils.substringAfter(message, ",");
			message = StringUtils.substringAfter(message, ",");
			String content = StringUtils.substringAfter(message, ",");
			ReceiveSMSLog log = new ReceiveSMSLog();
			Date dateTime = DateUtil.parse(date + " " + time);
			log.setSendTime(dateTime);
			log.setContent(content);
			log.setMobileid(mobileid);
			log.setCreateTime(new Date());
			log.setChannelName(channelName);
			log.persist();
			return log;
		} else {
			return null;
		}
	}
}
