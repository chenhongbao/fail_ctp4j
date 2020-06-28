/*
 * Copyright (c) 2020 Hongbao Chen <chenhongbao@outlook.com>
 *
 * Licensed under the  GNU Affero General Public License v3.0 and you may not use
 * this file except in compliance with the  License. You may obtain a copy of the
 * License at
 *
 *                    https://www.gnu.org/licenses/agpl-3.0.txt
 *
 * Permission is hereby  granted, free of charge, to any  person obtaining a copy
 * of this software and associated  documentation files (the "Software"), to deal
 * in the Software  without restriction, including without  limitation the rights
 * to  use, copy,  modify, merge,  publish, distribute,  sublicense, and/or  sell
 * copies  of  the Software,  and  to  permit persons  to  whom  the Software  is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE  IS PROVIDED "AS  IS", WITHOUT WARRANTY  OF ANY KIND,  EXPRESS OR
 * IMPLIED,  INCLUDING BUT  NOT  LIMITED TO  THE  WARRANTIES OF  MERCHANTABILITY,
 * FITNESS FOR  A PARTICULAR PURPOSE AND  NONINFRINGEMENT. IN NO EVENT  SHALL THE
 * AUTHORS  OR COPYRIGHT  HOLDERS  BE  LIABLE FOR  ANY  CLAIM,  DAMAGES OR  OTHER
 * LIABILITY, WHETHER IN AN ACTION OF  CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE  OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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

    private Integer sessionID, channelID;
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
        this.channelID = CtpNatives.CreateChannel();
        // Start channel reader.
        this.channelReader = new TraderChannelReader(this.channelID, this.spi);
        this.sessionID = CtpNatives.CreateTraderSession(
                this.profile, this.channelID);
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
        if (this.sessionID == null)
            return; // The trader api had been released.

        CtpNatives.DestroyTraderSession(this.sessionID);
        // Stop channel reader then destroy channel.
        this.channelReader.stop();
        this.channelReader = null;
        CtpNatives.DestroyChannel(this.channelID);
        this.sessionID = null;
        this.channelID = null;
        // Signal.
        this.cond.signalAll();
    }

    @Override
    public int ReqAuthenticate(CThostFtdcReqAuthenticateField reqAuthenticateField,
                               int requestID) {
        if (reqAuthenticateField == null)
            return ErrorCodes.NO_DATA;
        else
            return CtpNatives.ReqAuthenticate(
                    this.sessionID, reqAuthenticateField, requestID);
    }

    @Override
    public int ReqUserLogin(CThostFtdcReqUserLoginField reqUserLoginField,
                            int requestID) {
        if (reqUserLoginField == null)
            return ErrorCodes.NO_DATA;
        else
            return CtpNatives.ReqUserLogin(
                    this.sessionID, reqUserLoginField, requestID);
    }

    @Override
    public int ReqUserLogout(CThostFtdcUserLogoutField userLogout, int requestID) {
        if (userLogout == null)
            return ErrorCodes.NO_DATA;
        else
            return CtpNatives.ReqUserLogout(
                    this.sessionID, userLogout, requestID);
    }

    @Override
    public int ReqOrderInsert(CThostFtdcInputOrderField inputOrder, int requestID) {
        if (inputOrder == null)
            return ErrorCodes.NO_DATA;
        else
            return CtpNatives.ReqOrderInsert(
                    this.sessionID, inputOrder, requestID);
    }

    @Override
    public int ReqOrderAction(CThostFtdcInputOrderActionField inputOrderAction,
                              int requestID) {
        if (inputOrderAction == null)
            return ErrorCodes.NO_DATA;
        else
            return CtpNatives.ReqOrderAction(
                    this.sessionID, inputOrderAction, requestID);
    }

    @Override
    public int ReqQryInstrument(CThostFtdcQryInstrumentField qryInstrument,
                                int requestID) {
        if (qryInstrument == null)
            return ErrorCodes.NO_DATA;
        else
            return CtpNatives.ReqQryInstrument(
                    this.sessionID, qryInstrument, requestID);
    }

    @Override
    public int ReqQryInstrumentCommissionRate(
            CThostFtdcQryInstrumentCommissionRateField qryInstrumentCommissionRate,
            int requestID) {
        if (qryInstrumentCommissionRate == null)
            return ErrorCodes.NO_DATA;
        else
            return CtpNatives.ReqQryInstrumentCommissionRate(
                    this.sessionID, qryInstrumentCommissionRate, requestID);
    }

    @Override
    public int ReqQryInstrumentMarginRate(
            CThostFtdcQryInstrumentMarginRateField qryInstrumentMarginRate,
            int requestID) {
        if (qryInstrumentMarginRate == null)
            return ErrorCodes.NO_DATA;
        else
            return CtpNatives.ReqQryInstrumentMarginRate(
                    this.sessionID, qryInstrumentMarginRate, requestID);
    }

    @Override
    public int ReqQryTradingAccount(
            CThostFtdcQryTradingAccountField qryTradingAccount,
            int requestID) {
        if (qryTradingAccount == null)
            return ErrorCodes.NO_DATA;
        else
            return CtpNatives.ReqQryTradingAccount(
                    this.sessionID, qryTradingAccount, requestID);
    }

    @Override
    public int ReqQryInvestorPositionDetail(
            CThostFtdcQryInvestorPositionDetailField qryInvestorPositionDetail,
            int requestID) {
        if (qryInvestorPositionDetail == null)
            return ErrorCodes.NO_DATA;
        else
            return CtpNatives.ReqQryInvestorPositionDetail(
                    this.sessionID, qryInvestorPositionDetail, requestID);
    }

    @Override
    public void close() throws Exception {
        this.Release();
    }

    @Override
    public int ReqSettlementInfoConfirm(
            CThostFtdcSettlementInfoConfirmField settlementInfoConfirm,
            int requestID) {
        if (settlementInfoConfirm == null)
            return ErrorCodes.NO_DATA;
        else
            return CtpNatives.ReqSettlementInfoConfirm(
                    this.sessionID, settlementInfoConfirm, requestID);
    }
}
