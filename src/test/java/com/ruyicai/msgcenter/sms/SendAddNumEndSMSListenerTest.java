package com.ruyicai.msgcenter.sms;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ruyicai.msgcenter.jms.listener.SendAddNumEndSMSListener;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml",
		"classpath:/META-INF/spring/applicationContext-jms.xml",
		"classpath:/META-INF/spring/applicationContext-memcache.xml",
		"classpath:/META-INF/spring/applicationContext-mail.xml", })
public class SendAddNumEndSMSListenerTest {

	@Autowired
	SendAddNumEndSMSListener sendAddNumEndSMSListener;

	@Test
	public void testAddNumEndCustomer() {
		sendAddNumEndSMSListener.addNumEndCustomer("0000000000022019");
	}

}
