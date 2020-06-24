package com.nabiki.ctp4j.md;

import com.nabiki.ctp4j.struct.CThostFtdcReqUserLoginField;
import com.nabiki.ctp4j.struct.CThostFtdcUserLogoutField;

public abstract class CThostFtdcMdApi {
    protected CThostFtdcMdApi() {
    }

    public static CThostFtdcMdApi CreateFtdcMdApi(String szFlowPath, boolean isUsingUdp, boolean isMulticast) {
        return new CThostFtdcMdApiImpl(szFlowPath, isUsingUdp, isMulticast);
    }

    public abstract String GetApiVersion();

    public abstract String GetTradingDay();

    public abstract void Init();

    public abstract void Join();

    public abstract void RegisterFront(String szFrontAddress);

    public abstract void RegisterSpi(CThostFtdcMdSpi spi);

    public abstract void Release();

    public abstract int ReqUserLogin(CThostFtdcReqUserLoginField reqUserLoginField, int requestId);

    public abstract int ReqUserLogout(CThostFtdcUserLogoutField userLogout, int requestId);

    public abstract int SubscribeMarketData(String[] instrumentID, int count);

    public abstract int UnSubscribeMarketData(String[] instrumentID, int count);
}
