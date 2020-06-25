package com.nabiki.ctp4j.jni.struct;

import java.io.Serializable;

public class CThostFtdcRspUserLoginField implements Serializable {
	public String TradingDay;
	public String LoginTime;
	public String BrokerID;
	public String UserID;
	public String SystemName;
	public int FrontID;
	public int SessionID;
	public String MaxOrderRef;
	public String SHFETime;
	public String DCETime;
	public String CZCETime;
	public String FFEXTime;
	public String INETime;

	public CThostFtdcRspUserLoginField() {
	}
}
