package com.ruyicai.msgcenter.consts;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum IOSType {

	IOS91("91", "91_cert.p12"),
	ruyicai("ruyicai", "ruyicai_cert.p12"),
	appStore("appStore", "appStore_cert.p12"),
	IOS91appStore("91appStore", "91appStore.p12"),
	ruyicai_guanfang("ruyicai_guanfang", "ruyicai_guangfang_production.p12"),
	ruyicai_cooperation("ruyicai_cooperation","boya_cooperation.p12"),
	ruyicai_happy_develop("ruyicai_happy_develop","happyRuyicai_develop.p12"),
	ruyicai_happy_product("ruyicai_happy_product","happyRuyicai_production.p12");

	public String key;

	public String value;

	IOSType(String key, String value) {
		this.key = key;
		this.value = value;
	}

	private static final Map<String, IOSType> lookup = new HashMap<String, IOSType>();

	static {
		for (IOSType s : EnumSet.allOf(IOSType.class)) {
			lookup.put(s.key, s);
		}
	}

	public static IOSType get(String key) {
		return lookup.get(key);
	}
}
