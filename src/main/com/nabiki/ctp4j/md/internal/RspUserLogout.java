package com.nabiki.ctp4j.md.internal;

import com.nabiki.ctp4j.struct.CThostFtdcRspInfoField;
import com.nabiki.ctp4j.struct.CThostFtdcUserLogoutField;

public class RspUserLogout {
	public CThostFtdcUserLogoutField UserLogout;
	public CThostFtdcRspInfoField RspInfo;
	public int RequestId;
	public boolean IsLast;

	public RspUserLogout() {
	}
}
