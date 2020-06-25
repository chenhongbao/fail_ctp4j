package com.nabiki.ctp4j.trader;

import com.nabiki.ctp4j.exception.ErrorCodes;
import com.nabiki.ctp4j.jni.CtpNatives;
import com.nabiki.ctp4j.jni.flag.ThostTeResumeType;
import com.nabiki.ctp4j.jni.struct.*;
import com.nabiki.ctp4j.trader.internal.TraderChannelReader;
import com.nabiki.ctp4j.trader.internal.TraderLoginProfile;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;

public class CThostFtdcTraderApiImpl extends CThostFtdcTraderApi implements AutoCloseable {
    private static final String apiVersion = "0.0.1";

    // Front address pattern.
    private final Pattern addrRegex = Pattern.compile(
            "tcp://((\\d{1})|([1-9]\\d{1,2}))(\\.((\\d{1})|([1-9]\\d{1,2}))){3}:\\d+");
    // Join/Release.
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition cond = lock.newCondition();
    private final TraderLoginProfile profile = new TraderLoginProfile();

    private int sessionId, channelId;
    private CThostFtdcTraderSpi spi;
    private TraderChannelReader channelReader;

    CThostFtdcTraderApiImpl(String szFlowPath) {
        this.profile.FlowPath = szFlowPath;
    }

    @Override
    public String GetApiVersion() {
        return CThostFtdcTraderApiImpl.apiVersion;
    }

    @Override
    public String GetTradingDay() {
        return this.channelReader == null ? null : this.channelReader.tradingDay();
    }

    @Override
    public void Init() {
        this.channelId = CtpNatives.CreateChannel();
        // Start channel reader.
        this.channelReader = new TraderChannelReader(this.channelId, this.spi);
        this.sessionId = CtpNatives.CreateTraderSession(
                this.profile, this.channelId);
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
    public void SubscribePrivateTopic(int type) {
        switch (type) {
            case ThostTeResumeType.THOST_TERT_RESTART:
            case ThostTeResumeType.THOST_TERT_RESUME:
            case ThostTeResumeType.THOST_TERT_QUICK:
                this.profile.PrivateTopicType = type;
                break;
            default:
                break;
        }
    }

    @Override
    public void SubscribePublicTopic(int type) {
        switch (type) {
            case ThostTeResumeType.THOST_TERT_RESTART:
            case ThostTeResumeType.THOST_TERT_RESUME:
            case ThostTeResumeType.THOST_TERT_QUICK:
                this.profile.PrivateTopicType = type;
                break;
            default:
                break;
        }
    }

    @Override
    public void RegisterFront(String frontAddress) {
        // Filter mal-formatted address.
        if (this.addrRegex.matcher(frontAddress).matches())
            this.profile.FrontAddresses.add(frontAddress);
    }

    @Override
    public void RegisterSpi(CThostFtdcTraderSpi spi) {
        this.spi = spi;
    }

    @Override
    public void Release() {
        CtpNatives.DestroyTraderSession(this.sessionId);
        // Stop channel reader then destroy channel.
        this.channelReader.stop();
        this.channelReader = null;
        CtpNatives.DestroyChannel(this.channelId);
        // Signal.
        this.cond.signalAll();
    }

    @Override
    public int ReqAuthenticate(CThostFtdcReqAuthenticateField reqAuthenticateField,
                               int requestId) {
        if (reqAuthenticateField == null)
            return ErrorCodes.NO_DATA;
        else
            return CtpNatives.ReqAuthenticate(
                    this.sessionId, reqAuthenticateField, requestId);
    }

    @Override
    public int ReqUserLogin(CThostFtdcReqUserLoginField reqUserLoginField,
                            int requestId) {
        if (reqUserLoginField == null)
            return ErrorCodes.NO_DATA;
        else
            return CtpNatives.ReqUserLogin(
                    this.sessionId, reqUserLoginField, requestId);
    }

    @Override
    public int ReqUserLogout(CThostFtdcUserLogoutField userLogout, int requestId) {
        if (userLogout == null)
            return ErrorCodes.NO_DATA;
        else
            return CtpNatives.ReqUserLogout(
                    this.sessionId, userLogout, requestId);
    }

    @Override
    public int ReqOrderInsert(CThostFtdcInputOrderField inputOrder, int requestId) {
        if (inputOrder == null)
            return ErrorCodes.NO_DATA;
        else
            return CtpNatives.ReqOrderInsert(
                    this.sessionId, inputOrder, requestId);
    }

    @Override
    public int ReqOrderAction(CThostFtdcInputOrderActionField inputOrderAction,
                              int requestId) {
        if (inputOrderAction == null)
            return ErrorCodes.NO_DATA;
        else
            return CtpNatives.ReqOrderAction(
                    this.sessionId, inputOrderAction, requestId);
    }

    @Override
    public int ReqQryInstrument(CThostFtdcQryInstrumentField qryInstrument,
                                int requestId) {
        if (qryInstrument == null)
            return ErrorCodes.NO_DATA;
        else
            return CtpNatives.ReqQryInstrument(
                    this.sessionId, qryInstrument, requestId);
    }

    @Override
    public int ReqQryInstrumentCommissionRate(
            CThostFtdcQryInstrumentCommissionRateField qryInstrumentCommissionRate,
            int requestId) {
        if (qryInstrumentCommissionRate == null)
            return ErrorCodes.NO_DATA;
        else
            return CtpNatives.ReqQryInstrumentCommissionRate(
                    this.sessionId, qryInstrumentCommissionRate, requestId);
    }

    @Override
    public int ReqQryInstrumentMarginRate(
            CThostFtdcQryInstrumentMarginRateField qryInstrumentMarginRate,
            int requestId) {
        if (qryInstrumentMarginRate == null)
            return ErrorCodes.NO_DATA;
        else
            return CtpNatives.ReqQryInstrumentMarginRate(
                    this.sessionId, qryInstrumentMarginRate, requestId);
    }

    @Override
    public int ReqQryTradingAccount(
            CThostFtdcQryTradingAccountField qryTradingAccount,
            int requestId) {
        if (qryTradingAccount == null)
            return ErrorCodes.NO_DATA;
        else
            return CtpNatives.ReqQryTradingAccount(
                    this.sessionId, qryTradingAccount, requestId);
    }

    @Override
    public int ReqQryInvestorPositionDetail(
            CThostFtdcQryInvestorPositionDetailField qryInvestorPositionDetail,
            int requestId) {
        if (qryInvestorPositionDetail == null)
            return ErrorCodes.NO_DATA;
        else
            return CtpNatives.ReqQryInvestorPositionDetail(
                    this.sessionId, qryInvestorPositionDetail, requestId);
    }

    @Override
    public void close() throws Exception {
        this.Release();
    }

    @Override
    public int ReqSettlementInfoConfirm(
            CThostFtdcSettlementInfoConfirmField settlementInfoConfirm,
            int requestId) {
        if (settlementInfoConfirm == null)
            return ErrorCodes.NO_DATA;
        else
            return CtpNatives.ReqSettlementInfoConfirm(
                    this.sessionId, settlementInfoConfirm, requestId);
    }
}
