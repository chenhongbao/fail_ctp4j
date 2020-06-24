package com.nabiki.ctp4j.trader.internal;

import com.nabiki.ctp4j.struct.CThostFtdcInvestorPositionDetailField;
import com.nabiki.ctp4j.struct.CThostFtdcRspInfoField;

public class RspQryInvestorPositionDetail {
	public CThostFtdcInvestorPositionDetailField InvestorPositionDetail;
	public CThostFtdcRspInfoField RspInfo;
	public int RequestId;
	public boolean IsLast;

	public RspQryInvestorPositionDetail() {
	}
}
