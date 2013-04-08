package com.ruyicai.msgcenter.controller;

import java.util.Date;
import java.util.List;
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

import com.ruyicai.msgcenter.dao.SendIOSDetailDao;
import com.ruyicai.msgcenter.domain.DeviceToken;
import com.ruyicai.msgcenter.domain.LogIOSSend;
import com.ruyicai.msgcenter.domain.SendIOSDetail;
import com.ruyicai.msgcenter.exception.RuyicaiException;
import com.ruyicai.msgcenter.service.IOSService;
import com.ruyicai.msgcenter.util.DateUtil;
import com.ruyicai.msgcenter.util.ErrorCode;
import com.ruyicai.msgcenter.util.JsonUtil;
import com.ruyicai.msgcenter.util.Page;

@RequestMapping("/ios")
@Controller
public class IOSController {

	private Logger logger = LoggerFactory.getLogger(SMSController.class);

	@Autowired
	private IOSService iosService;

	@Autowired
	private SendIOSDetailDao sendIOSDetailDao;

	/**
	 * 给用户推送ios消息
	 * 
	 * @param userno
	 * @param text
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/send")
	public @ResponseBody
	ResponseData sendIOSMessage(@RequestParam("usernos") String usernos, @RequestParam("text") String text) {
		logger.info("send ios message ,usernos:" + usernos + ",text:" + text);
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;
		try {
			String[] usernoArray = usernos.split("\\,");
			for (String userno : usernoArray) {
				List<DeviceToken> list = DeviceToken.findAllDeviceTokensByUserno(userno);
				for (DeviceToken token : list) {
					logger.info("token:" + token);
					iosService.sendMsg(token.getId().getToken(), text, token.getId().getType(), userno);
				}
			}
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
	 * 给用户推送ios消息
	 * 
	 * @param userno
	 * @param text
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/delaysend")
	public @ResponseBody
	ResponseData delaySendIOSMessage(@RequestParam("usernos") String usernos, @RequestParam("text") String text,
			@RequestParam(value = "sendTime", required = false) String sendTime) {
		logger.info("send ios message ,usernos:" + usernos + ",text:" + text);
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;
		try {
			String[] usernoArray = usernos.split("\\,");
			Date sendTimeDate = null;
			if (StringUtils.isBlank(sendTime)) {
				sendTimeDate = new Date();
			} else {
				sendTimeDate = DateUtil.parse(sendTime);
			}
			iosService.createDelayIOSDetail(usernoArray, text, sendTimeDate);
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
	 * 给所有用户创建定时IOS渠道推送
	 * 
	 * @param text
	 *            推送内容
	 * @param sendTime
	 *            发送时间，格式为yyyy-MM-dd HH:mm:ss的字符串.
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/sendAll")
	public @ResponseBody
	ResponseData sendAll(@RequestParam("text") String text,
			@RequestParam(value = "sendTime", required = false) String sendTime) {
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;
		logger.info("给所有用户创建定时IOS渠道推送, text: " + text + ", sendTime: " + sendTime);
		try {
			Date sendTimeDate = null;
			if (StringUtils.isBlank(sendTime)) {
				sendTimeDate = new Date();
			} else {
				sendTimeDate = DateUtil.parse(sendTime);
			}
			iosService.createAllUserIOSDetail(text, sendTimeDate);
			rd.setValue("complete");
		} catch (RuyicaiException e) {
			logger.error("sendAll error", e);
			result = e.getErrorCode();
		} catch (Exception e) {
			logger.error("sendAll error", e);
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
		Page<SendIOSDetail> page = new Page<SendIOSDetail>(startLine, endLine, orderBy, orderDir);
		ErrorCode result = ErrorCode.OK;
		try {
			Map<String, Object> conditionMap = JsonUtil.transferJson2Map(condition);
			sendIOSDetailDao.findWaitforSend(conditionMap, page);
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

	@RequestMapping(value = "/selectIOSLog")
	public @ResponseBody
	ResponseData selectSMSLog(@RequestParam(value = "condition", required = false) String condition,
			@RequestParam(value = "startLine", required = false, defaultValue = "0") int startLine,
			@RequestParam(value = "endLine", required = false, defaultValue = "30") int endLine,
			@RequestParam(value = "orderBy", required = false) String orderBy,
			@RequestParam(value = "orderDir", required = false) String orderDir) {
		logger.info("/selectIOSLog condition:{}", new String[] { condition });
		ResponseData rd = new ResponseData();
		Page<LogIOSSend> page = new Page<LogIOSSend>(startLine, endLine, orderBy, orderDir);
		ErrorCode result = ErrorCode.OK;
		try {
			Map<String, Object> conditionMap = JsonUtil.transferJson2Map(condition);
			LogIOSSend.findSMSLog(conditionMap, page);
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

	@RequestMapping(value = "/selectDeviceToken")
	public @ResponseBody
	ResponseData selectDeviceToken(@RequestParam(value = "condition", required = false) String condition,
			@RequestParam(value = "startLine", required = false, defaultValue = "0") int startLine,
			@RequestParam(value = "endLine", required = false, defaultValue = "30") int endLine,
			@RequestParam(value = "orderBy", required = false) String orderBy,
			@RequestParam(value = "orderDir", required = false) String orderDir) {
		logger.info("/selectDeviceToken condition:{}", new String[] { condition });
		ResponseData rd = new ResponseData();
		Page<DeviceToken> page = new Page<DeviceToken>(startLine, endLine, orderBy, orderDir);
		ErrorCode result = ErrorCode.OK;
		try {
			Map<String, Object> conditionMap = JsonUtil.transferJson2Map(condition);
			DeviceToken.findDeviceTokenByPage(conditionMap, page);
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
