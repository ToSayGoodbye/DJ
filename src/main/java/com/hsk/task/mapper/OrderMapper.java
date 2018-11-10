package com.hsk.task.mapper;

import java.util.List;
import java.util.Map;

import com.hsk.task.bean.GasInfo;
import com.hsk.task.bean.Order;

public interface OrderMapper {
	Integer insertOrder(Map map);
	
	List<Order> queryOrderList(Map map);
	
	String selectBalance(String infoNum);
	
	int updateBalance(Map map);
	
	int insertAccountFlow(Map map);
	
	int addCount(String infoNum);
	

}
