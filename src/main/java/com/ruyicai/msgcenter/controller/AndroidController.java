package com.ruyicai.msgcenter.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ruyicai.msgcenter.dao.SendAndroidDetailDao;
import com.ruyicai.msgcenter.domain.AndroidAlias;
import com.ruyicai.msgcenter.exception.RuyicaiException;
import com.ruyicai.msgcenter.util.ErrorCode;

@RequestMapping("/android")
@Controller
public class AndroidController {

	private Logger logger = LoggerFactory.getLogger(AndroidController.class);

	@Autowired
	private SendAndroidDetailDao sendAndroidDetailDao;

	/**
	 * 保存android客户端用户的alias
	 * 
	 * @param userno
	 *            用户编号
	 * @param pkgName
	 *            包名
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/saveAlias")
	public @ResponseBody
	ResponseData saveAlias(@RequestParam("userno") String userno, @RequestParam("pkgName") String pkgName) {
		logger.info("/android/saveAlias ,userno:" + userno + ",pkgName:" + pkgName);
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;
		try {
			rd.setValue(AndroidAlias.createIfNotExist(userno, pkgName));
		} catch (RuyicaiException e) {
			logger.error("saveAlias error，", e);
			result = e.getErrorCode();
		} catch (Exception e) {
			logger.error("saveAlias error，", e);
			result = ErrorCode.ERROR;
		}
		rd.setErrorCode(result.value);
		return rd;
	}

	/**
	 * 发送Android通知
	 * 
	 * @param receiverType
	 *            发送(Jpush接收)的类型。 @see
	 *            com.ruyicai.msgcenter.consts.JpushReciverType.java
	 * @param receiverValue
	 *            发送(Jpush接收)的值
	 * @param pkgName
	 *            客户端包名，使用哪个包进行发送。@see
	 *            com.ruyicai.msgcenter.consts.AndroidPkgType.java
	 * @param msgTitle
	 *            通知标题
	 * @param msgContent
	 *            通知内容
	 * @param sendTime
	 *            发送时间(选填，默认为当前时间)
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/createSendAndroidDetail")
	public @ResponseBody
	ResponseData createSendAndroidDetail(@RequestParam(value = "receiverType") Integer receiverType,
			@RequestParam(value = "receiverValue") String receiverValue,
			@RequestParam(value = "pkgName") String pkgName, @RequestParam(value = "msgTitle") String msgTitle,
			@RequestParam(value = "msgContent") String msgContent,
			@RequestParam(value = "sendTime", required = false) Date sendTime) {
		logger.info(
				"/android/createSendAndroidDetail receiverType:{},receiverValue:{},pkgName:{},msgTitle:{},msgContent:{},sendTime:{}",
				new String[] { receiverType + "", receiverValue, pkgName, msgTitle, msgContent, sendTime + "" });
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;
		try {
			if (sendTime == null) {
				sendTime = new Date();
			}
			rd.setValue(sendAndroidDetailDao.createAndroidDetails(receiverType, receiverValue, pkgName, msgTitle,
					msgContent, sendTime));
		} catch (RuyicaiException e) {
			logger.error("createSendAndroidDetail error，", e);
			result = e.getErrorCode();
		} catch (Exception e) {
			logger.error("createSendAndroidDetail error，", e);
			result = ErrorCode.ERROR;
		}
		rd.setErrorCode(result.value);
		return rd;
	}

	/**
	 * 发送Android通知
	 * 
	 * @param usernos
	 *            用户编号字符串，使用","分隔
	 * @param msgTitle
	 *            通知标题
	 * @param msgContent
	 *            通知内容
	 * @param sendTime
	 *            发送时间(选填，默认为当前时间)
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/createSendAndroidDetailByUserno")
	public @ResponseBody
	ResponseData createSendAndroidDetailByUserno(@RequestParam(value = "usernos") String usernos,
			@RequestParam(value = "msgTitle") String msgTitle, @RequestParam(value = "msgContent") String msgContent,
			@RequestParam(value = "sendTime", required = false) Date sendTime) {
		logger.info("/android/createSendAndroidDetailByUserno usernos:{},msgTitle:{},msgContent:{},sendTime:{}",
				new String[] { usernos, msgTitle, msgContent, sendTime + "" });
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;
		try {
			if (sendTime == null) {
				sendTime = new Date();
			}
			if (StringUtils.isBlank(usernos)) {
				throw new IllegalArgumentException("the argument usernos is not blant");
			}
			String[] usernoArray = usernos.split("\\,");
			List<String> list = Arrays.asList(usernoArray);
			sendAndroidDetailDao.createAndroidDetailsByUsernos(list, msgTitle, msgContent, sendTime);
		} catch (RuyicaiException e) {
			logger.error("createSendAndroidDetail error，", e);
			result = e.getErrorCode();
		} catch (Exception e) {
			logger.error("createSendAndroidDetail error，", e);
			result = ErrorCode.ERROR;
		}
		rd.setErrorCode(result.value);
		return rd;
	}
}
