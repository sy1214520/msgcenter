package com.ruyicai.msgcenter.jms.listener;

import org.apache.camel.Header;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ruyicai.lottery.domain.Tuserinfo;
import com.ruyicai.msgcenter.consts.SMSType;
import com.ruyicai.msgcenter.domain.LotConfig;
import com.ruyicai.msgcenter.service.LotteryService;
import com.ruyicai.msgcenter.service.SMSService;
import com.ruyicai.msgcenter.service.UserSettingService;

@Service
public class SendAddNumSMSListener {

	private Logger logger = LoggerFactory.getLogger(SendAddNumSMSListener.class);

	@Value("${addnumFailureSMS}")
	private String addnumFailureSMS;

	@Autowired
	SMSService smsService;

	@Autowired
	private UserSettingService userSettingService;

	@Autowired
	LotteryService lotteryService;

	public void addNumCustomer(@Header("lotno") String lotno, @Header("batchcode") String batchcode,
			@Header("userno") String userno) {
		logger.info("发送追号金额不足短信lotno:{},batchcode:{},userno:{}", new String[] { lotno, batchcode, userno });
		if (StringUtils.isBlank(userno)) {
			return;
		}
		// 判断此用户是否发送追号失败短信
		if (userSettingService.judgeNotSend(userno, SMSType.ADDNUM)) {
			return;
		}
		Tuserinfo tuserinfo = lotteryService.findTuserinfoByUserno(userno);
		if (tuserinfo == null) {
			return;
		}
		String nickname = tuserinfo.getUserName();
		if (StringUtils.isEmpty(nickname)) {
			nickname = "如意彩用户";
		}
		String lotname = "";
		try {
			LotConfig lotConfig = LotConfig.findByLotno(lotno);
			if (lotConfig != null) {
				lotname = lotConfig.getName();
			}
			String content = addnumFailureSMS;
			content = content.replace("nickname", nickname).replace("lotname", lotname).replace("batchcode", batchcode);
			smsService.immediateSendMessage(new String[] { tuserinfo.getMobileid() }, content);
		} catch (Exception e) {
			logger.error("发送追号金额不足短信出错", e);
		}
	}
}
