package com.ruyicai.msgcenter.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.msgcenter.consts.SMSType;
import com.ruyicai.msgcenter.consts.SendType;
import com.ruyicai.msgcenter.controller.dto.UserSettingDTO;
import com.ruyicai.msgcenter.domain.SendChannel;
import com.ruyicai.msgcenter.domain.UserSMSTiming;
import com.ruyicai.msgcenter.observers.ISendObserver;
import com.ruyicai.msgcenter.observers.SendEmailObserver;
import com.ruyicai.msgcenter.observers.SendIOSMsgObserver;
import com.ruyicai.msgcenter.observers.SendLetterObserver;
import com.ruyicai.msgcenter.observers.SendSMSObserver;

@Service
public class UserSettingService {

	private Logger logger = LoggerFactory.getLogger(UserSettingService.class);

	@Autowired
	SendEmailObserver sendEmail;

	@Autowired
	SendSMSObserver sendSMS;

	@Autowired
	SendIOSMsgObserver sendIOSMsg;

	@Autowired
	SendLetterObserver sendLetterObserver;

	public Boolean judgeNotSend(String userno, SMSType smstype) {
		Boolean flag = false;
		UserSMSTiming timing = UserSMSTiming.findByUsernoAndSmstypeFromCache(userno, smstype.getValue());
		if (timing != null && timing.getNeedToSend() == 0) {
			// 用户设置不发送
			logger.info("userno:{},smstype:{},设置不发送短信", new String[] { userno, smstype.getValue() + "" });
			flag = true;
		}
		return flag;
	}

	/**
	 * 获取用户设置
	 * 
	 * @param userno
	 * @param smstype
	 * @return
	 */
	@Transactional
	public UserSettingDTO getUserSMSTiming(String userno, BigDecimal smstype) {
		if (StringUtils.isBlank(userno)) {
			throw new IllegalArgumentException("the argument userno is required");
		}
		if (smstype == null) {
			throw new IllegalArgumentException("the argument smstype is required");
		}
		SendType[] sendTypes = SendType.values();
		List<SendChannel> sendChannels = new ArrayList<SendChannel>();
		UserSMSTiming userSMSTiming = UserSMSTiming.findByUsernoAndSmstypeFromCache(userno, smstype);
		// 缓存和数据库中都没有
		if (userSMSTiming == null) {
			userSMSTiming = UserSMSTiming.createUserSMSTiming(userno, smstype, 1);
			for (SendType sendType : sendTypes) {
				Integer needToSend = 0;
				// 默认开启短信
				if (sendType.getValue() == SendType.MESSAGE.getValue()) {
					needToSend = 1;
				}
				SendChannel sendchannel = SendChannel.createOrMergeSendChannel(userSMSTiming.getId(), needToSend,
						sendType.getValue());
				sendChannels.add(sendchannel);
			}
		} else {
			// 内存中有用户设置缓存 开始查询发送渠道
			for (SendType sendType : sendTypes) {
				SendChannel sendchannel = SendChannel.findByUserSMStimingIdAndSendtypeFromCache(userSMSTiming.getId(),
						sendType.getValue());
				// 缓存和数据库中都没有
				if (sendchannel == null) {
					Integer needToSend = 0;
					if (sendType.getValue() == SendType.MESSAGE.getValue()) {
						needToSend = 1;
					}
					sendchannel = SendChannel.createSendChannel(userSMSTiming.getId(), needToSend, sendType.getValue());
				}
				sendChannels.add(sendchannel);
			}
		}
		if (sendChannels.size() == 0) {

		}
		return new UserSettingDTO(userSMSTiming, sendChannels);
	}

	@Transactional
	public UserSMSTiming updateUserSMSTiming(String userno, BigDecimal smstype, Integer needToSend) {
		return UserSMSTiming.createOrMergeUserSMSTiming(userno, smstype, needToSend);
	}

	@Transactional
	public SendChannel updateSendChannel(Long userSMStimingId, Integer sendtype, Integer needToSend) {
		return SendChannel.createOrMergeSendChannel(userSMStimingId, needToSend, sendtype);
	}

	/**
	 * 查询用户发送设置
	 * 
	 * @param userno
	 * @param type
	 * @return
	 */
	public Set<ISendObserver> findSendSetting(String userno, BigDecimal type) {
		Set<ISendObserver> sendables = new HashSet<ISendObserver>();
		UserSMSTiming userSMSTiming = UserSMSTiming.findByUsernoAndSmstypeFromCache(userno, type);
		if (userSMSTiming == null) {
			sendables.add(sendSMS);
			// 增加站内信发送方式
			sendables.add(sendLetterObserver);
			return sendables;
		} else {
			if (userSMSTiming.getNeedToSend() != null && userSMSTiming.getNeedToSend() == 0) {
				return sendables;
			}
			SendType[] sendTypes = SendType.values();
			for (SendType sendType : sendTypes) {
				SendChannel sendChannel = SendChannel.findByUserSMStimingIdAndSendtypeFromCache(userSMSTiming.getId(),
						sendType.getValue());
				if (sendChannel == null) {
					Integer needToSend = 0;
					if (sendType.getValue() == SendType.MESSAGE.getValue()) {
						needToSend = 1;
					}
					sendChannel = SendChannel.createSendChannel(userSMSTiming.getId(), needToSend, sendType.getValue());
				}
				if (sendChannel.getNeedToSend().intValue() == 1) {
					if (sendChannel.getSendtype().equals(SendType.MESSAGE.getValue())) {
						sendables.add(sendSMS);
					}
					if (sendChannel.getSendtype().equals(SendType.EMAIL.getValue())) {
						sendables.add(sendEmail);
					}
					if (sendChannel.getSendtype().equals(SendType.IPHONE.getValue())) {
						sendables.add(sendIOSMsg);
					}
				}
			}
			// 增加站内信发送方式
			sendables.add(sendLetterObserver);
		}
		return sendables;
	}

	@Transactional
	public void updateUserSMSTimingBatch(String json) {
		if (StringUtils.isBlank(json)) {
			throw new IllegalArgumentException("the argument json is required");
		}
		List<UserSettingDTO> list = (List<UserSettingDTO>) UserSettingDTO.fromJsonArrayToUserSettingDTO(json);
		for (UserSettingDTO dto : list) {
			if (dto != null) {
				// UserSMSTiming设置
				UserSMSTiming userSMSTiming = dto.getUserSMSTiming();
				if (userSMSTiming != null) {
					Long id = userSMSTiming.getId();
					Integer needToSend = userSMSTiming.getNeedToSend();
					if (id != null && needToSend != null) {
						UserSMSTiming timing = UserSMSTiming.findUserSMSTiming(id);
						if (timing != null) {
							if (timing.getNeedToSend() == null || timing.getNeedToSend() != needToSend) {
								timing.updateNeedToSend(needToSend);
							}
						}
					}
				}
				// SendChannel设置
				List<SendChannel> sendChannels = dto.getSendChannels();
				if (sendChannels != null && sendChannels.size() > 0) {
					for (SendChannel channel : sendChannels) {
						if (channel != null) {
							Long id = channel.getId();
							Integer needToSend = channel.getNeedToSend();
							if (id != null && needToSend != null) {
								SendChannel sendChannel = SendChannel.findSendChannel(id);
								if (sendChannel != null) {
									if (sendChannel.getNeedToSend() == null
											|| sendChannel.getNeedToSend() != needToSend) {
										sendChannel.setNeedToSend(needToSend);
										sendChannel.setModifyTime(new Date());
										sendChannel.merge();
										new SendChannel().memcachedService.set(
												"SendChannel" + sendChannel.getUserSMStimingId()
														+ sendChannel.getSendtype(), sendChannel);
									}
								}
							}
						}
					}
				}
			}
		}
	}
}
