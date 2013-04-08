package com.ruyicai.lottery.domain;

import java.util.List;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;

@RooJson
@RooJavaBean
public class CaselotDTO {
	Tuserinfo starter;
	CaseLot caseLot;
	Torder torder;
	List<Tlot> tlots;
}
