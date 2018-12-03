package com.hsk.task.serviceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hsk.task.bean.GasInfo;
import com.hsk.task.bean.Order;
import com.hsk.task.mapper.GasMapper;
import com.hsk.task.mapper.OrderMapper;
import com.hsk.task.service.GasService;

@Service
public class GasServiceImpl implements GasService {
	
	@Autowired
	GasMapper gasMapper;
	
	@Autowired
	OrderMapper orderMapper;

	@Override
	public List<GasInfo> queryGasList(String page, String pagesize,
			String type, String latitude, String longitude,String order) {
		
		List<GasInfo> list = new ArrayList<GasInfo>();
		List<GasInfo> returnList = new ArrayList<GasInfo>();
		
		Map map = new HashMap();
		map.put("page", Integer.valueOf(page));
		map.put("pagesize", Integer.valueOf(pagesize));
		map.put("type", type);
		map.put("latitude", latitude);
		map.put("longitude", longitude);
		map.put("order", order);
		
		list = gasMapper.queryGasList(map);//查询结果
		
		//若查询距离最近 则把米换成千米或者米
		for(GasInfo gasInfo:list){
			returnList.add(getDistanceStr(gasInfo));
		}
		
		return returnList;
	}
	
	/**
	 * 格式化对象（显示米和千米；将图片地址放入list）
	 * @param gasInfo
	 * @return
	 */
	private GasInfo getDistanceStr(GasInfo  gasInfo){
        try{
	        String detail = "";
	        if(null == gasInfo.getDistance()){
	        	return gasInfo;
	        }
	        double dis = Double.parseDouble(gasInfo.getDistance());
	        double tempDis = dis / 1000;
	        detail = rvZeroAndDot(String.format("%.1f", tempDis));
	        if(Double.parseDouble(detail) >= 1){
	            detail = detail + "Km";
	            gasInfo.setDistance(detail);
	        }else{
	            gasInfo.setDistance((int) Math.ceil(Double.parseDouble(detail) * 1000) + "m");
	        }
	        return gasInfo;
	       }catch(Exception e){
	    	   e.printStackTrace();
	    	   return gasInfo;
	       }
    }
	
	/**
	 * 去掉小数点后多余0
	 * @param s
	 * @return
	 */
	 public static String rvZeroAndDot(String s){  
	       if (s.isEmpty()) {
	    	   return null;
		     }
	         if(s.indexOf(".") > 0){  
	         s = s.replaceAll("0+?$", "");//去掉多余的0  
	         s = s.replaceAll("[.]$", "");//如最后一位是.则去掉  
	         }  
	        return s;  
   }  

	@Override
	public List<Order> queryOrderList(String page, String pagesize,
			String phone,String id) {
		List<Order> list = new ArrayList<Order>();
		
		Map map = new HashMap();
		map.put("page", Integer.valueOf(page));
		map.put("pagesize", Integer.valueOf(pagesize));
		map.put("phone", phone);
		map.put("id", id);
		
		list = orderMapper.queryOrderList(map);//查询结果

		return list;
	}

	@Override
	public GasInfo selectOilById(String infoNum,String type) {
		Map map = new HashMap();
		map.put("infoNum",infoNum);
		map.put("type",type);
		// TODO Auto-generated method stub
		return gasMapper.selectOilById(map);
	}

	@Override
	public List<Map> queryGasPrices(String infoNum) {
		return gasMapper.queryGasPrices(infoNum);
	}

	@Override
	public int updatePrice(String id, String price,String saveprice) {
		Map map = new HashMap();
		map.put("id", id);
		map.put("price", price);
		map.put("saveprice", saveprice);
		return gasMapper.updatePrice(map);
	}

	@Override
	public List<Map> queryOilTypes(String infoNum) {
		return gasMapper.queryOilTypes(infoNum);
	}

	@Override
	public Map selectGasByPhone(String phone) {
		return gasMapper.selectGasByPhone(phone);
	}
}
