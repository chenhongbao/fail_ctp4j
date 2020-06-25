package com.nabiki.ctp4j.trader.internal;

import com.nabiki.ctp4j.jni.struct.CThostFtdcInstrumentMarginRateField;
import com.nabiki.ctp4j.jni.struct.CThostFtdcRspInfoField;

public class RspQryInstrumentMarginRate {
	public CThostFtdcInstrumentMarginRateField InstrumentMarginRate;
	public CThostFtdcRspInfoField RspInfo;
	public int RequestId;
	public boolean IsLast;

	public RspQryInstrumentMarginRate() {
	}
}
