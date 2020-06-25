package com.nabiki.ctp4j.trader.internal;

import com.nabiki.ctp4j.jni.struct.CThostFtdcOrderActionField;
import com.nabiki.ctp4j.jni.struct.CThostFtdcRspInfoField;

public class ErrRtnOrderAction {
	public CThostFtdcOrderActionField OrderAction;
	public CThostFtdcRspInfoField RspInfo;

	public ErrRtnOrderAction() {
	}
}
