package com.hsk.task.serviceImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hsk.task.mapper.OrderMapper;
import com.hsk.task.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	OrderMapper orderMapper;
	
	@Override
	public Integer insertOrder(Map map) {
		return orderMapper.insertOrder(map);
	}

	@Override
	public String selectBalance(String infoNum) {
		return orderMapper.selectBalance(infoNum);
	}
	
	@Override
	public String selectJiangLi(String phone) {
		return orderMapper.selectJiangLi(phone);
	}

	@Override
	public int updateBalance(String infoNum, String jine) {
		Map map = new HashMap();
		map.put("infoNum", infoNum);
		map.put("jine", jine);
		return orderMapper.updateBalance(map);
	}

	@Override
	public int insertAccountFlow(String infoNum, String price, String phone) {
		Map map = new HashMap();
		map.put("infoNum", infoNum);
		map.put("price", price);
		map.put("phone", phone);
		return orderMapper.insertAccountFlow(map);
	}

	@Override
	public int addCount(String infoNum) {
		return orderMapper.addCount(infoNum);
	}

	@Override
	public List queryJilu(String infoNum,String page,String pageSize) {
		Map map = new HashMap();
		map.put("page", Integer.valueOf(page));
		map.put("pagesize", Integer.valueOf(pageSize));
		map.put("infoNum", infoNum);
		return orderMapper.queryJilu(map);
	}

	@Override
	public List queryFiveOrder(String infoNum) {
		Map map = new HashMap();
		map.put("infoNum", infoNum);
		return orderMapper.queryFiveOrder(map);
	}

}
