package com.ets.dictionary.district.entity;

import java.util.List;

import com.ets.common.Page;

/**
 * @author: 姚轶文
 * @date:2018年11月1日 下午6:39:26
 * @version :
 *  
 */
public class ProvinceListPageData extends Page{
	
	private List<tb_province> data;

	public List<tb_province> getData() {
		return data;
	}

	public void setData(List<tb_province> data) {
		this.data = data;
	}
	
	

}
