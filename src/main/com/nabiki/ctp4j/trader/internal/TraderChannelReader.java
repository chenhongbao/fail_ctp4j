package com.nabiki.ctp4j.trader.internal;

import com.nabiki.ctp4j.exception.ErrorCodes;
import com.nabiki.ctp4j.struct.CThostFtdcRspInfoField;
import com.nabiki.ctp4j.trader.CThostFtdcTraderSpi;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class TraderChannelReader {
	private final static long WAIT_MILLIS = 1000;

	private final int channelId;
	private final CThostFtdcTraderSpi spi;
	private final Thread readerThread, callbackThread;
	private final BlockingQueue<TraderChannelData> bQueue = new LinkedBlockingQueue<>();

	// Stop marker.
	private final AtomicBoolean stopped;

	private String tradingDay;

	public TraderChannelReader(int channelId, CThostFtdcTraderSpi spi) {
		if (spi == null)
			throw new NullPointerException("SPI null pointer.");

		this.channelId = channelId;
		this.spi = spi;
		this.stopped = new AtomicBoolean(false);

		// Threads.
		this.readerThread = new Thread(() -> {
			while (!Thread.currentThread().isInterrupted()) {
				try {
					// Don't wait too long because it could miss the signal when new data arrives before java
					// codes returns to native codes and wait again.
					// Delay at most 1000 milliseconds.
					TraderNatives.WaitOnChannel(this.channelId, WAIT_MILLIS);

					var data = new TraderChannelData();
					// Read channel data.
					TraderNatives.ReadChannel(this.channelId, data);
					if (!this.bQueue.offer(data, WAIT_MILLIS, TimeUnit.MILLISECONDS)) {
						onErrorRsp(ErrorCodes.NO_SPACE, "Blocking queue's offer timeout.", 0, true);
					}
				} catch (InterruptedException e) {
					if (!this.stopped.get())
						onErrorRsp(ErrorCodes.UNNO_INTERRUPTED, e.getMessage(), 0, true);
				} catch (Throwable th) {
					onErrorRsp(ErrorCodes.JVM_INTERNAL, th.getMessage(), 0, true);
				}
			}
		});

		this.callbackThread = new Thread(() -> {
			while (!Thread.currentThread().isInterrupted()) {
				try {
					onChannelData(this.bQueue.poll(WAIT_MILLIS, TimeUnit.MILLISECONDS));
				} catch (InterruptedException e) {
					if (!this.stopped.get())
						onErrorRsp(ErrorCodes.UNNO_INTERRUPTED, e.getMessage(), 0, true);
				} catch (Throwable th) {
					onErrorRsp(ErrorCodes.UNNO_THROW, th.getMessage(), 0, true);
				}
			}

			this.readerThread.interrupt();
			try {
				this.readerThread.join(WAIT_MILLIS * 2);
			} catch (InterruptedException e) {
				onErrorRsp(ErrorCodes.UNNO_INTERRUPTED, e.getMessage(), 0, true);
			}
		});
	}

	public String tradingDay() {
		return this.tradingDay;
	}

	private void onErrorRsp(int code, String message, int requestId, boolean isLast) {
		var rsp = new CThostFtdcRspInfoField();
		rsp.code = code;
		rsp.message = message;
		try {
			this.spi.OnRspError(rsp, requestId, isLast);
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
				this.spi.OnRspAuthenticate(m.RspAuthenticateField, m.RspInfo, m.RequestId, m.IsLast);

			for (var m : data.ListRspError)
				this.spi.OnRspError(m.RspInfo, m.RequestId, m.IsLast);

			for (var m : data.ListRspOrderAction)
				this.spi.OnRspOrderAction(m.InputOrderAction, m.RspInfo, m.RequestId, m.IsLast);

			for (var m : data.ListRspOrderInsert)
				this.spi.OnRspOrderInsert(m.InputOrder, m.RspInfo, m.RequestId, m.IsLast);

			for (var m : data.ListRspQryInstrument)
				this.spi.OnRspQryInstrument(m.Instrument, m.RspInfo, m.RequestId, m.IsLast);

			for (var m : data.ListRspQryInstrumentCommissionRate)
				this.spi.OnRspQryInstrumentCommissionRate(m.InstrumentCommissionRate, m.RspInfo, m.RequestId, m.IsLast);

			for (var m : data.ListRspQryInstrumentMarginRate)
				this.spi.OnRspQryInstrumentMarginRate(m.InstrumentMarginRate, m.RspInfo, m.RequestId, m.IsLast);

			for (var m : data.ListRspQryInvestorPositionDetail)
				this.spi.OnRspQryInvestorPositionDetail(m.InvestorPositionDetail, m.RspInfo, m.RequestId, m.IsLast);

			for (var m : data.ListRspQryTradingAccount)
				this.spi.OnRspQryTradingAccount(m.TradingAccount, m.RspInfo, m.RequestId, m.IsLast);

			for (var m : data.ListRspSettlementInfoConfirm)
				this.spi.OnRspSettlementInfoConfirm(m.SettlementInfoConfirm, m.RspInfo, m.RequestId, m.IsLast);

			for (var m : data.ListRspUserLogin) {
				this.spi.OnRspUserLogin(m.RspUserLogin, m.RspInfo, m.RequestId, m.IsLast);
				this.tradingDay = m.RspUserLogin.TradingDay;
			}

			for (var m : data.ListRspUserLogout) {
				this.spi.OnRspUserLogout(m.UserLogout, m.RspInfo, m.RequestId, m.IsLast);
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
		this.callbackThread.interrupt();

		try {
			this.callbackThread.join(WAIT_MILLIS * 4);
		} catch (InterruptedException e) {
			onErrorRsp(ErrorCodes.UNNO_INTERRUPTED, e.getMessage(), 0, true);
		}
	}
}
