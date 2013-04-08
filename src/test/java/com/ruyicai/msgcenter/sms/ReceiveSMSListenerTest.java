package com.ruyicai.msgcenter.sms;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ruyicai.msgcenter.jms.listener.ReceiveSMSListener;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml",
		"classpath:/META-INF/spring/applicationContext-jms.xml",
		"classpath:/META-INF/spring/applicationContext-memcache.xml",
		"classpath:/META-INF/spring/applicationContext-mail.xml", })
public class ReceiveSMSListenerTest {

	@Autowired
	ReceiveSMSListener receiveSMSListener;

	@Test
	public void testReceiveSMSCustomer() {
		receiveSMSListener.receiveSMSCustomer(123456L, "13810747309", "1 test", "menWangSMSServiceProvider");
	}
}
