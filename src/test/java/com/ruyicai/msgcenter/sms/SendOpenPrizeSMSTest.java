package com.ruyicai.msgcenter.sms;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ruyicai.msgcenter.jms.listener.SendOpenPrizeListener;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml",
		"classpath:/META-INF/spring/applicationContext-jms.xml",
		"classpath:/META-INF/spring/applicationContext-memcache.xml",
		"classpath:/META-INF/spring/applicationContext-mail.xml" })
public class SendOpenPrizeSMSTest {

	@Autowired
	private SendOpenPrizeListener sendOpenPrizeListener;

	@Test
	public void test() throws Exception {
		sendOpenPrizeListener.openPrizeCustomer("F47104", "2012168", "010203040506|10", "010203040506", "10");
		Thread.sleep(30 * 1000L);
	}

}
