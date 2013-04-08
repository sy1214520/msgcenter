package com.ruyicai.msgcenter.consts;

public enum SendType {

	MESSAGE(0, "sms"), // 短信

	EMAIL(1, "email"), // 邮件

	IPHONE(2, "IOS"); // IOS消息

	private Integer value;

	private String memo;

	public Integer getValue() {
		return value;
	}

	public String getMemo() {
		return memo;
	}

	private SendType(int value, String memo) {
		this.value = value;
		this.memo = memo;
	}

	public static boolean isInThis(int value) {
		SendType[] sendTypes = values();
		for (SendType sendType : sendTypes) {
			if (sendType.value == value) {
				return true;
			}
		}
		return false;
	}
}
