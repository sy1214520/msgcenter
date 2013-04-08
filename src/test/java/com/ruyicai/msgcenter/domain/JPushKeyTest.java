package com.ruyicai.msgcenter.domain;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml",
		"classpath:/META-INF/spring/applicationContext-jms.xml",
		"classpath:/META-INF/spring/applicationContext-memcache.xml",
		"classpath:/META-INF/spring/applicationContext-mail.xml", })
public class JPushKeyTest {

	private Logger logger = LoggerFactory.getLogger(JPushKey.class);

	@Test
	public void initData() {
		List<JPushKey> list = JPushKey.findAllJPushKeys();
		for (JPushKey key : list) {
			key.remove();
		}
		JPushKey.saveOrUpdate("RuyicaiAndroid", "581b489e89361b3b8df4ffe0", "e4a4a68d4585996779499860", 1);
		JPushKey.saveOrUpdate("RuyicaiAndroid168", "cb6c77c6fdfd35472f646471", "7c0f0798db955c7613976c0a", 1);
		JPushKey.saveOrUpdate("RuyicaiAndroid91", "7776447c5a5b2281e3c02948", "551997b1652fa681f60edc6c", 1);
		JPushKey.saveOrUpdate("RuyicaiAndroidxuancai", "9d5a24f1955289a13889c932", "2343d1cc4bd6769a650fee6e", 1);
		JPushKey.saveOrUpdate("RuyicaiAndroidSuNing", "adf7260d80756e57d672da59", "075e1e7f7c500584cb9aa7fc", 1);
	}

	@Test
	public void testFindAll() {
		List<JPushKey> list = JPushKey.findAllJPushKeys();
		for (JPushKey key : list) {
			logger.info(key.toString());
		}
	}

}
