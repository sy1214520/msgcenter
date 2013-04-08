package com.ruyicai.msgcenter.domain.dto;

import java.math.BigDecimal;

import org.springframework.roo.addon.javabean.RooJavaBean;

@RooJavaBean
public class SendPrizeSMSDTO {
	private String userno;
	private String buyuserno;
	private String agencyno;
	private String channel;
	private String orderid;
	private String lotno;
	private String batchcode;
	private BigDecimal orderprizeamt;
	private BigDecimal bettype;
	private String mobileid;
	private String cname;
	private int type;
}
