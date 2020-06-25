package com.nabiki.ctp4j.md.internal;

import com.nabiki.ctp4j.internal.*;

import java.util.List;

public class MdChannelData {
	public List<FrontConnect> ListConnect;

	public List<FrontDisconnect> ListDisconnect;

	public List<RspAuthenticate> ListRspAuthenticate;

	public List<RspError> ListRspError;

	public List<RspUserLogin> ListRspUserLogin;

	public List<RspUserLogout> ListRspUserLogout;

	public List<RtnDepthMarketData> ListRtnDepthMarketData;

	public List<RspSubMarketData> ListRspSubMarketData;

	public List<RspUnSubMarketData> ListRspUnSubMarketData;
}
