package com.ets.dictionary.settlement.service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ets.common.UUIDUtils;
import com.ets.dictionary.settlement.entity.nb_settlement_dict;
import com.ets.dictionary.settlement.dao.SettlementDao;

/**
 * 结算方式字典表操作数据库类
 * @author Administrator
 *
 */
@Service
@Transactional
public class SettlementService {
	
	@Resource
	SettlementDao settlementDao;

	/**
	 * 根据条件查询结算方式字典表
	 * @param map
	 * @return
	 */
	public List<nb_settlement_dict> getSettlement(Map<String, Object> map) {
		try {
			return settlementDao.selectSettlement(map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	

	/**
	 * 根据条件查询结算方式字典表总数
	 * @return
	 */
	public long getCount() {
		try {
			return settlementDao.selectCount();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 更新结算方式字段数据
	 * @param settlement
	 */
	public int opentionSettlement(nb_settlement_dict settlement) {
		int result=1;
		long num = settlementDao.findSettlement(settlement.getSettlementmethod());
		try {
			if(settlement.getId()!=null && !settlement.getId().equals(""))
			{
				settlementDao.updateSettlement(settlement);
				return result=-1;			}
			else {
				if (num==0) {
					settlement.setId(UUIDUtils.getUUID());
					settlement.setCtime(new Timestamp(System.currentTimeMillis()));
					settlement.setRemarks(settlement.getDescribe());
					settlementDao.insertSettlement(settlement);
					return result=0;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 根据ID查询结算方式数据字典信息
	 * @param id
	 * @return
	 */
	public nb_settlement_dict infoSettlement(String id) {
		try {
			return settlementDao.infoSettlement(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	/**
	 * 根据ID删除结算方式数据字典信息
	 * @param id
	 */
	public void deleteSettlement(String[] id) {
		try {
			settlementDao.deleteSettlement(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public String infoSettlement(String[] id) {
		try {
			String settlementmethod = "";
			List<nb_settlement_dict> s = settlementDao.infoSettlementList(id);
			if(s.size() > 0){
				for (nb_settlement_dict settlemen : s) {
					settlementmethod += settlemen.getSettlementmethod()+",";
				}
			}
			return settlementmethod.substring(0,settlementmethod.length()-1);
					
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
