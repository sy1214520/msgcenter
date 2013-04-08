package com.ruyicai.msgcenter.controller.dto;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

@RooToString
@RooJavaBean
@RooJson
public class MsgRequest {

	String userno;
	String imsi;
	String content;
	String detail;
}
