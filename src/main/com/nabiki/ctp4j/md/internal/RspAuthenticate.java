package com.nabiki.ctp4j.md.internal;

import com.nabiki.ctp4j.struct.CThostFtdcRspAuthenticateField;
import com.nabiki.ctp4j.struct.CThostFtdcRspInfoField;

public class RspAuthenticate {
	public CThostFtdcRspAuthenticateField RspAuthenticateField;
	public CThostFtdcRspInfoField RspInfo;
	public int RequestId;
	public boolean IsLast;

	public RspAuthenticate() {
	}
}
