package com.ruyicai.msgcenter.service;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class MailService {

	private Logger logger = LoggerFactory.getLogger(MailService.class);

	@Value("${mail.from}")
	private String from;

	@Autowired
	JavaMailSender mailSender;

	@Autowired
	private AsyncLogSendService asyncLogSendService;

	/**
	 * 发送TEXT格式的邮件.
	 * 
	 * @param to
	 * @param subject
	 * @param content
	 * @return
	 */
	public Integer sendTextMail(String to, String subject, String content) {
		logger.info("发送TextMail消息to:{},subject:{}, content:{}", new String[] { to, subject, content });
		Integer result = 0;
		try {
			SimpleMailMessage mail = new SimpleMailMessage();
			String[] arrayTo = to.split(";");
			mail.setTo(arrayTo);
			String nickname = javax.mail.internet.MimeUtility.encodeText("如意彩");
			mail.setFrom(nickname + " <" + from + ">");
			mail.setSubject(subject);
			mail.setText(content);
			mailSender.send(mail);
			result = 1;
		} catch (Exception e) {
			logger.error("发送邮件失败to:" + to + ",subject:" + subject, e);
			result = 0;
		} finally {
			asyncLogSendService.createMailLog(to, content, result + "");
			// SendSMSLog.createSendSMSLog(to, content, "email", result);
		}
		return result;
	}

	/**
	 * 发送MIME格式的邮件.
	 * 
	 * @param to
	 * @param subject
	 * @param content
	 * @return
	 */
	public Integer sendMimeMail(String to, String subject, String content) {
		logger.info("发送MimeMail消息to:{},subject:{}, content:{}", new String[] { to, subject, content });
		Integer result = 0;
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper helper;
		try {
			helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
			String nickname = javax.mail.internet.MimeUtility.encodeText("如意彩");
			helper.setFrom(new InternetAddress(nickname + " <" + from + ">"));
			helper.setSubject(subject);
			String[] arrayTo = to.split(";");
			helper.setTo(arrayTo);
			helper.setText(content, true);
			mailSender.send(mimeMessage);
			result = 1;
		} catch (MessagingException e) {
			logger.error("发送邮件失败to:" + to + ",subject:" + subject, e);
			result = 0;
		} catch (Exception e) {
			logger.error("发送邮件失败to:" + to + ",subject:" + subject, e);
			result = 0;
		} finally {
			asyncLogSendService.createMailLog(to, content, result + "");
			// SendSMSLog.createSendSMSLog(to, content, "email", result);
		}
		return result;
	}
}
