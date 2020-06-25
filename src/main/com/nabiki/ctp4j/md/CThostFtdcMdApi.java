package com.nabiki.ctp4j.md;

import com.nabiki.ctp4j.jni.struct.CThostFtdcReqUserLoginField;
import com.nabiki.ctp4j.jni.struct.CThostFtdcUserLogoutField;
import com.nabiki.ctp4j.trader.CThostFtdcTraderApi;

public abstract class CThostFtdcMdApi {
    protected CThostFtdcMdApi() {
    }

    /**
     * Create new md api instance. The flow path {@link String} must points to a
     * valid directory path, or the native code would fail.
     *
     * <p>The combination of udp and multicast parameters apply to different md
     * front:
     * </p>
     * <ul>
     * <li>TCP, udp: {@code false}, multicast: {@code false}
     * <li>UDP, udp: {@code true}, multicast: {@code false}
     * <li>Multicast, udp: {@code true}, multicast: {@code true}
     * </ul>
     *
     * @param flowPath  path to the directory where native code keeps flow data
     * @param isUsingUdp UDP or TCP
     * @param isMulticast multicast or unicast
     * @return new md api instance
     */
    public static CThostFtdcMdApi CreateFtdcMdApi(
            String flowPath, boolean isUsingUdp, boolean isMulticast) {
        return new CThostFtdcMdApiImpl(flowPath, isUsingUdp, isMulticast);
    }

    /**
     * Get API version.
     *
     * @return API version string
     */
    public abstract String GetApiVersion();

    /**
     * Get trading day in string. If the session is not logined, the method returns
     * null.
     *
     * @return trading day string
     */
    public abstract String GetTradingDay();

    /**
     * Initialize session to remote counter. It is not login yet after the method
     * returns.
     */
    public abstract void Init();

    /**
     * Wait until the session is released. The method returns after
     * {@link CThostFtdcTraderApi#Release()} is called.
     */
    public abstract void Join();

    /**
     * Register front address for the session. Client can have more than one front
     * addresses and the native client chooses randomly to connect the remote counter.
     *
     * @param frontAddress front address in the format {@code tcp://127.0.0.1:40010}
     */
    public abstract void RegisterFront(String frontAddress);

    /**
     * Register SPI for the trader session.
     *
     * @param spi callback SPI for responses
     */
    public abstract void RegisterSpi(CThostFtdcMdSpi spi);

    /**
     * Release the native resources for the trader session.
     */
    public abstract void Release();

    /**
     * Request client login for the specified session. {@code OnRspUserLogin} is
     * called on login response.
     *
     * <p>The method doesn't throw exception.
     *
     * @param reqUserLoginField login request
     * @param requestId         identifier for this request
     * @return returned value from native function
     */
    public abstract int ReqUserLogin(CThostFtdcReqUserLoginField reqUserLoginField,
                                     int requestId);

    /**
     * Request client logout for the specified session. {@code OnRspUserLogout} is
     * called on logout response.
     *
     * <p>The method doesn't throw exception.
     *
     * @param userLogout logout request
     * @param requestId  identifier for this request
     * @return returned value from native function
     */
    public abstract int ReqUserLogout(CThostFtdcUserLogoutField userLogout,
                                      int requestId);

    /**
     * Subscribe the specified instruments from ma front. The instrument count is
     * not trivial and it is required by native code.
     *
     * @param instrumentID array of instruments to subscribe
     * @param count number of instruments in the specified array
     * @return returned value from native function
     */
    public abstract int SubscribeMarketData(String[] instrumentID, int count);

    /**
     * Un-subscribe the specified instruments from ma front. The instrument count is
     * not trivial and it is required by native code.
     *
     * @param instrumentID array of instruments to subscribe
     * @param count number of instruments in the specified array
     * @return returned value from native function
     */
    public abstract int UnSubscribeMarketData(String[] instrumentID, int count);
}
