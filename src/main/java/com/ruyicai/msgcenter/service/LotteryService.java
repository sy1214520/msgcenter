package com.ruyicai.msgcenter.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ruyicai.lottery.domain.CaseLot;
import com.ruyicai.lottery.domain.CaselotDTO;
import com.ruyicai.lottery.domain.Torder;
import com.ruyicai.lottery.domain.Tsubscribe;
import com.ruyicai.lottery.domain.Tuserinfo;
import com.ruyicai.msgcenter.exception.RuyicaiException;
import com.ruyicai.msgcenter.util.ErrorCode;
import com.ruyicai.msgcenter.util.HttpUtil;
import com.ruyicai.msgcenter.util.JsonUtil;

@Service
public class LotteryService {

	private Logger logger = Logger.getLogger(LotteryService.class);

	@Autowired
	MemcachedService<String> memcachedService;

	@Value("${lotteryurl}")
	String lotteryurl;

	/**
	 * @param lotno
	 *            彩种
	 * @param batchcode
	 *            期号
	 * @param state
	 *            状态，Null则默认1
	 * @return List<Tuserinfo>
	 */
	@SuppressWarnings("unchecked")
	public List<String> findUsernosByLotnoAndBatchcodeFromTlot(String lotno, String batchcode, BigDecimal state) {
		if (StringUtils.isBlank(lotno)) {
			throw new IllegalArgumentException("the argument lotno is required");
		}
		if (StringUtils.isBlank(batchcode)) {
			throw new IllegalArgumentException("the argument batchcode is required");
		}
		if (state == null) {
			state = BigDecimal.ONE;
		}
		List<String> list = new ArrayList<String>();
		String url = lotteryurl + "/select/findUsernosByLotnoAndBatchcode?lotno=" + lotno + "&batchcode=" + batchcode
				+ "&state=" + state;
		try {
			String result = HttpUtil.getResultMessage(url.toString());
			list = JsonUtil.fromJsonToObject(result, ArrayList.class);
		} catch (Exception e) {
			logger.error("请求" + url + "失败" + e.getMessage(), e);
			throw new RuyicaiException("请求lottery失败", e);
		}
		return list;
	}

	/**
	 * @param lotno
	 *            彩种
	 * @param batchcode
	 *            期号
	 * @param state
	 *            状态，Null则默认4
	 * @return List<Tuserinfo>
	 */
	@SuppressWarnings("unchecked")
	public List<String> findStarterByLotnoAndBatchcodeFromCaselot(String lotno, String batchcode, BigDecimal state) {
		if (StringUtils.isBlank(lotno)) {
			throw new IllegalArgumentException("the argument lotno is required");
		}
		if (StringUtils.isBlank(batchcode)) {
			throw new IllegalArgumentException("the argument batchcode is required");
		}
		if (state == null) {
			state = new BigDecimal(4);
		}
		List<String> list = new ArrayList<String>();
		String url = lotteryurl + "/select/findStarterByLotnoAndBatchcode?lotno=" + lotno + "&batchcode=" + batchcode
				+ "&state=" + state;
		try {
			String result = HttpUtil.getResultMessage(url.toString());
			list = JsonUtil.fromJsonToObject(result, ArrayList.class);
		} catch (Exception e) {
			logger.error("请求" + url + "失败" + e.getMessage(), e);
			throw new RuyicaiException("请求lottery失败", e);
		}
		return list;
	}

	/**
	 * @param userno
	 *            用户编号
	 * @return Tuserinfo
	 */
	public Tuserinfo findTuserinfoByUserno(String userno) {
		if (StringUtils.isBlank(userno)) {
			throw new IllegalArgumentException("the argument userno is required");
		}
		Tuserinfo tuserinfo = null;
		String url = lotteryurl + "/tuserinfoes?find=ByUserno&json&userno=" + userno;
		try {
			String userJson = memcachedService.get(StringUtils.join(new String[]{"Tuserinfo", userno},"_"));
			if (StringUtils.isNotBlank(userJson)) {
				tuserinfo = Tuserinfo.fromJsonToTuserinfo(userJson);
			}
			if (tuserinfo != null) {
				logger.info("found user from cache,userno:" + userno);
				return tuserinfo;
			}
			logger.info("find user from lottery,userno:" + userno);
			String result = HttpUtil.getResultMessage(url.toString());
			if (StringUtils.isNotBlank(result)) {
				JSONObject jsonObject = new JSONObject(result);
				String errorCode = jsonObject.getString("errorCode");
				if (errorCode.equals(ErrorCode.OK.value)) {
					String value = jsonObject.getString("value");
					tuserinfo = Tuserinfo.fromJsonToTuserinfo(value);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("请求" + url + "失败" + e.getMessage());
			throw new RuyicaiException(ErrorCode.ERROR);
		}
		return tuserinfo;
	}

	public Tsubscribe selectTsubscribeByFlowno(String flowno) {
		if (StringUtils.isBlank(flowno)) {
			throw new IllegalArgumentException("the argument flowno is required");
		}
		logger.info("selectTsubscribeByFlowno flowno:" + flowno);
		String url = lotteryurl + "/select/getTsubscribeByFlowno?flowno=" + flowno;
		Tsubscribe tsubscribe = null;
		try {
			String result = HttpUtil.getResultMessage(url.toString());
			if (StringUtils.isNotBlank(result)) {
				JSONObject jsonObject = new JSONObject(result);
				String errorCode = jsonObject.getString("errorCode");
				if (errorCode.equals(ErrorCode.OK.value)) {
					String value = jsonObject.getString("value");
					if (StringUtils.isNotBlank(value)) {
						tsubscribe = Tsubscribe.fromJsonToTsubscribe(value);
					}
				}
			}
		} catch (Exception e) {
			logger.error("请求" + url + "失败" + e.getMessage(), e);
			throw new RuyicaiException("请求lottery失败", e);
		}
		return tsubscribe;
	}

	/**
	 * @param orderid
	 * @return Torder
	 */
	public Torder findTorderById(String orderid) {
		if (StringUtils.isBlank(orderid)) {
			throw new IllegalArgumentException("The orderid argument is required");
		}
		logger.info("findTorderById orderid:" + orderid);
		Torder torder = null;
		String url = lotteryurl + "/select/getTorder?orderid=" + orderid;
		try {
			String result = HttpUtil.getResultMessage(url.toString());
			if (StringUtils.isNotBlank(result)) {
				JSONObject jsonObject = new JSONObject(result);
				String errorCode = jsonObject.getString("errorCode");
				if (errorCode.equals(ErrorCode.OK.value)) {
					String value = jsonObject.getString("value");
					if (StringUtils.isNotBlank(value)) {
						torder = Torder.fromJsonToTorder(value);
					}
				} else {
					String value = jsonObject.getString("value");
					logger.error("errorCode:" + errorCode + ",value:" + value);
					throw new RuyicaiException("请求lottery失败");
				}
			}
		} catch (Exception e) {
			logger.error("请求" + url + "失败" + e.getMessage(), e);
			throw new RuyicaiException("请求lottery失败", e);
		}
		return torder;
	}

	/**
	 * 获取某场比赛的信息(供竟彩使用)
	 * 
	 * @param lotno
	 * @param day
	 * @param weekid
	 * @param teamid
	 * @return
	 */
	public String findJingCaiMatches(String lotno, String day, String weekid, String teamid) {
		String getIssueUrl = lotteryurl + "/select/getjingcaimatches";
		try {
			StringBuffer paramStr = new StringBuffer();
			paramStr.append("lotno=" + lotno);
			paramStr.append("&day=" + day);
			paramStr.append("&weekid=" + weekid);
			paramStr.append("&teamid=" + teamid);
			String resultStr = HttpUtil.post(getIssueUrl, paramStr.toString());
			logger.info("getjingcaimatches的返回结果:" + resultStr + ",lotno=" + lotno + ",day=" + day + ",weekid=" + weekid
					+ ",teamid=" + teamid);
			return resultStr;
		} catch (Exception e) {
			logger.error("getjingcaimatches发生异常:" + e.toString());
		}
		return "";
	}

	/**
	 * @param caselotid
	 *            合买编号
	 * @return CaseLot
	 */
	public CaseLot findCaseLotById(String caselotid) {
		if (StringUtils.isBlank(caselotid)) {
			throw new IllegalArgumentException("the argument userno is required");
		}
		logger.info("find caselot from lottery by id:" + caselotid);
		CaseLot caselot = null;
		String url = lotteryurl + "/select/selectCaseLotDetail?caselotid=" + caselotid;
		try {
			String result = HttpUtil.getResultMessage(url.toString());
			if (StringUtils.isNotBlank(result)) {
				JSONObject jsonObject = new JSONObject(result);
				String errorCode = jsonObject.getString("errorCode");
				if (errorCode.equals(ErrorCode.OK.value)) {
					String value = jsonObject.getString("value");
					CaselotDTO caselotDTO = CaselotDTO.fromJsonToCaselotDTO(value);
					if (caselotDTO.getCaseLot() != null) {
						caselot = caselotDTO.getCaseLot();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("请求" + url + "失败" + e.getMessage());
			throw new RuyicaiException("请求lottery失败");
		}
		return caselot;
	}
}
