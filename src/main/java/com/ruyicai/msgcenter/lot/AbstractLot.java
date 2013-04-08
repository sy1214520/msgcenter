package com.ruyicai.msgcenter.lot;

public abstract class AbstractLot implements Lot {

	@Override
	public String getOpenSMSWincode(String winbasecode, String winspecialcode) {
		return winbasecode;
	}

	@Override
	public String getGiveSMSWincode(String betcode) {
		return betcode;
	}
}
