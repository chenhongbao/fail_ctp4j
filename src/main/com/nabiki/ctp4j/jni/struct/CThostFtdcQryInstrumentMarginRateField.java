package com.nabiki.ctp4j.jni.struct;

import java.io.Serializable;

public class CThostFtdcQryInstrumentMarginRateField implements Serializable {
	public String BrokerID;
	public String InvestorID;
	public String InstrumentID;
	public byte HedgeFlag;
	public String ExchangeID;
	public String InvestUnitID;

	public CThostFtdcQryInstrumentMarginRateField() {
	}
}
