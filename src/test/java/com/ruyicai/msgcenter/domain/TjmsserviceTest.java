package com.ruyicai.msgcenter.domain;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ruyicai.msgcenter.consts.JmsserviceType;
import com.ruyicai.msgcenter.domain.Tjmsservice;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml",
		"classpath:/META-INF/spring/applicationContext-jms.xml",
		"classpath:/META-INF/spring/applicationContext-memcache.xml",
		"classpath:/META-INF/spring/applicationContext-mail.xml", })
public class TjmsserviceTest {

	@Test
	public void testCreateAndUpdate() {
		System.out.println(Tjmsservice.createTjmsservice("11111", JmsserviceType.opensms));
		System.out.println(Tjmsservice.createTjmsservice("11111", JmsserviceType.opensms));
	}
}
