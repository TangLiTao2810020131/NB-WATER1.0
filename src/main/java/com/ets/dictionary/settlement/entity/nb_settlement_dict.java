package com.ets.dictionary.settlement.entity;

import java.sql.Timestamp;

/**
 * 接入类型字典表实体类
 * @author WH
 *
 */
public class nb_settlement_dict {
	  private String id;
	  private String dictype;
	  private String code;
	  private String settlementmethod;
	  private String settlementrules;
	  private String describe;
	  private Timestamp  ctime;
	  private String  remarks;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDictype() {
		return dictype;
	}
	public void setDictype(String dictype) {
		this.dictype = dictype;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getSettlementmethod() {
		return settlementmethod;
	}
	public void setSettlementmethod(String settlementmethod) {
		this.settlementmethod = settlementmethod;
	}
	public String getSettlementrules() {
		return settlementrules;
	}
	public void setSettlementrules(String settlementrules) {
		this.settlementrules = settlementrules;
	}
	public String getDescribe() {
		return describe;
	}
	public void setDescribe(String describe) {
		this.describe = describe;
	}
	public Timestamp getCtime() {
		return ctime;
	}
	public void setCtime(Timestamp ctime) {
		this.ctime = ctime;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
}
