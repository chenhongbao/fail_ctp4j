package com.nabiki.ctp4j.md.internal;

import com.nabiki.ctp4j.struct.CThostFtdcConnect;
import com.nabiki.ctp4j.struct.CThostFtdcDepthMarketDataField;
import com.nabiki.ctp4j.struct.CThostFtdcDisconnect;

import java.util.List;

public class MdChannelData {
	public List<CThostFtdcConnect> ListConnect;

	public List<CThostFtdcDisconnect> ListDisconnect;

	public List<RspAuthenticate> ListRspAuthenticate;

	public List<RspError> ListRspError;

	public List<RspUserLogin> ListRspUserLogin;

	public List<RspUserLogout> ListRspUserLogout;

	public List<CThostFtdcDepthMarketDataField> ListRtnDepthMarketData;

	public List<RspSubMarketData> ListRspSubMarketData;

	public List<RspUnSubMarketData> ListRspUnSubMarketData;
}
