package com.ruyicai.msgcenter.consts;

import java.math.BigDecimal;

public enum JmsserviceType {

	newissue(1, "新期"),
	
	endissue(2, "期结"),
	
	openlottery(3, "开奖"),
	
	betsuccess(4, "投注成功"),
	
	tlotencash(5, "tlot兑奖成功"),
	
	torderencash(6, "torder兑奖成功"),
	
	addnum(7, "追号"),
	
	prizesms(8, "中奖短信"),
	
	encashend(9, "派奖结束"),
	
	opensms(10, "开奖短信"),
	
	tordersave(11, "torder保存成功"),
	
	senddelaysms(12,"发送延迟短信");
	
	public BigDecimal value;
	
	public String memo;
	
	JmsserviceType(int value, String memo) {
		this.value = new BigDecimal(value);
		this.memo = memo;
	}
}
