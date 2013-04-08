package com.ruyicai.msgcenter.service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.ruyicai.msgcenter.consts.Const;
import com.ruyicai.msgcenter.consts.SendState;
import com.ruyicai.msgcenter.dao.SendSMSDetailDao;
import com.ruyicai.msgcenter.domain.SendIOSDetail;
import com.ruyicai.msgcenter.domain.SendSMSDetail;
import com.ruyicai.msgcenter.util.DateUtil;
import com.ruyicai.msgcenter.util.PackageSendUtil;
import com.ruyicai.msgcenter.util.SpringUtils;

@Service
public class QuartzService implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final Logger logger = Logger.getLogger(QuartzService.class);

	/**
	 * 循环发送到时的短信
	 */
	public void cyclicSendWaitSMSDetail() {
		logger.info("执行定时发送短信任务" + DateUtil.format(new Date()));

		defaultSendWaitSMSDetail(Const.defaultSMSChannel);
		defaultSendWaitSMSDetail(Const.menWangOldSMSServiceProvider);

		List<SendIOSDetail> sendIOSDetails = SendIOSDetail
				.findDefaultReady2SendIOSDetailsBySendState(SendState.SEND_WAIT.value());
		logger.info("推送发送数量为:" + sendIOSDetails.size());
		IOSService iosService = SpringUtils.getBean(IOSService.class);
		iosService.sendIOSJms(sendIOSDetails);
	}

	public void deleteInactiveDevices() {
		if (Const.queryStatusReport.equals("1")) {
			logger.info("删除不活跃的token");
			IOSService iosService = SpringUtils.getBean(IOSService.class);
			Integer count = iosService.deleteInactiveDevices();
			if (count != null && count > 0) {
				logger.info("删除token" + count + "条");
			}
		}
	}

	/**
	 * 根据channelName查找待发送短信，并将内容相同的短信按100个分组，进行发送
	 * 
	 * @param channelName
	 */
	public void defaultSendWaitSMSDetail(String channelName) {
		List<Object[]> packageDateByContent = findReady2SendSMSDetail(SendState.SEND_WAIT.value(), channelName);
		List<Object[]> reorganizeData = PackageSendUtil.reorganizeData(packageDateByContent, 100);
		if (reorganizeData != null && reorganizeData.size() > 0) {
			for (Object[] obj : reorganizeData) {
				sendSMS((String) obj[1], (String) obj[0], channelName, (String) obj[2]);
			}
		}
	}

	public static void main(String[] args) {
		String[] ids = new String[] {};
		int x = 0;
		for (int i = 0; i < 10; i++) {
			ids[x] = i + "";
			x++;
		}
		System.out.println(ids.length);
		for (int i = 0; i < ids.length; i++) {
			System.out.println(ids[i]);
		}
	}

	/**
	 * 查找准备发送的短信，并将相同内容的短信，进行打包
	 * 
	 * @param sendState
	 *            待发送短信状态
	 * @param channelName
	 *            短信接口名称
	 * @return Object[0]:短信内容,Object[1]:List<Object[]>短信号码和detailId的数组集合
	 */
	public List<Object[]> findReady2SendSMSDetail(BigDecimal sendState, String channelName) {
		SendSMSDetailDao smsDetailDao = SpringUtils.getBean(SendSMSDetailDao.class);
		List<SendSMSDetail> list = smsDetailDao.findDefaultReady2SendSMSDetailsBySendState(sendState, channelName);
		List<Object[]> resultList = new ArrayList<Object[]>();
		Map<Integer, List<SendSMSDetail>> map = new HashMap<Integer, List<SendSMSDetail>>();
		Set<Long> ids = new HashSet<Long>();
		// 按内容的hashCode分组为Map<#相同的hashcode#, List<SendSMSDetail>>
		for (SendSMSDetail detail : list) {
			ids.add(detail.getId());
			Integer hashCode = detail.getHashCode();
			List<SendSMSDetail> groupList = map.get(hashCode);
			if (groupList == null) {
				groupList = new ArrayList<SendSMSDetail>();
				groupList.add(detail);
				map.put(hashCode, groupList);
			} else {
				groupList.add(detail);
			}
		}
		//
		smsDetailDao.sendSMSSending(ids);
		// 将Map<#相同的hashcode#, List<SendSMSDetail>>组成List<Object[]>对象，有一点绕
		for (List<SendSMSDetail> groupList : map.values()) {
			List<Object[]> mobileIds = new ArrayList<Object[]>();
			String text = null;
			if (groupList != null && groupList.size() > 0) {
				text = groupList.get(0).getContent();
				for (SendSMSDetail detail : groupList) {
					mobileIds.add(new Object[] { detail.getMobileid(), detail.getId() });
				}
				resultList.add(new Object[] { text, mobileIds });
			}
		}
		return resultList;
	}

	/**
	 * 调用发送短信
	 * 
	 * @param mobileid
	 *            手机号(','分隔的字符串)
	 * @param text
	 *            短信内容
	 * @param channelName
	 *            短信渠道
	 * @param detailid
	 *            对象id(','分隔的字符串)
	 * @return
	 */
	private Integer sendSMS(String mobileid, String text, String channelName, String detailid) {
		Integer result = 0;
		try {
			SMSService smsService = SpringUtils.getBean(SMSService.class);
			SendSMSDetailDao smsDetailDao = SpringUtils.getBean(SendSMSDetailDao.class);
			logger.info("发送定时短信 text:[" + text + "],channelName:[" + channelName + "],mobileid:[" + mobileid
					+ "],detailid:[" + detailid + "]");
			result = smsService.immediateSendMessage(mobileid, text, channelName);
			if (null == result || 1 != result) {
				logger.error("轮询发送短信失败, detailid:" + detailid + ",result:" + result);
				String[] ids = detailid.split("\\,");
				smsDetailDao.sendSMSFailure(ids);
			} else {
				logger.info("轮询发送短信成功, detailid:" + detailid + ",result:" + result);
				String[] ids = detailid.split("\\,");
				smsDetailDao.sendSMSSuccess(ids);
			}
		} catch (Exception e) {
			logger.error("定时轮询发送短信失败 text:[" + text + "],channelName:[" + channelName + "],mobileid:[" + mobileid
					+ "],detailid:[" + detailid + "]", e);
		}
		return result;
	}

	public void cyclicGetMessage() {
		logger.info("执行定时接收短信任务" + DateUtil.format(new Date()));
		try {
			SMSService smsService = SpringUtils.getBean(SMSService.class);
			logger.info("接收" + Const.receiveSMSChannel + "渠道短信");
			smsService.receivesms(Const.receiveSMSChannel);
		} catch (Exception e) {
			logger.error("执行定时接收短信任务", e);
		}
	}

	public void cyclicQueryStatusReport() {
		logger.info("执行定时接收发送报告" + DateUtil.format(new Date()));
		if (Const.queryStatusReport.equals("1")) {
			try {
				SMSService smsService = SpringUtils.getBean(SMSService.class);
				logger.info("接收" + Const.defaultSMSChannel + "发送报告");
				smsService.queryStatusReport(Const.defaultSMSChannel);
			} catch (Exception e) {
				logger.error("执行定时接收发送报告", e);
			}
			try {
				SMSService smsService = SpringUtils.getBean(SMSService.class);
				logger.info("接收" + Const.menWangOldSMSServiceProvider + "发送报告");
				smsService.queryStatusReport(Const.menWangOldSMSServiceProvider);
			} catch (Exception e) {
				logger.error("执行定时接收发送报告", e);
			}
		}
	}
}
