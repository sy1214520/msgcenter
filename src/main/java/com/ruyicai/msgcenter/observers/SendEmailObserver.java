package com.ruyicai.msgcenter.observers;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ruyicai.lottery.domain.Tuserinfo;
import com.ruyicai.msgcenter.consts.SMSType;
import com.ruyicai.msgcenter.service.MailService;

@Component
public class SendEmailObserver implements ISendObserver {

	private Logger logger = LoggerFactory.getLogger(SendEmailObserver.class);

	@Autowired
	private MailService mailService;

	@Override
	public void sendMsg(Tuserinfo tuserinfo, SMSType smsType, Map<String, Object> params) {
		String subject = null;
		String content = null;
		if (tuserinfo == null || StringUtils.isBlank(tuserinfo.getEmail())) {
			logger.info("用户为空或邮箱地址为空tuserinfo:" + tuserinfo);
			return;
		}
		subject = getSubject(tuserinfo, smsType, params);
		content = getContent(tuserinfo, smsType, params);
		if (StringUtils.isNotBlank(subject) && StringUtils.isNotBlank(content)) {
			logger.info("发送邮件,地址:" + tuserinfo.getEmail());
			mailService.sendMimeMail(tuserinfo.getEmail(), subject, content);
		}
	}

	private String getSubject(Tuserinfo tuserinfo, SMSType smsType, Map<String, Object> params) {
		// String channel = tuserinfo.getChannel();
		String subject = null;
		return subject;
	}

	private String getContent(Tuserinfo tuserinfo, SMSType smsType, Map<String, Object> params) {
		// String channel = tuserinfo.getChannel();
		String content = null;
		return content;
	}

}
