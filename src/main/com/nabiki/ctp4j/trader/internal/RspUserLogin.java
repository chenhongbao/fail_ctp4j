package com.nabiki.ctp4j.trader.internal;

import com.nabiki.ctp4j.struct.CThostFtdcRspInfoField;
import com.nabiki.ctp4j.struct.CThostFtdcRspUserLoginField;

public class RspUserLogin {
	public CThostFtdcRspUserLoginField RspUserLogin;
	public CThostFtdcRspInfoField RspInfo;
	public int RequestId;
	public boolean IsLast;

	public RspUserLogin() {
	}
}
