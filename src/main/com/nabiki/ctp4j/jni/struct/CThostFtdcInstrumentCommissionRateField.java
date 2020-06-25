package com.nabiki.ctp4j.jni.struct;

import java.io.Serializable;

public class CThostFtdcInstrumentCommissionRateField implements Serializable {
	public String InstrumentID;
	public byte InvestorRange;
	public String BrokerID;
	public String InvestorID;
	public double OpenRatioByMoney;
	public double OpenRatioByVolume;
	public double CloseRatioByMoney;
	public double CloseRatioByVolume;
	public double CloseTodayRatioByMoney;
	public double CloseTodayRatioByVolume;
	public String ExchangeID;
	public byte BizType;
	public String InvestUnitID;

	public CThostFtdcInstrumentCommissionRateField() {
	}
}
