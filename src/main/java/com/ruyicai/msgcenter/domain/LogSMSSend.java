package com.ruyicai.msgcenter.domain;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.EntityManager;

import org.apache.commons.lang.StringUtils;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

@RooToString
@RooJavaBean
@RooEntity(versionField = "", table = "LOGSMSSEND")
public class LogSMSSend {

	@Column(name = "CHANNELNAME", length = 50)
	private String channelName;

	@Column(name = "MOBILEID", columnDefinition = "text")
	private String mobileid;

	@Column(name = "CONTENT", columnDefinition = "text")
	private String content;

	@Column(name = "RESULT", length = 50)
	private String result;

	@Column(name = "STATUSREPORT", length = 200)
	private String statusReport;

	@Column(name = "CREATETIME")
	private Date createTime;

	@Column(name = "SMSCOUNT")
	private Integer smscount;

	public static LogSMSSend createLogSmsSend(String mobileid, String content, String channelName, String result) {
		LogSMSSend log = new LogSMSSend();
		log.setChannelName(channelName);
		log.setMobileid(mobileid);
		log.setContent(content);
		log.setResult(result);
		log.setCreateTime(new Date());
		log.setSmscount(computeSmsCount(mobileid));
		log.persist();
		return log;
	}

	private static Integer computeSmsCount(String mobileid) {
		if (StringUtils.isNotBlank(mobileid)) {
			String[] mobileArray = mobileid.split("\\,");
			return mobileArray.length;
		} else {
			return 0;
		}
	}

	public static void updateStatusReport(String statusReport) {
		if (StringUtils.isBlank(statusReport)) {
			return;
		}
		String[] split = statusReport.split("\\,");
		if (split == null || split.length < 6) {
			return;
		}
		String result = split[2];
		String report = split[4] + "," + split[5];
		EntityManager em = entityManager();
		List<LogSMSSend> resultList = em
				.createQuery("SELECT o FROM LogSMSSend o where o.result = :result", LogSMSSend.class)
				.setParameter("result", result).getResultList();
		for (LogSMSSend logSMSSend : resultList) {
			if (StringUtils.isBlank(logSMSSend.getStatusReport())) {
				logSMSSend.setStatusReport(report);
			} else {
				logSMSSend.setStatusReport(logSMSSend.getStatusReport() + ";" + report);
			}
			logSMSSend.merge();
		}
	}
}
