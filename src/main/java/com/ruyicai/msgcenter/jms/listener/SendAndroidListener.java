package com.ruyicai.msgcenter.jms.listener;

import java.util.Date;

import org.apache.camel.Header;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.jpush.api.ErrorCodeEnum;
import cn.jpush.api.MessageResult;

import com.ruyicai.msgcenter.consts.SendState;
import com.ruyicai.msgcenter.dao.SendAndroidDetailDao;
import com.ruyicai.msgcenter.domain.SendAndroidDetail;
import com.ruyicai.msgcenter.service.SendAndroidService;

@Service
public class SendAndroidListener {

	private Logger logger = LoggerFactory.getLogger(SendAndroidListener.class);

	@Autowired
	private SendAndroidService sendAndroidService;

	@Autowired
	private SendAndroidDetailDao sendAndroidDetailDao;

	@Transactional
	public void sendAndroidMS(@Header(value = "sendNo") Integer sendNo,
			@Header(value = "receiverType") Integer receiverType,
			@Header(value = "receiverValue") String receiverValue, @Header(value = "pkgName") String pkgName,
			@Header(value = "msgTitle") String msgTitle, @Header(value = "msgContent") String msgContent) {
		logger.info("发送Android消息,sendNo:{},receiverType:{},receiverValue:{},pkgName:{}", new String[] { sendNo + "",
				receiverType + "", receiverValue, pkgName });
		if (sendNo == null || receiverType == null || StringUtils.isBlank(pkgName)) {
			logger.error("参数错误,有必填参数为空sendNo:{},receiverType:{},pkgName:{}", new String[] { sendNo + "",
					receiverType + "", pkgName });
			return;
		}
		MessageResult result = null;
		switch (receiverType) {
		case 2:
			result = sendAndroidService.pushWithTag(pkgName, sendNo, receiverValue, msgTitle, msgContent);
			break;
		case 3:
			result = sendAndroidService.pushWithAlias(pkgName, sendNo, receiverValue, msgTitle, msgContent);
			break;
		case 4:
			result = sendAndroidService.pushWithAppKey(pkgName, sendNo, msgTitle, msgContent);
			break;
		default:
			break;
		}
		SendAndroidDetail sendAndroidDetail = sendAndroidDetailDao.findSendAndroidDetail(sendNo);
		if (null != result) {
			if (result.getErrcode() == ErrorCodeEnum.NOERROR.value()) {
				logger.info("发送成功,sendNo=" + result.getSendno());
				sendAndroidDetail.setSendState(SendState.SEND_SUCCESS.value());
				sendAndroidDetail.setSendSuccTime(new Date());
				sendAndroidDetail.setErrorCode(result.getErrcode());
				sendAndroidDetailDao.merge(sendAndroidDetail);
			} else {
				logger.error("发送失败, 错误代码=" + result.getErrcode() + ", 错误消息=" + result.getErrmsg());
				sendAndroidDetail.setSendState(SendState.SEND_FAILURE.value());
				sendAndroidDetail.setErrorCode(result.getErrcode());
				sendAndroidDetailDao.merge(sendAndroidDetail);
			}
		} else {
			logger.error("无法获取数据或接收类型不匹配receiverType:" + receiverType);
			sendAndroidDetail.setSendState(SendState.SEND_FAILURE.value());
			sendAndroidDetailDao.merge(sendAndroidDetail);
		}
	}
}
