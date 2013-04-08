package com.ruyicai.msgcenter.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

import com.ruyicai.msgcenter.service.MemcachedService;

@RooToString
@RooJavaBean
@RooJson
@RooEntity(versionField = "", table = "SMSAUTHENTICATION")
public class SmsAuthentication implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "CHANNELNAME", length = 50)
	private String channelName;

	@Column(name = "USERNAME", length = 50)
	private String userName;

	@Column(name = "PWD", length = 50)
	private String pwd;

	@Column(name = "PORT", length = 10)
	private String port;

	@Column(name = "LASTMODIFYTIME")
	private Date lastModifyTime;

	@Autowired
	transient MemcachedService<SmsAuthentication> memcachedService;

	public static SmsAuthentication createOrMergeSmsAuthentication(String channelName, String userName, String pwd,
			String port) {
		if (StringUtils.isBlank(channelName)) {
			throw new IllegalArgumentException("the argument channelName is required");
		}
		if (StringUtils.isBlank(userName)) {
			throw new IllegalArgumentException("the argument userName is required");
		}
		if (StringUtils.isBlank(pwd)) {
			throw new IllegalArgumentException("the argument pwd is required");
		}
		SmsAuthentication authentication = SmsAuthentication.findSmsAuthentication(channelName);
		if (authentication == null) {
			authentication = new SmsAuthentication();
			authentication.setChannelName(channelName);
			authentication.setUserName(userName);
			authentication.setPwd(pwd);
			authentication.setPort(port);
			authentication.setLastModifyTime(new Date());
			authentication.persist();
		} else {
			authentication.setUserName(userName);
			authentication.setPwd(pwd);
			authentication.setPort(port);
			authentication.setLastModifyTime(new Date());
			authentication.merge();
		}
		new SmsAuthentication().memcachedService.set("SmsAuthentication" + channelName, authentication);
		return authentication;
	}

	public static SmsAuthentication findSmsAuthenticationFromCache(String channelName) {
		if (StringUtils.isBlank(channelName)) {
			throw new IllegalArgumentException("the argument channelName is required");
		}
		SmsAuthentication authentication = new SmsAuthentication().memcachedService.get("SmsAuthentication"
				+ channelName);
		if (authentication != null) {
			return authentication;
		} else {
			authentication = SmsAuthentication.findSmsAuthentication(channelName);
			if (authentication == null) {
				return null;
			} else {
				new SmsAuthentication().memcachedService.checkToSet("SmsAuthentication" + channelName, authentication);
				return authentication;
			}
		}
	}
}
