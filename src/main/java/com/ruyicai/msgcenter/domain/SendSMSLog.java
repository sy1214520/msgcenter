package com.ruyicai.msgcenter.domain;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;

import org.apache.commons.lang.StringUtils;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

/**
 * 发送短信记录
 */
@RooToString
@RooJavaBean
@RooEntity(versionField = "", table = "SENDSMSLOG")
public class SendSMSLog {

	@Column(name = "mobileid", columnDefinition = "text")
	private String mobileid;

	@Column(name = "content", columnDefinition = "text")
	private String content;

	@Column(name = "channelName", length = 100)
	private String channelName;

	@Column(name = "result")
	private String result;

	@Column(name = "createTime")
	private Date createTime;

	@Column(name = "smscount")
	private Integer smscount;

	public SendSMSLog() {
		super();
	}

	public SendSMSLog(String mobileid, String content, String channelName, String result) {
		super();
		this.mobileid = mobileid;
		this.content = content;
		this.channelName = channelName;
		this.result = result;
		this.createTime = new Date();
	}

	public static SendSMSLog createSendSMSLog(String mobileid, String content, String channelName, String result) {
		System.out.println("save1");
		SendSMSLog log = new SendSMSLog(mobileid, content, channelName, result);
		System.out.println("save2");
		log.setSmscount(computeSmsCount(mobileid));
		System.out.println("save3");
		log.persist();
		System.out.println("save4");
		return log;
	}

	private static Integer computeSmsCount(String mobileid) {
		if (StringUtils.isNotBlank(mobileid)) {
			String[] mobileArray = mobileid.split("\\,");
			return mobileArray.length;
		} else {
			return 0;
		}
	}

	public static void computeCount() {
		int i = 0;
		int step = 100;
		List<SendSMSLog> list = findSendSMSLogEntries(i, step);
		while (list != null && list.size() > 0) {
			System.out.println(i);
			for (SendSMSLog log : list) {
				if (log.getSmscount() == null) {
					String mobileids = log.getMobileid();
					if (StringUtils.isNotBlank(mobileids)) {
						String[] split = mobileids.split(",");
						int sum = split.length;
						log.setSmscount(sum);
						log.merge();
					}
				}
			}
			i = i + step;
			list = findSendSMSLogEntries(i, step);
		}
	}
}
