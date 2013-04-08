package com.ruyicai.msgcenter.observers;

import com.ruyicai.msgcenter.consts.Const;

public abstract class AbstractSendObserver implements ISendObserver {

	protected String getChannelName(String channel) {
		if ("545".equals(channel) || "651".equals(channel)) {
			return Const.menWangOldSMSServiceProvider;
		} else {
			return Const.defaultSMSChannel;
		}
	}
}
