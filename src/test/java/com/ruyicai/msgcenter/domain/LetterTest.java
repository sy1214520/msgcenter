package com.ruyicai.msgcenter.domain;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ruyicai.msgcenter.dao.LetterDao;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml",
		"classpath:/META-INF/spring/applicationContext-jms.xml",
		"classpath:/META-INF/spring/applicationContext-memcache.xml",
		"classpath:/META-INF/spring/applicationContext-mail.xml", })
public class LetterTest {

	@Autowired
	private LetterDao letterDao;

	@Test
	public void testCreateLetter() {
		letterDao.createLetter("00001", "00002", 0, "你好", "你好");
	}

	@Test
	public void testCreateLetterBatch() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 1000; i++) {
			sb.append(i + ",");
		}
		sb.delete(sb.length() - 1, sb.length());
		letterDao.createLetterBatch("00001", sb.toString(), 0, "你好", "你好");
	}

}
