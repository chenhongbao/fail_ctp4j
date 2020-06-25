package com.nabiki.ctp4j.trader.internal;

import com.nabiki.ctp4j.jni.struct.CThostFtdcRspInfoField;
import com.nabiki.ctp4j.jni.struct.CThostFtdcTradingAccountField;

public class RspQryTradingAccount {
	public CThostFtdcTradingAccountField TradingAccount;
	public CThostFtdcRspInfoField RspInfo;
	public int RequestId;
	public boolean IsLast;

	public RspQryTradingAccount() {
	}
}
