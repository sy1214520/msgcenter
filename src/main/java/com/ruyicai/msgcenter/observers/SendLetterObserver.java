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
import com.ruyicai.msgcenter.dao.LetterDao;

@Component
public class SendLetterObserver implements ISendObserver {

	private Logger logger = LoggerFactory.getLogger(SendLetterObserver.class);

	@Autowired
	private LetterDao letterDao;

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

	@Value("${letterUserno}")
	private String letterUserno;

	@Override
	public void sendMsg(Tuserinfo tuserinfo, SMSType smsType, Map<String, Object> params) {
		if (tuserinfo == null || tuserinfo.getUserno() == null) {
			logger.info("用户为空");
			return;
		}
		String content = getContent(tuserinfo, smsType, params);
		if (StringUtils.isNotBlank(content)) {
			try {
				letterDao.createLetter(letterUserno, tuserinfo.getUserno(), 0, content, content);
				logger.info("创建站内信" + tuserinfo.getUserno() + "," + content);
			} catch (Exception e) {
				logger.error("创建站内信失败", e);
			}
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
			logger.info("发送开奖站内信");
			content = rycDrawLotteryMessage;
			if ("545".equals(channel) || "651".equals(channel)) {
				content = nineonekaiJiangSMS;
			}
			content = content.replace("caizhong", (String) params.get("lotname"))
					.replace("qihao", (String) params.get("batchcode"))
					.replace("winNumber", (String) params.get("wincode"));
			break;
		case HIT:
			logger.info("发送中奖站内信");
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
			logger.info("发送合买中奖站内信");
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
			logger.info("发送追号失败站内信");
			break;
		case ADDNUMEND:
			logger.info("发送追号剩余3期提醒站内信");
			caizhong = (String) params.get("lotname");
			content = addNumEndSMS.replaceAll("caizhong", caizhong);
			break;
		case TAOCAN:
			logger.info("发送套餐金额不足站内信");
			break;
		case DELAY:
			logger.info("发送延迟站内信");
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
