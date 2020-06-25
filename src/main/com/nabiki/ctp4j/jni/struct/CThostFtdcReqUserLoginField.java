package com.nabiki.ctp4j.jni.struct;

import java.io.Serializable;

public class CThostFtdcReqUserLoginField implements Serializable {
	public String TradingDay;
	public String BrokerID;
	public String UserID;
	public String Password;
	public String UserProductInfo;
	public String InterfaceProductInfo;
	public String ProtocolInfo;
	public String MacAddress;
	public String OneTimePassword;
	public String ClientIPAddress;
	public String LoginRemark;
	public int ClientIPPort;

	public CThostFtdcReqUserLoginField() {
	}
}
