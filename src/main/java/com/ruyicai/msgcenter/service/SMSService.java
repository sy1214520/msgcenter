package com.ruyicai.msgcenter.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.msgcenter.consts.Const;
import com.ruyicai.msgcenter.dao.LogSMSSendDao;
import com.ruyicai.msgcenter.domain.ReceiveSMSLog;
import com.ruyicai.msgcenter.domain.SmsAuthentication;
import com.ruyicai.sms.SMSServiceProvider;

@Service("smsservice")
public class SMSService {

	private Logger logger = LoggerFactory.getLogger(SMSService.class);

	@Autowired
	Map<String, SMSServiceProvider> providers = new HashMap<String, SMSServiceProvider>();

	@Autowired
	private SendSMSDetailService sendSMSDetailService;

	@Autowired
	private AsyncLogSendService asyncLogSendService;

	@Autowired
	private LogSMSSendDao logSMSSendDao;

	@Produce(uri = "secondjms:topic:receiveSMSMessage")
	private ProducerTemplate receiveSMSMessageProducer;

	/**
	 * 接收上行短信
	 * 
	 * @param channelName
	 * @return
	 */
	public String[] receivesms(String channelName) {
		logger.info("接收上行短信");
		SMSServiceProvider smsServiceProvider = providers.get(channelName);
		if (smsServiceProvider == null) {
			logger.error("sms send error, smsServiceProvider is null");
			return null;
		}
		SmsAuthentication authentication = SmsAuthentication.findSmsAuthenticationFromCache(channelName);
		if (authentication == null || StringUtils.isBlank(authentication.getUserName())
				|| StringUtils.isBlank(authentication.getPwd())) {
			return null;
		}
		String[] messages = smsServiceProvider.receiveMessage(authentication.getUserName(), authentication.getPwd());
		if (messages != null) {
			for (String message : messages) {
				ReceiveSMSLog receiveSMSLog = ReceiveSMSLog.createSendSMSLog(message, channelName);
				if (receiveSMSLog != null) {
					sendReceiveSMSJms(receiveSMSLog);
				}
			}
			return messages;
		} else {
			return null;
		}
	}

	private void sendReceiveSMSJms(ReceiveSMSLog receiveSMSLog) {
		logger.info("发送上行短信广播JMS:" + receiveSMSLog.toString());
		Map<String, Object> header = new HashMap<String, Object>();
		header.put("id", receiveSMSLog.getId());
		header.put("mobileid", receiveSMSLog.getMobileid());
		header.put("content", receiveSMSLog.getContent());
		header.put("channelName", receiveSMSLog.getChannelName());
		receiveSMSMessageProducer.sendBodyAndHeaders(null, header);
	}

	/**
	 * 发送短信
	 * 
	 * @param mobileid
	 * @param text
	 * @param channelName
	 * @return
	 */
	public Integer sendsms(String mobileid, String text, String channelName) {
		logger.debug("发送短信, mobileIds:" + mobileid + ",text:" + text + ", channelName:" + channelName);
		final SMSServiceProvider smsServiceProvider = providers.get(channelName);
		if (smsServiceProvider == null) {
			logger.error("sms send error, smsServiceProvider is null");
			return null;
		}
		SmsAuthentication authentication = SmsAuthentication.findSmsAuthenticationFromCache(channelName);
		if (authentication == null || StringUtils.isBlank(authentication.getUserName())
				|| StringUtils.isBlank(authentication.getPwd())) {
			return null;
		}
		String result = null;
		String[] mobileArray = mobileid.split("\\,");
		result = smsServiceProvider.sendMessage(mobileArray, text, authentication.getUserName(),
				authentication.getPwd());
		asyncLogSendService.createSMSLog(mobileid, text, channelName, result);
		// SendSMSLog.createSendSMSLog(mobileid, text, channelName, result);
		if (result.length() > 10 && result.length() < 25) { // 返回的流水大于10位小于25位为提交成功
			logger.info("短信发送成功,mobileId:" + mobileid + ",result:" + result);
			return 1;
		} else {
			logger.info("短信发送失败,mobileId:" + mobileid + ",result:" + result);
			return Integer.parseInt(result);
		}
	}

	public void queryStatusReport(String channelName) {
		logger.debug("查询发送报告channelName:" + channelName);
		final SMSServiceProvider smsServiceProvider = providers.get(channelName);
		if (smsServiceProvider == null) {
			logger.error("queryStatusReport error, smsServiceProvider is null");
			return;
		}
		SmsAuthentication authentication = SmsAuthentication.findSmsAuthenticationFromCache(channelName);
		if (authentication == null || StringUtils.isBlank(authentication.getUserName())
				|| StringUtils.isBlank(authentication.getPwd())) {
			return;
		}
		String[] statusReportArray = smsServiceProvider.queryStatusReport(authentication.getUserName(),
				authentication.getPwd());
		while (statusReportArray != null) {
			logSMSSendDao.updateStatusReport(statusReportArray);
			statusReportArray = smsServiceProvider.queryStatusReport(authentication.getUserName(),
					authentication.getPwd());
		}
	}

	/**
	 * 立即发送
	 */
	public Integer immediateSendMessage(final String[] mobileIds, final String text) {
		return this.immediateSendMessage(mobileIds, text, Const.defaultSMSChannel);
	}

	/**
	 * 立即发送
	 */
	public Integer immediateSendMessage(final String[] mobileIds, final String text, final String channelName) {
		return sendsms(StringUtils.join(mobileIds, ","), text, channelName);
	}

	/**
	 * 立即发送
	 */
	public Integer immediateSendMessage(final String mobileId, final String text) {
		return this.immediateSendMessage(mobileId, text, Const.defaultSMSChannel);
	}

	/**
	 * 立即发送
	 */
	public Integer immediateSendMessage(final String mobileId, final String text, final String channelName) {
		return sendsms(mobileId, text, channelName);
	}

	/**
	 * 轮询发送，会判断是否延迟发送
	 */
	@Transactional
	public void sendMessage(final String[] mobileIds, final String text) {
		this.sendMessage(mobileIds, text, Const.defaultSMSChannel, null);
	}

	/**
	 * 轮询发送，会判断是否延迟发送,可以设定发送时间
	 */
	public void sendMessage(final String[] mobileIds, final String text, Date sendTime) {
		this.sendMessage(mobileIds, text, Const.defaultSMSChannel, sendTime);
	}

	/**
	 * 轮询发送，会判断是否延迟发送
	 */
	public void sendMessage(final String mobileIds, final String text) {
		this.sendMessage(mobileIds.split("\\,"), text, Const.defaultSMSChannel, null);
	}

	/**
	 * 轮询发送，会判断是否延迟发送,可以设定发送时间
	 */
	public void sendMessage(final String mobileIds, final String text, Date sendTime) {
		this.sendMessage(mobileIds.split("\\,"), text, Const.defaultSMSChannel, sendTime);
	}

	/**
	 * 轮询发送，会判断是否延迟发送,可以设定发送时间
	 */
	public void sendMessage(final String[] mobileIds, final String text, final String channelName, Date sendTime) {
		if (mobileIds != null && mobileIds.length > 0) {
			sendSMSDetailService.createSendSMSDetail(StringUtils.join(mobileIds, ","), text, channelName, null);
		}
	}
}
