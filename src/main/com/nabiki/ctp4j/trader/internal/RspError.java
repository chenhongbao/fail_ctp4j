package com.nabiki.ctp4j.trader.internal;

import com.nabiki.ctp4j.struct.CThostFtdcRspInfoField;

public class RspError {
	public CThostFtdcRspInfoField RspInfo;
	public int RequestId;
	public boolean IsLast;

	public RspError() {
	}
}
