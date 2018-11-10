package com.hsk.task.utils;
	
import java.net.URLEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SendMsg {
	
	private Logger logger = LoggerFactory.getLogger(SendMsg.class);  
	
	@Value("${global.msg_accountSid}")
    private String msg_accountSid;
	
	@Value("${global.msg_baseUrl}")
	private String msg_baseUrl;
	
    /**
     * 验证码通知短信
     */
    public void execute(String phone,String code)
    {
        String tmpSmsContent = null;
        try{
          tmpSmsContent = URLEncoder.encode("【未来商贸】登录验证码："+code+"，如非本人操作，请忽略此短信。", "UTF-8");
        }catch(Exception e){
        }
        String url = msg_baseUrl;
        String body = "accountSid=" + msg_accountSid + "&to=" + phone + "&smsContent="
        + tmpSmsContent
            + HttpRequest.createCommonParam();
        // 提交请求
        String result = HttpRequest.sendPost(url, body);
        logger.info("result:" + System.lineSeparator() + result);
    }
}
