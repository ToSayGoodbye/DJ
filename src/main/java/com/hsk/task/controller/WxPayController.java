package com.hsk.task.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.hsk.task.bean.Result;
import com.hsk.task.bean.ResultEnum;
import com.hsk.task.bean.WxAuth;
import com.hsk.task.service.GasService;
import com.hsk.task.service.OrderService;
import com.hsk.task.utils.HttpRequest;
import com.hsk.task.utils.MathUtil;
import com.hsk.task.utils.PayUtil;
import com.hsk.task.utils.ResultUtil;
import com.hsk.task.utils.SslRequest;
import com.hsk.task.utils.XmlUtil;


@RestController
@Api(tags="支付相关")
public class WxPayController {
	
	private Logger logger = LoggerFactory.getLogger(WxPayController.class); 
	
	@Autowired
	private WxAuth wxAuth;
	
	@Autowired
	OrderService orderService;
	
	@Autowired
	GasService gasService;
	
	@RequestMapping(value = "/getOpenid") 
	@ApiOperation(value = "获取openid")
	public String getUserInfo(@RequestParam String js_code){
		StringBuffer sb = new StringBuffer();
		sb.append("appid=").append(wxAuth.getAppId());
		sb.append("&secret=").append(wxAuth.getSecret());
		sb.append("&js_code=").append(js_code);
		sb.append("&grant_type=").append(wxAuth.getGrantType());
		String res = HttpRequest.sendGet(wxAuth.getSessionHost(), sb.toString());
		if(res == null || res.equals("")){
			return null;
		}
		Map<String,Object> openidMap = JSON.parseObject(res, Map.class);
		return (String) openidMap.get("openid");
	}

	/**
	 * 微信下单支付
	 * @param openid
	 * @param phone
	 * @param info_id 哪个油站
	 * @param type 油类型
	 * @param shootNum 几号枪
	 * @param real_price 实付金额
	 * @param save_price 优惠金额
	 * @param request
	 * @return
	 */
	
	@GetMapping(value = "/wxPay") 
	@ApiOperation(value = "微信下单支付")
	public Map<String, Object> wxPay(String openid,String phone,
			String info_id,String type,String shootNum,String real_price,
			String save_price,HttpServletRequest request,String infoNum){
		try{
			//生成的随机字符串
			String nonce_str = getRandomStringByLength(32);
			//商品名称
			String body = "豆丁加油订单支付";
			//获取客户端的ip地址
			String spbill_create_ip = getIpAddr(request);
			if("0:0:0:0:0:0:0:1".equals(spbill_create_ip)){
				spbill_create_ip = "127.0.0.1";
			}
			
			//商户订单号
			String out_trade_no = MathUtil.generateOrderSN();
			
			//支付金额元转分
			String total_fee = String.valueOf((Double.valueOf(real_price))*100).split("\\.")[0];
			
			//额外参数  回调原样返回
			StringBuffer attachBuffer = new StringBuffer();
			attachBuffer.append(info_id).append("-")
			.append(phone).append("-")
			.append(type).append("-")
			.append(shootNum).append("-")
			.append(save_price).append("-")
			.append(openid).append("-")
			.append(infoNum);
			
			String attach = attachBuffer.toString();
			
			//组装参数，用户生成统一下单接口的签名
			Map<String, String> packageParams = new HashMap<String, String>();
			packageParams.put("appid", wxAuth.getAppId());
			packageParams.put("attach", attach);
			packageParams.put("mch_id", wxAuth.getMch_id());
			packageParams.put("nonce_str", nonce_str);
			packageParams.put("body", body);
			packageParams.put("out_trade_no", out_trade_no);//商户订单号
			packageParams.put("total_fee", total_fee);//支付金额，这边需要转成字符串类型，否则后面的签名会失败
			packageParams.put("spbill_create_ip", spbill_create_ip);
			packageParams.put("notify_url", wxAuth.getNotify_url());//支付成功后的回调地址
			packageParams.put("trade_type", wxAuth.getTradetype());//支付方式
			packageParams.put("openid", openid);
			   
        	String prestr = PayUtil.createLinkString(packageParams); // 把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串 
        
        	//MD5运算生成签名，这里是第一次签名，用于调用统一下单接口
        	String mysign = PayUtil.sign(prestr, wxAuth.getKey(), "utf-8").toUpperCase();
	        
	        //拼接统一下单接口使用的xml数据，要将上一步生成的签名一起拼接进去
	        String xml = "<xml>" + "<appid>" + wxAuth.getAppId() + "</appid>" 
	        		+ "<attach>" + attach + "</attach>" 
                    + "<body><![CDATA[" + body + "]]></body>" 
                    + "<mch_id>" + wxAuth.getMch_id() + "</mch_id>" 
                    + "<nonce_str>" + nonce_str + "</nonce_str>" 
                    + "<notify_url>" + wxAuth.getNotify_url() + "</notify_url>" 
                    + "<openid>" + openid + "</openid>" 
                    + "<out_trade_no>" + out_trade_no + "</out_trade_no>" 
                    + "<spbill_create_ip>" + spbill_create_ip + "</spbill_create_ip>" 
                    + "<total_fee>" + total_fee + "</total_fee>"
                    + "<trade_type>" + wxAuth.getTradetype() + "</trade_type>" 
                    + "<sign>" + mysign + "</sign>"
                    + "</xml>";
	        
	        logger.info("调试模式_统一下单接口 请求XML数据：" + xml);
 
	        //调用统一下单接口，并接受返回的结果
	        String result = PayUtil.httpRequest(wxAuth.getPay_url(), "POST", xml);
	        
	        logger.info("调试模式_统一下单接口 返回XML数据：" + result);
	        
	        // 将解析结果存储在HashMap中   
	        Map map = PayUtil.doXMLParse(result);
	        
	        String return_code = (String) map.get("return_code");//返回状态码
	        
		    Map<String, Object> response = new HashMap<String, Object>();//返回给小程序端需要的参数
	        if(return_code.equals("SUCCESS")){   
	            String prepay_id = (String) map.get("prepay_id");//返回的预付单信息   
	            response.put("nonceStr", nonce_str);
	            response.put("package", "prepay_id=" + prepay_id);
	            Long timeStamp = System.currentTimeMillis() / 1000;   
	            response.put("timeStamp", timeStamp + "");//这边要将返回的时间戳转化成字符串，不然小程序端调用wx.requestPayment方法会报签名错误
	            //拼接签名需要的参数
	            String stringSignTemp = "appId=" + wxAuth.getAppId() + "&nonceStr=" + nonce_str + "&package=prepay_id=" + prepay_id+ "&signType=MD5&timeStamp=" + timeStamp;   
	            //再次签名，这个签名用于小程序端调用wx.requesetPayment方法
	            String paySign = PayUtil.sign(stringSignTemp, wxAuth.getKey(), "utf-8").toUpperCase();
	            
	            response.put("paySign", paySign);
	        }
			
	        response.put("appid", wxAuth.getAppId());
			
	        return response;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 微信支付回调
	 * @param request
	 * @throws IOException 
	 * @throws JDOMException 
	 */
	@RequestMapping("/wxNotify")
	public void wxNotify(HttpServletRequest request,HttpServletResponse response) throws IOException, JDOMException{
		
		logger.info("微信支付回调开始...");
		//读取xml
		StringBuffer sb = new StringBuffer();  
		BufferedReader in = null;
		InputStream inputStream = null;  
		try{
	       inputStream = request.getInputStream();  
	       String s ;  
	       in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));  
	       while ((s = in.readLine()) != null){  
	           sb.append(s);
	       }
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			if(null != in){
				 in.close();
			}
			if(null != inputStream){
				inputStream.close();
			}
		}
	      
	  
	       //解析xml成map  
	       Map<String, String> m = new HashMap<String, String>();  
	       m = XmlUtil.xml2Map(sb.toString());  
	        
	       //过滤空 设置 TreeMap  
	       SortedMap<Object,Object> packageParams = new TreeMap<Object,Object>();        
	       Iterator it = m.keySet().iterator();  
	       while (it.hasNext()) {  
	           String parameter = (String) it.next();
	           String parameterValue = m.get(parameter);
	            
	           String v = "";  
	           if(null != parameterValue) {
	               v = parameterValue.trim();  
	           }  
	           packageParams.put(parameter, v);  
	       }  
	          
	       //判断签名是否正确  
	       if(PayUtil.verify("UTF-8", packageParams,wxAuth.getKey())) {  
	    	   
	    	   logger.info("微信回调--验签成功");
	    	   
	           String return_code = (String)packageParams.get("return_code");
	           String result_code = (String)packageParams.get("result_code");
	           logger.info("业务返回："+return_code+";支付情况："+result_code);
	           
	           //处理业务开始  
	           String resXml = "";  
	           if("SUCCESS".equals(return_code) && "SUCCESS".equals(result_code)){  
	              
	               String out_trade_no = (String)packageParams.get("out_trade_no");  //商户订单号
	               String total_fee = (String)packageParams.get("total_fee");  
	               String attach = (String)packageParams.get("attach");  
	               String transaction_id = (String)packageParams.get("transaction_id");  //微信支付订单号
	               String bank_type = (String)packageParams.get("bank_type");  //付款银行
	               String time_end = (String)packageParams.get("time_end");  //支付完成时间
	               
	               logger.info("out_trade_no=="+out_trade_no);
	               logger.info("total_fee=="+total_fee);
	               logger.info("attach=="+attach);
	               logger.info("transaction_id=="+transaction_id);
	               logger.info("bank_type=="+bank_type);
	               logger.info("time_end=="+time_end);
	               
	               String[] attachs = attach.split("-");
	               
	               HashMap<String,String> map = new HashMap();
	               map.put("out_trade_no", out_trade_no);
	               map.put("real_price", total_fee);
	               map.put("transaction_id", transaction_id);
	               map.put("bank_type", bank_type);
	               map.put("time_end", time_end);
	               map.put("info_id", attachs[0]);
	               map.put("phone", attachs[1]);
	               map.put("type", attachs[2]);
	               map.put("shootNum", attachs[3]);
	               map.put("save_price", attachs[4]);
	               map.put("openid", attachs[5]);
	               map.put("infoNum", attachs[6]);

	               Integer insertNum = orderService.insertOrder(map);//插入订单
	               
	               orderService.updateBalance(attachs[6], String.valueOf(Double.valueOf(total_fee)/100));//更新余额
	               
	               orderService.addCount(attachs[6]);//增加油站订单数量
	               
	               logger.info("订单插入条数："+insertNum);
	               
	               //通知微信.异步确认成功.必写.不然会一直通知后台.八次之后就认为交易失败了.  
	               resXml = "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>"  
	                       + "<return_msg><![CDATA[OK]]></return_msg>" + "</xml> ";  
	                  
	           } else {
	               resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>"  
	                       + "<return_msg><![CDATA[报文为空]]></return_msg>" + "</xml> ";  
	           }
	           //处理业务完毕  
	           BufferedOutputStream out = new BufferedOutputStream(  
	                   response.getOutputStream());  
	           out.write(resXml.getBytes());  
	           out.flush();  
	           out.close();  
	       } else{  
	         logger.info("通知签名验证失败");  
	       }
	}
	
	/**
	 * StringUtils工具类方法
	 * 获取一定长度的随机字符串，范围0-9，a-z
	 * @param length：指定字符串长度
	 * @return 一定长度的随机字符串
	 */
	public static String getRandomStringByLength(int length) {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
       }
	/**
	 * IpUtils工具类方法
	 * 获取真实的ip地址
	 * @param request
	 * @return
	 */
	public static String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");
	    if(!StringUtils.isEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)){
	         //多次反向代理后会有多个ip值，第一个ip才是真实ip
	    	int index = ip.indexOf(",");
	        if(index != -1){
	            return ip.substring(0,index);
	        }else{
	            return ip;
	        }
	    }
	    ip = request.getHeader("X-Real-IP");
	    if(!StringUtils.isEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)){
	       return ip;
	    }
	    return request.getRemoteAddr();
	}
	
	/**
	 * 企业向个人支付转账
	 * @param request
	 * @param response
	 * @param openid 用户openid
	 * @param callback
	 */
	@PostMapping(value = "/wxPaySome") 
	@ApiOperation(value = "企业向个人转账")
	@Transactional
	public Result transferPay(HttpServletRequest request, HttpServletResponse response,
			String openid,String infoNum,String jine,String phone) {
		logger.info("用户：" + phone + "申请转账中..." + "金额："+jine);
		if(Double.parseDouble(jine)<2){
			return ResultUtil.error(-1, "最低提现金额为2元!");
		}
		
		Map<String,String> gasInfoMap = new HashMap<String,String>();
		
		gasInfoMap = gasService.selectGasByPhone(phone);
		
		Map<String, String> restmap = null;
		try {
			Map<String, String> parm = new HashMap<String, String>();
			parm.put("mch_appid", wxAuth.getAppId()); //公众账号appid
			parm.put("mchid", wxAuth.getMch_id()); //商户号
			parm.put("nonce_str", getRandomStringByLength(32)); //随机字符串
			parm.put("partner_trade_no", getRandomStringByLength(8)); //商户订单号
			parm.put("openid", openid); //用户openid	
			parm.put("check_name", "NO_CHECK"); //校验用户姓名选项 OPTION_CHECK
			//parm.put("re_user_name", "安迪"); //check_name设置为FORCE_CHECK或OPTION_CHECK，则必填
			parm.put("amount", (int)(Double.parseDouble(jine)*99)+""); //转账金额
			parm.put("desc", "豆丁加油提现"); //企业付款描述信息
			parm.put("spbill_create_ip", getIpAddr(request)); //Ip地址
			
			String prestr = PayUtil.createLinkString(parm);
			parm.put("sign", PayUtil.sign(prestr, wxAuth.getKey(),"utf-8"));

			String restxml = SslRequest.doRefund("https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers", XmlUtil.xmlFormat(parm, false));
			// 将解析结果存储在HashMap中   
			restmap = PayUtil.doXMLParse(restxml);
		} catch (Exception e) {
			e.printStackTrace();
			return ResultUtil.error(ResultEnum.UNKNOWN_ERROR.getCode(), ResultEnum.UNKNOWN_ERROR.getMsg());
		}

		if ("SUCCESS".equals(restmap.get("result_code"))) {
			logger.info("用户：" + phone + "转账成功！" + "金额："+jine);
			
		}else {
			if (null != restmap) {
				logger.info("转账失败：" + restmap.get("err_code") + ":" + restmap.get("err_code_des"));
			}
			return ResultUtil.error(-1, restmap.get("err_code_des"));
		}
		jine = '-' + jine;
		
		orderService.updateBalance(infoNum, jine);
		
		orderService.insertAccountFlow(infoNum, jine, phone);
		
		return ResultUtil.success();
	}
}
