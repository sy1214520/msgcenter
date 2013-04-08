package com.ruyicai.msgcenter.util;

import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

public class VerifyUtil {

	private static final String regEmail = "^[_\\-a-z0-9A-Z]*?[\\._\\-a-z0-9]*?[a-z0-9]+@[a-z0-9]+[a-z0-9\\-]*?[a-z0-9]+\\.[\\.a-z0-9]*$";

	private static final String regAmount = "[0-9]+";

	/**
	 * 验证是否邮箱
	 * 
	 * @param email
	 * @return
	 */
	public static boolean isEmail(String email) {

		if (StringUtils.isEmpty(email)) {

			return false;
		}
		if (Pattern.matches(regEmail, email)) {

			return true;
		}
		return false;
	}

	/**
	 * 验证是否手机号
	 * 
	 * @param phone
	 * @return
	 */
	public static boolean isPhone(String phone) {

		return true;
		/*
		 * if(StringUtils.isEmpty(phone)) {
		 * 
		 * return false; } if(Pattern.matches(regPhone, phone)) {
		 * 
		 * return true; } return false;
		 */
	}

	/**
	 * 验证是否为金额
	 * 
	 * @param amount
	 * @return
	 */
	public static boolean isInt(String amount) {
		if (StringUtils.isBlank(amount)) {
			return false;
		}
		if (Pattern.matches(regAmount, amount)) {

			return true;
		}
		return false;
	}

	/**
	 * 将token每8个字符添加一个空格
	 * 
	 * @param token
	 * @return
	 */
	public static String formatToken(String token) {
		StringBuilder sb = new StringBuilder();
		for (int i = 1; i <= token.length(); i++) {
			sb.append(token.charAt(i - 1));
			if (i % 8 == 0) {
				sb.append(" ");
			}
		}
		return sb.toString().trim();
	}
}
