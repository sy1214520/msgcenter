package com.ruyicai.msgcenter.controller.dto;

import java.io.Serializable;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;

import com.ruyicai.lottery.domain.Tuserinfo;
import com.ruyicai.msgcenter.domain.Letter;

@RooJson
@RooJavaBean
public class LetterDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Letter letter;

	private Tuserinfo fromTuserinfo;

	private Tuserinfo toTuserinfo;
}
