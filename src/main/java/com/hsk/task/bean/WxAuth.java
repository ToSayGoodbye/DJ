package com.hsk.task.bean;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Component 
@ConfigurationProperties(prefix = "wxapp")
@Data
public class WxAuth {
	private String appId;
	
	private String secret;
	
	private String grantType;
	
	private String sessionHost;
	
	private String mch_id;//微信支付的商户id
	
	private String key;//微信支付的商户密钥
	
	private String notify_url;//支付成功后的服务器回调url
	
	private String signtype;//签名方式，固定值MD5
	
	private String tradetype;//交易类型，小程序支付的固定值为JSAPI
	
	private String pay_url;//微信统一下单接口地址
}
