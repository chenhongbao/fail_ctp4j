package com.nabiki.ctp4j.trader.internal;

import com.nabiki.ctp4j.struct.CThostFtdcInputOrderActionField;
import com.nabiki.ctp4j.struct.CThostFtdcRspInfoField;

public class RspOrderAction {
	public CThostFtdcInputOrderActionField InputOrderAction;
	public CThostFtdcRspInfoField RspInfo;
	public int RequestId;
	public boolean IsLast;

	public RspOrderAction() {
	}
}
