package com.ruyicai.msgcenter.controller.dto;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

@RooToString
@RooJavaBean
@RooJson
public class DeviceTokenRequest {

	private String userno;

	private String token;

	private String machine;
	
	private int needToSend;
}
