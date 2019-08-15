package com.ets.dictionary.settlement.dao;

import java.util.List;
import java.util.Map;

import com.ets.dictionary.settlement.entity.nb_settlement_dict;

/**
 * 结算方式字典表链接数据库操作类
 * @author WH
 *
 */
public interface SettlementDao {

	/**
	 * 根据条件查询结算方式字典表
	 * @param map
	 * @return
	 */
	List<nb_settlement_dict> selectSettlement(Map<String, Object> map);

	/**
	 * 根据条件查询结算方式字典表总数
	 * @return
	 */
	long selectCount();

	/**
	 * 更新结算方式字段数据
	 * @param Settlement
	 */
	void updateSettlement(nb_settlement_dict settlement);

	/**
	 * 添加结算方式字段数据
	 * @param Settlement
	 */
	void insertSettlement(nb_settlement_dict settlement);

	/**
	 * 根据ID查询结算方式数据字典信息
	 * @param id
	 * @return
	 */
	nb_settlement_dict infoSettlement(String id);

	/**
	 * 根据ID删除结算方式数据字典信息
	 * @param id
	 */
	void deleteSettlement(String[] id);

	List<nb_settlement_dict> infoSettlementList(String[] id);
	long findSettlement(String settlementmethod);

}
