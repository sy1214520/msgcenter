package com.ruyicai.msgcenter.domain;

import javax.persistence.Column;

import org.springframework.roo.addon.entity.RooIdentifier;
import org.springframework.roo.addon.tostring.RooToString;

@RooIdentifier
@RooToString
public class AndroidAliasPK {

	private static final long serialVersionUID = 1L;

	@Column(name = "USERNO", length = 50)
	private String userno;

	@Column(name = "PKGNAME", length = 100)
	private String pkgName;
}
