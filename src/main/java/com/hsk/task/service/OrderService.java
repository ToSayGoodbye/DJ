package com.hsk.task.service;

import java.util.Map;

public interface OrderService {
	Integer insertOrder(Map map);
	
	String selectBalance(String infoNum);
	
	int updateBalance(String infoNum,String jine);
	
	int insertAccountFlow(String infoNum,String price,String phone);
	
	int addCount(String infoNum);
}
