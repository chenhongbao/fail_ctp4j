package com.nabiki.ctp4j.internal;

import com.nabiki.ctp4j.jni.struct.CThostFtdcRspInfoField;
import com.nabiki.ctp4j.jni.struct.CThostFtdcUserLogoutField;

public class RspUserLogout {
	public CThostFtdcUserLogoutField UserLogout;
	public CThostFtdcRspInfoField RspInfo;
	public int RequestId;
	public boolean IsLast;

	public RspUserLogout() {
	}
}
