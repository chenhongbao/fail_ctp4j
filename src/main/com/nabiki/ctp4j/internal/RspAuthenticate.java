package com.nabiki.ctp4j.internal;

import com.nabiki.ctp4j.jni.struct.CThostFtdcRspAuthenticateField;
import com.nabiki.ctp4j.jni.struct.CThostFtdcRspInfoField;

public class RspAuthenticate {
	public CThostFtdcRspAuthenticateField RspAuthenticateField;
	public CThostFtdcRspInfoField RspInfo;
	public int RequestId;
	public boolean IsLast;

	public RspAuthenticate() {
	}
}
