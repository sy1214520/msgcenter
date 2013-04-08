package com.ruyicai.msgcenter.service;

import java.util.Date;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.msgcenter.controller.dto.MsgRequest;
import com.ruyicai.msgcenter.domain.Message;
@Service
public class SaveMsgService {
	
	
	@Transactional
	public Message saveMsg(MsgRequest msgRequest) {
		Message message = new Message();
		message.setUserno(msgRequest.getUserno());
		message.setContent(msgRequest.getContent());
		message.setDetail(msgRequest.getDetail());
		message.setFlag(0);
		message.setImsi(msgRequest.getImsi());
		message.setCreatetime(new Date());
		message.setHasRead(1);
		message.persist();
		return message;
	}
	

}
