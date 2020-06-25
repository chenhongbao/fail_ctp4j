package com.nabiki.ctp4j.trader.internal;

import com.nabiki.ctp4j.jni.struct.CThostFtdcInstrumentField;
import com.nabiki.ctp4j.jni.struct.CThostFtdcRspInfoField;

public class RspQryInstrument {
	public CThostFtdcInstrumentField Instrument;
	public CThostFtdcRspInfoField RspInfo;
	public int RequestId;
	public boolean IsLast;

	public RspQryInstrument() {
	}
}
