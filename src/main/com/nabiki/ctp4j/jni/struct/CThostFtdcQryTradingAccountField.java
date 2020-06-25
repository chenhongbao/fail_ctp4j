package com.nabiki.ctp4j.jni.struct;

import java.io.Serializable;

public class CThostFtdcQryTradingAccountField implements Serializable {
	public String BrokerID;
	public String InvestorID;
	public String CurrencyID;
	public byte BizType;
	public String AccountID;

	public CThostFtdcQryTradingAccountField() {
	}
}
