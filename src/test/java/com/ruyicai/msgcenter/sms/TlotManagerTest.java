package com.ruyicai.msgcenter.sms;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ruyicai.msgcenter.lot.Lot;
import com.ruyicai.msgcenter.service.TlotManager;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml",
		"classpath:/META-INF/spring/applicationContext-jms.xml",
		"classpath:/META-INF/spring/applicationContext-memcache.xml" })
public class TlotManagerTest {

	@Autowired
	TlotManager tlotManager;

	@Test
	public void testTlotManager() {
		Assert.assertEquals(tlotManager.contains("123"), false);
		Assert.assertEquals(tlotManager.contains("F47104"), true);
		Lot lot = tlotManager.getLot("F47104");
		Assert.assertNotNull(lot);
		String wincode = lot.getOpenSMSWincode("010203040506", "1");
		System.out.println(wincode);
		Lot lot2 = tlotManager.getLot("F47103");
		Assert.assertNotNull(lot2);
		String wincode2 = lot2.getOpenSMSWincode("000100", "1");
		System.out.println(wincode2);
	}
}
