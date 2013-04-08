package com.ruyicai.msgcenter.lot.lottype;

import org.springframework.stereotype.Component;

import com.ruyicai.msgcenter.lot.AbstractLot;

@Component("F47103")
public class ThreeD extends AbstractLot {

	@Override
	public String getOpenSMSWincode(String winbasecode, String winspecialcode) {
		String wincode = winbasecode;
		String winNumber = wincode.substring(1, 2);
		winNumber += wincode.substring(3, 4);
		winNumber += wincode.substring(5, 6);
		return winNumber;
	}

	@Override
	public String getGiveSMSWincode(String betcode) {
		String winNumber = betcode.substring(4, 5);
		winNumber += betcode.substring(6, 7);
		winNumber += betcode.substring(8, 9);
		return winNumber;
	}
}
