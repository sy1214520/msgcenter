package com.ruyicai.msgcenter.sms;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsService;
import com.ruyicai.msgcenter.domain.DeviceToken;
import com.ruyicai.msgcenter.jms.listener.SendAddNumEndSMSListener;
import com.ruyicai.msgcenter.service.IOSService;
import com.ruyicai.msgcenter.util.DateUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml",
		"classpath:/META-INF/spring/applicationContext-jms.xml",
		"classpath:/META-INF/spring/applicationContext-memcache.xml",
		"classpath:/META-INF/spring/applicationContext-mail.xml", })
public class IOSServiceTest {

	@Autowired
	private IOSService iosService;

	@Autowired
	private SendAddNumEndSMSListener addNumEndSMSListener;
	
	@Test
	public void testRemoveToken(){
		Integer count = DeviceToken.removeDeviceTokenByToken("637345cd f1adaef1 aa348215 8c8d672d 0113d4f4 3f292503 9383eb26 6cf90c17");
		System.out.println(count);
	}

	@Test
	public void testSendIOS() {
		String token = "2a55f713 ce0152cc 009f3324 79af3337 30cf28fc 41eafe9b 1731acc1 b6c4fc2c";
		ApnsService apnsService = iosService.apnsServiceMap.get("ruyicai");
		String payload = APNS.newPayload().badge(1).sound("default")
				.alertBody("帅帅帅帅帅帅帅帅帅帅帅帅帅帅帅").build();
		apnsService.push(token, payload);
	}

	/*
	 * 366D91BFD66D801E13F99891013F7F5F5A2FC8EAD24543AA016066C41DD7E437:2013-01-24
	 * 21:46:08
	 * 637345CDF1ADAEF1AA3482158C8D672D0113D4F43F2925039383EB266CF90C17:
	 * 2013-01-25 14:10:49
	 * 3C03F1B16885E0B5DF04D09A7D02A8AFFA6C50A7236D284B7FCCD4DDE56478CE
	 * :2013-01-25 14:11:04
	 * DE13CFBE0F8DA6D2D61D1A7A92A1D29DC945626C901A21C033CB4123E81DA910
	 * :2013-01-25 14:10:47
	 */

	public static void main(String[] args) throws UnsupportedEncodingException {
		String str = "孙扬1234";
		System.out.println(StringUtils.length(str));
		System.out.println(str.length());
		str = new String(str.getBytes("GBK"), "iso-8859-1");
		System.out.println(StringUtils.length(str));
		System.out.println(str.length());
		System.out.println("------------------------");
		// 366d91bf d66d801e 13f99891 013f7f5f 5a2fc8ea d24543aa 016066c4
		// 1dd7e437
		String token = "366D91BFD66D801E13F99891013F7F5F5A2FC8EAD24543AA016066C41DD7E437";
		String token1 = "637345CDF1ADAEF1AA3482158C8D672D0113D4F43F2925039383EB266CF90C17";
		String token2 = "3C03F1B16885E0B5DF04D09A7D02A8AFFA6C50A7236D284B7FCCD4DDE56478CE";
		String token3 = "DE13CFBE0F8DA6D2D61D1A7A92A1D29DC945626C901A21C033CB4123E81DA910";
		System.out.println(formatToken(token));
		System.out.println(formatToken(token1));
		System.out.println(formatToken(token2));
		System.out.println(formatToken(token3));
	}

	public static String formatToken(String token) {
		StringBuilder sb = new StringBuilder();
		for (int i = 1; i <= token.length(); i++) {
			sb.append(token.charAt(i - 1));
			if (i % 8 == 0) {
				sb.append(" ");
			}
		}
		return sb.toString().trim();
	}

	@Test
	public void testInactiveDevices() {
		Map<String, ApnsService> map = iosService.apnsServiceMap;
		for(String key : map.keySet()){
			System.out.println("key:"+key);
			ApnsService apnsService = iosService.apnsServiceMap.get(key);
			Map<String, Date> inactiveDevices = apnsService.getInactiveDevices();
			for (String deviceToken : inactiveDevices.keySet()) {
				System.out.println("inactiveDevicesToken:"+formatToken(deviceToken));
				Date inactiveAsOf = inactiveDevices.get(deviceToken);
				System.out.println("inactiveAsOf:" + DateUtil.format(inactiveAsOf));
			}
		}
	}
	
	@Test
	public void testSendAllIOS() {
		List<DeviceToken> list = DeviceToken.findAllDeviceTokens();
		for (DeviceToken token : list) {
			System.out.println(token);
			iosService.sendMsg(token.getId().getToken(), "测试发送的推送消息，孙扬", token.getId().getType(), token.getId()
					.getUserno());
		}
	}

	@Test
	public void testSendAddNumEndSMSListener() {
		String flowno = "0000000000295818";
		addNumEndSMSListener.addNumEndCustomer(flowno);
	}
}
