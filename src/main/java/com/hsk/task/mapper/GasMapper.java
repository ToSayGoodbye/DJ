package com.hsk.task.mapper;

import java.util.List;
import java.util.Map;

import com.hsk.task.bean.GasInfo;

public interface GasMapper {
	List<GasInfo> queryGasList(Map map);
	
	GasInfo selectOilById(Map map);
	
	List<Map> queryGasPrices(String infoNum);

	int updatePrice(Map map);
	
	Map selectGasByPhone(String phone);
	
	List<Map> queryOilTypes(String infoNum);
}
