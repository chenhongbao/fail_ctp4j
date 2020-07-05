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

package com.nabiki.ctp4j.md.internal;

import com.nabiki.ctp4j.exception.ErrorCodes;
import com.nabiki.ctp4j.jni.CtpNatives;
import com.nabiki.ctp4j.jni.struct.CThostFtdcRspInfoField;
import com.nabiki.ctp4j.md.CThostFtdcMdSpi;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class MdChannelReader {
	private final static long WAIT_MS = 1000;

	private final int channelId;
	private final CThostFtdcMdSpi spi;
	private final BlockingQueue<MdChannelData> bQueue;
	private final Thread readerThread,		// Read channel data.
			spiThread;						// Call SPI methods.
	private final AtomicBoolean stopped;	// Stop marker.
	private String tradingDay;				// Provide value for GetTradingDay method

	// Same code with trader channel reader.
	public MdChannelReader(int channelId, CThostFtdcMdSpi spi) {
		if (spi == null)
			throw new NullPointerException("SPI null pointer");

		this.channelId = channelId;
		this.spi = spi;
		this.stopped = new AtomicBoolean(false);
		this.bQueue = new LinkedBlockingQueue<>();

		// Threads.
		this.readerThread = new Thread(() -> {
			while (!Thread.currentThread().isInterrupted()) {
				try {
					CtpNatives.WaitOnChannel(this.channelId, WAIT_MS);

					var data = new MdChannelData();
					// Read channel data.
					if (ErrorCodes.NO_ERROR == CtpNatives.ReadMdChannel(
							this.channelId, data)) {
						if (!this.bQueue.offer(
								data, WAIT_MS, TimeUnit.MILLISECONDS)) {
							onErrorRsp(ErrorCodes.NO_SPACE,
									"blocking queue offer timeout",
									0, true);
						}
					}
				} catch (InterruptedException e) {
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
				onErrorRsp(ErrorCodes.UNNO_INTERRUPTED, e.getMessage(), 0,
						true);
			}
		});
	}

	public String tradingDay() {
		return this.tradingDay;
	}

	private void onErrorRsp(int code, String msg, int req, boolean last) {
		var rsp = new CThostFtdcRspInfoField();
		rsp.ErrorID = code;
		rsp.ErrorMsg = msg;
		try {
			this.spi.OnRspError(rsp, req, last);
		} catch (Throwable ignored) {
		}
	}

	private void onChannelData(MdChannelData data) {
		if (data == null)
			return;

		try {
			for (var m : data.ListRtnDepthMarketData)
				this.spi.OnRtnDepthMarketData(m.DepthMarketData);

			for (@SuppressWarnings("unused") var m : data.ListConnect)
				this.spi.OnFrontConnected();

			for (var m : data.ListDisconnect)
				this.spi.OnFrontDisconnected(m.Reason);

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

			for (var m : data.ListRspSubMarketData)
				this.spi.OnRspSubMarketData(
						m.SpecificInstrument, m.RspInfo, m.RequestId, m.IsLast);

			for (var m : data.ListRspUnSubMarketData)
				this.spi.OnRspUnSubMarketData(
						m.SpecificInstrument, m.RspInfo, m.RequestId, m.IsLast);
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
