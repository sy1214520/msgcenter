package com.ruyicai.msgcenter.service;

import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.msgcenter.dao.SendSMSDetailDao;

@Service
public class SendSMSDetailService {

	private Logger logger = LoggerFactory.getLogger(SendSMSDetailService.class);

	@Autowired
	public SendSMSDetailDao publicDao;

	/**
	 * 创建定时短信
	 * 
	 * @param mobileIds
	 * @param content
	 * @param channelName
	 * @return
	 */
	@Transactional
	public void createSendSMSDetail(String mobileIds, String content, String channelName, Date date) {
		logger.info("保存定时短信mobileid:{},content:{},channelName:{}", new String[] { mobileIds, content, channelName });
		Calendar calendar = Calendar.getInstance();
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		// 22点后8点前，延迟发送
		if (date != null) {
			calendar.setTime(date);
		} else if (hour >= 22) {
			calendar.add(Calendar.DATE, 1);
			calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 8, 0,
					0);
		} else if (hour <= 8) {
			calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 8, 0,
					0);
		}
		publicDao.createSendSMSDetail(mobileIds, content, channelName, calendar.getTime());
	}
}
