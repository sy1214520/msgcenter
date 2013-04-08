package com.ruyicai.msgcenter.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

import com.ruyicai.msgcenter.consts.SendState;

/**
 * 待发送短信记录
 */
@RooJavaBean
@RooJson
@RooToString
@Entity()
@Table(name = "SENDSMSDETAIL")
public class SendSMSDetail {
	
	@Id	
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

	@Column(name = "mobileid")
	private String mobileid;

	@Column(name = "content", columnDefinition = "text")
	private String content;

	@Column(name = "channelName", length = 100)
	private String channelName;

	@Column(name = "sendState")
	private BigDecimal sendState;

	@Column(name = "createTime")
	private Date createTime;

	@Column(name = "sendTime")
	private Date sendTime;

	@Column(name = "sendSuccTime")
	private Date sendSuccTime;

	@Column(name = "hashCode")
	private Integer hashCode;

	public SendSMSDetail() {
		super();
	}

	public SendSMSDetail(String mobileid, String content, String channelName, SendState sendState) {
		super();
		this.mobileid = mobileid;
		this.content = content;
		this.channelName = channelName;
		this.sendState = sendState.value();
		this.createTime = new Date();
	}

	public SendSMSDetail(String mobileid, String content, String channelName, SendState sendState, Date sendTime) {
		super();
		this.mobileid = mobileid;
		this.content = content;
		this.channelName = channelName;
		this.sendState = sendState.value();
		this.createTime = new Date();
		this.sendTime = sendTime;
	}
}
