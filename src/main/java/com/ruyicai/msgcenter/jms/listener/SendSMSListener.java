package com.ruyicai.msgcenter.jms.listener;

import org.apache.camel.Header;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruyicai.msgcenter.service.SMSService;

@Service
public class SendSMSListener {

	private Logger logger = LoggerFactory.getLogger(SendSMSListener.class);

	@Autowired
	private SMSService smsService;

	public void sendSMS(@Header(value = "mobileid") String mobileIds, @Header(value = "text") String text,
			@Header(value = "channelName") String channelName) {
		logger.info("发送短信, mobileIds:" + mobileIds + ", text: " + text + ", channelName: " + channelName);
		if (StringUtils.isBlank(mobileIds)) {
			return;
		}
		String[] mobiles = mobileIds.split(",");
		if (StringUtils.isNotBlank(channelName)) {
			smsService.immediateSendMessage(mobiles, text, channelName);
		} else {
			smsService.immediateSendMessage(mobiles, text);
		}
	}

}
