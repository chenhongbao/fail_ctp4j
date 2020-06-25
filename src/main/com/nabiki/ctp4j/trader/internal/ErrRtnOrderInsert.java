package com.nabiki.ctp4j.trader.internal;

import com.nabiki.ctp4j.jni.struct.CThostFtdcInputOrderField;
import com.nabiki.ctp4j.jni.struct.CThostFtdcRspInfoField;

public class ErrRtnOrderInsert {
	public CThostFtdcInputOrderField InputOrder;
	public CThostFtdcRspInfoField RspInfo;

	public ErrRtnOrderInsert() {
	}
}
