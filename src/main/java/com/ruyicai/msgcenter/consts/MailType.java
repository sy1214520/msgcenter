package com.ruyicai.msgcenter.consts;

import java.math.BigDecimal;

public enum MailType {

	HITBIG(1, "大奖邮件"),
	
	BETFAIL(2, "投注失败"),
	
	DOSUCCESSERROR(2, "成功处理Error");

	BigDecimal state;

	String memo;

	public BigDecimal value() {
		return state;
	}

	public String memo() {
		return memo;
	}

	MailType(int val, String memo) {
		this.state = new BigDecimal(val);
		this.memo = memo;
	}
}
