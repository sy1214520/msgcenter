package com.ruyicai.msgcenter.jms.listener;

import java.math.BigDecimal;

import org.apache.camel.Body;
import org.apache.camel.Header;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruyicai.lottery.domain.Tuserinfo;
import com.ruyicai.msgcenter.consts.BetType;
import com.ruyicai.msgcenter.domain.LotConfig;
import com.ruyicai.msgcenter.domain.dto.OrderRequest;
import com.ruyicai.msgcenter.service.LotteryService;
import com.ruyicai.msgcenter.service.SMSService;
import com.ruyicai.msgcenter.util.JsonUtil;
import com.ruyicai.msgcenter.util.VerifyUtil;

@Service
public class SendSavePresentSMSListener {

	private Logger logger = LoggerFactory.getLogger(SendSavePresentSMSListener.class);

	@Autowired
	private LotteryService lotteryService;

	@Autowired
	private SMSService smsService;

	public void sendSavePresentSMSCustomer(@Body String body, @Header("NEWUSER") Integer newUser) {
		OrderRequest orderRequest = JsonUtil.fromJsonToObject(body, OrderRequest.class);
		if (orderRequest == null) {
			return;
		}
		String lotno = orderRequest.getLotno();
		LotConfig lot = LotConfig.findByLotno(lotno);
		if (lot == null) {
			return;
		}
		String caizhong = lot.getName();
		String batchcode = orderRequest.getBatchcode();
		BigDecimal betnum = orderRequest.getAmt().divide(orderRequest.getOneamount(), 0, BigDecimal.ROUND_HALF_UP)
				.divide(orderRequest.getLotmulti(), 0, BigDecimal.ROUND_HALF_UP);
		String buyuserno = orderRequest.getBuyuserno();
		String userno = orderRequest.getUserno();
		// 赠送订单成功短信
		if (orderRequest.getBettype().equals(BetType.zengsong.value())) {
			Tuserinfo tuserinfo = lotteryService.findTuserinfoByUserno(userno);
			Tuserinfo buyTuserinfo = lotteryService.findTuserinfoByUserno(buyuserno);
			if (buyTuserinfo != null) {
				String blessing = orderRequest.getBlessing();
				String buyMobileId = buyTuserinfo.getMobileid();
				if (StringUtils.isBlank(buyMobileId)) {
					buyMobileId = buyTuserinfo.getUserName();
				}
				if (StringUtils.isBlank(buyMobileId)) {
					buyMobileId = "";
				}
				if (StringUtils.isNotBlank(orderRequest.getReciverMobile())) {
					// 发送赠送短信
					String mobileid = orderRequest.getReciverMobile();
					StringBuffer text = new StringBuffer();
					orderRequest.getLotno();
					text.append("【赠彩通知】您的好友").append(buyMobileId).append("赠送您").append(caizhong).append(batchcode)
							.append("期彩票").append(betnum.toString()).append("注.");
					if (StringUtils.isNotBlank(blessing)) {
						text.append(blessing + " ");
					}
					if (newUser == 0) {
						text.append("请您使用帐号[" + tuserinfo.getUserName() + "]登陆查看,24小时客服 4006651000");
					} else if (newUser == 1) {
						text.append("请您使用帐号[" + tuserinfo.getUserName()
								+ "]登陆 http://t.cn/zWFESBg? 找回密码后登陆查看,24小时客服 4006651000");
					} else {
						text.append("请您登录如意彩平台领取彩票 ,24小时客服 4006651000");
					}
					if (VerifyUtil.isInt(mobileid)) {
						logger.info("出票赠送短信：" + mobileid + ",内容：" + text.toString());
						smsService.sendMessage(new String[] { mobileid }, text.toString());
					}
				}
			}
		}
	}
}
