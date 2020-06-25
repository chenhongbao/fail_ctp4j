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
