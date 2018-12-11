package com.hsk.task.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.hsk.task.bean.Result;
import com.hsk.task.bean.ResultEnum;
import com.hsk.task.service.GasService;
import com.hsk.task.utils.MathUtil;
import com.hsk.task.utils.RedisUtils;
import com.hsk.task.utils.ResultUtil;
import com.hsk.task.utils.SendMsg;

@RestController
@Api(tags="登陆相关")
public class LoginController {
	
	private Logger logger = LoggerFactory.getLogger(LoginController.class);  
	
	@Autowired
	SendMsg sendMsg;
	
	@Resource
	private RedisUtils redisUtils;
	
	@Autowired
	GasService gasService;
	
	
	/**
	 * 生成验证码
	 * @param phone
	 * @return
	 */
	@GetMapping("/getCode")
	@ApiOperation(value = "生成验证码")
	public Result getCode(String phone){
		
		String code = MathUtil.smsCode();//生成验证码
		redisUtils.set(phone, code, 5*60);//保存到redis中,5分钟有效
		
		logger.info(phone+"正在生成验证码"+code);
		
		sendMsg.execute(phone, code);
		
		return ResultUtil.success(code);
	}
	
	/**
	 * 登陆
	 * @param request
	 * @param phone
	 * @return
	 */
	@GetMapping("/login")
	@ResponseBody
	@ApiOperation(value = "登陆")
	public Result login(HttpServletRequest request, String phone,String code) {
		logger.info("用户："+phone+"使用验证码"+code+"，登录中。。。");
		
		String cacheCode = redisUtils.get(phone);//获取发送的验证码
		if(cacheCode == null){
			return ResultUtil.error(ResultEnum.CODE_OVERDUE.getCode(),
					ResultEnum.CODE_OVERDUE.getMsg());
		}
		
		if(cacheCode!=null && cacheCode.equals(code)){//若相等则登录成功
			Map<String,String> map = new HashMap<String,String>();
			map = gasService.selectGasByPhone(phone);//查看是否为管理员
			
			String uuid = MathUtil.getUUID();//生成唯一标志，缓存到redis并保存到微信中作为登录标志
			redisUtils.set(uuid, phone);
			
			Map resultMap = new HashMap();
			resultMap.put("oil", map);
			resultMap.put("uuid", uuid);
			
			gasService.insertUser(phone);//插入登录表
			
			return ResultUtil.success(resultMap);
		}else{//否则返回验证码过期
			return ResultUtil.error(ResultEnum.CODE_ERROR.getCode(),
					ResultEnum.CODE_ERROR.getMsg());
		}
	}
	
	/**
	 * 验证是否登录
	 * @param request
	 * @param uuid
	 * @return
	 */
	@GetMapping("/ifLogin")
	@ResponseBody
	@ApiOperation(value = "根据cookieid验证是否登陆")
	public Result ifLogin(HttpServletRequest request, String uuid) {
		
		//若KEY存在 返回成功
		if(redisUtils.hasKey(uuid)){
			return ResultUtil.success();
		}else{
			return ResultUtil.error(ResultEnum.NO_LOGIN.getCode(),ResultEnum.NO_LOGIN.getMsg());
		}
	}
	/**
	 * 退出登录
	 * @param request
	 * @param uuid
	 * @return
	 */
	@GetMapping("/logout")
	@ResponseBody
	@ApiOperation(value = "退出登陆")
	public Result logout(HttpServletRequest request, String uuid) {
		
		redisUtils.delete(uuid);
		
		return ResultUtil.success();
	}
}
