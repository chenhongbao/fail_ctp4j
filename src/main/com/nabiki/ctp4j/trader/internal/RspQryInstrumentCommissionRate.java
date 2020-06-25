package com.nabiki.ctp4j.trader.internal;

import com.nabiki.ctp4j.jni.struct.CThostFtdcInstrumentCommissionRateField;
import com.nabiki.ctp4j.jni.struct.CThostFtdcRspInfoField;

public class RspQryInstrumentCommissionRate {
	public CThostFtdcInstrumentCommissionRateField InstrumentCommissionRate;
	public CThostFtdcRspInfoField RspInfo;
	public int RequestId;
	public boolean IsLast;

	public RspQryInstrumentCommissionRate() {
	}
}
