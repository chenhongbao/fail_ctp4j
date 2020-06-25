package com.nabiki.ctp4j.jni.struct;

import java.io.Serializable;

public class CThostFtdcInstrumentMarginRateField implements Serializable {
	public String InstrumentID;
	public byte InvestorRange;
	public String BrokerID;
	public String InvestorID;
	public byte HedgeFlag;
	public double LongMarginRatioByMoney;
	public double LongMarginRatioByVolume;
	public double ShortMarginRatioByMoney;
	public double ShortMarginRatioByVolume;
	public int IsRelative;
	public String ExchangeID;
	public String InvestUnitID;

	public CThostFtdcInstrumentMarginRateField() {
	}
}
