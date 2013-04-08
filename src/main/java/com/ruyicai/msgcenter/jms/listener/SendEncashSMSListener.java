package com.ruyicai.msgcenter.jms.listener;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.camel.Body;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.lottery.domain.CaseLot;
import com.ruyicai.lottery.domain.Torder;
import com.ruyicai.lottery.domain.Tuserinfo;
import com.ruyicai.msgcenter.consts.JmsserviceType;
import com.ruyicai.msgcenter.consts.SMSType;
import com.ruyicai.msgcenter.domain.LotConfig;
import com.ruyicai.msgcenter.domain.Tjmsservice;
import com.ruyicai.msgcenter.observers.ISendObserver;
import com.ruyicai.msgcenter.service.LotteryService;
import com.ruyicai.msgcenter.service.UserSettingService;

/**
 * 中奖短信监听
 */
@Service
public class SendEncashSMSListener {

	private Logger logger = LoggerFactory.getLogger(SendEncashSMSListener.class);

	// @Value("${ruyicaiUserno}")
	// private String ruyicaiUserno;

	@Autowired
	private UserSettingService userSettingService;

	@Autowired
	private LotteryService lotteryService;

	@Transactional
	public void encashCustomer(@Body String body) {
		logger.info("中奖短信发送开始body:" + body);
		if (StringUtils.isBlank(body)) {
			return;
		}
		Torder order = Torder.fromJsonToTorder(body);
		if (order == null || order.getUserno() == null || order.getLotno() == null
				|| order.getOrderstate().compareTo(BigDecimal.ONE) != 0) {
			return;
		}
		String userno = order.getUserno();
		SMSType smsType = SMSType.HIT;
		if (StringUtils.isNotBlank(order.getTlotcaseid())) {
			String caselotid = order.getTlotcaseid();
			CaseLot caseLot = lotteryService.findCaseLotById(caselotid);
			if (caseLot == null || StringUtils.isBlank(caseLot.getStarter())) {
				return;
			}
			userno = caseLot.getStarter();
			smsType = SMSType.CASELOTHIT;
		}
		LotConfig lotConfig = LotConfig.findByLotno(order.getLotno());
		if (lotConfig == null) {
			logger.info("此彩种不下发短信, lotno: " + order.getLotno());
			return;
		}
		if (!Tjmsservice.createTjmsservice(order.getId(), JmsserviceType.prizesms)) {
			logger.info("中奖短信已发送过" + order.getId());
			return;
		}
		Set<ISendObserver> sendables = userSettingService.findSendSetting(userno, SMSType.HIT.getValue());
		if (sendables.size() == 0) {
			logger.info("用户设置不发送中奖短信");
			return;
		}
		Tuserinfo tuserinfo = lotteryService.findTuserinfoByUserno(userno);
		if (null == tuserinfo) {
			logger.info("中奖短信用户未找到" + userno);
			return;
		}

		Integer type = null;
		if (order.getPrizestate().equals(new BigDecimal(4))) {
			type = 2;// 中大奖
		}
		if (order.getPrizestate().equals(new BigDecimal(5))) {
			type = 1;// 中小奖
		}
		if (type == null) {
			logger.info("中奖类型:order.getPrizestate():" + order.getPrizestate());
			if (order.getOrderprizeamt().compareTo(BigDecimal.ZERO) > 0
					&& order.getPrizestate().compareTo(BigDecimal.ZERO) == 0) {
				type = 1;
			} else {
				return;
			}
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("batchcode", order.getBatchcode() == null ? "" : order.getBatchcode());
		params.put("bettype", order.getBettype());
		params.put("lotname", lotConfig.getName());
		params.put("lotno", order.getLotno());
		params.put("orderid", order.getId());
		params.put("orderprizeamt", order.getOrderprizeamt());
		params.put("type", type);
		params.put("userno", order.getUserno());
		params.put("buyuserno", order.getBuyuserno());
		for (ISendObserver sendable : sendables) {
			sendable.sendMsg(tuserinfo, smsType, params);
		}
	}
}
