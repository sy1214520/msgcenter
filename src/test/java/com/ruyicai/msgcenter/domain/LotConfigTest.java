package com.ruyicai.msgcenter.domain;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ruyicai.msgcenter.domain.LotConfig;
import com.ruyicai.msgcenter.service.MemcachedService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml",
		"classpath:/META-INF/spring/applicationContext-jms.xml",
		"classpath:/META-INF/spring/applicationContext-memcache.xml",
		"classpath:/META-INF/spring/applicationContext-mail.xml", })
public class LotConfigTest {
	
	@Autowired
	MemcachedService<LotConfig> memcachedService;

	@Test
	public void testCreateAndUpdate() {
		System.out.println(LotConfig.saveOrUpdate("T01006", "足彩半全场", false));
		System.out.println(LotConfig.saveOrUpdate("T01001", "超级大乐透", true));
		System.out.println(LotConfig.saveOrUpdate("T01008", "北京单场", true));
		System.out.println(LotConfig.saveOrUpdate("T01010", "多乐彩", false));
		System.out.println(LotConfig.saveOrUpdate("T01012", "十一运夺金", false));
		System.out.println(LotConfig.saveOrUpdate("J00001", "竞彩足球胜负平", false));
		System.out.println(LotConfig.saveOrUpdate("T01005", "进球彩", false));
		System.out.println(LotConfig.saveOrUpdate("T01002", "排列三", true));
		System.out.println(LotConfig.saveOrUpdate("T01011", "排列五", true));
		System.out.println(LotConfig.saveOrUpdate("F47102", "七乐彩", true));
		System.out.println(LotConfig.saveOrUpdate("T01009", "七星彩", true));
		System.out.println(LotConfig.saveOrUpdate("F47105", "群英会", true));
		System.out.println(LotConfig.saveOrUpdate("T01004", "任九场", false));
		System.out.println(LotConfig.saveOrUpdate("T01003", "胜负彩", false));
		System.out.println(LotConfig.saveOrUpdate("T01007", "时时彩", false));
		System.out.println(LotConfig.saveOrUpdate("F47104", "双色球", true));
		System.out.println(LotConfig.saveOrUpdate("F47103", "3D", true));
		System.out.println(LotConfig.saveOrUpdate("T01013", "22选5", true));
		
		System.out.println(LotConfig.saveOrUpdate("J00001", "竞彩足球胜负平", false));
		System.out.println(LotConfig.saveOrUpdate("J00002", "竞彩足球比分", false));
		System.out.println(LotConfig.saveOrUpdate("J00003", "竞彩足球总进球", false));
		System.out.println(LotConfig.saveOrUpdate("J00004", "竞彩足球半场胜负平", false));
		System.out.println(LotConfig.saveOrUpdate("J00005", "竞彩篮球胜负", false));
		System.out.println(LotConfig.saveOrUpdate("J00006", "竞彩篮球让分胜负", false));
		System.out.println(LotConfig.saveOrUpdate("J00007", "竞彩篮球胜负差", false));
		System.out.println(LotConfig.saveOrUpdate("J00008", "竞彩篮球大小分", false));
		System.out.println(LotConfig.saveOrUpdate("J00009", "冠军", false));
		System.out.println(LotConfig.saveOrUpdate("J00010", "冠亚军", false));
		System.out.println(LotConfig.saveOrUpdate("T01014", "广东十一选五", false));
	}

}
