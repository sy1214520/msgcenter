package com.ruyicai.msgcenter.service;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsService;
import com.notnoop.exceptions.InvalidSSLConfig;
import com.notnoop.exceptions.NetworkIOException;
import com.notnoop.exceptions.RuntimeIOException;
import com.ruyicai.msgcenter.consts.IOSType;
import com.ruyicai.msgcenter.consts.SendState;
import com.ruyicai.msgcenter.dao.SendIOSDetailDao;
import com.ruyicai.msgcenter.domain.DeviceToken;
import com.ruyicai.msgcenter.domain.SendIOSDetail;
import com.ruyicai.msgcenter.domain.Token;
import com.ruyicai.msgcenter.util.VerifyUtil;

@Service
public class IOSService implements ApplicationListener<ContextRefreshedEvent> {

	private Logger logger = LoggerFactory.getLogger(IOSService.class);

	static Map<String, String> map = new HashMap<String, String>();
	private ResourceLoader resourceLoader = new DefaultResourceLoader();

	@Produce(uri = "secondjms:topic:sendIOS2UserProducer")
	private ProducerTemplate sendIOS2UserProducer;

	@Autowired
	private AsyncLogSendService asyncLogSendService;

	@Autowired
	private SendIOSDetailDao sendIOSDetailDao;

	public Map<String, ApnsService> apnsServiceMap = new HashMap<String, ApnsService>();

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		logger.info("init ApnsService start");
		try {
			for (IOSType iosType : IOSType.values()) {
				if (iosType.key.equals("appStore") || iosType.key.equals("ruyicai_happy_product")) {
					ApnsService iosService = APNS.newService()
							.withCert(resourceLoader.getResource(iosType.value).getInputStream(), "haojie123")
							.withProductionDestination().build();
					apnsServiceMap.put(iosType.key, iosService);
				} else {
					ApnsService iosService = APNS.newService()
							.withCert(resourceLoader.getResource(iosType.value).getInputStream(), "haojie123")
							.withSandboxDestination().build();
					apnsServiceMap.put(iosType.key, iosService);
				}
			}
			logger.info("init ApnsService end");
		} catch (InvalidSSLConfig e) {
			logger.error("init ApnsService error", e);
		} catch (IOException e) {
			logger.error("init ApnsService error", e);
		}
	}

	public void createAllUserIOSDetail(String text, Date sendTimeDate) {
		List<Token> findAllTokens = Token.findAllTokens();
		sendIOSDetailDao.createIOSDetailsByToken(findAllTokens, text, sendTimeDate);
		// List<DeviceToken> list = DeviceToken.findAllDeviceTokens();
		// sendIOSDetailDao.createIOSDetails(list, text, sendTimeDate);
	}

	public void sendIOSJms(List<SendIOSDetail> sendIOSDetails) {
		logger.info("发送IOSJMS:" + sendIOSDetails.size());
		for (SendIOSDetail sendIOSDetail : sendIOSDetails) {
			sendIOSDetail.setSendState(SendState.SMS_SENDING.value());
			sendIOSDetail.merge();
			Map<String, Object> header = new HashMap<String, Object>();
			header.put("id", sendIOSDetail.getId());
			header.put("token", sendIOSDetail.getToken());
			header.put("text", sendIOSDetail.getContent());
			header.put("channelName", sendIOSDetail.getChannelName());
			header.put("userno", sendIOSDetail.getUserno());
			sendIOS2UserProducer.sendBodyAndHeaders(null, header);
		}
	}

	public Integer sendMsg(Collection<String> token, String content, String type, String userno) {
		Integer result = 0;
		if (token == null || token.isEmpty()) {
			return result;
		}
		logger.info("发送IOS消息content:{}, type:{}", new String[] { content, type });
		ApnsService iosService = apnsServiceMap.get(type);
		if (iosService == null) {
			logger.error("无效的ApnsService:" + type);
			return result;
		}
		try {
			String payload = APNS.newPayload().badge(1).sound("default").alertBody(content).build();
			iosService.push(token, payload);
			result = 1;
		} catch (RuntimeIOException e) {
			logger.error("发送IOS消息异常token:" + token + ",content:" + content + ",type:" + type, e);
			result = 0;
		} catch (InvalidSSLConfig e) {
			logger.error("发送IOS消息异常token:" + token + ",content:" + content + ",type:" + type, e);
			result = 0;
		} catch (NetworkIOException e) {
			logger.error("发送IOS消息异常token:" + token + ",content:" + content + ",type:" + type, e);
			result = 0;
		} finally {
			asyncLogSendService.createIOSLog(token, type, content, result + "", userno);
		}
		return result;
	}

	public Integer sendMsg(String token, String content, String type, String userno) {
		Set<String> tokenSet = new HashSet<String>();
		tokenSet.add(token);
		return sendMsg(tokenSet, content, type, userno);
	}

	@Transactional
	public void createDelayIOSDetail(String[] usernoArray, String text, Date sendTimeDate) {
		Set<String> usernoSet = new HashSet<String>();
		for (String userno : usernoArray) {
			if (StringUtils.isNotBlank(userno)) {
				usernoSet.add(userno);
			}
		}
		Set<DeviceToken> dtSet = new HashSet<DeviceToken>();
		for (String userno : usernoSet) {
			List<DeviceToken> list = DeviceToken.findAllDeviceTokensByUserno(userno);
			if (list != null && list.size() > 0) {
				dtSet.addAll(list);
			}
		}
		sendIOSDetailDao.createIOSDetails(dtSet, text, sendTimeDate);
	}

	@Transactional
	public Integer deleteInactiveDevices() {
		int i = 0;
		for (String key : apnsServiceMap.keySet()) {
			ApnsService apnsService = apnsServiceMap.get(key);
			Map<String, Date> inactiveDevices = apnsService.getInactiveDevices();
			for (String deviceToken : inactiveDevices.keySet()) {
				String formatToken = null;
				if (StringUtils.isNotBlank(deviceToken)) {
					formatToken = VerifyUtil.formatToken(deviceToken);
				}
				if (StringUtils.isNotEmpty(formatToken)) {
					Integer count = DeviceToken.removeDeviceTokenByToken(deviceToken);
					if (count != null && count > 0) {
						logger.info("删除IOS证书key:{},token:{},count:{}", new String[] { key, formatToken, count + "" });
						i += count;
					}
					Boolean flag = Token.removeTokenByToken(deviceToken);
					if (flag) {
						logger.info("删除推广IOS证书key:{}", new String[] { formatToken });
					}
				}
			}
		}
		return i;
	}
}
