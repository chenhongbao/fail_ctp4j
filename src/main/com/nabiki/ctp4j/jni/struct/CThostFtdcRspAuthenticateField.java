package com.nabiki.ctp4j.jni.struct;

import java.io.Serializable;

public class CThostFtdcRspAuthenticateField implements Serializable {
	public String BrokerID;
	public String UserID;
	public String UserProductInfo;
	public String AppID;
	public byte AppType;

	public CThostFtdcRspAuthenticateField() {
	}
}
