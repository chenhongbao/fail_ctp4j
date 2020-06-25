package com.nabiki.ctp4j.jni.struct;

import java.io.Serializable;

public class CThostFtdcOrderField implements Serializable {
	public String BrokerID;
	public String InvestorID;
	public String InstrumentID;
	public String OrderRef;
	public String UserID;
	public byte OrderPriceType;
	public byte Direction;
	public byte CombOffsetFlag;
	public byte CombHedgeFlag;
	public double LimitPrice;
	public int VolumeTotalOriginal;
	public byte TimeCondition;
	public String GTDDate;
	public byte VolumeCondition;
	public int MinVolume;
	public byte ContingentCondition;
	public double StopPrice;
	public byte ForceCloseReason;
	public int IsAutoSuspend;
	public String BusinessUnit;
	public int RequestID;
	public String OrderLocalID;
	public String ExchangeID;
	public String ParticipantID;
	public String ClientID;
	public String ExchangeInstID;
	public String TraderID;
	public int InstallID;
	public byte OrderSubmitStatus;
	public int NotifySequence;
	public String TradingDay;
	public int SettlementID;
	public String OrderSysID;
	public byte OrderSource;
	public byte OrderStatus;
	public byte OrderType;
	public int VolumeTraded;
	public int VolumeTotal;
	public String InsertDate;
	public String InsertTime;
	public String ActiveTime;
	public String SuspendTime;
	public String UpdateTime;
	public String CancelTime;
	public String ActiveTraderID;
	public String ClearingPartID;
	public int SequenceNo;
	public int FrontID;
	public int SessionID;
	public String UserProductInfo;
	public String StatusMsg;
	public int UserForceClose;
	public String ActiveUserID;
	public int BrokerOrderSeq;
	public String RelativeOrderSysID;
	public int ZCETotalTradedVolume;
	public int IsSwapOrder;
	public String BranchID;
	public String InvestUnitID;
	public String AccountID;
	public String CurrencyID;
	public String IPAddress;
	public String MacAddress;

	public CThostFtdcOrderField() {
	}
}
