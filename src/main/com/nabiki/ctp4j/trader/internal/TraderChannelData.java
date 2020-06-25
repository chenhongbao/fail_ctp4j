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

package com.nabiki.ctp4j.trader.internal;

import com.nabiki.ctp4j.internal.*;
import com.nabiki.ctp4j.jni.struct.CThostFtdcOrderField;
import com.nabiki.ctp4j.jni.struct.CThostFtdcTradeField;

import java.util.List;

public class TraderChannelData {
	public List<ErrRtnOrderAction> ListErrRtnOrderAction;

	public List<ErrRtnOrderInsert> ListErrRtnOrderInsert;

	public List<FrontConnect> ListConnect;

	public List<FrontDisconnect> ListDisconnect;

	public List<RspAuthenticate> ListRspAuthenticate;

	public List<RspError> ListRspError;

	public List<RspOrderAction> ListRspOrderAction;

	public List<RspOrderInsert> ListRspOrderInsert;

	public List<RspQryInstrument> ListRspQryInstrument;

	public List<RspQryInstrumentCommissionRate> ListRspQryInstrumentCommissionRate;

	public List<RspQryInstrumentMarginRate> ListRspQryInstrumentMarginRate;

	public List<RspQryInvestorPositionDetail> ListRspQryInvestorPositionDetail;

	public List<RspQryTradingAccount> ListRspQryTradingAccount;

	public List<RspSettlementInfoConfirm> ListRspSettlementInfoConfirm;

	public List<RspUserLogin> ListRspUserLogin;

	public List<RspUserLogout> ListRspUserLogout;

	public List<CThostFtdcOrderField> ListRtnOrder;

	public List<CThostFtdcTradeField> ListRtnTrade;
}
