package com.nabiki.ctp4j.trader.internal;

import com.nabiki.ctp4j.exception.ErrorCodes;
import com.nabiki.ctp4j.jni.CtpNatives;
import com.nabiki.ctp4j.jni.struct.CThostFtdcRspInfoField;
import com.nabiki.ctp4j.trader.CThostFtdcTraderSpi;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class TraderChannelReader {
    private final static long WAIT_MS = 1000;

    private final int channelId;
    private final CThostFtdcTraderSpi spi;
    private final BlockingQueue<TraderChannelData> bQueue;
    private final Thread readerThread,  // Read data from channel.
            spiThread;                  // Call SPI methods.
    private final AtomicBoolean stopped;// Stop marker.
    private String tradingDay;          // Provide value for GetTradingDay method.

    // Read from the specified channel and invoke the specified SPI.
    // A stopped reader can't be reused.
    public TraderChannelReader(int channelId, CThostFtdcTraderSpi spi) {
        if (spi == null)
            throw new NullPointerException("SPI null pointer.");

        this.channelId = channelId;
        this.spi = spi;
        this.stopped = new AtomicBoolean(false);
        this.bQueue = new LinkedBlockingQueue<>();

        // Threads.
        this.readerThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    // Don't wait too long because it could miss the signal when
                    // new data arrives before java codes return to native.
                    CtpNatives.WaitOnChannel(this.channelId, WAIT_MS);

                    var data = new TraderChannelData();
                    // Read channel data.
                    if (ErrorCodes.NO_ERROR == CtpNatives.ReadTraderChannel(
                            this.channelId, data)) {
                        if (!this.bQueue.offer(
                                data, WAIT_MS, TimeUnit.MILLISECONDS)) {
                            onErrorRsp(ErrorCodes.NO_SPACE,
                                    "blocking queue offer timeout.",
                                    0, true);
                        }
                    }
                } catch (InterruptedException e) {
                    // offer method could be interrupted
                    if (!this.stopped.get())
                        onErrorRsp(ErrorCodes.UNNO_INTERRUPTED, e.getMessage(),
                                0, true);
                } catch (Throwable th) {
                    onErrorRsp(ErrorCodes.JVM_INTERNAL, th.getMessage(), 0,
                            true);
                }
            }
        });

        this.spiThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    onChannelData(this.bQueue.poll(WAIT_MS, TimeUnit.MILLISECONDS));
                } catch (InterruptedException e) {
                    // poll could be interrupted to exit
                    if (!this.stopped.get())
                        onErrorRsp(ErrorCodes.UNNO_INTERRUPTED, e.getMessage(),
                                0, true);
                } catch (Throwable th) {
                    onErrorRsp(ErrorCodes.UNNO_THROW, th.getMessage(), 0,
                            true);
                }
            }

            this.readerThread.interrupt();
            try {
                this.readerThread.join(WAIT_MS * 2);
            } catch (InterruptedException e) {
                onErrorRsp(
                        ErrorCodes.UNNO_INTERRUPTED, e.getMessage(), 0,
                        true);
            }
        });
    }

    public String tradingDay() {
        return this.tradingDay;
    }

    private void onErrorRsp(int code, String msg, int req, boolean last) {
        var rsp = new CThostFtdcRspInfoField();
        rsp.code = code;
        rsp.message = msg;
        try {
            this.spi.OnRspError(rsp, req, last);
        } catch (Throwable ignored) {
        }
    }

    private void onChannelData(TraderChannelData data) {
        if (data == null)
            return;

        try {
            for (@SuppressWarnings("unused") var m : data.ListConnect)
                this.spi.OnFrontConnected();

            for (var m : data.ListDisconnect)
                this.spi.OnFrontDisconnected(m.Reason);

            for (var m : data.ListErrRtnOrderAction)
                this.spi.OnErrRtnOrderAction(m.OrderAction, m.RspInfo);

            for (var m : data.ListErrRtnOrderInsert)
                this.spi.OnErrRtnOrderInsert(m.InputOrder, m.RspInfo);

            for (var m : data.ListRspAuthenticate)
                this.spi.OnRspAuthenticate(
                        m.RspAuthenticateField, m.RspInfo, m.RequestId, m.IsLast);

            for (var m : data.ListRspError)
                this.spi.OnRspError(m.RspInfo, m.RequestId, m.IsLast);

            for (var m : data.ListRspOrderAction)
                this.spi.OnRspOrderAction(
                        m.InputOrderAction, m.RspInfo, m.RequestId, m.IsLast);

            for (var m : data.ListRspOrderInsert)
                this.spi.OnRspOrderInsert(
                        m.InputOrder, m.RspInfo, m.RequestId, m.IsLast);

            for (var m : data.ListRspQryInstrument)
                this.spi.OnRspQryInstrument(
                        m.Instrument, m.RspInfo, m.RequestId, m.IsLast);

            for (var m : data.ListRspQryInstrumentCommissionRate)
                this.spi.OnRspQryInstrumentCommissionRate(
                        m.InstrumentCommissionRate, m.RspInfo, m.RequestId,
                        m.IsLast);

            for (var m : data.ListRspQryInstrumentMarginRate)
                this.spi.OnRspQryInstrumentMarginRate(
                        m.InstrumentMarginRate, m.RspInfo, m.RequestId, m.IsLast);

            for (var m : data.ListRspQryInvestorPositionDetail)
                this.spi.OnRspQryInvestorPositionDetail(
                        m.InvestorPositionDetail, m.RspInfo, m.RequestId, m.IsLast);

            for (var m : data.ListRspQryTradingAccount)
                this.spi.OnRspQryTradingAccount(
                        m.TradingAccount, m.RspInfo, m.RequestId, m.IsLast);

            for (var m : data.ListRspSettlementInfoConfirm)
                this.spi.OnRspSettlementInfoConfirm(
                        m.SettlementInfoConfirm, m.RspInfo, m.RequestId, m.IsLast);

            for (var m : data.ListRspUserLogin) {
                this.spi.OnRspUserLogin(
                        m.RspUserLogin, m.RspInfo, m.RequestId, m.IsLast);
                this.tradingDay = m.RspUserLogin.TradingDay;
            }

            for (var m : data.ListRspUserLogout) {
                this.spi.OnRspUserLogout(
                        m.UserLogout, m.RspInfo, m.RequestId, m.IsLast);
                this.tradingDay = null;
            }

            for (var m : data.ListRtnOrder)
                this.spi.OnRtnOrder(m);

            for (var m : data.ListRtnTrade)
                this.spi.OnRtnTrade(m);
        } catch (Throwable th) {
            onErrorRsp(ErrorCodes.UNNO_THROW, th.getMessage(), 0, true);
        }
    }

    public void stop() {
        if (this.stopped.get())
            return;
        // Stop threads.
        this.stopped.set(true);
        this.spiThread.interrupt();

        try {
            this.spiThread.join(WAIT_MS * 4);
        } catch (InterruptedException e) {
            onErrorRsp(ErrorCodes.UNNO_INTERRUPTED,
                    e.getMessage(), 0, true);
        }
    }
}
