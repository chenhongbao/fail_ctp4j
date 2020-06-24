package com.nabiki.ctp4j.trader.internal;

import com.nabiki.ctp4j.struct.CThostFtdcInputOrderField;
import com.nabiki.ctp4j.struct.CThostFtdcRspInfoField;

public class RspOrderInsert {
	public CThostFtdcInputOrderField InputOrder;
	public CThostFtdcRspInfoField RspInfo;
	public int RequestId;
	public boolean IsLast;

	public RspOrderInsert() {
	}
}
