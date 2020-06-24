package com.nabiki.ctp4j.trader;

import com.nabiki.ctp4j.struct.*;
import com.nabiki.ctp4j.trader.internal.LoginProfile;
import com.nabiki.ctp4j.trader.internal.TraderChannelReader;
import com.nabiki.ctp4j.trader.internal.TraderNatives;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;

public class CThostFtdcTraderApiImpl extends CThostFtdcTraderApi implements AutoCloseable {
	private static final String apiVersion = "0.0.1";

	private final LoginProfile profile = new LoginProfile();

	// Front address pattern.
	private final Pattern addressRegex = Pattern
			.compile("tcp://((\\d{1})|([1-9]\\d{1,2}))(\\.((\\d{1})|([1-9]\\d{1,2}))){3}:\\d+");
	// Join/Release.
	private final ReentrantLock lock = new ReentrantLock();
	private final Condition cond = lock.newCondition();
	// Spi.
	private CThostFtdcTraderSpi spi;
	// Channel reader.
	private TraderChannelReader channelReader;
	// Identifiers for CTP session and native channel.
	private int sessionId, channelId;

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
		this.channelId = TraderNatives.CreateChannel();
		// Start channel reader.
		this.channelReader = new TraderChannelReader(this.channelId, this.spi);
		this.sessionId = TraderNatives.CreateTraderSession(profile, this.channelId);
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
		// Filter mal-formated address.
		if (this.addressRegex.matcher(frontAddress).matches())
			this.profile.FrontAddresses.add(frontAddress);
	}

	@Override
	public void RegisterSpi(CThostFtdcTraderSpi spi) {
		this.spi = spi;
	}

	@Override
	public void Release() {
		TraderNatives.DestroyTraderSession(this.sessionId);
		// Stop channel reader then destroy channel.
		this.channelReader.stop();
		this.channelReader = null;

		TraderNatives.DestroyChannel(this.channelId);
		// Signal.
		this.cond.signalAll();
	}

	@Override
	public int ReqAuthenticate(CThostFtdcReqAuthenticateField reqAuthenticateField, int requestId) {
		return TraderNatives.ReqAuthenticate(this.sessionId, reqAuthenticateField, requestId);
	}

	@Override
	public int ReqUserLogin(CThostFtdcReqUserLoginField reqUserLoginField, int requestId) {
		return TraderNatives.ReqUserLogin(this.sessionId, reqUserLoginField, requestId);
	}

	@Override
	public int ReqUserLogout(CThostFtdcUserLogoutField userLogout, int requestId) {
		return TraderNatives.ReqUserLogout(this.sessionId, userLogout, requestId);
	}

	@Override
	public int ReqOrderInsert(CThostFtdcInputOrderField inputOrder, int requestId) {
		return TraderNatives.ReqOrderInsert(this.sessionId, inputOrder, requestId);
	}

	@Override
	public int ReqOrderAction(CThostFtdcInputOrderActionField inputOrderAction, int requestId) {
		return TraderNatives.ReqOrderAction(this.sessionId, inputOrderAction, requestId);
	}

	@Override
	public int ReqQryInstrument(CThostFtdcQryInstrumentField qryInstrument, int requestId) {
		return TraderNatives.ReqQryInstrument(this.sessionId, qryInstrument, requestId);
	}

	@Override
	public int ReqQryInstrumentCommissionRate(CThostFtdcQryInstrumentCommissionRateField qryInstrumentCommissionRate,
											  int requestId) {
		return TraderNatives.ReqQryInstrumentCommissionRate(this.sessionId, qryInstrumentCommissionRate, requestId);
	}

	@Override
	public int ReqQryInstrumentMarginRate(CThostFtdcQryInstrumentMarginRateField qryInstrumentMarginRate,
										  int requestId) {
		return TraderNatives.ReqQryInstrumentMarginRate(this.sessionId, qryInstrumentMarginRate, requestId);
	}

	@Override
	public int ReqQryTradingAccount(CThostFtdcQryTradingAccountField qryTradingAccount, int requestId) {
		return TraderNatives.ReqQryTradingAccount(this.sessionId, qryTradingAccount, requestId);
	}

	@Override
	public int ReqQryInvestorPositionDetail(CThostFtdcQryInvestorPositionDetailField qryInvestorPositionDetail,
											int requestId) {
		return TraderNatives.ReqQryInvestorPositionDetail(this.sessionId, qryInvestorPositionDetail, requestId);
	}

	@Override
	public void close() throws Exception {
		this.Release();
	}

	@Override
	public int ReqSettlementInfoConfirm(CThostFtdcSettlementInfoConfirmField settlementInfoConfirm, int requestId) {
		return TraderNatives.ReqSettlementInfoConfirm(this.sessionId, settlementInfoConfirm, requestId);
	}
}
