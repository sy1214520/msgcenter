package com.ruyicai.msgcenter.util;

import java.util.Vector;

public class LotteryAlgorithmUtil {

	// 获取双色球投注类型
	public static char getDoubleBallType(int redBallCount, int blueBallCount) {
		char type = '0';
		if (redBallCount == 6 && blueBallCount == 1) {
			type = 'S'; // 单式投注
		} else if ((redBallCount >= 7 && redBallCount <= 20) && blueBallCount == 1) {
			type = 'R'; // 红复式
		} else if (redBallCount == 6 && (blueBallCount >= 2 && blueBallCount <= 16)) {
			type = 'B'; // 蓝复式
		} else if ((redBallCount >= 7 && redBallCount <= 20) && (blueBallCount >= 2 && blueBallCount <= 16)) {
			type = 'D'; // 双复式 或者红蓝复式
		}
		return type;
	}

	/**
	 * 将输入的注码转换成数组
	 * 
	 * @param strArray
	 *            输入参数:注码数组,格式为字符串
	 * @return 输出参数:注码数组
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Vector getStringArrayFromString(String strArray) {
		try {
			Vector v = new Vector();
			int l = strArray.length();
			int h = l / 2;
			int n = 0;
			for (int i = 0; i < h; i++) {
				String ss = strArray.substring(n, n + 2);
				n = n + 2;
				v.add(ss);
			}
			return v;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 将注码数组转换成带","的字符串
	 * 
	 * @param stringArray
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	public static String joinStringArrayWithComma(Vector v) {
		String resultStr = "";
		if (v == null || v.size() == 0) {
			return "";
		} else {
			for (int i = 0; i < v.size(); i++) {
				resultStr += v.get(i) + ",";
			}
			if (resultStr.charAt(resultStr.length() - 1) == ',') {
				resultStr = resultStr.substring(0, resultStr.length() - 1);
			}
			return resultStr;
		}
	}

	/**
	 * 将3D注码中的"0"去掉
	 * 
	 * @param str
	 * @return
	 */
	public static String removeZero3D(String str) {
		StringBuffer sBuffer = new StringBuffer();
		int j = 1;
		for (int i = 0; i < str.length() / 2; i++) {
			sBuffer.append(str.substring(j, j + 1));
			j += 2;
		}
		return sBuffer.toString();
	}

	/**
	 * 组合公式
	 * 
	 * @param m
	 * @param n
	 * @return
	 */
	public static long zuhe(int m, int n) {
		long t_a = 0L;
		long total = 1L;
		int temp = n;
		for (int i = 0; i < m; i++) {
			total = total * temp;
			temp--;
		}
		t_a = total / jiec(m);
		return t_a;
	}

	/**
	 * 求阶乘
	 * 
	 * @param a
	 * @return
	 */
	public static long jiec(int a) {
		long t_a = 1L;
		for (long i = 1; i <= a; i++) {
			t_a = t_a * i;
		}
		return t_a;
	}

}
