package com.ruyicai.lottery.domain;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooJson
@RooToString
public class Tsubscribe {

	private String flowno;

	private String userno;

	private String lotno;

	private BigDecimal batchnum;

	private BigDecimal lastnum;

	private String beginbatch;

	private String lastbatch;

	private BigDecimal betnum;

	private BigDecimal drawway;

	private BigDecimal sellway;

	private String betcode;

	private BigDecimal amount;

	private BigDecimal totalamt;

	private Date ordertime;

	private String mac;

	private BigDecimal type;

	private BigDecimal state;

	private Date endtime;

	private BigDecimal lotmulti;

	private String channel;

	private String subchannel;

	private String subaccount;

	private BigDecimal prizeend;

	private BigDecimal accountnomoneysms;

	private String memo;

	private Date changetime;

	private String desc;

	private BigDecimal prizeendamt;

	private BigDecimal leijiprizeendamt;

	private BigDecimal endsms;

}
