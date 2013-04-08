package com.ruyicai.msgcenter.service;

import java.util.Collection;
import java.util.concurrent.RejectedExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.ruyicai.msgcenter.domain.LogIOSSend;
import com.ruyicai.msgcenter.domain.LogMailSend;
import com.ruyicai.msgcenter.domain.LogSMSSend;

@Service
public class AsyncLogSendService {

	private Logger logger = LoggerFactory.getLogger(AsyncLogSendService.class);

	@Async
	public void createSMSLog(String mobileid, String text, String channelName, String result) {
		try {
			LogSMSSend.createLogSmsSend(mobileid, text, channelName, result);
		} catch (RejectedExecutionException e) {
			logger.error("异步创建短信日志第一次异常mobileid:" + mobileid + ",text:" + text + ",channelName:" + channelName
					+ ",result:" + result, e);
			try {
				LogSMSSend.createLogSmsSend(mobileid, text, channelName, result);
			} catch (RejectedExecutionException e2) {
				logger.error("异步创建短信日志第二次异常mobileid:" + mobileid + ",text:" + text + ",channelName:" + channelName
						+ ",result:" + result, e2);
			}
		}
	}

	@Async
	public void createMailLog(String sendTo, String content, String result) {
		try {
			LogMailSend.createLogMailSend(sendTo, content, result);
		} catch (RejectedExecutionException e) {
			logger.error("异步创建邮件日志第一次异常sendTo:" + sendTo + ",content:" + content + ",result:" + result, e);
			try {
				LogMailSend.createLogMailSend(sendTo, content, result);
			} catch (RejectedExecutionException e2) {
				logger.error("异步创建邮件日志第二次异常sendTo:" + sendTo + ",content:" + content + ",result:" + result, e);
			}
		}
	}

	@Async
	public void createIOSLog(String token, String type, String content, String result, String userno) {
		try {
			LogIOSSend.createLogIOSSend(token, type, content, result, userno);
		} catch (RejectedExecutionException e) {
			logger.error(
					"异步创建IOS日志第一次异常token:" + token + ",type:" + type + ",content:" + content + ",result:" + result, e);
			try {
				LogIOSSend.createLogIOSSend(token, type, content, result, userno);
			} catch (RejectedExecutionException e2) {
				logger.error("异步创建IOS日志第二次异常token:" + token + ",type:" + type + ",content:" + content + ",result:"
						+ result, e);
			}
		}
	}

	@Async
	public void createIOSLog(Collection<String> token, String type, String content, String result, String userno) {
		StringBuilder tokenSB = new StringBuilder();
		for (String str : token) {
			tokenSB.append(str + ",");
		}
		tokenSB.delete(tokenSB.length() - 1, tokenSB.length());
		try {
			LogIOSSend.createLogIOSSend(tokenSB.toString(), type, content, result, userno);
		} catch (RejectedExecutionException e) {
			logger.error("异步创建IOS日志第一次异常token:" + tokenSB.toString() + ",type:" + type + ",content:" + content
					+ ",result:" + result, e);
			try {
				LogIOSSend.createLogIOSSend(tokenSB.toString(), type, content, result, userno);
			} catch (RejectedExecutionException e2) {
				logger.error("异步创建IOS日志第二次异常token:" + tokenSB.toString() + ",type:" + type + ",content:" + content
						+ ",result:" + result, e);
			}
		}
	}
}
