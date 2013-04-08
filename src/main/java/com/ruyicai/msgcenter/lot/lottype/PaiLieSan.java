package com.ruyicai.msgcenter.lot.lottype;

import org.springframework.stereotype.Component;

import com.ruyicai.msgcenter.lot.AbstractLot;

@Component("T01002")
public class PaiLieSan extends AbstractLot {

	@Override
	public String getGiveSMSWincode(String betcode) {
		String wincode = betcode;
		wincode = wincode.substring(2);
		return wincode;
	}

}
