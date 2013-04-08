package com.ruyicai.msgcenter.controller;

import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ruyicai.msgcenter.controller.dto.MsgRequest;
import com.ruyicai.msgcenter.dao.MessageDao;
import com.ruyicai.msgcenter.domain.Message;
import com.ruyicai.msgcenter.domain.Reply;
import com.ruyicai.msgcenter.exception.RuyicaiException;
import com.ruyicai.msgcenter.service.SaveMsgService;
import com.ruyicai.msgcenter.util.ErrorCode;
import com.ruyicai.msgcenter.util.JsonUtil;
import com.ruyicai.msgcenter.util.Page;

@RequestMapping("/msg")
@Controller
public class MsgController {
	private Logger logger = LoggerFactory.getLogger(MsgController.class);

	@Autowired
	SaveMsgService saveMsgService;
	@RequestMapping(value = "/saveMsg")
	public @ResponseBody
	ResponseData saveMsg(@RequestParam("body") String body) throws Exception {
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;
		logger.info("/saveMessage body:" + body);
		try {
			if (!body.contains("<")) {
				MsgRequest msgRequest = JsonUtil.fromJsonToObject(body, MsgRequest.class);
				Message message = Message.findLastMsg(msgRequest.getUserno());
				if(message != null){
					long l = System.currentTimeMillis()-message.getCreatetime().getTime();
					if(l>600000){
						rd.setValue(saveMsgService.saveMsg(msgRequest));
					}else{
						result = ErrorCode.Msg_TooFrequent;
					}
				}else{
					rd.setValue(saveMsgService.saveMsg(msgRequest));
				}
			}else{
				result = ErrorCode.STR_ParaWrong;
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = ErrorCode.ERROR;
			rd.setValue(e.getMessage());
		}
		rd.setErrorCode(result.value);
		return rd;
	}
	/**
	 * 查询分页列表
	 * @return Page<Message>
	 */
	@RequestMapping(value = "/selectMsgs")
	public @ResponseBody
	ResponseData selectMsgs(@RequestParam(value = "condition", required = false) String condition,
			@RequestParam(value = "startLine", required = false, defaultValue = "0") int startLine,
			@RequestParam(value = "endLine", required = false, defaultValue = "30") int endLine) {
		ResponseData rd = new ResponseData();
		Page<Message> page = new Page<Message>(startLine, endLine);
		try {
			Map<String, Object> conditionMap = JsonUtil.transferJson2Map(condition);
			Page<Message> msgPage =Message.findMsg(conditionMap, page);
			rd.setValue(msgPage);
			rd.setErrorCode(ErrorCode.OK.value);
		}  catch (Exception e) {
			e.printStackTrace();
			logger.error("出错", e);
			rd.setErrorCode(ErrorCode.ERROR.value);
		}
		return rd;
	}
	
	/**
	 * 更新审核状态
	 * @return Page<Message>
	 */
	@RequestMapping(value = "/updateFlag")
	public @ResponseBody
	ResponseData updateFlag(@RequestParam(value = "id") Integer id,@RequestParam(value = "flag") Integer flag) {
		ResponseData rd = new ResponseData();
		try {
			Message m = Message.findMessage(id);
			m.setFlag(flag);
			m.merge();
			rd.setErrorCode(ErrorCode.OK.value);
		}  catch (Exception e) {
			e.printStackTrace();
			logger.error("出错", e);
			rd.setErrorCode(ErrorCode.ERROR.value);
		}
		return rd;
	}
	/**
	 * 更新回复
	 * @return Page<Message>
	 */
	@RequestMapping(value = "/reply")
	public @ResponseBody
	ResponseData reply(@RequestParam(value = "id") Integer id,
			@RequestParam(value = "username") String username,
			@RequestParam(value = "reply") String reply,
			@RequestParam(value = "bz") String bz) {
		ResponseData rd = new ResponseData();
		try {
			Message m = Message.findMessage(id);
			m.setReply(reply);
			m.setHasRead(0);
			m.merge();
			
			Reply r = Reply.findReply(id);
			if (r==null) {
				r = new Reply();
				r.setId(id);
			}
			r.setCreatetime(new Date());
			r.setReply(reply);
			r.setUsername(username);
			r.setBezu(bz);
			r.merge();
			rd.setErrorCode(ErrorCode.OK.value);
		}  catch (Exception e) {
			e.printStackTrace();
			logger.error("出错", e);
			rd.setErrorCode(ErrorCode.ERROR.value);
		}
		return rd;
	}
	
	/**
	 */
	@RequestMapping(value = "/selectReply")
	public @ResponseBody
	ResponseData selectReply(@RequestParam(value = "id") int id){
		ResponseData rd = new ResponseData();
		try {
			rd.setValue(Reply.findReply(id));
			rd.setErrorCode(ErrorCode.OK.value);
		}  catch (Exception e) {
			e.printStackTrace();
			logger.error("出错", e);
			rd.setErrorCode(ErrorCode.ERROR.value);
		}
		return rd;
	}
	
	/**
	 * 查询分页列表
	 * @return Page<Message>
	 */
	@RequestMapping(value = "/selectReplys")
	public @ResponseBody
	ResponseData selectReplys(@RequestParam(value = "condition", required = false) String condition,
			@RequestParam(value = "startLine", required = false, defaultValue = "0") int startLine,
			@RequestParam(value = "endLine", required = false, defaultValue = "30") int endLine) {
		ResponseData rd = new ResponseData();
		Page<Reply> page = new Page<Reply>(startLine, endLine);
		try {
			Map<String, Object> conditionMap = JsonUtil.transferJson2Map(condition);
			Page<Reply> msgPage =Reply.findMsg(conditionMap, page);
			rd.setValue(msgPage);
			rd.setErrorCode(ErrorCode.OK.value);
		}  catch (Exception e) {
			e.printStackTrace();
			logger.error("出错", e);
			rd.setErrorCode(ErrorCode.ERROR.value);
		}
		return rd;
	}
	@Autowired
	MessageDao messageDao;
	
	/**
	 * 查询未读留言数量
	 * @param userno 用户编号
	 */
	@RequestMapping(value = "/findCountUnread")
	public @ResponseBody
	ResponseData findCountUnread(@RequestParam(value = "userno") String userno) {
		logger.info("/findCountUnread userno:{}", new String[] { userno });
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;
		try {
			rd.setValue(messageDao.findCountUnread(userno));
		} catch (RuyicaiException e) {
			logger.error("查询未读数量异常{}", new String[] { e.getErrorCode() == null ? e.getMessage()
					: e.getErrorCode().memo }, e);
			result = e.getErrorCode() == null ? ErrorCode.ERROR : e.getErrorCode();
			rd.setValue(e.getErrorCode() == null ? e.getMessage() : e.getErrorCode().memo);
		} catch (Exception e) {
			logger.error("查询未读数量异常{}", new String[] { e.getMessage() }, e);
			result = ErrorCode.ERROR;
			rd.setValue(e.getMessage());
		}
		rd.setErrorCode(result.value);
		return rd;
	}

	/**
	 * 更新留言为已读
	 * @param id 站内短信ID
	 */
	@RequestMapping(value = "/update2Read", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData update2Read(@RequestParam(value = "id") String id) {
		logger.info("/update2Read id:"+id);
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;
		try {
			String[] ids = id.split(",");
			for (String id2 : ids) {
				rd.setValue(messageDao.update2Read(new Integer(id2)));
			}
		} catch (RuyicaiException e) {
			logger.error("更新留言为已读异常{}", new String[] { e.getErrorCode() == null ? e.getMessage()
					: e.getErrorCode().memo }, e);
			result = e.getErrorCode() == null ? ErrorCode.ERROR : e.getErrorCode();
			rd.setValue(e.getErrorCode() == null ? e.getMessage() : e.getErrorCode().memo);
		} catch (Exception e) {
			logger.error("更新留言为已读异常{}", new String[] { e.getMessage() }, e);
			result = ErrorCode.ERROR;
			rd.setValue(e.getMessage());
		}
		rd.setErrorCode(result.value);
		return rd;
	}
}
