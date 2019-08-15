package com.ets.dictionary.settlement.entity;

import java.util.List;

import com.ets.common.Page;


/**
 * 分页集合
 * @author WH
 *
 */
public class SettlementListPageData extends Page{
	
	private List<nb_settlement_dict> data;

	public List<nb_settlement_dict> getData() {
		return data;
	}

	public void setData(List<nb_settlement_dict> data) {
		this.data = data;
	}
}
