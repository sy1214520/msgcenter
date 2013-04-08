package com.ruyicai.msgcenter.lot.lottype;

import java.util.Arrays;

import org.springframework.stereotype.Component;

import com.ruyicai.msgcenter.lot.AbstractLot;

@Component("F47102")
public class QiLeCai extends AbstractLot {

	@Override
	public String getOpenSMSWincode(String winbasecode, String winspecialcode) {
		String wincode = winbasecode;
		String[] bolls = new String[wincode.length() / 2];
		int index = 0;
		while (wincode.length() > 0) {
			bolls[index] = wincode.substring(0, 2);
			wincode = wincode.substring(2);
			index++;
		}
		Arrays.sort(bolls);
		String winNumber = "";
		for (String boll : bolls) {
			winNumber += boll;
		}
		winNumber += "+" + winspecialcode;
		return winNumber;
	}
}
