package com.nabiki.ctp4j.jni.struct;

import java.io.Serializable;

public class CThostFtdcReqAuthenticateField implements Serializable {
	public String BrokerID;
	public String UserID;
	public String UserProductInfo;
	public String AuthCode;
	public String AppID;

	public CThostFtdcReqAuthenticateField() {
	}
}
