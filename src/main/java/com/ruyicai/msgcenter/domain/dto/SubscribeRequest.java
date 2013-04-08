package com.ruyicai.msgcenter.domain.dto;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooJson
@RooToString
public class SubscribeRequest implements Comparable<SubscribeRequest> {

	private BigDecimal lotmulti;
	
	private BigDecimal amt;
	
	private String batchcode;
	
	private Date endtime;
	
	private String desc;
	
	private BigDecimal lotsType;

	public BigDecimal getLotsType() {
		return lotsType;
	}

	public void setLotsType(BigDecimal lotsType) {
		this.lotsType = lotsType;
	}

	@Override
	public int compareTo(SubscribeRequest o) {
		if(Integer.parseInt(batchcode) > Integer.parseInt(o.getBatchcode())) {
			return 1;
		}
		if(Integer.parseInt(batchcode) < Integer.parseInt(o.getBatchcode())) {
			return -1;
		}
		return 0;
	}
}
