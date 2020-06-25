package com.nabiki.ctp4j.internal;

import com.nabiki.ctp4j.jni.struct.CThostFtdcRspInfoField;
import com.nabiki.ctp4j.jni.struct.CThostFtdcRspUserLoginField;

public class RspUserLogin {
	public CThostFtdcRspUserLoginField RspUserLogin;
	public CThostFtdcRspInfoField RspInfo;
	public int RequestId;
	public boolean IsLast;

	public RspUserLogin() {
	}
}
