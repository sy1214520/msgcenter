package com.ruyicai.msgcenter.observers;

import java.math.BigDecimal;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ruyicai.lottery.domain.Tuserinfo;
import com.ruyicai.msgcenter.consts.BetType;
import com.ruyicai.msgcenter.consts.SMSType;
import com.ruyicai.msgcenter.service.SendSMSDetailService;
import com.ruyicai.msgcenter.util.VerifyUtil;

@Component
public class SendSMSObserver extends AbstractSendObserver {

	private Logger logger = LoggerFactory.getLogger(SendSMSObserver.class);

	/*------------------------开奖------------------------ */
	@Value("${rycDrawLotteryMessage}")
	private String rycDrawLotteryMessage;

	@Value("${nineonekaiJiangSMS}")
	private String nineonekaiJiangSMS;

	/*------------------------中奖------------------------ */
	@Value("${nineonePrizeSMS}")
	private String nineonePrizeSMS;

	@Value("${nineonePrizeBigSMS}")
	private String nineonePrizeBigSMS;

	@Value("${yinhuaprizeZensongSMS}")
	private String yinhuaprizeZensongSMS;

	@Value("${yinhuaprizeBigZensongSMS}")
	private String yinhuaprizeBigZensongSMS;

	@Value("${prizeSMS}")
	private String prizeSMS;

	@Value("${caselotprizeSMS}")
	private String caselotprizeSMS;

	@Value("${prizeBigSMS}")
	private String prizeBigSMS;

	@Value("${caselotprizeBigSMS}")
	private String caselotprizeBigSMS;

	@Value("${prizeZensongSMS}")
	private String prizeZensongSMS;

	@Value("${prizeBigZensongSMS}")
	private String prizeBigZensongSMS;

	/*------------------------追号剩余3期------------------------ */
	@Value("${addNumEndSMS}")
	private String addNumEndSMS;

	@Autowired
	SendSMSDetailService sendSMSDetailService;

	@Override
	public void sendMsg(Tuserinfo tuserinfo, SMSType smsType, Map<String, Object> params) {
		if (tuserinfo == null || !VerifyUtil.isInt(tuserinfo.getMobileid())) {
			logger.info("短信手机号验证未通过tuserinfo:" + tuserinfo);
			return;
		}
		String channelName = getChannelName(tuserinfo.getChannel());
		String content = getContent(tuserinfo, smsType, params);
		if (StringUtils.isNotBlank(channelName) && StringUtils.isNotBlank(content)) {
			logger.info("发送开奖短信 content=" + content + ",mobileId=" + tuserinfo.getMobileid());
			sendSMSDetailService.createSendSMSDetail(tuserinfo.getMobileid(), content, channelName, null);
		}
	}

	private String getContent(Tuserinfo tuserinfo, SMSType smsType, Map<String, Object> params) {
		String channel = tuserinfo.getChannel();
		String content = "";
		String winMoney = "";
		String batchcode = "";
		String caizhong = "";
		Integer type = null;
		switch (smsType) {
		case OPEN:
			logger.info("发送开奖短信");
			content = rycDrawLotteryMessage;
			if ("545".equals(channel) || "651".equals(channel)) {
				content = nineonekaiJiangSMS;
			}
			content = content.replace("caizhong", (String) params.get("lotname"))
					.replace("qihao", (String) params.get("batchcode"))
					.replace("winNumber", (String) params.get("wincode"));
			break;
		case HIT:
			logger.info("发送中奖短信");
			winMoney = getWinMoney((BigDecimal) params.get("orderprizeamt"));
			batchcode = (String) params.get("batchcode");
			caizhong = (String) params.get("lotname");
			type = (Integer) params.get("type");
			BigDecimal bettype = (BigDecimal) params.get("bettype");
			String buyuserno = (String) params.get("buyuserno");
			if (1 == type) {
				if (bettype.compareTo(BetType.zengsong.value()) == 0
						|| bettype.compareTo(BetType.zengsong_nosms.value()) == 0) {
					if (buyuserno != null && buyuserno.equals("00000295")) {
						content = yinhuaprizeZensongSMS;
					} else {
						content = prizeZensongSMS;
					}
				} else {
					if (channel != null && (channel.trim().equals("545") || channel.trim().equals("651"))) {
						content = nineonePrizeSMS;
					} else {
						content = prizeSMS;
					}
				}
			} else if (2 == type) {
				if (bettype.compareTo(BetType.zengsong.value()) == 0
						|| bettype.compareTo(BetType.zengsong_nosms.value()) == 0) {
					if (buyuserno != null && buyuserno.equals("00000295")) {
						content = yinhuaprizeBigZensongSMS;
					} else {
						content = prizeBigZensongSMS;
					}
				} else {
					if (channel != null && (channel.trim().equals("545") || channel.trim().equals("651"))) {
						content = nineonePrizeBigSMS;
					} else {
						content = prizeBigSMS;
					}
				}
			}
			if (StringUtils.isNotBlank(content)) {
				content = content.replace("money", winMoney).replace("caizhong", caizhong).replace("qihao", batchcode);
			}
			break;
		case CASELOTHIT:
			logger.info("发送合买中奖短信");
			winMoney = getWinMoney((BigDecimal) params.get("orderprizeamt"));
			batchcode = (String) params.get("batchcode");
			caizhong = (String) params.get("lotname");
			type = (Integer) params.get("type");
			if (1 == type) {
				content = caselotprizeSMS;
			} else if (2 == type) {
				content = caselotprizeBigSMS;
			}
			if (StringUtils.isNotBlank(content)) {
				content = content.replace("money", winMoney).replace("caizhong", caizhong).replace("qihao", batchcode);
			}
			break;
		case ADDNUM:
			logger.info("发送追号失败短信");
			break;
		case ADDNUMEND:
			logger.info("发送追号剩余3期提醒短信");
			caizhong = (String) params.get("lotname");
			content = addNumEndSMS.replaceAll("caizhong", caizhong);
			break;
		case TAOCAN:
			logger.info("发送套餐金额不足短信");
			break;
		case DELAY:
			logger.info("发送延迟短信");
			break;
		}
		return content;
	}

	/**
	 * 中奖金额转换
	 */
	private String getWinMoney(BigDecimal amount) {
		BigDecimal winamount = amount.divide(new BigDecimal(100 * 10000));// 中奖金额
		if (winamount.intValue() >= 1) {
			return winamount + "万";
		} else {
			return amount.divide(new BigDecimal(100)).toString();
		}
	}
}
