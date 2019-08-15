package com.ets.business.nb_iot.hac.model;

public class Signal {
	private String rssi;//信号强度
	private String snr;//信噪比
	private String bn;// /99/0
	public String getRssi() {
		return rssi;
	}
	public void setRssi(String rssi) {
		this.rssi = rssi;
	}
	public String getSnr() {
		return snr;
	}
	public void setSnr(String snr) {
		this.snr = snr;
	}
	public String getBn() {
		return bn;
	}
	public void setBn(String bn) {
		this.bn = bn;
	}
	@Override
	public String toString() {
		return "Signal [rssi=" + rssi + ", snr=" + snr + ", bn=" + bn + "]";
	}
	
	
}
