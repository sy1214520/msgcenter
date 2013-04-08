package com.ruyicai.msgcenter.jms.listener;

import java.util.Date;

import org.apache.camel.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruyicai.msgcenter.consts.SendState;
import com.ruyicai.msgcenter.domain.SendIOSDetail;
import com.ruyicai.msgcenter.service.IOSService;

@Service
public class SendIOSListener {

	private Logger logger = LoggerFactory.getLogger(SendIOSListener.class);

	@Autowired
	private IOSService iosService;

	public void sendIOSMS(@Header(value = "id") long id, @Header(value = "token") String token,
			@Header(value = "text") String text, @Header(value = "channelName") String channelName,
			@Header(value = "userno") String userno) {
		logger.info("发送IOS消息, token:" + token + ", text: " + text + ", channelName: " + channelName);
		int result = iosService.sendMsg(token, text, channelName, userno);
		SendIOSDetail sendIOSDetail = SendIOSDetail.findSendIOSDetail(id);
		if (result == 1) {
			sendIOSDetail.setSendState(SendState.SEND_SUCCESS.value());
			sendIOSDetail.setSendSuccTime(new Date());
			sendIOSDetail.merge();
		} else if (result == 0) {
			sendIOSDetail.setSendState(SendState.SEND_FAILURE.value());
			sendIOSDetail.merge();
		}
	}

}
