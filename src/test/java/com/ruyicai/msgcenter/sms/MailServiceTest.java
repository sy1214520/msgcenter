package com.ruyicai.msgcenter.sms;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ruyicai.msgcenter.service.MailService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml",
		"classpath:/META-INF/spring/applicationContext-jms.xml",
		"classpath:/META-INF/spring/applicationContext-memcache.xml",
		"classpath:/META-INF/spring/applicationContext-mail.xml", })
public class MailServiceTest {

	@Autowired
	private MailService mailService;

	@Test
	public void testSendEmail() throws Exception {
		mailService.sendMimeMail("sunyang@ruyicai.com;xuying@ruyicai.com", "请下午5点开会",
				"<html><body><h1>下午5点开会</h1></body></html>");
		mailService.sendTextMail("sunyang@ruyicai.com", "双色球开奖", "双色球第112期开奖号码：1 2 3 4 5 6 | 7");
	}
}
