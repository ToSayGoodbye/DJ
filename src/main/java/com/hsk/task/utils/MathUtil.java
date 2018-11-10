package com.hsk.task.utils;

import java.util.UUID;

public class MathUtil {
	
	/**
	 * 生成6位验证码
	 * @return
	 */
    public static String smsCode(){
        String random=(int)((Math.random()*9+1)*1000)+""; 
        return random;
    }
    
    /**
     * 获得一个UUID 
     * @return String UUID 
     */
    public static String getUUID(){
        String uuid = UUID.randomUUID().toString();
        //去掉“-”符号 
        return uuid.replaceAll("-", "");
    }
    
    /**
     * 针对微信支付生成商户订单号，为了避免微信商户订单号重复（下单单位支付），
     *
     * @return
     */
    public static String generateOrderSN() {
        StringBuffer orderSNBuffer = new StringBuffer();
        orderSNBuffer.append(System.currentTimeMillis());
        orderSNBuffer.append((int)((Math.random()*9+1)*10000)+"");
        return orderSNBuffer.toString();
    }
}
