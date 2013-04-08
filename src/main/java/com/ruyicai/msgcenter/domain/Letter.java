package com.ruyicai.msgcenter.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

/**
 * 站内信设置
 */
@RooJavaBean
@RooJson
@RooToString
@Entity()
@Table(name = "LETTER")
public class Letter {

	@Id
	@GenericGenerator(name = "generator", strategy = "org.hibernate.id.UUIDHexGenerator")
	@GeneratedValue(generator = "generator")
	@Column(name = "ID")
	private String id;

	/** 发送人用户编号 */
	@Column(name = "FROMUSERNO", length = 32)
	private String fromUserno;

	/** 接收人用户编号 */
	@Column(name = "TOUSERNO", length = 32)
	private String toUserno;

	/** 站内信类别 可以不穿 0：普通站内信 ，1：开奖站内信，2：中奖站内信 */
	@Column(name = "LETTERTYPE")
	private Integer letterType;

	/** 标题 限100汉字 */
	@Column(name = "TITLE", length = 200)
	private String title;

	/** 内容 */
	@Column(name = "CONTENT", columnDefinition = "text")
	private String content;

	/** 0：未读，1：已读 */
	@Column(name = "HASREAD")
	private int hasRead;

	/** 0：未删除，1：已删除 */
	@Column(name = "HASDEL")
	private int hasDel;

	/** 创建时间 */
	@Column(name = "CREATETIME")
	private Date createTime;

	/** 读取时间 */
	@Column(name = "READTIME")
	private Date readTime;

	/** 删除时间 */
	@Column(name = "DELTIME")
	private Date delTime;

}
