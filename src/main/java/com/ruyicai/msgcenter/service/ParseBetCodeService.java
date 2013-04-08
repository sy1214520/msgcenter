package com.ruyicai.msgcenter.service;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruyicai.msgcenter.util.LotteryAlgorithmUtil;

@Service
public class ParseBetCodeService {

	@Autowired
	private LotteryService lotteryService;

	/**
	 * 解析注码
	 * 
	 * @param lotNo
	 *            彩种
	 * @param betCode
	 *            数据库的注码
	 * @return
	 */
	public JSONArray parseBetCode(String lotNo, String betCode, String multiple) {
		JSONArray jsonArray = new JSONArray();
		if ("F47104".equals(lotNo)) { // 双色球
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("lotName", "双色球");
			String[] betCodes = new String[] { "" };
			if (betCode.indexOf("!") > -1) {
				betCodes = betCode.split("!");
			} else {
				betCodes = betCode.split("\\^");
			}
			for (String code : betCodes) {
				if (code.length() > 4) {
					String play = "0"; // 投注类型
					jsonObject.put("multiple", code.substring(2, 4)); // 倍数
					if (code.trim().substring(0, 2).equals("00")) {
						play = "DS"; // 单式
						jsonObject.put("play", "单式");
					} else if (code.trim().substring(0, 2).equals("10") || code.trim().substring(0, 2).equals("20")
							|| code.trim().substring(0, 2).equals("30")) {
						play = "FS"; // 复式
						jsonObject.put("play", "复式");
					} else if (code.trim().substring(0, 2).equals("40") || code.trim().substring(0, 2).equals("50")) { // 胆拖
						play = "DT";
						jsonObject.put("play", "胆拖");
					}
					if (play.trim().equals("DS")) { // 单式
						String red = LotteryAlgorithmUtil.joinStringArrayWithComma(LotteryAlgorithmUtil
								.getStringArrayFromString(code.substring(4, code.indexOf('~'))));
						if (betCode.indexOf("!") > -1) {
							String blue = code.substring(code.indexOf('~') + 1, code.indexOf("^"));
							jsonObject.put("betCode", "红球:" + red + "蓝球:" + blue); // 解析后的注码
						} else {
							String blue = code.substring(code.indexOf('~') + 1);
							jsonObject.put("betCode", "红球:" + red + "蓝球:" + blue); // 解析后的注码
						}
					} else if (play.trim().equals("FS")) {
						String red = LotteryAlgorithmUtil.joinStringArrayWithComma(LotteryAlgorithmUtil
								.getStringArrayFromString(code.substring(code.indexOf('*') + 1, code.indexOf('~'))));
						String blue = LotteryAlgorithmUtil.joinStringArrayWithComma(LotteryAlgorithmUtil
								.getStringArrayFromString(code.substring(code.indexOf('~') + 1)));
						jsonObject.put("betCode", "红球:" + red + "蓝球:" + blue); // 解析后的注码
					} else if (play.trim().equals("DT")) { // 胆拖
						String redDanma = LotteryAlgorithmUtil.joinStringArrayWithComma(LotteryAlgorithmUtil
								.getStringArrayFromString(code.substring(4, code.indexOf("*"))));
						String redTuoma = LotteryAlgorithmUtil.joinStringArrayWithComma(LotteryAlgorithmUtil
								.getStringArrayFromString(code.substring(code.indexOf("*") + 1, code.indexOf("~"))));
						String blue = LotteryAlgorithmUtil.joinStringArrayWithComma(LotteryAlgorithmUtil
								.getStringArrayFromString(code.substring(code.indexOf("~") + 1)));
						jsonObject.put("betCode", "红球胆码:" + redDanma + "红球拖码:" + redTuoma + "蓝球:" + blue);
					} else {
						jsonObject.put("multiple", multiple); // 倍数
						jsonObject.put("play", "未知");
						jsonObject.put("betCode", code);
					}
				}
				jsonArray.add(jsonObject);
			}
		} else if ("F47103".equals(lotNo)) { // 福彩3D
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("lotName", "福彩3D");
			if (betCode.length() > 4) {
				String play = "0"; // 投注类型
				if (betCode.substring(0, 2).equals("20")) { // 直选复式
															// 20020102^0106^0100^
					jsonObject.put("multiple", betCode.substring(2, 4)); // 倍数
					play = "ZHF";
					jsonObject.put("play", "直选复式");
					String[] str = betCode.split("\\^");
					String bai = LotteryAlgorithmUtil.removeZero3D(str[0].substring(6));
					String shi = LotteryAlgorithmUtil.removeZero3D(str[1].substring(2));
					String ge = LotteryAlgorithmUtil.removeZero3D(str[2].substring(2));
					jsonObject.put("betCode", "百位:" + bai + " 十位:" + shi + " 个位:" + ge);
					jsonArray.add(jsonObject);
					return jsonArray;
				}
				String[] betCodes = new String[] { "" };
				if (betCode.indexOf("!") > -1) {
					betCodes = betCode.split("!");
				} else {
					betCodes = betCode.split("\\^");
				}
				for (String code : betCodes) {
					jsonObject.put("multiple", betCode.substring(2, 4)); // 倍数
					if (code.substring(0, 2).equals("00")) { // 单选单式
						play = "DXD";
						jsonObject.put("play", "单选单式");
					} else if (code.substring(0, 2).equals("01")) { // 组3单式
						play = "Z3D";
						jsonObject.put("play", "组3单式");
					} else if (code.substring(0, 2).equals("02")) { // 组6单式
						play = "Z6D";
						jsonObject.put("play", "组6单式");
					} else if (code.substring(0, 2).equals("31")) { // 组3复式
						play = "Z3F";
						jsonObject.put("play", "组3复式");
					} else if (code.substring(0, 2).equals("32")) { // 组6复式
						play = "Z6F";
						jsonObject.put("play", "组6复式");
					} else if (code.substring(0, 2).equals("34")) { // 单选单复式
						play = "DXDF";
						jsonObject.put("play", "单选单复式");
					} else if (code.substring(0, 2).equals("54")) { // 胆拖
						play = "DT";
						jsonObject.put("play", "胆拖");
					} else if (code.substring(0, 2).equals("10")) { // 直选和值
						play = "ZXHZ";
						jsonObject.put("play", "直选和值");
					} else if (code.substring(0, 2).equals("11")) { // 组3和值
						play = "Z3HZ";
						jsonObject.put("play", "组3和值");
					} else if (code.substring(0, 2).equals("12")) { // 组6和值
						play = "Z6HZ";
						jsonObject.put("play", "组6和值");
					}
					if (play.trim().equals("DXD") || play.trim().equals("Z3D") || play.trim().equals("Z6D")) { // 单式
						String bet_code = LotteryAlgorithmUtil.joinStringArrayWithComma(LotteryAlgorithmUtil
								.getStringArrayFromString(code.substring(4, code.length())));
						jsonObject.put("betCode", bet_code);
					} else if (play.trim().equals("Z3F") || play.trim().equals("Z6F") || play.trim().equals("DXDF")) { // 复式
						String bet_code = LotteryAlgorithmUtil.joinStringArrayWithComma(LotteryAlgorithmUtil
								.getStringArrayFromString(code.substring(6)));
						jsonObject.put("betCode", bet_code);
					} else if (play.trim().equals("DT")) {
						String danMa = LotteryAlgorithmUtil.joinStringArrayWithComma(LotteryAlgorithmUtil
								.getStringArrayFromString(code.substring(4, code.indexOf("*")))); // 胆码
						String tuoMa = LotteryAlgorithmUtil.joinStringArrayWithComma(LotteryAlgorithmUtil
								.getStringArrayFromString(code.substring(code.indexOf("*") + 1))); // 拖码
						jsonObject.put("betCode", "胆码:" + danMa + "拖码:" + tuoMa);
					} else if (play.trim().equals("ZXHZ") || play.trim().equals("Z3HZ") || play.trim().equals("Z6HZ")) {
						String bet_code = LotteryAlgorithmUtil.joinStringArrayWithComma(LotteryAlgorithmUtil
								.getStringArrayFromString(code.substring(4, code.length())));
						jsonObject.put("betCode", bet_code);
					} else {
						jsonObject.put("multiple", multiple); // 倍数
						jsonObject.put("play", "未知");
						jsonObject.put("betCode", code);
					}
					jsonArray.add(jsonObject);
				}
			}
		} else if ("F47102".equals(lotNo)) { // 七乐彩
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("lotName", "七乐彩");
			if (betCode.length() > 4) {
				String[] betCodes = new String[] { "" };
				if (betCode.indexOf("!") > -1) {
					betCodes = betCode.split("!");
				} else {
					betCodes = betCode.split("\\^");
				}
				for (String code : betCodes) {
					jsonObject.put("multiple", code.substring(2, 4)); // 倍数
					if (code.substring(0, 2).equals("00")) { // 单式
						jsonObject.put("play", "单式");
						if (betCode.indexOf("!") > -1) {
							String bet_code = LotteryAlgorithmUtil.joinStringArrayWithComma(LotteryAlgorithmUtil
									.getStringArrayFromString(code.substring(4, code.indexOf("^"))));
							jsonObject.put("betCode", bet_code);
						} else {
							String bet_code = LotteryAlgorithmUtil.joinStringArrayWithComma(LotteryAlgorithmUtil
									.getStringArrayFromString(code.substring(4)));
							jsonObject.put("betCode", bet_code);
						}
					} else if (code.substring(0, 2).equals("10")) { // 复式
						jsonObject.put("play", "复式");
						String bet_code = LotteryAlgorithmUtil.joinStringArrayWithComma(LotteryAlgorithmUtil
								.getStringArrayFromString(code.substring(5)));
						jsonObject.put("betCode", bet_code);
					} else if (code.substring(0, 2).equals("20")) { // 胆拖
						jsonObject.put("play", "胆拖");
						String danMa = LotteryAlgorithmUtil.joinStringArrayWithComma(LotteryAlgorithmUtil
								.getStringArrayFromString(code.substring(4, code.indexOf("*")))); // 胆码
						String tuoMa = LotteryAlgorithmUtil.joinStringArrayWithComma(LotteryAlgorithmUtil
								.getStringArrayFromString(code.substring(code.indexOf("*") + 1))); // 拖码
						jsonObject.put("betCode", "胆码:" + danMa + "拖码:" + tuoMa);
					} else {
						jsonObject.put("multiple", multiple); // 倍数
						jsonObject.put("play", "未知");
						jsonObject.put("betCode", code);
					}
					jsonArray.add(jsonObject);
				}
			}
		} else if ("T01001".equals(lotNo)) { // 超级大乐透
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("lotName", "超级大乐透");
			jsonObject.put("multiple", multiple); // 倍数
			String[] betCodes = new String[] { "" };
			if (betCode.indexOf("!") > -1) {
				betCodes = betCode.split("!");
			} else {
				betCodes = betCode.split(";");
			}
			for (String code : betCodes) {
				if (code.indexOf("-") > -1) { // 单式或复式或胆拖
					if (code.indexOf("$") > -1) { // 胆拖
						jsonObject.put("play", "胆拖");
						String[] qianHou = code.split("-");
						String qianQuDanMa = ""; // 前驱胆码
						String qianQuTuoMa = ""; // 前驱拖码
						String houQuDanMa = ""; // 后驱胆码
						String houQuTuoMa = ""; // 后驱拖码
						// 前区
						if (qianHou[0].indexOf("$") > -1) {
							String[] qianQu = qianHou[0].split("\\$");
							qianQuDanMa = qianQu[0].replaceAll(" ", ","); // 前驱胆码
							qianQuTuoMa = qianQu[1].replaceAll(" ", ","); // 前驱拖码
						} else {
							qianQuTuoMa = qianHou[0].replace(" ", ",");
						}
						// 后区
						if (qianHou[1].indexOf("$") > -1) {
							String[] houQu = qianHou[1].split("\\$");
							houQuDanMa = houQu[0].replaceAll(" ", ",");
							houQuTuoMa = houQu[1].replaceAll(" ", ",");
						} else {
							houQuTuoMa = qianHou[1].replaceAll(" ", ",");
						}
						jsonObject.put("betCode", "前区胆码:" + qianQuDanMa + "\n前区拖码:" + qianQuTuoMa + "\n后区胆码:"
								+ houQuDanMa + "\n后区拖码:" + houQuTuoMa);
					} else { // 单式或复式
						String[] qianHou = code.split("-");
						String[] qian = qianHou[0].split(" ");
						String[] hou = qianHou[1].split(" ");
						if (qian.length == 5 && hou.length == 2) {
							jsonObject.put("play", "单式");
						} else {
							jsonObject.put("play", "复式");
						}
						jsonObject.put("betCode",
								qianHou[0].replaceAll(" ", ",") + "+" + qianHou[1].replaceAll(" ", ","));
					}
				} else { // 生肖乐
					String[] split = code.split(" ");
					if (split.length == 2) {
						jsonObject.put("play", "生肖乐单式");
					} else {
						jsonObject.put("play", "生肖乐复式");
					}
					jsonObject.put("betCode", code.replaceAll(" ", ","));
				}
				jsonArray.add(jsonObject);
			}
		} else if ("T01002".equals(lotNo)) { // 排列三
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("lotName", "排列三");
			jsonObject.put("multiple", multiple); // 倍数
			String[] betCodes = new String[] { "" };
			if (betCode.indexOf("!") > -1) {
				betCodes = betCode.split("!");
			} else {
				betCodes = betCode.split(";");
			}
			for (String code : betCodes) {
				if (betCode.indexOf("|") > -1) {
					String[] split = code.split("\\|");
					if (split[0].equals("1")) { // 直选
						jsonObject.put("play", "直选");
					} else if (split[0].equals("6")) { // 组选
						jsonObject.put("play", "组选");
					} else if (split[0].equals("S1")) { // 直选和值
						jsonObject.put("play", "直选和值");
					} else if (split[0].equals("S9")) { // 组选和值
						jsonObject.put("play", "组选和值");
					} else if (split[0].equals("S3")) { // 组3和值
						jsonObject.put("play", "组3和值");
					} else if (split[0].equals("S6")) { // 组6和值
						jsonObject.put("play", "组6和值");
					} else if (split[0].equals("F3")) { // 组3包号
						jsonObject.put("play", "组3包号");
					} else if (split[0].equals("F6")) { // 组6包号
						jsonObject.put("play", "组6包号");
					} else {
						jsonObject.put("play", "未知");
					}
					jsonObject.put("betCode", split[1]);
				} else {
					jsonObject.put("play", "未知");
					jsonObject.put("betCode", code);
				}
				jsonArray.add(jsonObject);
			}
		} else if ("T01007".equals(lotNo)) { // 时时彩
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("lotName", "时时彩");
			jsonObject.put("multiple", multiple); // 倍数
			String[] betCodes = new String[] { "" };
			if (betCode.indexOf("!") > -1) {
				betCodes = betCode.split("!");
			} else {
				betCodes = betCode.split(";");
			}
			for (String code : betCodes) {
				if (betCode.indexOf("|") > -1) {
					String[] split = code.split("\\|");
					if (split[0].equals("5D")) { // 五星
						jsonObject.put("play", "五星");
					} else if (split[0].equals("3D")) { // 三星
						jsonObject.put("play", "三星");
					} else if (split[0].equals("2D")) { // 二星
						jsonObject.put("play", "二星");
					} else if (split[0].equals("1D")) { // 一星
						jsonObject.put("play", "一星");
					} else if (split[0].equals("5F")) { // 五星复选
						jsonObject.put("play", "五星复选");
					} else if (split[0].equals("5T")) { // 五星通选
						jsonObject.put("play", "五星通选");
					} else if (split[0].equals("3F")) { // 三星复选
						jsonObject.put("play", "三星复选");
					} else if (split[0].equals("2F")) { // 二星复选
						jsonObject.put("play", "二星复选");
					} else if (split[0].equals("H2")) { // 二星和值
						jsonObject.put("play", "二星和值");
					} else if (split[0].equals("S2")) { // 二星组选和值
						jsonObject.put("play", "二星组选和值");
					} else if (split[0].equals("DD")) { // 大小单双
						jsonObject.put("play", "大小单双");
						String shi = split[1].substring(0, 1); // 大小
						String ge = split[1].substring(1, 2); // 单双
						String shiStr = "";
						if (shi.equals("2")) {
							shiStr = "大";
						} else if (shi.equals("1")) {
							shiStr = "小";
						} else if (shi.equals("5")) {
							shiStr = "单";
						} else if (shi.equals("4")) {
							shiStr = "双";
						}
						String geStr = "";
						if (ge.equals("2")) {
							geStr = "大";
						} else if (ge.equals("1")) {
							geStr = "小";
						} else if (ge.equals("5")) {
							geStr = "单";
						} else if (ge.equals("4")) {
							geStr = "双";
						}
						jsonObject.put("betCode", "十位:" + shiStr + "|个位:" + geStr);
						jsonArray.add(jsonObject);
						continue;
					} else if (split[0].equals("Z2")) { // 二星组选
						jsonObject.put("play", "二星组选");
					} else if (split[0].equals("F2")) { // 二星组选复式
						jsonObject.put("play", "二星组选复式");
					} else {
						jsonObject.put("play", "未知");
					}
					jsonObject.put("betCode", split[1]);
				} else {
					jsonObject.put("play", "未知");
					jsonObject.put("betCode", code);
				}
				jsonArray.add(jsonObject);
			}
		} else if ("T01009".equals(lotNo)) { // 七星彩
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("lotName", "七星彩");
			jsonObject.put("multiple", multiple); // 倍数
			String[] betCodes = new String[] { "" };
			if (betCode.indexOf("!") > -1) {
				betCodes = betCode.split("!");
			} else {
				betCodes = betCode.split(";");
			}
			for (String code : betCodes) {
				if (code.indexOf(",") > -1) {
					boolean isD = true;
					String[] split = code.split(",");
					for (int i = 0; i < split.length; i++) {
						if (split[i].length() > 1) {
							isD = false;
							break;
						}
					}
					if (isD) {
						jsonObject.put("play", "单式");
					} else {
						jsonObject.put("play", "复式");
					}
				} else {
					jsonObject.put("play", "单式");
				}
				jsonObject.put("betCode", code);
				jsonArray.add(jsonObject);
			}
		} else if ("T01011".equals(lotNo)) { // 排列五
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("lotName", "排列五");
			jsonObject.put("multiple", multiple); // 倍数
			String[] betCodes = new String[] { "" };
			if (betCode.indexOf("!") > -1) {
				betCodes = betCode.split("!");
			} else {
				betCodes = betCode.split(";");
			}
			for (String code : betCodes) {
				if (code.indexOf(",") > -1) {
					boolean isD = true;
					String[] split = code.split(",");
					for (int i = 0; i < split.length; i++) {
						if (split[i].length() > 1) {
							isD = false;
							break;
						}
					}
					if (isD) {
						jsonObject.put("play", "单式");
					} else {
						jsonObject.put("play", "复式");
					}
				} else {
					jsonObject.put("play", "单式");
				}
				jsonObject.put("betCode", code);
				jsonArray.add(jsonObject);
			}
		} else if ("T01010".equals(lotNo)) { // 十一选五
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("lotName", "11选5");
			jsonObject.put("multiple", multiple); // 倍数
			String[] betCodes = new String[] { "" };
			if (betCode.indexOf("!") > -1) {
				betCodes = betCode.split("!");
			} else {
				betCodes = betCode.split(";");
			}
			for (String code : betCodes) {
				if (code.indexOf("$") > -1) { // 胆拖
					if (code.indexOf("|") > -1) {
						String[] split = code.split("\\|");
						if (split[0].equals("R2")) { // 任选二胆拖
							jsonObject.put("play", "任选二胆拖");
						} else if (split[0].equals("R3")) { // 任选三胆拖
							jsonObject.put("play", "任选三胆拖");
						} else if (split[0].equals("R4")) { // 任选四胆拖
							jsonObject.put("play", "任选四胆拖");
						} else if (split[0].equals("R5")) { // 任选五胆拖
							jsonObject.put("play", "任选五胆拖");
						} else if (split[0].equals("R6")) { // 任选六胆拖
							jsonObject.put("play", "任选六胆拖");
						} else if (split[0].equals("R7")) { // 任选七胆拖
							jsonObject.put("play", "任选七胆拖");
						} else if (split[0].equals("R8")) { // 任选八胆拖
							jsonObject.put("play", "任选八胆拖");
						} else if (split[0].equals("Q2")) { // 选前二直选胆拖
							jsonObject.put("play", "选前二直选胆拖");
						} else if (split[0].equals("Q3")) { // 选前三直选胆拖
							jsonObject.put("play", "选前三直选胆拖");
						} else if (split[0].equals("Z2")) { // 选前二组选胆拖
							jsonObject.put("play", "选前二组选胆拖");
						} else if (split[0].equals("Z3")) { // 选前三组选胆拖
							jsonObject.put("play", "选前三组选胆拖");
						} else {
							jsonObject.put("play", "未知");
						}
						jsonObject.put("betCode", split[1]);
					}
				} else {
					if (code.indexOf("|") > -1) {
						String[] split = code.split("\\|");
						if (split[0].equals("R1")) { // 任选一
							jsonObject.put("play", "任选一");
						} else if (split[0].equals("R2")) { // 任选二
							jsonObject.put("play", "任选二");
						} else if (split[0].equals("R3")) { // 任选三
							jsonObject.put("play", "任选三");
						} else if (split[0].equals("R4")) { // 任选四
							jsonObject.put("play", "任选四");
						} else if (split[0].equals("R5")) { // 任选五
							jsonObject.put("play", "任选五");
						} else if (split[0].equals("R6")) { // 任选六
							jsonObject.put("play", "任选六");
						} else if (split[0].equals("R7")) { // 任选七
							jsonObject.put("play", "任选七");
						} else if (split[0].equals("R8")) { // 任选八
							jsonObject.put("play", "任选八");
						} else if (split[0].equals("Q2")) { // 选前二直选
							jsonObject.put("play", "选前二直选");
						} else if (split[0].equals("Q3")) { // 选前三直选
							jsonObject.put("play", "选前三直选");
						} else if (split[0].equals("Z2")) { // 选前二组选
							jsonObject.put("play", "选前二组选");
						} else if (split[0].equals("Z3")) { // 选前三组选
							jsonObject.put("play", "选前三组选");
						} else {
							jsonObject.put("play", "未知");
						}
						jsonObject.put("betCode", split[1]);
					}
				}
				jsonArray.add(jsonObject);
			}
		} else if ("T01003".equals(lotNo)) { // 足球胜负彩
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("lotName", "足球胜负彩");
			jsonObject.put("multiple", multiple); // 倍数
			String[] betCodes = new String[] { "" };
			if (betCode.indexOf("!") > -1) {
				betCodes = betCode.split("!");
			} else {
				betCodes = betCode.split(";");
			}
			for (String code : betCodes) {
				if (code.indexOf(",") > -1) {
					boolean isD = true;
					String[] split = code.split(",");
					for (int i = 0; i < split.length; i++) {
						if (split[i].length() > 1) {
							isD = false;
							break;
						}
					}
					if (isD) {
						jsonObject.put("play", "单式");
					} else {
						jsonObject.put("play", "复式");
					}
				} else {
					jsonObject.put("play", "单式");
				}
				jsonObject.put("betCode", code);
				jsonArray.add(jsonObject);
			}
		} else if ("T01004".equals(lotNo)) { // 足球任九场
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("lotName", "足球任九场");
			jsonObject.put("multiple", multiple); // 倍数
			String[] betCodes = new String[] { "" };
			if (betCode.indexOf("!") > -1) {
				betCodes = betCode.split("!");
			} else {
				betCodes = betCode.split(";");
			}
			for (String code : betCodes) {
				if (code.indexOf("$") > -1) {
					jsonObject.put("play", "胆拖");
				} else {
					if (code.indexOf(",") > -1) {
						String[] split = code.split(",");
						boolean isD = true;
						for (int i = 0; i < split.length; i++) {
							if (split[i].length() > 1) {
								isD = false;
								break;
							}
						}
						if (isD) {
							jsonObject.put("play", "单式");
						} else {
							jsonObject.put("play", "复式");
						}
					} else {
						jsonObject.put("play", "单式");
					}
				}
				jsonObject.put("betCode", code);
				jsonArray.add(jsonObject);
			}
		} else if ("T01005".equals(lotNo)) { // 足球进球彩
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("lotName", "足球进球彩");
			jsonObject.put("multiple", multiple); // 倍数
			String[] betCodes = new String[] { "" };
			if (betCode.indexOf("!") > -1) {
				betCodes = betCode.split("!");
			} else {
				betCodes = betCode.split(";");
			}
			for (String code : betCodes) {
				if (code.indexOf(",") > -1) {
					String[] split = code.split(",");
					boolean isD = true;
					for (int i = 0; i < split.length; i++) {
						if (split[i].length() > 1) {
							isD = false;
							break;
						}
					}
					if (isD) {
						jsonObject.put("play", "单式");
					} else {
						jsonObject.put("play", "复式");
					}
				} else {
					jsonObject.put("play", "单式");
				}
				jsonObject.put("betCode", code);
				jsonArray.add(jsonObject);
			}
		} else if ("T01006".equals(lotNo)) { // 足球半全场
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("lotName", "足球半全场");
			jsonObject.put("multiple", multiple); // 倍数
			String[] betCodes = new String[] { "" };
			if (betCode.indexOf("!") > -1) {
				betCodes = betCode.split("!");
			} else {
				betCodes = betCode.split(";");
			}
			for (String code : betCodes) {
				if (code.indexOf(",") > -1) {
					String[] split = code.split(",");
					boolean isD = true;
					for (int i = 0; i < split.length; i++) {
						if (split[i].length() > 1) {
							isD = false;
							break;
						}
					}
					if (isD) {
						jsonObject.put("play", "单式");
					} else {
						jsonObject.put("play", "复式");
					}
				} else {
					jsonObject.put("play", "单式");
				}
				jsonObject.put("betCode", code);
				jsonArray.add(jsonObject);
			}
		} else if ("T01008".equals(lotNo)) { // 单场
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("lotName", "单场");
			jsonObject.put("multiple", multiple); // 倍数
			jsonObject.put("play", "未知");
			jsonObject.put("betCode", betCode);
			jsonArray.add(jsonObject);
		} else if ("T01012".equals(lotNo)) { // 十一运夺金
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("lotName", "十一运夺金");
			jsonObject.put("multiple", multiple); // 倍数
			jsonObject.put("play", "未知");
			jsonObject.put("betCode", betCode);
			jsonArray.add(jsonObject);
		} else if ("J00001".equals(lotNo)) { // 竞彩足球胜负平
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("lotName", "竞彩足球胜负平");
			jsonObject.put("multiple", multiple); // 倍数
			String[] split1 = betCode.split("@"); // 502@20111121|1|001|31^20111121|1|002|3^
			String wanfa = split1[0]; // 玩法
			if (wanfa != null && wanfa.trim().equals("500")) {
				jsonObject.put("play", "单关");
			} else if (wanfa != null && wanfa.trim().equals("502")) {
				jsonObject.put("play", "过关:2串1");
			} else if (wanfa != null && wanfa.trim().equals("503")) {
				jsonObject.put("play", "过关:3串1");
			} else if (wanfa != null && wanfa.trim().equals("504")) {
				jsonObject.put("play", "过关:4串1");
			} else if (wanfa != null && wanfa.trim().equals("505")) {
				jsonObject.put("play", "过关:5串1");
			} else if (wanfa != null && wanfa.trim().equals("506")) {
				jsonObject.put("play", "过关:6串1");
			} else if (wanfa != null && wanfa.trim().equals("507")) {
				jsonObject.put("play", "过关:7串1");
			} else if (wanfa != null && wanfa.trim().equals("508")) {
				jsonObject.put("play", "过关:8串1");
			} else {
				jsonObject.put("play", "未知");
			}
			StringBuffer sBuffer = new StringBuffer();
			String[] split2 = split1[1].split("\\^");
			for (String string : split2) {
				String[] split3 = string.split("\\|");
				String day = split3[0];
				String weekid = split3[1];
				String teamid = split3[2];
				String match = lotteryService.findJingCaiMatches(lotNo, day, weekid, teamid);
				if (match != null && !match.trim().equals("")) {
					JSONObject fromObject = JSONObject.fromObject(match);
					if (fromObject.has("errorCode") && fromObject.getString("errorCode").equals("0")) {
						JSONObject valueObject = (JSONObject) fromObject.get("value");
						JSONObject matchObject = (JSONObject) valueObject.get("matches");
						String team = matchObject.getString("team");
						String zhu = ""; // 主队
						String ke = ""; // 客队
						if (team != null && !team.trim().equals("") && !team.trim().equals("null")
								&& team.indexOf(":") > -1) {
							String[] split = team.split(":");
							zhu = split[0];
							ke = split[1];
						}
						sBuffer.append(zhu + "vs" + ke + ":" + split3[3] + ";").append("\n");
					}
				}
			}
			if (sBuffer.toString().endsWith("\n")) {
				jsonObject.put("betCode", sBuffer.toString().substring(0, sBuffer.toString().length() - 1));
			} else {
				jsonObject.put("betCode", sBuffer.toString());
			}
			jsonArray.add(jsonObject);
		} else { // 未知彩种
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("lotName", "未知");
			jsonObject.put("multiple", multiple); // 倍数
			jsonObject.put("play", "未知");
			jsonObject.put("betCode", betCode);
			jsonArray.add(jsonObject);
		}
		return jsonArray;
	}

}
