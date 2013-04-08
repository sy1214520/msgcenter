package com.ruyicai.msgcenter.sms;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ruyicai.msgcenter.domain.DeviceToken;
import com.ruyicai.msgcenter.service.UserSettingService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml",
		"classpath:/META-INF/spring/applicationContext-jms.xml",
		"classpath:/META-INF/spring/applicationContext-memcache.xml",
		"classpath:/META-INF/spring/applicationContext-mail.xml", })
public class UserSettingServiceTest {

	@Autowired
	UserSettingService userSettingService;

	@Test
	public void testUpdateUserSettingBatch() {
		String str = "[{\"sendChannels\":[],\"userSMSTiming\":{\"id\":31,\"needToSend\":0}},{\"sendChannels\":[],\"userSMSTiming\":{\"id\":32,\"needToSend\":0}},{\"sendChannels\":[],\"userSMSTiming\":{\"id\":33,\"needToSend\":0}}]";
		userSettingService.updateUserSMSTimingBatch(str);
	}

	@Test
	public void testUpdateUserSettingBatch2() {
		String str = "[{\"sendChannels\":[{\"needToSend\":1,\"userSMStimingId\":63}],\"userSMSTiming\":{\"id\":31,\"needToSend\":1}},{\"sendChannels\":[],\"userSMSTiming\":{\"id\":32,\"needToSend\":1}},{\"sendChannels\":[],\"userSMSTiming\":{\"id\":33,\"needToSend\":1}}]";
		userSettingService.updateUserSMSTimingBatch(str);
	}

	@Test
	public void testCreateDeviceToken() {
		DeviceToken.createIfNotExist("00000042",
				"de13cfbe 0f8da6d2 d61d1a7a 92a1d29d c945626c 901a21c0 33cb4123 e81da910", "91", "iPad", 1);
	}
}
