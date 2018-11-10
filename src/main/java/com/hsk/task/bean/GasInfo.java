package com.hsk.task.bean;

import lombok.Data;

@Data
public class GasInfo {
	
	private int id;
	private int infoNum;//哪个油站
	private String name;
	private String imgUrl;
	private String address;//地址
	private String type;//型号
	private String price;//价格
	private String savePrice;//优惠价格
	private String marketPrice;//市场价格
	private String order_count;//订单数
	private String distance;//距离
	private String latitude;//纬度
	private String longitude;//经度
	private String shoots;//枪列表(逗号分隔)
}
