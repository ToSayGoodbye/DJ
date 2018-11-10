package com.hsk.task.bean;

import lombok.Data;

@Data
public class Order {
	private int id;
	private String info_id;//哪个站
	private String phone;//
	private String type;//油类型
	private String shootNum;//哪个枪
	private String real_price;//实付金额
	private String save_price;//优惠金额
	private String insertDate;//插入时间
	private String time_end;//微信支付完成时间
	private String transaction_id;//微信支付订单号
	private String bank_type;//付款银行
	private String out_trade_no;//商户订单号
	private String openid;
	private String name;//油站名称
}
