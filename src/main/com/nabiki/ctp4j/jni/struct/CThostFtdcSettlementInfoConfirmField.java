package com.nabiki.ctp4j.jni.struct;

import java.io.Serializable;

public class CThostFtdcSettlementInfoConfirmField implements Serializable {
	public String BrokerID;
	public String InvestorID;
	public String ConfirmDate;
	public String ConfirmTime;
	public int SettlementID;
	public String AccountID;
	public String CurrencyID;

	public CThostFtdcSettlementInfoConfirmField() {
	}
}
