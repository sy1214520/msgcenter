package com.ruyicai.msgcenter.domain;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ruyicai.msgcenter.consts.Const;
import com.ruyicai.msgcenter.dao.SendSMSDetailDao;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml",
		"classpath:/META-INF/spring/applicationContext-jms.xml",
		"classpath:/META-INF/spring/applicationContext-memcache.xml",
		"classpath:/META-INF/spring/applicationContext-mail.xml", })
public class SendSMSDetailTest {

	@Autowired
	private SendSMSDetailDao dao;

	@Test
	public void testBatchCreate() {

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 20000; i++) {
			sb.append(i + ",");
		}
		sb.delete(sb.length() - 1, sb.length());
		long currentTimeMillis = System.currentTimeMillis();
		dao.createSendSMSDetail(sb.toString(), "测试", Const.defaultSMSChannel, new Date());
		System.out.println("执行时间" + (System.currentTimeMillis() - currentTimeMillis));
	}
}
