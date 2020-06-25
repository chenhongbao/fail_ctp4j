package com.nabiki.ctp4j.internal;

import com.nabiki.ctp4j.jni.struct.CThostFtdcRspInfoField;

public class RspError {
	public CThostFtdcRspInfoField RspInfo;
	public int RequestId;
	public boolean IsLast;

	public RspError() {
	}
}
