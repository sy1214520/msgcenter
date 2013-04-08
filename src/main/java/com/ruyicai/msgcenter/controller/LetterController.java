package com.ruyicai.msgcenter.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ruyicai.msgcenter.controller.dto.LetterDTO;
import com.ruyicai.msgcenter.dao.LetterDao;
import com.ruyicai.msgcenter.domain.Letter;
import com.ruyicai.msgcenter.exception.RuyicaiException;
import com.ruyicai.msgcenter.service.LotteryService;
import com.ruyicai.msgcenter.util.ErrorCode;
import com.ruyicai.msgcenter.util.JsonUtil;
import com.ruyicai.msgcenter.util.Page;

@RequestMapping("/letter")
@Controller
public class LetterController {

	private Logger logger = LoggerFactory.getLogger(LetterController.class);

	@Autowired
	private LotteryService lotteryService;

	@Autowired
	private LetterDao letterDao;

	/**
	 * 创建站内短信
	 * 
	 * @param fromUserno
	 *            发送用户编号
	 * @param toUserno
	 *            接收用户编号
	 * @param title
	 *            标题，限100汉字
	 * @param letterType
	 *            站内短信类型，可不传
	 * @param content
	 *            站内短信内容
	 * @return Letter
	 */
	@RequestMapping(value = "/createLetter", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData createLetter(@RequestParam(value = "fromUserno", required = false) String fromUserno,
			@RequestParam(value = "toUserno") String toUserno, @RequestParam(value = "title") String title,
			@RequestParam(value = "letterType", required = false, defaultValue = "0") Integer letterType,
			@RequestParam(value = "content", required = false) String content) {
		logger.info("/createLetter fromUserno:{}, toUserno:{}, title:{}, letterType:{}, content:{}", new String[] {
				fromUserno, toUserno, title, letterType + "", content });
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;
		try {
			Letter letter = letterDao.createLetter(fromUserno, toUserno, letterType, title, content);
			rd.setValue(letter);
		} catch (RuyicaiException e) {
			logger.error("创建站内短信异常{}",
					new String[] { e.getErrorCode() == null ? e.getMessage() : e.getErrorCode().memo }, e);
			result = e.getErrorCode() == null ? ErrorCode.ERROR : e.getErrorCode();
			rd.setValue(e.getErrorCode() == null ? e.getMessage() : e.getErrorCode().memo);
		} catch (Exception e) {
			logger.error("创建站内短信异常{}", new String[] { e.getMessage() }, e);
			result = ErrorCode.ERROR;
			rd.setValue(e.getMessage());
		}
		rd.setErrorCode(result.value);
		return rd;
	}

	/**
	 * 批量创建站内短信
	 * 
	 * @param fromUserno
	 *            发送用户编号
	 * @param toUserno
	 *            接收用户编号,以逗号分隔
	 * @param title
	 *            标题，限100汉字
	 * @param letterType
	 *            站内短信类型，可不传
	 * @param content
	 *            站内短信内容
	 * @return List<Letter>
	 */
	@RequestMapping(value = "/createLetterBatch", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData createLetterBatch(@RequestParam(value = "fromUserno", required = false) String fromUserno,
			@RequestParam(value = "toUserno") String toUserno, @RequestParam(value = "title") String title,
			@RequestParam(value = "letterType", required = false, defaultValue = "0") Integer letterType,
			@RequestParam(value = "content", required = false) String content) {
		logger.info("/createLetterBatch fromUserno:{}, toUserno:{}, title:{}, letterType:{}, content:{}", new String[] {
				fromUserno, toUserno, title, letterType + "", content });
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;
		try {
			List<Letter> letter = letterDao.createLetterBatch(fromUserno, toUserno, letterType, title, content);
			rd.setValue(letter);
		} catch (RuyicaiException e) {
			logger.error("批量创建站内短信异常{}", new String[] { e.getErrorCode() == null ? e.getMessage()
					: e.getErrorCode().memo }, e);
			result = e.getErrorCode() == null ? ErrorCode.ERROR : e.getErrorCode();
			rd.setValue(e.getErrorCode() == null ? e.getMessage() : e.getErrorCode().memo);
		} catch (Exception e) {
			logger.error("批量创建站内短信异常{}", new String[] { e.getMessage() }, e);
			result = ErrorCode.ERROR;
			rd.setValue(e.getMessage());
		}
		rd.setErrorCode(result.value);
		return rd;
	}

	/**
	 * 更新站内短信为已读
	 * 
	 * @param id
	 *            站内短信ID
	 * @return Letter
	 */
	@RequestMapping(value = "/update2Read", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData update2Read(@RequestParam(value = "id") String id) {
		logger.info("/update2Read id:{}", new String[] { id });
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;
		try {
			Letter letter = letterDao.update2Read(id);
			rd.setValue(letter);
		} catch (RuyicaiException e) {
			logger.error("更新站内短信为已读异常{}", new String[] { e.getErrorCode() == null ? e.getMessage()
					: e.getErrorCode().memo }, e);
			result = e.getErrorCode() == null ? ErrorCode.ERROR : e.getErrorCode();
			rd.setValue(e.getErrorCode() == null ? e.getMessage() : e.getErrorCode().memo);
		} catch (Exception e) {
			logger.error("更新站内短信为已读异常{}", new String[] { e.getMessage() }, e);
			result = ErrorCode.ERROR;
			rd.setValue(e.getMessage());
		}
		rd.setErrorCode(result.value);
		return rd;
	}

	/**
	 * 批量更新站内短信为已读
	 * 
	 * @param id
	 *            站内短信ID
	 * @return List<Letter>
	 */
	@RequestMapping(value = "/update2ReadBatch", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData update2ReadBatch(@RequestParam(value = "id") String id) {
		logger.info("/update2ReadBatch id:{}", new String[] { id });
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;
		try {
			List<Letter> letter = letterDao.update2ReadBatch(id);
			rd.setValue(letter);
		} catch (RuyicaiException e) {
			logger.error("批量更新站内短信为已读异常{}", new String[] { e.getErrorCode() == null ? e.getMessage()
					: e.getErrorCode().memo }, e);
			result = e.getErrorCode() == null ? ErrorCode.ERROR : e.getErrorCode();
			rd.setValue(e.getErrorCode() == null ? e.getMessage() : e.getErrorCode().memo);
		} catch (Exception e) {
			logger.error("批量更新站内短信为已读异常{}", new String[] { e.getMessage() }, e);
			result = ErrorCode.ERROR;
			rd.setValue(e.getMessage());
		}
		rd.setErrorCode(result.value);
		return rd;
	}

	/**
	 * 更新站内短信为已删除
	 * 
	 * @param id
	 *            站内短信ID
	 * @return Letter
	 */
	@RequestMapping(value = "/update2Del", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData update2Del(@RequestParam(value = "id") String id) {
		logger.info("/update2Del id:{}", new String[] { id });
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;
		try {
			Letter letter = letterDao.update2Del(id);
			rd.setValue(letter);
		} catch (RuyicaiException e) {
			logger.error("更新站内短信为已删除异常{}", new String[] { e.getErrorCode() == null ? e.getMessage()
					: e.getErrorCode().memo }, e);
			result = e.getErrorCode() == null ? ErrorCode.ERROR : e.getErrorCode();
			rd.setValue(e.getErrorCode() == null ? e.getMessage() : e.getErrorCode().memo);
		} catch (Exception e) {
			logger.error("更新站内短信为已删除异常{}", new String[] { e.getMessage() }, e);
			result = ErrorCode.ERROR;
			rd.setValue(e.getMessage());
		}
		rd.setErrorCode(result.value);
		return rd;
	}

	/**
	 * 批量更新站内短信为已删除
	 * 
	 * @param id
	 *            站内短信ID
	 * @return List<Letter>
	 */
	@RequestMapping(value = "/update2DelBatch", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData update2DelBatch(@RequestParam(value = "id") String id) {
		logger.info("/update2Del id:{}", new String[] { id });
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;
		try {
			List<Letter> letter = letterDao.update2DelBatch(id);
			rd.setValue(letter);
		} catch (RuyicaiException e) {
			logger.error("批量更新站内短信为已删除异常{}",
					new String[] { e.getErrorCode() == null ? e.getMessage() : e.getErrorCode().memo }, e);
			result = e.getErrorCode() == null ? ErrorCode.ERROR : e.getErrorCode();
			rd.setValue(e.getErrorCode() == null ? e.getMessage() : e.getErrorCode().memo);
		} catch (Exception e) {
			logger.error("批量更新站内短信为已删除异常{}", new String[] { e.getMessage() }, e);
			result = ErrorCode.ERROR;
			rd.setValue(e.getMessage());
		}
		rd.setErrorCode(result.value);
		return rd;
	}

	/**
	 * 查找站内短信带分页
	 * 
	 * @param condition
	 *            查询条件json串
	 * @param startLine
	 *            开始记录数
	 * @param endLine
	 *            每页显示记录数，默认20
	 * @param orderBy
	 *            排序字段
	 * @param orderDir
	 *            排序条件
	 * @return Page
	 */
	@RequestMapping(value = "/findLetterByPage")
	public @ResponseBody
	ResponseData findLetterByPage(@RequestParam(value = "condition", required = false) String condition,
			@RequestParam(value = "startLine", required = false, defaultValue = "0") int startLine,
			@RequestParam(value = "endLine", required = false, defaultValue = "20") int endLine,
			@RequestParam(value = "orderBy", required = false) String orderBy,
			@RequestParam(value = "orderDir", required = false) String orderDir) {
		logger.info("/findLetterByPage condition:{},startLine:{},endLine:{},orderBy:{},orderDir:{}", new String[] {
				condition, startLine + "", endLine + "", orderBy, orderDir });
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;
		Page<Letter> page = new Page<Letter>(startLine, endLine, orderBy, orderDir);
		List<LetterDTO> letterDTOList = new ArrayList<LetterDTO>();
		try {
			Map<String, Object> conditionMap = JsonUtil.transferJson2Map(condition);
			letterDao.findLetterByPage(conditionMap, page);
			List<Letter> list = page.getList();
			for (Letter letter : list) {
				LetterDTO letterDTO = new LetterDTO();
				letterDTO.setLetter(letter);
				if (StringUtils.isNotBlank(letter.getFromUserno())) {
					letterDTO.setFromTuserinfo(lotteryService.findTuserinfoByUserno(letter.getFromUserno()));
				}
				if (StringUtils.isNotBlank(letter.getToUserno())) {
					letterDTO.setToTuserinfo(lotteryService.findTuserinfoByUserno(letter.getToUserno()));
				}
				letterDTOList.add(letterDTO);
			}
			rd.setValue(new Page<LetterDTO>(startLine, endLine, page.getTotalResult(), orderBy, orderDir, letterDTOList));
		} catch (RuyicaiException e) {
			logger.error("查找站内短信带分页异常{}", new String[] { e.getErrorCode() == null ? e.getMessage()
					: e.getErrorCode().memo }, e);
			result = e.getErrorCode() == null ? ErrorCode.ERROR : e.getErrorCode();
			rd.setValue(e.getErrorCode() == null ? e.getMessage() : e.getErrorCode().memo);
		} catch (Exception e) {
			logger.error("查找站内短信带分页异常{}", new String[] { e.getMessage() }, e);
			result = ErrorCode.ERROR;
			rd.setValue(e.getMessage());
		}
		rd.setErrorCode(result.value);
		return rd;
	}

	/**
	 * 查询未读站内信数量
	 * 
	 * @param userno
	 *            用户编号
	 * @return Page
	 */
	@RequestMapping(value = "/findCountUnread")
	public @ResponseBody
	ResponseData findCountUnread(@RequestParam(value = "userno") String userno) {
		logger.info("/findCountUnread userno:{}", new String[] { userno });
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;
		try {
			rd.setValue(letterDao.findCountUnread(userno));
		} catch (RuyicaiException e) {
			logger.error("查询未读站内信数量异常{}", new String[] { e.getErrorCode() == null ? e.getMessage()
					: e.getErrorCode().memo }, e);
			result = e.getErrorCode() == null ? ErrorCode.ERROR : e.getErrorCode();
			rd.setValue(e.getErrorCode() == null ? e.getMessage() : e.getErrorCode().memo);
		} catch (Exception e) {
			logger.error("查询未读站内信数量异常{}", new String[] { e.getMessage() }, e);
			result = ErrorCode.ERROR;
			rd.setValue(e.getMessage());
		}
		rd.setErrorCode(result.value);
		return rd;
	}

}
