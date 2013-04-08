package com.ruyicai.lottery.domain;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooJson
@RooToString
public class Tlot {

	private String flowno;

	private String userno;

	private String lotno;

	private String lotpwd;

	private String agencyno;

	private String batchcode;

	private String validcode;

	private String runcode;

	private BigDecimal betnum;

	private BigDecimal drawway;

	private BigDecimal sellway;

	private String betcode;

	private String checkcode;

	private BigDecimal amt;

	private Date ordertime;

	private BigDecimal settleflag;

	private BigDecimal prizeamt;

	private BigDecimal preprizeamt;

	private BigDecimal prizelittle;

	private String prizeinfo = " ";

	private Date prizetime;

	private String machineno;

	private Date giveuptime;

	private BigDecimal state;

	private BigDecimal transferstate;

	private BigDecimal bettype;

	private String subscribeno;

	private String pbatchcode;

	private String caseid;

	private String buyUserno;

	private BigDecimal lotmulti;

	private String channel;

	private String subchannel;

	private String torderid;

	private BigDecimal instate;

	private String subaccount;

	private BigDecimal paystate;

	private String currentbetcode;

	private BigDecimal failnum;

	private Date lotcenterordertime;

	private String lotcentercashterm;

	private Date lotcentercashtime;

	private String messageid;

	private BigDecimal returnvalue;

	private String eventcode;

	private BigDecimal lotcenterstate;

}
