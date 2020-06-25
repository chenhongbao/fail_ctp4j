package com.nabiki.ctp4j.trader.internal;

import com.nabiki.ctp4j.jni.struct.CThostFtdcRspInfoField;
import com.nabiki.ctp4j.jni.struct.CThostFtdcSettlementInfoConfirmField;

public class RspSettlementInfoConfirm {
	public CThostFtdcSettlementInfoConfirmField SettlementInfoConfirm;
	public CThostFtdcRspInfoField RspInfo;
	public int RequestId;
	public boolean IsLast;

	public RspSettlementInfoConfirm() {
	}
}
