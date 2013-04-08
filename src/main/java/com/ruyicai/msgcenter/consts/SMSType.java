package com.ruyicai.msgcenter.consts;

import java.math.BigDecimal;

public enum SMSType {

	OPEN(1), // 开奖短信

	HIT(2), // 中奖短信

	CASELOTHIT(10), // 合买中奖短信

	ADDNUMEND(3), // 追号剩余3期提醒

	ADDNUM(4), // 追号失败短信

	// HITSMALL(2, "hitsmall"), // 中小奖短信

	// HITBIG(3, "hitbig"), // 中大奖短信

	// HITSMALLGIFT(4, "hitsmallgift"), // 中小奖赠送短信

	// HITBIGGIFT(5, "hitbiggift"), // 中大奖赠送短信

	// GIFT(6, "gift"), // 赠送短信

	TAOCAN(8), // 套餐金额不足短信

	DELAY(9); // 延迟的短信

	private BigDecimal value;

	public BigDecimal getValue() {
		return value;
	}

	private SMSType(int value) {
		this.value = new BigDecimal(value);
	}
}
