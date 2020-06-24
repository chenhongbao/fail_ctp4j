package com.nabiki.ctp4j.md.internal;

import com.nabiki.ctp4j.struct.CThostFtdcRspInfoField;
import com.nabiki.ctp4j.struct.CThostFtdcSpecificInstrumentField;

public class RspUnSubMarketData {
	public CThostFtdcSpecificInstrumentField SpecificInstrument;
	public CThostFtdcRspInfoField RspInfo;
	public int RequestId;
	public boolean IsLast;

	public RspUnSubMarketData() {
	}
}