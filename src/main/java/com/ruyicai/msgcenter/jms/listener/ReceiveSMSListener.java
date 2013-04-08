package com.ruyicai.msgcenter.jms.listener;

import java.util.StringTokenizer;

import org.apache.camel.Header;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruyicai.msgcenter.domain.AutoReply;
import com.ruyicai.msgcenter.service.SMSService;

@Service
public class ReceiveSMSListener {

	private Logger logger = LoggerFactory.getLogger(ReceiveSMSListener.class);

	@Autowired
	private SMSService smsService;

	public void receiveSMSCustomer(@Header("id") Long id, @Header("mobileid") String mobileid,
			@Header("content") String content, @Header("channelName") String channelName) {
		logger.info("接收到上行短信id:{},mobileid:{},content:{},channelName:{}", new String[] { id + "", mobileid, content,
				channelName });
		if (StringUtils.isNotBlank(content)) {
			StringTokenizer st = new StringTokenizer(content);
			String key = st.nextToken();
			AutoReply autoReply = AutoReply.findByReceiveKey(key);
			if (autoReply != null && StringUtils.isNotBlank(autoReply.getReplyContent())) {
				smsService.immediateSendMessage(mobileid, autoReply.getReplyContent(), channelName);
			}
		}
	}
}
