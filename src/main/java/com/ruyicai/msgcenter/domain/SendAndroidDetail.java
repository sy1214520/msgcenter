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

@RooJavaBean
@RooJson
@RooToString
@Entity()
@Table(name = "SENDANDROIDDETAIL")
public class SendAndroidDetail {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "SENDNO")
	private Integer sendNo;

	@Column(name = "RECEIVERTYPE")
	private Integer receiverType;

	@Column(name = "RECEIVERVALUE", length = 100)
	private String receiverValue;

	@Column(name = "PKGNAME", length = 100)
	private String pkgName;

	@Column(name = "MSGTITLE", columnDefinition = "text")
	private String msgTitle;

	@Column(name = "MSGCONTENT", columnDefinition = "text")
	private String msgContent;

	@Column(name = "SENDSTATE")
	private BigDecimal sendState;

	@Column(name = "CREATETIME")
	private Date createTime;

	@Column(name = "SENDTIME")
	private Date sendTime;

	@Column(name = "SENDSUCCTIME")
	private Date sendSuccTime;

	@Column(name = "ERRORCODE")
	private Integer errorCode;

	public SendAndroidDetail() {
		super();
	}

	/**
	 * @param receiverType
	 *            发送(Jpush接收)的类型。 @see
	 *            com.ruyicai.msgcenter.consts.JpushReciverType.java
	 * @param receiverValue
	 *            发送(Jpush接收)的值
	 * @param pkgName
	 *            客户端包名，使用哪个包进行发送。@see
	 *            com.ruyicai.msgcenter.consts.AndroidPkgType.java
	 * @param msgTitle
	 *            通知标题
	 * @param msgContent
	 *            通知内容
	 * @param sendTime
	 *            发送时间
	 */
	public SendAndroidDetail(Integer receiverType, String receiverValue, String pkgName, String msgTitle,
			String msgContent, Date sendTime) {
		super();
		this.receiverType = receiverType;
		this.receiverValue = receiverValue;
		this.pkgName = pkgName;
		this.msgTitle = msgTitle;
		this.msgContent = msgContent;
		this.sendState = SendState.SEND_WAIT.value();
		this.sendTime = sendTime;
		this.createTime = new Date();
	}
}
