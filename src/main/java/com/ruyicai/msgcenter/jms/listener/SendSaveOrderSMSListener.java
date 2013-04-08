package com.ruyicai.msgcenter.jms.listener;

import java.math.BigDecimal;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.camel.Body;
import org.apache.camel.Header;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ruyicai.lottery.domain.Torder;
import com.ruyicai.lottery.domain.Tuserinfo;
import com.ruyicai.msgcenter.consts.BetType;
import com.ruyicai.msgcenter.consts.Const;
import com.ruyicai.msgcenter.consts.JmsserviceType;
import com.ruyicai.msgcenter.domain.LotConfig;
import com.ruyicai.msgcenter.domain.Tjmsservice;
import com.ruyicai.msgcenter.domain.dto.OrderRequest;
import com.ruyicai.msgcenter.service.LotteryService;
import com.ruyicai.msgcenter.service.ParseBetCodeService;
import com.ruyicai.msgcenter.service.SMSService;
import com.ruyicai.msgcenter.util.JsonUtil;
import com.ruyicai.msgcenter.util.VerifyUtil;

@Service
@Deprecated
public class SendSaveOrderSMSListener {

	private Logger logger = LoggerFactory.getLogger(SendSaveOrderSMSListener.class);

	@Value("${lianSaveOrderSMS}")
	private String lianSaveOrderSMS;

	@Autowired
	private SMSService smsService;

	@Autowired
	private LotteryService lotteryService;

	@Autowired
	private ParseBetCodeService parseBetCodeService;

	public void saveOrderSMSCustomer(@Body String body, @Header("orderid") String orderid,
			@Header("buyuserno") String buyuserno) {
		logger.info("保存订单成功消息orderid:{},buyuserno:{}", new String[] { orderid, buyuserno });
		if (StringUtils.isBlank(body)) {
			logger.info("body is blank");
			return;
		}
		OrderRequest orderRequest = JsonUtil.fromJsonToObject(body, OrderRequest.class);
		if (orderRequest == null) {
			return;
		}
		String userno = orderRequest.getUserno();
		Tuserinfo tuserinfo = null;
		String lotno = orderRequest.getLotno();
		LotConfig lot = LotConfig.findByLotno(lotno);
		if (lot == null) {
			return;
		}
		if (!Tjmsservice.createTjmsservice(StringUtils.join(new String[] { orderid, buyuserno }, "_"),
				JmsserviceType.tordersave)) {
			return;
		}
		String caizhong = lot.getName();
		String batchcode = orderRequest.getBatchcode();
		BigDecimal money = orderRequest.getAmt().divide(new BigDecimal(100));
		BigDecimal betnum = orderRequest.getAmt().divide(orderRequest.getOneamount(), 0, BigDecimal.ROUND_HALF_UP)
				.divide(orderRequest.getLotmulti(), 0, BigDecimal.ROUND_HALF_UP);
		String content = null;
		String mobileid = null;
		// 利安订单成功短信
		if (buyuserno != null && buyuserno.equals(Const.LiAnUserno)) {
			if (StringUtils.isNotBlank(userno)) {
				tuserinfo = lotteryService.findTuserinfoByUserno(userno);
				if (tuserinfo != null) {
					mobileid = tuserinfo.getMobileid();
					if (VerifyUtil.isInt(mobileid)) {
						// 利安渠道,根据用户userno判断
						logger.info("发送利安渠道短信");
						Torder torder = lotteryService.findTorderById(orderid);
						String betcode = getBetCode(lotno, torder.getBetcode(), torder.getLotmulti() + "");
						content = lianSaveOrderSMS;
						content = content.replace("caizhong", caizhong).replace("qihao", batchcode)
								.replace("zhushu", betnum.toString()).replace("money", money.toString())
								.replace("betcode", betcode);
						if (content != null) {
							logger.info("发送保存订单成功短信orderid:{},mobileid:{},content:{}", new String[] { orderid,
									mobileid, content });
							smsService.immediateSendMessage(new String[] { mobileid }, content);
						}
					}
				}
			}
		}
		// 赠送订单成功短信
		if (orderRequest.getBettype().equals(BetType.zengsong.value())) {
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
					mobileid = orderRequest.getReciverMobile();
					StringBuffer text = new StringBuffer();
					orderRequest.getLotno();
					text.append("【赠彩通知】您的好友").append(buyMobileId).append("赠送您").append(caizhong).append(batchcode)
							.append("期彩票").append(betnum.toString()).append("注.");
					if (StringUtils.isNotBlank(blessing)) {
						text.append(blessing + " ");
					}
					text.append("请您登录如意彩平台领取彩票,查询 www.ruyicai.com");
					if (VerifyUtil.isInt(mobileid)) {
						logger.info("出票赠送短信：" + mobileid + ",内容：" + text.toString());
						smsService.sendMessage(new String[] { mobileid }, text.toString());
					}
				}
			}
		}
	}

	private String getBetCode(String lotno, String betcode, String multiple) {
		JSONArray parseBetCodes = parseBetCodeService.parseBetCode(lotno, betcode, multiple);
		StringBuffer sBuffer = new StringBuffer();
		for (int j = 0; j < parseBetCodes.size(); j++) {
			JSONObject parseBetCodeJson = (JSONObject) parseBetCodes.get(j);
			sBuffer.append(parseBetCodeJson.getString("betCode")).append(",");
		}
		if (sBuffer.toString().endsWith(",")) {
			sBuffer.delete(sBuffer.length() - 1, sBuffer.length());
		}
		return sBuffer.toString();
	}

}
