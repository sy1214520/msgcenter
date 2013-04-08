package com.ruyicai.msgcenter.controller;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ruyicai.msgcenter.domain.DeviceToken;
import com.ruyicai.msgcenter.domain.DeviceTokenPK;
import com.ruyicai.msgcenter.domain.Token;
import com.ruyicai.msgcenter.domain.UserSMSTiming;
import com.ruyicai.msgcenter.exception.RuyicaiException;
import com.ruyicai.msgcenter.service.UserSettingService;
import com.ruyicai.msgcenter.util.ErrorCode;

@RequestMapping("/usersetting")
@Controller
public class UserSettingController {

	private Logger logger = LoggerFactory.getLogger(UserSettingController.class);

	@Autowired
	UserSettingService userSettingService;

	/**
	 * （旧方法 不用了） 保存或修改用户短信设置
	 * 
	 * @param userno
	 *            用户编号
	 * @param smstype
	 *            短信类型
	 * @param state
	 *            是否有效 0:不发送,1:发送
	 * @return
	 * @deprecated
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/saveorupdate")
	public @ResponseBody
	ResponseData saveorupdateUserSMSTiming(@RequestParam("userno") String userno,
			@RequestParam("smstype") BigDecimal smstype, @RequestParam("state") int state) {
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;
		logger.info("设置用户短信发送状态userno:" + userno + ",state:" + state);
		try {
			UserSMSTiming timing = UserSMSTiming.createOrMergeUserSMSTiming(userno, smstype, state);
			rd.setValue(timing);
		} catch (RuyicaiException e) {
			logger.error(
					"saveorupdateUserSMSTiming error,userno:" + userno + ",smstype:" + smstype + ",state:" + state, e);
			result = e.getErrorCode();
		} catch (Exception e) {
			logger.error(
					"saveorupdateUserSMSTiming error,userno:" + userno + ",smstype:" + smstype + ",state:" + state, e);
			result = ErrorCode.ERROR;
		}
		rd.setErrorCode(result.value);
		return rd;
	}

	/**
	 * （旧方法 不用了） 查询用户消息设置
	 * 
	 * @param userno
	 *            用户编号
	 * @param smstype
	 *            事件类型
	 * @return
	 * @deprecated
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/findbyuserno")
	public @ResponseBody
	ResponseData findUserSMSTiming(@RequestParam("userno") String userno, @RequestParam("smstype") BigDecimal smstype) {
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;
		logger.info("查询用户userno:" + userno + "短信发送设置");
		try {
			UserSMSTiming timing = UserSMSTiming.findByUsernoAndSmstypeFromCache(userno, smstype);
			rd.setValue(timing);
		} catch (RuyicaiException e) {
			logger.error("findUserSMSTiming error,userno:" + userno + ",smstype:" + smstype, e);
			result = e.getErrorCode();
		} catch (Exception e) {
			logger.error("findUserSMSTiming error,userno:" + userno + ",smstype:" + smstype, e);
			result = ErrorCode.ERROR;
		}
		rd.setErrorCode(result.value);
		return rd;
	}

	/**
	 * 查询用户设置
	 * 
	 * @param userno
	 *            用户编号
	 * @param smstype
	 *            事件类型， 1为开奖
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/findUserSetting")
	public @ResponseBody
	ResponseData findUserSetting(@RequestParam("userno") String userno, @RequestParam("smstype") BigDecimal smstype) {
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;
		logger.info("查询用户userno:" + userno + ",smstype:" + smstype + "设置");
		try {
			rd.setValue(userSettingService.getUserSMSTiming(userno, smstype));
		} catch (RuyicaiException e) {
			logger.error("findUserSetting error,userno:" + userno + ",smstype:" + smstype, e);
			result = e.getErrorCode();
		} catch (Exception e) {
			logger.error("findUserSetting error,userno:" + userno + ",smstype:" + smstype, e);
			result = ErrorCode.ERROR;
		}
		rd.setErrorCode(result.value);
		return rd;
	}

	/**
	 * 修改用户某一事件设置是否发送
	 * 
	 * @param userno
	 *            用户编号
	 * @param smstype
	 *            事件类型， 1为开奖
	 * @param needToSend
	 *            是否有效 0:不发送,1:发送
	 * @return
	 */
	@RequestMapping(value = "/setUserSMSTiming")
	public @ResponseBody
	ResponseData setUserSMSTiming(@RequestParam("userno") String userno, @RequestParam("smstype") BigDecimal smstype,
			@RequestParam("needToSend") int needToSend) {
		logger.info("setUserSMSTiming,userno:{},smstype:{},needToSend:{}", new String[] { userno, smstype + "",
				needToSend + "" });
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;
		try {
			rd.setValue(userSettingService.updateUserSMSTiming(userno, smstype, needToSend));
		} catch (RuyicaiException e) {
			logger.error("setUserSMSTiming error,userno:" + userno + ",smstype:" + smstype + ",needToSend:"
					+ needToSend, e);
			result = e.getErrorCode();
		} catch (Exception e) {
			logger.error("setUserSMSTiming error,userno:" + userno + ",smstype:" + smstype + ",needToSend:"
					+ needToSend, e);
			result = ErrorCode.ERROR;
		}
		rd.setErrorCode(result.value);
		return rd;
	}

	/**
	 * 批量更新用户设置。for IOS
	 * 
	 * @param json
	 *            UserSettingDTO集合转化的JSOn串
	 * @return
	 */
	@RequestMapping(value = "/updateUserSMSTimingBatch", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData updateUserSMSTimingBatch(@RequestParam("json") String json) {
		logger.info("updateUserSMSTimingBatch json:" + json);
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;
		try {
			userSettingService.updateUserSMSTimingBatch(json);
		} catch (RuyicaiException e) {
			logger.error("updateUserSMSTimingBatch error,json:" + json, e);
			result = e.getErrorCode();
		} catch (Exception e) {
			logger.error("updateUserSMSTimingBatch error,json:" + json, e);
			result = ErrorCode.ERROR;
		}
		rd.setErrorCode(result.value);
		return rd;
	}

	/**
	 * 修改短信、邮件、iphone设置是否发送
	 * 
	 * @param id
	 *            记录编号
	 * @param state
	 *            是否有效 0:不发送,1:发送
	 * @return
	 */
	@RequestMapping(value = "/updateSendChannel")
	public @ResponseBody
	ResponseData updateSendChannel(@RequestParam("id") Long userSMStimingId,
			@RequestParam("sendtype") Integer sendtype, @RequestParam("needToSend") Integer needToSend) {
		logger.info("updateSendChannel id:{},sendtype:{},needToSend:{}", new String[] { userSMStimingId + "",
				sendtype + "", needToSend + "" });
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;
		try {
			rd.setValue(userSettingService.updateSendChannel(userSMStimingId, sendtype, needToSend));
		} catch (RuyicaiException e) {
			logger.error("updateSendChannel error,userSMStimingId:" + userSMStimingId + ",sendtype:" + sendtype
					+ ",needToSend:" + needToSend, e);
			result = e.getErrorCode();
		} catch (Exception e) {
			logger.error("updateSendChannel error,userSMStimingId:" + userSMStimingId + ",sendtype:" + sendtype
					+ ",needToSend:" + needToSend, e);
			result = ErrorCode.ERROR;
		}
		rd.setErrorCode(result.value);
		return rd;
	}

	/**
	 * 存储token
	 * 
	 * @param machine
	 * @param token
	 * @param userno
	 * @param needToSend
	 * @param type
	 * @return
	 */
	@RequestMapping(value = "/saveToken")
	public @ResponseBody
	ResponseData saveToken(@RequestParam("userno") String userno, @RequestParam("token") String token,
			@RequestParam("type") String type, @RequestParam("machine") String machine,
			@RequestParam(value = "needToSend", required = false, defaultValue = "1") Integer needToSend) {
		logger.info("/saveToken userno:" + userno + ",token:" + token + ",type:" + type + ",needToSend:"
				+ needToSend + ",machine:" + machine);
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;
		try {
			rd.setValue(DeviceToken.createIfNotExist(userno, token, type, machine, needToSend));
		} catch (RuyicaiException e) {
			logger.error("saveToken error,userno:" + userno + ",token:" + token + ",type:" + type + ",needToSend:"
					+ needToSend + ",machine:" + machine, e);
			result = e.getErrorCode();
		} catch (Exception e) {
			logger.error("saveToken error,userno:" + userno + ",token:" + token + ",type:" + type + ",needToSend:"
					+ needToSend + ",machine:" + machine, e);
			result = ErrorCode.ERROR;
		}
		rd.setErrorCode(result.value);
		return rd;
	}

	/**
	 * 更改token是否发送
	 * 
	 * @param id
	 *            记录编号
	 * @param state
	 *            是否有效 0:不发送,1:发送
	 * @return
	 */
	@RequestMapping(value = "/updateToken")
	public @ResponseBody
	ResponseData updateToken(@RequestParam("userno") String userno, @RequestParam("token") String token,
			@RequestParam("type") String type, @RequestParam("needToSend") int needToSend) {
		logger.info("updateToken userno:{},token:{},type:{},needToSend:{}"
				+ new String[] { userno, token, type, needToSend + "" });
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;
		try {
			DeviceToken deviceToken = DeviceToken.findDeviceToken(new DeviceTokenPK(userno, token, type));
			deviceToken.setNeedToSend(needToSend);
			deviceToken.merge();
			rd.setValue(deviceToken);
		} catch (RuyicaiException e) {
			logger.error("updateToken error,userno:" + userno + ",token:" + token + ",type:" + type + ",needToSend:"
					+ needToSend, e);
			result = e.getErrorCode();
		} catch (Exception e) {
			logger.error("updateToken error,userno:" + userno + ",token:" + token + ",type:" + type + ",needToSend:"
					+ needToSend, e);
			result = ErrorCode.ERROR;
		}
		rd.setErrorCode(result.value);
		return rd;
	}

	/**
	 * 删除Token记录
	 * 
	 * @param id
	 *            记录编号
	 * @return
	 */
	@RequestMapping(value = "/removeToken")
	public @ResponseBody
	ResponseData removeToken(@RequestParam("userno") String userno, @RequestParam("token") String token,
			@RequestParam("type") String type) {
		logger.info("removeToekn userno:{},token:{},type:{}" + new String[] { userno, token, type });
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;
		try {
			DeviceToken deviceToken = DeviceToken.findDeviceToken(new DeviceTokenPK(userno, token, type));
			deviceToken.remove();
		} catch (RuyicaiException e) {
			logger.error("removeToken error,userno:" + userno + ",token:" + token + ",type:" + type, e);
			result = e.getErrorCode();
		} catch (Exception e) {
			logger.error("removeToken error,userno:" + userno + ",token:" + token + ",type:" + type, e);
			result = ErrorCode.ERROR;
		}
		rd.setErrorCode(result.value);
		return rd;
	}

	/**
	 * 找到对应的Token集合
	 * 
	 * @param userno
	 *            用户编号
	 * @return
	 */
	@RequestMapping(value = "/findTokens")
	public @ResponseBody
	ResponseData findTokens(@RequestParam("userno") String userno) {
		logger.info("findTokens userno:" + userno);
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;
		try {
			List<DeviceToken> deviceTokens = DeviceToken.findAllDeviceTokensByUserno(userno);
			rd.setValue(deviceTokens);
		} catch (RuyicaiException e) {
			logger.error("findTokens error,userno:" + userno, e);
			result = e.getErrorCode();
		} catch (Exception e) {
			logger.error("findTokens error,userno:" + userno, e);
			result = ErrorCode.ERROR;
		}
		rd.setErrorCode(result.value);
		return rd;
	}

	/**
	 * 存储token
	 * 
	 * @param token
	 * @param type
	 * @return
	 */
	@RequestMapping(value = "/saveTk")
	public @ResponseBody
	ResponseData saveTk(@RequestParam("token") String token, @RequestParam("type") String type) {
		logger.info("saveTk token:" + token + ",type:" + type);
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;
		try {
			rd.setValue(Token.createOrUpdate(token, type));
		} catch (RuyicaiException e) {
			logger.error("saveTk error,token:" + token + ",type:" + type, e);
			result = e.getErrorCode();
		} catch (Exception e) {
			logger.error("saveTk error,token:" + token + ",type:" + type, e);
			result = ErrorCode.ERROR;
		}
		rd.setErrorCode(result.value);
		return rd;
	}
}
