package com.ruyicai.msgcenter.observers;

import java.util.Map;

import com.ruyicai.lottery.domain.Tuserinfo;
import com.ruyicai.msgcenter.consts.SMSType;

public interface ISendObserver {
	public void sendMsg(Tuserinfo tuserinfo, SMSType smsType, Map<String, Object> params);
}