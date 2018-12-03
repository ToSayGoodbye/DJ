package com.hsk.task.service;

import java.util.List;
import java.util.Map;

public interface OrderService {
	Integer insertOrder(Map map);
	
	String selectBalance(String infoNum);
	
	int updateBalance(String infoNum,String jine);
	
	int insertAccountFlow(String infoNum,String price,String phone);
	
	int addCount(String infoNum);
	
	List queryJilu(String infoNum,String page,String pageSize);
	
	List queryFiveOrder(String infoNum);
}
