package com.ruyicai.msgcenter.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ruyicai.msgcenter.exception.RuyicaiException;
import com.ruyicai.msgcenter.service.MailService;
import com.ruyicai.msgcenter.util.ErrorCode;

/**
 * 发送邮件接口
 */
@RequestMapping("/mail")
@Controller
public class MailController {

	private Logger logger = LoggerFactory.getLogger(SMSController.class);

	@Autowired
	private MailService mailService;

	/**
	 * 发送邮件
	 * 
	 * @param to
	 *            要发送的邮箱地址，多个邮箱用';'分隔
	 * @param subject
	 *            邮件标题
	 * @param content
	 *            邮件内容
	 * @return
	 */
	@RequestMapping(value = "/sendMail", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData sendMail(@RequestParam(value = "to") String to, @RequestParam(value = "subject") String subject,
			@RequestParam(value = "content") String content) {
		logger.info("/mail/sendMail");
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;
		try {
			rd.setValue(mailService.sendMimeMail(to, subject, content));
		} catch (RuyicaiException e) {
			logger.error("发送邮件异常,{}", new String[] { e.getMessage() }, e);
			rd.setValue(e.getMessage());
			result = ErrorCode.ERROR;
		} catch (Exception e) {
			logger.error("发送邮件异常,{}", new String[] { e.getMessage() }, e);
			result = ErrorCode.ERROR;
			rd.setValue(e.getMessage());
		}
		rd.setErrorCode(result.value);
		return rd;
	}
}
