package com.hsk.task.service;

import java.util.List;
import java.util.Map;

import com.hsk.task.bean.GasInfo;
import com.hsk.task.bean.Order;

public interface GasService {
	List<GasInfo> queryGasList(String page,String pagesize,String type,
			String latitude,String longitude,String order);
	
	List<Order> queryOrderList(String page,String pagesize,String phone,String id);
	
	GasInfo selectOilById(String infoNum,String type);
	
	List<Map> queryGasPrices(String infoNum);
	
	int updatePrice(String id,String price,String saveprice);

	List<Map> queryOilTypes(String infoNum);
	
	Map selectGasByPhone(String phone);
	
	int insertUser(String phone,String rePhone);
}
