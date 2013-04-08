package com.ruyicai.msgcenter.domain;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ruyicai.msgcenter.domain.SmsAuthentication;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml",
		"classpath:/META-INF/spring/applicationContext-jms.xml",
		"classpath:/META-INF/spring/applicationContext-memcache.xml",
		"classpath:/META-INF/spring/applicationContext-mail.xml", })
public class SmsAuthenticationTest {

	@Test
	public void testCreate() {
		SmsAuthentication.createOrMergeSmsAuthentication("menWangSMSServiceProvider", "J20062", "220001", null);
		SmsAuthentication.createOrMergeSmsAuthentication("menWangOldSMSServiceProvider", "J00348", "142753", null);
		SmsAuthentication.createOrMergeSmsAuthentication("mockSMSServiceProvider", "mock", "mock", null);
	}

}
