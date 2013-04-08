package com.ruyicai.msgcenter.controller.dto;

import java.io.Serializable;
import java.util.List;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;

import com.ruyicai.msgcenter.domain.SendChannel;
import com.ruyicai.msgcenter.domain.UserSMSTiming;

@RooJson
@RooJavaBean
public class UserSettingDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private UserSMSTiming userSMSTiming;

	private List<SendChannel> sendChannels;

	public UserSettingDTO() {
		super();
	}

	public UserSettingDTO(UserSMSTiming userSMSTiming, List<SendChannel> sendChannels) {
		super();
		this.userSMSTiming = userSMSTiming;
		this.sendChannels = sendChannels;
	}

}
