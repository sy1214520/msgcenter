package com.ruyicai.msgcenter.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ruyicai.msgcenter.consts.Const;
import com.ruyicai.msgcenter.dao.LogSMSSendDao;
import com.ruyicai.msgcenter.dao.SendSMSDetailDao;
import com.ruyicai.msgcenter.domain.AutoReply;
import com.ruyicai.msgcenter.domain.LogSMSSend;
import com.ruyicai.msgcenter.domain.SendSMSDetail;
import com.ruyicai.msgcenter.exception.RuyicaiException;
import com.ruyicai.msgcenter.service.SMSService;
import com.ruyicai.msgcenter.util.DateUtil;
import com.ruyicai.msgcenter.util.ErrorCode;
import com.ruyicai.msgcenter.util.JsonUtil;
import com.ruyicai.msgcenter.util.Page;

/**
 * 发送短信接口
 */
@RequestMapping("/sms")
@Controller
public class SMSController {

	private Logger logger = LoggerFactory.getLogger(SMSController.class);

	@Autowired
	private SMSService smsService;

	@Autowired
	private SendSMSDetailDao sendSMSDetailDao;

	@Autowired
	private LogSMSSendDao logSMSSendDao;

	/**
	 * 发送短信接口,原lottery短信接口
	 * 
	 * @param mobileIds
	 *            手机号数组
	 * @param text
	 *            短信内容
	 * @param channelName
	 *            短信渠道（可不传，默认梦网网管）
	 * @return ResponseData
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/send")
	public @ResponseBody
	ResponseData send(@RequestParam("mobileIds") String[] mobileIds, @RequestParam("text") String text,
			@RequestParam(value = "channelName", required = false) String channelName) {
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;
		logger.info("发送短信请求, mobileIds: " + Arrays.toString(mobileIds) + ", text: " + text);
		try {
			if (StringUtils.isBlank(channelName)) {
				channelName = Const.defaultSMSChannel;
			}
			rd.setValue(smsService.immediateSendMessage(mobileIds, text, channelName));
		} catch (RuyicaiException e) {
			logger.error("sms send error，", e);
			result = e.getErrorCode();
		} catch (Exception e) {
			logger.error("sms send error，", e);
			result = ErrorCode.ERROR;
		}
		rd.setErrorCode(result.value);
		return rd;
	}

	/**
	 * 延迟策略发送短信，按系统的延迟定时策略发送短信，例如晚10点后短信于第二天8点发送
	 * 
	 * @param mobileIds
	 *            手机号数组
	 * @param text
	 *            短信内容
	 * @return ResponseData
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/delaysend")
	public @ResponseBody
	ResponseData delaySend(@RequestParam("mobileIds") String[] mobileIds, @RequestParam("text") String text) {
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;
		logger.info("发送短信请求, mobileIds: " + Arrays.toString(mobileIds) + ", text: " + text);
		try {
			smsService.sendMessage(mobileIds, text);
		} catch (RuyicaiException e) {
			logger.error("sms send error，", e);
			result = e.getErrorCode();
		} catch (Exception e) {
			logger.error("sms send error，", e);
			result = ErrorCode.ERROR;
		}
		rd.setErrorCode(result.value);
		return rd;
	}

	/**
	 * 延迟策略发送短信，按系统的延迟定时策略发送短信，例如晚10点后短信于第二天8点发送
	 * 
	 * @param mobileIds
	 *            手机号数组
	 * @param text
	 *            短信内容
	 * @return ResponseData
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/delaysendWithString")
	public @ResponseBody
	ResponseData delaySendWithString(@RequestParam("mobileIds") String mobileIds, @RequestParam("text") String text) {
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;
		logger.info("发送短信请求, mobileIds: " + mobileIds + ", text: " + text);
		try {
			smsService.sendMessage(mobileIds, text);
		} catch (RuyicaiException e) {
			logger.error("sms send error，", e);
			result = e.getErrorCode();
		} catch (Exception e) {
			logger.error("sms send error，", e);
			result = ErrorCode.ERROR;
		}
		rd.setErrorCode(result.value);
		return rd;
	}

	/**
	 * 创建定时短信
	 * 
	 * @param mobileIds
	 *            手机号数组
	 * @param text
	 *            短信内容
	 * @param sendTime
	 *            发送时间,可以为空(则发送时间为当前时间)，格式为yyyy-MM-dd HH:mm:ss的字符串.
	 * @return ResponseData
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/createdelaysms")
	public @ResponseBody
	ResponseData createDelaySMS(
			@RequestParam("mobileIds") String[] mobileIds,
			@RequestParam("text") String text,
			@RequestParam(value = "sendTime", required = false) String sendTime,
			@RequestParam(value = "channelName", required = false, defaultValue = "menWangSMSServiceProvider") String channelName) {
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;
		logger.info("创建延迟短信, mobileIds: " + Arrays.toString(mobileIds) + ", text: " + text);
		try {
			Date sendTimeDate = null;
			if (StringUtils.isBlank(sendTime)) {
				sendTimeDate = new Date();
			} else {
				sendTimeDate = DateUtil.parse(sendTime);
			}
			sendSMSDetailDao.createSendSMSDetail(mobileIds, text, channelName, sendTimeDate);
		} catch (RuyicaiException e) {
			logger.error("sms send error，", e);
			result = e.getErrorCode();
		} catch (Exception e) {
			logger.error("sms send error，", e);
			result = ErrorCode.ERROR;
		}
		rd.setErrorCode(result.value);
		return rd;
	}

	/**
	 * 创建定时短信
	 * 
	 * @param mobileIds
	 *            手机号数组
	 * @param text
	 *            短信内容
	 * @param sendTime
	 *            发送时间,可以为空(则发送时间为当前时间)，格式为yyyy-MM-dd HH:mm:ss的字符串.
	 * @return ResponseData
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/createdelaysmsWithString")
	public @ResponseBody
	ResponseData createDelaySMSWithString(
			@RequestParam("mobileIds") String mobileIds,
			@RequestParam("text") String text,
			@RequestParam(value = "sendTime", required = false) String sendTime,
			@RequestParam(value = "channelName", required = false, defaultValue = "menWangSMSServiceProvider") String channelName) {
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;
		logger.info("创建延迟短信, mobileIds: " + mobileIds + ", text: " + text);
		try {
			Date sendTimeDate = null;
			if (StringUtils.isBlank(sendTime)) {
				sendTimeDate = new Date();
			} else {
				sendTimeDate = DateUtil.parse(sendTime);
			}
			sendSMSDetailDao.createSendSMSDetail(mobileIds, text, channelName, sendTimeDate);
		} catch (RuyicaiException e) {
			logger.error("sms send error，", e);
			result = e.getErrorCode();
		} catch (Exception e) {
			logger.error("sms send error，", e);
			result = ErrorCode.ERROR;
		}
		rd.setErrorCode(result.value);
		return rd;
	}

	@RequestMapping(value = "/createOrMergeAutoReply", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData createOrMergeAutoReply(@RequestParam("receiveKey") String receiveKey,
			@RequestParam("replyContent") String replyContent) {
		logger.info("createOrMergeAutoReply receiveKey:{},replyContent:{}", new String[] { receiveKey, replyContent });
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;
		try {
			rd.setValue(AutoReply.createOrMergeAutoReply(receiveKey, replyContent));
		} catch (RuyicaiException e) {
			logger.error("createOrMergeAutoReply error，", e);
			result = e.getErrorCode();
		} catch (Exception e) {
			logger.error("createOrMergeAutoReply error，", e);
			result = ErrorCode.ERROR;
		}
		rd.setErrorCode(result.value);
		return rd;
	}

	@RequestMapping(value = "/selectWaitforSend")
	public @ResponseBody
	ResponseData selectWaitforSend(@RequestParam(value = "condition", required = false) String condition,
			@RequestParam(value = "startLine", required = false, defaultValue = "0") int startLine,
			@RequestParam(value = "endLine", required = false, defaultValue = "30") int endLine,
			@RequestParam(value = "orderBy", required = false) String orderBy,
			@RequestParam(value = "orderDir", required = false) String orderDir) {
		logger.info("/selectWaitforSend condition:{}", new String[] { condition });
		ResponseData rd = new ResponseData();
		Page<SendSMSDetail> page = new Page<SendSMSDetail>(startLine, endLine, orderBy, orderDir);
		ErrorCode result = ErrorCode.OK;
		try {
			Map<String, Object> conditionMap = JsonUtil.transferJson2Map(condition);
			sendSMSDetailDao.findWaitforSend(conditionMap, page);
			rd.setValue(page);
		} catch (RuyicaiException e) {
			logger.error("selectWaitforSend error", e);
			result = e.getErrorCode();
		} catch (Exception e) {
			logger.error("selectWaitforSend error", e);
			result = ErrorCode.ERROR;
		}
		rd.setErrorCode(result.value);
		return rd;
	}

	@RequestMapping(value = "/selectSMSLog")
	public @ResponseBody
	ResponseData selectSMSLog(@RequestParam(value = "condition", required = false) String condition,
			@RequestParam(value = "startLine", required = false, defaultValue = "0") int startLine,
			@RequestParam(value = "endLine", required = false, defaultValue = "30") int endLine,
			@RequestParam(value = "orderBy", required = false) String orderBy,
			@RequestParam(value = "orderDir", required = false) String orderDir) {
		logger.info("/selectSMSLog condition:{}", new String[] { condition });
		ResponseData rd = new ResponseData();
		Page<LogSMSSend> page = new Page<LogSMSSend>(startLine, endLine, orderBy, orderDir);
		ErrorCode result = ErrorCode.OK;
		try {
			Map<String, Object> conditionMap = JsonUtil.transferJson2Map(condition);
			logSMSSendDao.findSMSLog(conditionMap, page);
			rd.setValue(page);
		} catch (RuyicaiException e) {
			logger.error("selectSMSLog error", e);
			result = e.getErrorCode();
		} catch (Exception e) {
			logger.error("selectSMSLog error", e);
			result = ErrorCode.ERROR;
		}
		rd.setErrorCode(result.value);
		return rd;
	}

}
