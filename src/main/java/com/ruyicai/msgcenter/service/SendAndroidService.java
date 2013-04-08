package com.ruyicai.msgcenter.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

import cn.jpush.api.JPushClient;
import cn.jpush.api.MessageResult;

import com.ruyicai.msgcenter.consts.AndroidPkgType;
import com.ruyicai.msgcenter.domain.JPushKey;
import com.ruyicai.msgcenter.exception.RuyicaiException;
import com.ruyicai.msgcenter.util.ErrorCode;

@Service
public class SendAndroidService implements ApplicationListener<ContextRefreshedEvent> {

	private Logger logger = LoggerFactory.getLogger(SendAndroidService.class);

	public Map<String, JPushClient> jpushMap = new HashMap<String, JPushClient>();

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		logger.info("init JPushClient start");
		try {
			for (AndroidPkgType type : AndroidPkgType.values()) {
				JPushKey jPushKey = JPushKey.findByPkgName(type.pkgName);
				if (jPushKey != null) {
					jpushMap.put(jPushKey.getPkgName(),
							new JPushClient(jPushKey.getMasterSecret(), jPushKey.getAppKey()));
				} else {
					logger.error("pkgName:" + type.pkgName + " not found jPushKey");
				}
			}
			logger.info("init JPushClient end");
		} catch (Exception e) {
			logger.error("init JPushClient error", e);
		}
	}

	/**
	 * 发送带Tag的通知
	 * 
	 * @param pkgName
	 * @param sendNo
	 * @param tag
	 * @param msgTitle
	 * @param msgContent
	 * @return
	 */
	public MessageResult pushWithTag(String pkgName, int sendNo, String tag, String msgTitle, String msgContent) {
		if (StringUtils.isBlank(pkgName)) {
			throw new IllegalArgumentException("the argument pkgName is require");
		}
		if (StringUtils.isBlank(tag)) {
			throw new IllegalArgumentException("the argument tag is require");
		}
		if (StringUtils.isBlank(msgTitle)) {
			throw new IllegalArgumentException("the argument msgTitle is require");
		}
		if (StringUtils.isBlank(msgContent)) {
			throw new IllegalArgumentException("the argument msgContent is require");
		}
		JPushClient jPushClient = this.getJpushClient(pkgName);
		if (jPushClient == null) {
			logger.error("pkgName:" + pkgName + " error");
			throw new RuyicaiException(ErrorCode.PARAMTER_ERROR);
		}
		MessageResult msgResult = jPushClient.sendNotificationWithTag(sendNo, tag, msgTitle, msgContent);
		return msgResult;
	}

	/**
	 * 发送带Alias的通知
	 * 
	 * @param pkgName
	 * @param sendNo
	 * @param alias
	 * @param msgTitle
	 * @param msgContent
	 * @return
	 */
	public MessageResult pushWithAlias(String pkgName, int sendNo, String alias, String msgTitle, String msgContent) {
		if (StringUtils.isBlank(pkgName)) {
			throw new IllegalArgumentException("the argument pkgName is require");
		}
		if (StringUtils.isBlank(alias)) {
			throw new IllegalArgumentException("the argument alias is require");
		}
		if (StringUtils.isBlank(msgTitle)) {
			throw new IllegalArgumentException("the argument msgTitle is require");
		}
		if (StringUtils.isBlank(msgContent)) {
			throw new IllegalArgumentException("the argument msgContent is require");
		}
		JPushClient jPushClient = this.getJpushClient(pkgName);
		if (jPushClient == null) {
			logger.error("pkgName:" + pkgName + " error");
			throw new RuyicaiException(ErrorCode.PARAMTER_ERROR);
		}
		MessageResult msgResult = jPushClient.sendNotificationWithAlias(sendNo, alias, msgTitle, msgContent);
		return msgResult;
	}

	/**
	 * 发送通知给AppKey的所有用户
	 * 
	 * @param pkgName
	 * @param sendNo
	 * @param msgTitle
	 * @param msgContent
	 * @return
	 */
	public MessageResult pushWithAppKey(String pkgName, int sendNo, String msgTitle, String msgContent) {
		if (StringUtils.isBlank(pkgName)) {
			throw new IllegalArgumentException("the argument pkgName is require");
		}
		if (StringUtils.isBlank(msgTitle)) {
			throw new IllegalArgumentException("the argument msgTitle is require");
		}
		if (StringUtils.isBlank(msgContent)) {
			throw new IllegalArgumentException("the argument msgContent is require");
		}
		JPushClient jPushClient = this.getJpushClient(pkgName);
		if (jPushClient == null) {
			logger.error("pkgName:" + pkgName + " error");
			throw new RuyicaiException(ErrorCode.PARAMTER_ERROR);
		}
		MessageResult msgResult = jPushClient.sendNotificationWithAppKey(sendNo, msgTitle, msgContent);
		return msgResult;
	}

	private JPushClient getJpushClient(String pkgName) {
		JPushClient jPushClient = jpushMap.get(pkgName);
		if (jPushClient == null) {
			logger.error("pkgName:" + pkgName + " error");
			throw new RuyicaiException(ErrorCode.PARAMTER_ERROR);
		}
		return jPushClient;
	}
}
