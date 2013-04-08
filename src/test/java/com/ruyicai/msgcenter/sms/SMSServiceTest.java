package com.ruyicai.msgcenter.sms;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import junit.framework.Assert;

import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ruyicai.msgcenter.consts.Const;
import com.ruyicai.msgcenter.jms.listener.SendEncashSMSListener;
import com.ruyicai.msgcenter.service.QuartzService;
import com.ruyicai.msgcenter.service.SMSService;
import com.ruyicai.msgcenter.service.SendSMSDetailService;
import com.ruyicai.msgcenter.util.PackageSendUtil;
import com.ruyicai.sms.MenWangSMSServiceProvider;
import com.ruyicai.sms.MockSMSServiceProvider;
import com.ruyicai.sms.SMSServiceProvider;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml",
		"classpath:/META-INF/spring/applicationContext-jms.xml",
		"classpath:/META-INF/spring/applicationContext-memcache.xml",
		"classpath:/META-INF/spring/applicationContext-mail.xml", })
public class SMSServiceTest {

	@Autowired
	SMSService smsService;

	@Autowired
	MockSMSServiceProvider mockSMSServiceProvider;

	@Autowired
	MenWangSMSServiceProvider menWangSMSServiceProvider;

	@Autowired
	SendEncashSMSListener sendPrizeSMSListener;

	@Produce(uri = "jms:queue:sms-queue")
	ProducerTemplate template;

	@Produce(uri = "jms:queue:sendAddNumSMS")
	ProducerTemplate addnumtemplate;

	@Autowired
	SendSMSDetailService sendSMSDetailService;

	@Autowired
	SendEncashSMSListener sendEncashSMSListener;

	@Autowired
	QuartzService quartzService;

	@Autowired
	Map<String, SMSServiceProvider> providers = new HashMap<String, SMSServiceProvider>();

	@Test
	public void testDisplayProvider() {
		for (Map.Entry<String, SMSServiceProvider> entry : providers.entrySet()) {
			System.out.println(entry.getKey());
		}
	}

	@Test
	public void testSend() throws InterruptedException {
		smsService.immediateSendMessage("13810747309", "test");
	}

	@Test
	public void testSendMessageString() throws InterruptedException {
		smsService.immediateSendMessage("13810747309", "test");
		smsService.immediateSendMessage(new String[] { "13810747309" }, "test");
		smsService.immediateSendMessage("13810747309", "test", Const.defaultSMSChannel);
		smsService.immediateSendMessage(new String[] { "13810747309" }, "test", Const.defaultSMSChannel);
		Thread.sleep(3000);
		Assert.assertEquals(4, mockSMSServiceProvider.getMessage("13810747309").size());
		// smsService.sendsms("13810747309", "测试梦网短信",
		// Const.menWangSMSServiceProvider);
		// smsService.sendsms("13810747309,13126537383",
		// "测试亿美接口短信",Const.emaySMSServiceProvider);
		smsService.sendsms("13810747309", "测试梦网旧接口短信", Const.menWangOldSMSServiceProvider);
		smsService.sendsms("13810747309", "测试梦网接口短信", Const.menWangSMSServiceProvider);
	}

	@Test
	public void testJMSSendMessage() throws InterruptedException {
		HashMap<String, Object> headers = new HashMap<String, Object>();
		headers.put("mobileid", "123456");
		headers.put("text", "you win");
		headers.put("channelName", "mockSMSServiceProvider");
		template.sendBodyAndHeaders(null, headers);
		Thread.sleep(3000);
		Assert.assertEquals(1, mockSMSServiceProvider.getMessage("123456").size());
	}

	@Test
	public void testAddNumSendSMS() throws InterruptedException {
		HashMap<String, Object> headers = new HashMap<String, Object>();
		headers.put("lotno", "F47104");
		headers.put("batchcode", "123");
		headers.put("userno", "00000059");
		addnumtemplate.sendBodyAndHeaders(null, headers);
		Thread.sleep(1000);
		Assert.assertEquals(1, mockSMSServiceProvider.getMessage("18210390102").size());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testPackage() {
		List<Object[]> list = quartzService.findReady2SendSMSDetail(BigDecimal.ZERO, Const.defaultSMSChannel);
		System.out.println("分类数量:" + list.size());
		int i = 0;
		for (Object[] obj : list) {
			List<Object[]> l = (List<Object[]>) obj[1];
			System.out.println(obj[0].toString() + ":" + l.size());
			i = i + l.size();
		}
		System.out.println("总数量:" + i);
		System.out.println("==============");
		List<Object[]> dataList = PackageSendUtil.reorganizeData(list, 100);
		for (Object[] objArray : dataList) {
			for (Object obj : objArray) {
				System.out.println(obj);
			}
		}
	}

	@Test
	public void testCreateSendSMSDetail() {
		for (int i = 0; i < 10; i++) {
			Long mobileid = 13810747309L + Long.parseLong(i + "");
			sendSMSDetailService.createSendSMSDetail(mobileid + "", "测试创建" + new Random().nextInt(10),
					Const.defaultSMSChannel, null);
		}
	}

	@Test
	public void testCreateOneSendSMSDetail() {
		sendSMSDetailService.createSendSMSDetail("13810747309", "测试定时短信" + new Random().nextInt(10),
				"menWangSMSServiceProvider", null);
	}

	@Test
	public void testSendEncashSMSListener() throws InterruptedException {
		String json = "{\"agencyno\":null,\"alreadytrans\":0,\"amt\":1800,\"batchcode\":null,\"betcode\":\"500@20120903|1|001|234^201     20903|1|002|123^20120903|1|003|123^\",\"betnum\":9,\"bettype\":2,\"body\":null,\"buyuserno\":\"00000042\",\"canceltime\":null,\"channel\":\"641     \",\"createtime\":1346644570034,\"desc\":null,\"encashtime\":null,\"endtime\":null,\"eventcode\":\"1_20120903_1_003\",\"hasachievement\":0,\"id     \":\"BJ2012090303912"
				+ RandomUtils.nextInt(1000)
				+ "\",\"instate\":1,\"latedteamid\":\"1_20120903_1_001\",\"lotmulti\":1,\"lotno\":\"J00003\",\"lotsType\":0,\"memo\":null,\"mod     ifytime\":null,\"orderamt\":null,\"orderinfo\":\"500@20120903|1|001|234^20120903|1|002|123^20120903|1|003|123^_1_200_1800\",\"orderprep     rizeamt\":2068,\"orderprize\":null,\"orderprizeamt\":2068,\"orderstate\":1,\"ordertype\":0,\"paystate\":3,\"paytype\":1,\"playtype\":\"500\",\"pr     izeinfo\":\"\",\"prizestate\":5,\"subaccount\":null,\"subaccountType\":null,\"subchannel\":\"00092493\",\"tlotcaseid\":null,\"tsubscribeflowno\"     :null,\"userno\":\"00000042\",\"winbasecode\":\" \"}";
		sendEncashSMSListener.encashCustomer(json);
		Thread.sleep(5 * 1000);
	}
}
