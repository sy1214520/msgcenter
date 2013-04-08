package com.ruyicai.msgcenter.sms;

import java.math.BigDecimal;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ruyicai.lottery.domain.Torder;
import com.ruyicai.lottery.domain.Tsubscribe;
import com.ruyicai.lottery.domain.Tuserinfo;
import com.ruyicai.msgcenter.service.LotteryService;
import com.ruyicai.msgcenter.service.ParseBetCodeService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml",
		"classpath:/META-INF/spring/applicationContext-jms.xml",
		"classpath:/META-INF/spring/applicationContext-memcache.xml",
		"classpath:/META-INF/spring/applicationContext-mail.xml" })
public class LotteryServiceTest {

	@Autowired
	LotteryService lotteryService;

	@Autowired
	ParseBetCodeService parseBetCodeService;

	@Test
	public void test() {
		Tuserinfo tuserinfo = lotteryService.findTuserinfoByUserno("00000059");
		Assert.assertNotNull(tuserinfo);
		List<String> list = lotteryService.findUsernosByLotnoAndBatchcodeFromTlot("F47102", "2011302", null);
		System.out.println(list);
		Tsubscribe tsubscribe = lotteryService.selectTsubscribeByFlowno("0000000000000011");
		Assert.assertNotNull(tsubscribe);
		Torder torder = lotteryService.findTorderById("TE2012021400081165");
		Assert.assertNotNull(torder);
		String matches = lotteryService.findJingCaiMatches("J00001", "20111021", "5", "003");
		Assert.assertNotNull(matches);
	}

	@Test
	public void testParseBetCode() {
		String lotno = "F47104";
		String betCode = "0001061422242833~06^!0001051112161830~05^!0001091416202427~13^!0001010217182021~07^!0001131420282930~15^";
		String multiple = "";
		JSONArray parseBetCodes = parseBetCodeService.parseBetCode(lotno, betCode, multiple);
		StringBuffer sBuffer = new StringBuffer();
		for (int j = 0; j < parseBetCodes.size(); j++) {
			JSONObject parseBetCodeJson = (JSONObject) parseBetCodes.get(j);
			sBuffer.append(parseBetCodeJson.getString("betCode")).append("\n");
		}
		System.out.println(sBuffer.toString());
	}

	@Test
	public void testFindStarterByLotnoAndBatchcode() {
		String lotno = "F47104";
		String batchcode = "2012135";
		BigDecimal state = new BigDecimal(4);
		System.out.println(lotteryService.findStarterByLotnoAndBatchcodeFromCaselot(lotno, batchcode, state));
	}
}
