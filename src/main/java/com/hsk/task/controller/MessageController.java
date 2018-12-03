package com.hsk.task.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hsk.task.bean.Result;
import com.hsk.task.service.MessageService;
import com.hsk.task.utils.ResultUtil;

@RestController
@Api(tags ="意见反馈相关")
public class MessageController {
	@Autowired
	MessageService messageService;
	
	@GetMapping("/addMessage")
	@ApiOperation(value = "添加意见反馈")
	public Result addMessage(String content,String phone){
		messageService.addMessage(content, phone);
		return ResultUtil.success();
	}
}
