package com.ruyicai.msgcenter.lot;

import org.springframework.stereotype.Service;

@Service
public interface Lot {

	/**
	 * 对于需要下发中奖短信的彩种，返回短信所需的格式
	 * 
	 * @param winbasecode
	 * @param winspecialcode
	 * @return
	 */
	String getOpenSMSWincode(String winbasecode, String winspecialcode);

	/**
	 * 返回赠彩短信所需的格式
	 * 
	 * @param winbasecode
	 * @param winspecialcode
	 * @return
	 */
	String getGiveSMSWincode(String betcode);
}
