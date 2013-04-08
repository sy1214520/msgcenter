package com.ruyicai.msgcenter.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruyicai.msgcenter.lot.Lot;

@Service
public class TlotManager {

	@Autowired
	private Map<String, Lot> map;

	public Lot getLot(String lotno) {
		return map.get(lotno);
	}

	public Boolean contains(String lotno) {
		return map.containsKey(lotno);
	}
}
