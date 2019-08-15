package com.ets.business.ownerrecord.dao;

import java.util.*;

import com.ets.business.ownerrecord.entity.nb_owner_record;

/**
 * @author 吴浩
 * @create 2019-02-22 14:35
 */
public interface OwnerRecordDao {
	
	public List<nb_owner_record> getOwnerRecordList(Map<String,Object> map);
	
	public long getCount(Map<String,Object> map);

	public nb_owner_record getOwnerRecord(Map<String, Object> map);

	public void insertOwnerRecord(nb_owner_record entity);

	public void updateOwnerRecord(nb_owner_record entity);
}
