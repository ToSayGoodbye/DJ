package com.hsk.task.serviceImpl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hsk.task.mapper.MessageMapper;
import com.hsk.task.service.MessageService;

@Service
public class MessageServiceImpl implements MessageService {

	@Autowired
	MessageMapper messageMapper;
	
	@Override
	public int addMessage(String content, String phone) {
		
		Map map = new HashMap();
		map.put("content", content);
		map.put("phone", phone);
		
		messageMapper.addMessage(map);
		
		return 0;
	}

}
