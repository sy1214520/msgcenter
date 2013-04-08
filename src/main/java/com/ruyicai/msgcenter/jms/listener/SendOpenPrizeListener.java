package com.ruyicai.msgcenter.jms.listener;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.camel.Header;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.lottery.domain.Tuserinfo;
import com.ruyicai.msgcenter.consts.JmsserviceType;
import com.ruyicai.msgcenter.consts.SMSType;
import com.ruyicai.msgcenter.domain.LotConfig;
import com.ruyicai.msgcenter.domain.Tjmsservice;
import com.ruyicai.msgcenter.domain.Tsms;
import com.ruyicai.msgcenter.observers.ISendObserver;
import com.ruyicai.msgcenter.service.LotteryService;
import com.ruyicai.msgcenter.service.TlotManager;
import com.ruyicai.msgcenter.service.UserSettingService;

@Service
public class SendOpenPrizeListener {

	private Logger logger = LoggerFactory.getLogger(SendOpenPrizeListener.class);

	@Autowired
	private UserSettingService userSettingService;

	@Autowired
	private LotteryService lotteryService;

	@Autowired
	private TlotManager tlotManager;

	@Produce(uri = "secondjms:topic:sendOpenSMS2User")
	private ProducerTemplate sendOpenSMS2UserProducer;

	@Transactional
	public void openPrizeCustomer(@Header("lotno") String lotno, @Header("batchcode") String batchcode,
			@Header("wincode") String wincode, @Header("winbasecode") String winbasecode,
			@Header("winspecialcode") String winspecialcode) {
		logger.info("接收到开奖消息, lotno:" + lotno + ", batchcode:" + batchcode + ",wincode" + wincode + ",winbasecode"
				+ winbasecode + ",winspecialcode" + winspecialcode);
		LotConfig lotConfig = LotConfig.findByLotno(lotno);
		if (lotConfig != null) {
			if (!lotConfig.getIssendsms()) {
				logger.info("此彩种不下发短信, lotno: " + lotno);
				return;
			}
		}
		if (!Tjmsservice.createTjmsservice(StringUtils.join(new String[] { lotno, batchcode }, "_"),
				JmsserviceType.opensms)) {
			logger.error("该lotno:{},batchcode:{}开奖短信已发送", new String[] { lotno, batchcode });
			return;
		}
		Set<String> usernos = new HashSet<String>();
		List<String> tlotusernos = lotteryService.findUsernosByLotnoAndBatchcodeFromTlot(lotno, batchcode,
				BigDecimal.ONE);
		List<String> caselotusernos = lotteryService.findStarterByLotnoAndBatchcodeFromCaselot(lotno, batchcode,
				new BigDecimal(5));
		usernos.addAll(tlotusernos);
		usernos.addAll(caselotusernos);
		logger.info("查询出发送开奖短信用户数量为:" + usernos.size());
		String lotame = lotConfig.getName();
		String wincoderesult = null;
		if (tlotManager.contains(lotno)) {
			wincoderesult = tlotManager.getLot(lotno).getOpenSMSWincode(winbasecode, winspecialcode);
		} else {
			wincoderesult = wincode;
		}
		for (String userno : usernos) {
			try {
				if (StringUtils.isNotBlank(userno)) {
					sendSMSJMS(userno, lotno, lotame, batchcode, wincoderesult);
				}
			} catch (Exception e) {
				logger.error("发送开奖短信出错lotno:" + lotno + ", batchcode:" + batchcode + ",userno:" + userno, e);
			}
		}
		List<Tsms> tsmses = Tsms.findBytype(SMSType.OPEN.getValue());
		logger.info("给监控人员发送开奖短信");
		for (Tsms tsms : tsmses) {
			String tsmsUserno = tsms.getUserno();
			if (usernos.contains(tsmsUserno)) {
				continue;
			}
			try {
				if (StringUtils.isNotBlank(tsmsUserno)) {
					sendSMSJMS(tsmsUserno, lotno, lotame, batchcode, wincoderesult);
				}
			} catch (Exception e) {
				logger.error("发送开奖短信出错lotno:" + lotno + ", batchcode:" + batchcode + ",userno:" + tsmsUserno, e);
			}
		}
	}

	private void sendSMSJMS(String userno, String lotno, String lotname, String batchcode, String wincode) {
		Map<String, Object> header = new HashMap<String, Object>();
		header.put("userno", userno);
		header.put("lotno", lotno);
		header.put("lotname", lotname);
		header.put("batchcode", batchcode);
		header.put("wincode", wincode);
		sendOpenSMS2UserProducer.sendBodyAndHeaders(null, header);
	}

	public void sendOpenSMS2User(@Header("userno") String userno, @Header("lotno") String lotno,
			@Header("lotname") String lotname, @Header("batchcode") String batchcode, @Header("wincode") String wincode) {
		logger.info("接收到开奖JMS userno:{},lotno:{},lotname:{},batchcode:{},wincode:{}", new String[] { userno, lotno,
				lotname, batchcode, wincode });
		if (StringUtils.isBlank(userno)) {
			return;
		}
		Tuserinfo tuserinfo = lotteryService.findTuserinfoByUserno(userno);
		if (tuserinfo == null) {
			logger.info("用户userno:{}不存在", userno);
			return;
		}
		Set<ISendObserver> sendables = userSettingService.findSendSetting(userno, SMSType.OPEN.getValue());
		if (sendables.size() == 0) {
			logger.info("用户设置不发送中奖短信");
			return;
		}

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("batchcode", batchcode);
		params.put("wincode", wincode);
		params.put("lotname", lotname);

		for (ISendObserver sendable : sendables) {
			sendable.sendMsg(tuserinfo, SMSType.OPEN, params);
		}
	}
}
