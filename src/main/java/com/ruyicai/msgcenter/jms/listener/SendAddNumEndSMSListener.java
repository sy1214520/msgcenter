package com.ruyicai.msgcenter.jms.listener;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.camel.Header;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruyicai.lottery.domain.Tsubscribe;
import com.ruyicai.lottery.domain.Tuserinfo;
import com.ruyicai.msgcenter.consts.SMSType;
import com.ruyicai.msgcenter.domain.LotConfig;
import com.ruyicai.msgcenter.observers.ISendObserver;
import com.ruyicai.msgcenter.service.LotteryService;
import com.ruyicai.msgcenter.service.UserSettingService;

@Service
public class SendAddNumEndSMSListener {

	private Logger logger = LoggerFactory.getLogger(SendAddNumEndSMSListener.class);

	@Autowired
	private UserSettingService userSettingService;

	@Autowired
	LotteryService lotteryService;

	public void addNumEndCustomer(@Header("flowno") String flowno) {
		logger.info("发送追号提醒短信flowno:{}", new String[] { flowno });
		if (StringUtils.isBlank(flowno)) {
			return;
		}
		Tsubscribe tsubscribe = lotteryService.selectTsubscribeByFlowno(flowno);
		if (tsubscribe == null) {
			logger.info("tsubscribe flowno:{}不存在", flowno);
			return;
		}
		String userno = tsubscribe.getUserno();
		Tuserinfo tuserinfo = lotteryService.findTuserinfoByUserno(userno);
		if (tuserinfo == null) {
			logger.info("用户userno:{}不存在", userno);
			return;
		}
		Set<ISendObserver> sendables = userSettingService.findSendSetting(userno, SMSType.ADDNUMEND.getValue());
		if (sendables.size() == 0) {
			logger.info("用户设置不发送中奖短信");
			return;
		}

		LotConfig lotConfig = LotConfig.findByLotno(tsubscribe.getLotno());
		if (lotConfig == null) {
			return;
		}

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("lotname", lotConfig.getName());

		for (ISendObserver sendable : sendables) {
			sendable.sendMsg(tuserinfo, SMSType.ADDNUMEND, params);
		}
	}

}
