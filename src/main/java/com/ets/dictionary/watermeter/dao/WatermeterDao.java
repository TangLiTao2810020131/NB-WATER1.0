package com.ets.dictionary.watermeter.dao;

import java.util.List;
import java.util.Map;

import com.ets.dictionary.watermeter.entity.nb_watermeter_dict;


public interface WatermeterDao {

	List<nb_watermeter_dict> selectWatermeter(Map<String, Object> map);

	long selectCount();

	void updateWatermeter(nb_watermeter_dict watermeter);

	void insertWatermeter(nb_watermeter_dict watermeter);

	nb_watermeter_dict infoWatermeter(String id);

	void deleteWatermeter(String[] id);

	List<nb_watermeter_dict> infoWatermeterList(String[] id);

	List<nb_watermeter_dict> selectWatermeterAll();
	
	nb_watermeter_dict selectWMTypeCusId(Map<String, String> map);

}
