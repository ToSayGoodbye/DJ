package com.hsk.task.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.hsk.task.bean.GasInfo;
import com.hsk.task.bean.Result;
import com.hsk.task.service.GasService;
import com.hsk.task.utils.RedisUtils;
import com.hsk.task.utils.ResultUtil;

@RestController
@Api(tags ="油站信息相关")
public class GasController {
	
	private Logger logger = LoggerFactory.getLogger(GasController.class); 
	
	@Autowired
	GasService gasService;
	
	@Resource
	private RedisUtils redisUtils;
	
	@GetMapping("/queryGasList")
	@ApiOperation(value = "首页查询油站列表")
	public Result queryGasList(String page,String pagesize,String type,
			String latitude,String longitude,String order){
		
		List<GasInfo> list = new ArrayList<GasInfo>();
		list = gasService.queryGasList(page, pagesize, type, latitude, longitude,order);
		
		return ResultUtil.success(list);
	}
	
	/**
	 * 查询订单列表
	 * @param page
	 * @param pagesize
	 * @param phone
	 * @param id 具体油站id
	 * @return
	 */
	@GetMapping("/queryOrderList")
	@ApiOperation(value = "查询订单列表")
	public Result queryOrderList(String page,String pagesize,String phone,String id){
		List list = new ArrayList();
		if(id.equals("undefined")){
			id="";
		}
		list = gasService.queryOrderList(page, pagesize, phone,id);
		return ResultUtil.success(list);
	}
	
	@GetMapping("/selectOilById")
	@ApiOperation(value = "根据油站num和type查询相关信息")
	public Result selectOilById(String infoNum,String type){
		GasInfo info = new GasInfo();
		info = gasService.selectOilById(infoNum,type);
		
		return ResultUtil.success(info);
	}
	
	@GetMapping("/queryGasPrices")
	@ApiOperation(value = "根据油站num查询油价格列表")
	public Result queryGasPrices(String infoNum){
		List list = new ArrayList();
		
		list = gasService.queryGasPrices(infoNum);
		return ResultUtil.success(list);
	}
	
	@GetMapping("/queryOilTypes")
	@ApiOperation(value = "根据油站num查询油种类")
	public Result queryOilTypes(String infoNum){
		List list = new ArrayList();
		
		list = gasService.queryOilTypes(infoNum);
		return ResultUtil.success(list);
	}
	
	@GetMapping("/updatePrice")
	@ApiOperation(value = "修改油价")
	public Result updatePrice(String id,String price,String saveprice){
		gasService.updatePrice(id,price,saveprice);
		return ResultUtil.success();
	}
	
	
}
