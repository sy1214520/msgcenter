package com.ruyicai.msgcenter.consts;

import java.math.BigDecimal;

public enum SendState {

	SEND_WAIT(0, "待发送"), SEND_SUCCESS(1, "发送成功"), SEND_FAILURE(2, "发送失败"), SMS_SENDING(3, "正在发送");

	BigDecimal state;

	String memo;

	public int intValue() {
		return state.intValue();
	}

	public BigDecimal value() {
		return state;
	}

	public String memo() {
		return memo;
	}

	SendState(int val, String memo) {
		this.state = new BigDecimal(val);
		this.memo = memo;
	}
}
