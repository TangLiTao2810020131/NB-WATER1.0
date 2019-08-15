package com.ets.dictionary.userpro.entity;

import java.util.List;

import com.ets.common.Page;


/**
 * 分页集合
 * @author WH
 *
 */
public class UserProListPageData extends Page{
	
	private List<nb_userpro_dict> data;

	public List<nb_userpro_dict> getData() {
		return data;
	}

	public void setData(List<nb_userpro_dict> data) {
		this.data = data;
	}
}
