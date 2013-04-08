package com.ruyicai.msgcenter.domain;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ruyicai.msgcenter.domain.SendSMSLog;
import com.ruyicai.msgcenter.domain.Tsms;
import com.ruyicai.msgcenter.jms.listener.SendEncashSMSListener;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml",
		"classpath:/META-INF/spring/applicationContext-jms.xml",
		"classpath:/META-INF/spring/applicationContext-memcache.xml",
		"classpath:/META-INF/spring/applicationContext-mail.xml", })
public class TsmsTest {

	@Autowired
	private SendEncashSMSListener sendEncashSMSListener;

	@Test
	public void tesetCreate() {
		String usernos = "00815136,00058371,00806499,00819583,00811411,00936216";
		String[] usernoArray = usernos.split(",");
		for (String userno : usernoArray) {
			Tsms t = new Tsms();
			t.setLotno("all");
			t.setUserno(userno);
			t.setType(BigDecimal.ONE);
			t.setState(BigDecimal.ONE);
			t.persist();
		}
	}

	@Test
	public void testSendEncashSMS() {
		String body = "{\"agencyno\":null,\"alreadytrans\":0,\"amt\":22400,\"batchcode\":\"2012121\",\"betcode\":\"32020402050708^!32020702040506070809^!120205^!120215^!32020403060809^!120223^\",\"betnum\":56,\"bettype\":2,\"body\":null,\"buyuserno\":\"00000138\",\"canceltime\":null,\"channel\":null,\"createtime\":1337852477969,\"desc\":null,\"encashtime\":null,\"endtime\":null,\"eventcode\":null,\"hasachievement\":0,\"id\":\"TE2012052400132268\",\"instate\":0,\"lotmulti\":2,\"lotno\":\"F47103\",\"lotsType\":0,\"memo\":null,\"modifytime\":null,\"orderamt\":null,\"orderinfo\":\"32010402050708^_2_200_800!32010702040506070809^_2_200_7000!120105^_2_200_400!120115^_2_200_2000!32010403060809^_2_200_800!120123^_2_200_200\",\"orderpreprizeamt\":0,\"orderprize\":null,\"orderprizeamt\":0,\"orderstate\":1,\"ordertype\":0,\"paystate\":3,\"paytype\":1,\"playtype\":\"MIX\",\"prizeinfo\":\"\",\"prizestate\":3,\"subaccount\":null,\"subaccountType\":null,\"subchannel\":\"00092493\",\"tlotcaseid\":null,\"tsubscribeflowno\":null,\"userno\":\"00000138\",\"winbasecode\":\"010203\"}";
		sendEncashSMSListener.encashCustomer(body);
	}

	@Test
	public void testComputeSum() {
		SendSMSLog.computeCount();
	}

}
