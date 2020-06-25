package com.nabiki.ctp4j.md;

import com.nabiki.ctp4j.jni.CtpNatives;
import com.nabiki.ctp4j.jni.struct.CThostFtdcReqUserLoginField;
import com.nabiki.ctp4j.jni.struct.CThostFtdcUserLogoutField;
import com.nabiki.ctp4j.md.internal.MdChannelReader;
import com.nabiki.ctp4j.md.internal.MdLoginProfile;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;

public class CThostFtdcMdApiImpl extends CThostFtdcMdApi implements AutoCloseable {
    private final static String apiVersion = "0.0.1";

    // Front address pattern.
    private final Pattern addressRegex = Pattern
            .compile("tcp://((\\d{1})|([1-9]\\d{1,2}))(\\.((\\d{1})|([1-9]\\d{1,2}))){3}:\\d+");
    // Join/Release.
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition cond = lock.newCondition();
    private final MdLoginProfile profile = new MdLoginProfile();

    private int sessionId, channelId;
    private CThostFtdcMdSpi spi;
    private MdChannelReader channelReader;

    CThostFtdcMdApiImpl(String szFlowPath, boolean isUsingUdp, boolean isMulticast) {
        this.profile.FlowPath = szFlowPath;
        this.profile.isUsingUdp = isUsingUdp;
        this.profile.isMulticast = isMulticast;
    }

    @Override
    public String GetApiVersion() {
        return CThostFtdcMdApiImpl.apiVersion;
    }

    @Override
    public String GetTradingDay() {
        return this.channelReader == null ? null : this.channelReader.tradingDay();
    }

    @Override
    public void Init() {
        this.channelId = CtpNatives.CreateChannel();
        this.channelReader = new MdChannelReader(this.channelId, this.spi);
        this.sessionId = CtpNatives.CreateMdSession(this.profile, this.channelId);
    }

    @Override
    public void Join() {
        while (true)
            try {
                this.cond.await();
                break;
            } catch (InterruptedException ignored) {
            }
    }

    @Override
    public void RegisterFront(String frontAddress) {
        // Filter mal-formatted address.
        if (this.addressRegex.matcher(frontAddress).matches())
            this.profile.FrontAddresses.add(frontAddress);
    }

    @Override
    public void RegisterSpi(CThostFtdcMdSpi spi) {
        this.spi = spi;
    }

    @Override
    public void Release() {
        CtpNatives.DestroyMdSession(this.sessionId);
        this.channelReader.stop();
        this.channelReader = null;
        CtpNatives.DestroyChannel(this.channelId);
        // Signal.
        this.cond.signalAll();
    }

    @Override
    public int ReqUserLogin(CThostFtdcReqUserLoginField reqUserLoginField,
                            int requestId) {
        return CtpNatives.ReqUserLogin(this.sessionId, reqUserLoginField, requestId);
    }

    @Override
    public int ReqUserLogout(CThostFtdcUserLogoutField userLogout, int requestId) {
        return CtpNatives.ReqUserLogout(this.sessionId, userLogout, requestId);
    }

    @Override
    public int SubscribeMarketData(String[] instrumentID, int count) {
        return CtpNatives.SubscribeMarketData(this.sessionId, instrumentID, count);
    }

    @Override
    public int UnSubscribeMarketData(String[] instrumentID, int count) {
        return CtpNatives.UnSubscribeMarketData(this.sessionId, instrumentID, count);
    }

    @Override
    public void close() throws Exception {
        this.Release();
    }
}
