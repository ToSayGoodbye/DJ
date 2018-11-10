package com.hsk.task.controller;

import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hsk.task.bean.Result;
import com.hsk.task.service.OrderService;
import com.hsk.task.utils.ResultUtil;

@RestController
@Api(tags ="订单以及金额相关")
public class OrderController {
	@Autowired
	OrderService orderService;
	
	@GetMapping("/selectBalance")
	@ApiOperation(value = "查询余额")
	public Result selectBalance(String infoNum){
		String balance = orderService.selectBalance(infoNum);
		return ResultUtil.success(balance);
	}
}
