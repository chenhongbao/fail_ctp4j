package com.nabiki.ctp4j.jni;

import com.nabiki.ctp4j.exception.ErrorCodes;
import com.nabiki.ctp4j.jni.struct.*;
import com.nabiki.ctp4j.md.internal.MdChannelData;
import com.nabiki.ctp4j.md.internal.MdLoginProfile;
import com.nabiki.ctp4j.trader.internal.TraderChannelData;
import com.nabiki.ctp4j.trader.internal.TraderLoginProfile;

public class CtpNatives {
    /**
     * Create native data channel whose identifier is then returned.
     *
     * <p>The method doesn't throw exception.</p>
     *
     * @return identifier for the new channel
     */
    native public static int CreateChannel();

    /**
     * Destroy the specified channel. If the channel with given channel ID
     * doesn't exist, nothing happens.
     *
     * <p>The method doesn't throw exception.</p>
     *
     * @param channelId identifier for the channel to be destroyed
     */
    native public static void DestroyChannel(int channelId);

    /**
     * Wait for signal on the specified channel for the specified milliseconds.
     * The method waits on a condition variable until it is signaled or the
     * specified milliseconds elapsed, and the returned value has the hints.
     *
     * <p>The method checks if there is data in channel before waiting on
     * conditional variable. If there is, it returns immediately, or it starts
     * to wait for signal.
     * </p>
     *
     * <p>It returns {@code 0} on normal signal, or
     * {@link ErrorCodes#NATIVE_TIMEOUT} on wait timeout.
     * </p>
     *
     * <p>If channel with the given identifier doesn't exist, nothing happens.
     * The method doesn't throw exception.
     * </p>
     *
     * @param channelId channel ID for the specified channel to wait on
     * @param millis    milliseconds to wait before timeout
     * @return {@code 0} for signal, or {@link ErrorCodes#NATIVE_TIMEOUT} for
     * timeout
     */
    native public static int WaitOnChannel(int channelId, long millis);

    /**
     * Signal the specified channel. If channel with the given identifier
     * doesn't exist, nothing happens.
     *
     * <p>The method doesn't throw exception.</p>
     *
     * @param channelId identifier for the specified channel
     */
    native public static void SignalChannel(int channelId);

    /**
     * Non-blocking read data from the specified channel. The method fills the
     * {@link TraderChannelData} object with the available data in the specified
     * channel and returns {@link ErrorCodes#NO_ERROR}, or nothing happens and
     * returns {@link ErrorCodes#NO_DATA}.
     *
     * <p>The method doesn't throw exception.</p>
     *
     * @param channelId identifier for the specified channel to read from
     * @param data      read-in channel data
     * @return {@link ErrorCodes#NO_ERROR} after it has read data, or
     * {@link ErrorCodes#NO_DATA} if there is no data to read
     */
    native public static int ReadTraderChannel(int channelId,
                                               TraderChannelData data);

    /**
     * Write the specified data to the specified channel. If there is data in
     * the {@link TraderChannelData} object and they are written to the
     * specified channel, the method returns {@link ErrorCodes#NO_ERROR}. If the
     * {@link TraderChannelData} object is empty, then no data can be written,
     * the method returns {@link ErrorCodes#NO_DATA}.
     *
     * <p>The method doesn't throw exception.</p>
     *
     * @param channelId identifier for the specified channel
     * @param data      data to be written to the channel
     * @return {@link ErrorCodes#NO_ERROR} if data is written to channel, or
     * {@link ErrorCodes#NO_DATA} if no data to write
     */
    native public static int WriteTraderChannel(
            int channelId, TraderChannelData data);

    /**
     * Non-blocking read data from the specified channel. The method fills the
     * {@link MdChannelData} object with the available data in the specified
     * channel and returns {@link ErrorCodes#NO_ERROR}, or nothing happens and
     * returns {@link ErrorCodes#NO_DATA}.
     *
     * <p>The method doesn't throw exception.</p>
     *
     * @param channelId identifier for the specified channel to read from
     * @param data      read-in channel data
     * @return {@link ErrorCodes#NO_ERROR} after it has read data, or
     * {@link ErrorCodes#NO_DATA} if there is no data to read
     */
    native public static int ReadMdChannel(int channelId, MdChannelData data);

    /**
     * Write the specified data to the specified channel. If there is data in
     * the {@link MdChannelData} object and they are written to the
     * specified channel, the method returns {@link ErrorCodes#NO_ERROR}. If the
     * {@link MdChannelData} object is empty, then no data can be written,
     * the method returns {@link ErrorCodes#NO_DATA}.
     *
     * <p>The method doesn't throw exception.</p>
     *
     * @param channelId identifier for the specified channel
     * @param data      data to be written to the channel
     * {@link ErrorCodes#NO_DATA} if no data to write
     */
    native public static void WriteMdChannel(int channelId, MdChannelData data);

    /**
     * Create a connection to remote trading counter with the specified profile
     * and associate the session with the specified channel. Data from remote
     * counter is written to the specified channel, which is read by calling
     * {@link CtpNatives#ReadTraderChannel(int, TraderChannelData)}.
     *
     * <p>The method doesn't throw exception.</p>
     *
     * @param profile   the profile that has initialization parameters for
     *                  trader counter
     * @param channelId the channel associated with this new trader session
     * @return identifier for the new session
     */
    native public static int CreateTraderSession(TraderLoginProfile profile,
                                                 int channelId);

    /**
     * Destroy the specified trader session. The method disconnects the specified
     * session from remote counter and release related native resources. If the
     * session with given ID doesn't exist, nothing happens.
     *
     * <p>For a better behavior, a logout request before destroying the session
     * is preferred.
     * </p>
     *
     * @param traderSessionId identifier for the trader session to be destroyed
     */
    native public static void DestroyTraderSession(int traderSessionId);

    /**
     * Create a connection to remote md counter with the specified profile
     * and associate the session with the specified channel. Data from remote
     * counter is written to the specified channel, which is read by calling
     * {@link CtpNatives#ReadMdChannel(int, MdChannelData)}.
     *
     * <p>The method doesn't throw exception.</p>
     *
     * @param profile   the profile that has initialization parameters for
     *                  trader counter
     * @param channelId the channel associated with this new trader session
     * @return identifier for the new session
     */
    native public static int CreateMdSession(MdLoginProfile profile, int channelId);

    /**
     * Destroy the specified md session. The method disconnects the specified session
     * from remote counter and release related native resources. If the session with
     * given ID doesn't exist, nothing happens.
     *
     * <p>For a better behavior, a logout request before destroying the session
     * is preferred.
     * </p>
     *
     * @param mdSessionId identifier for the trader session to be destroyed
     */
    native public static void DestroyMdSession(int mdSessionId);

    native public static int ReqAuthenticate(
            int traderSessionId,
            CThostFtdcReqAuthenticateField reqAuthenticateField, int requestId);

    native public static int ReqUserLogin(
            int traderSessionId, CThostFtdcReqUserLoginField reqUserLoginField,
            int requestId);

    native public static int ReqUserLogout(
            int traderSessionId, CThostFtdcUserLogoutField userLogout,
            int requestId);

    native public static int ReqSettlementInfoConfirm(
            int traderSessionId,
            CThostFtdcSettlementInfoConfirmField settlementInfoConfirm,
            int requestId);

    native public static int ReqOrderInsert(
            int traderSessionId, CThostFtdcInputOrderField inputOrder,
            int requestId);

    native public static int ReqOrderAction(
            int traderSessionId,
            CThostFtdcInputOrderActionField inputOrderAction, int requestId);

    native public static int ReqQryInstrument(
            int traderSessionId, CThostFtdcQryInstrumentField qryInstrument,
            int requestId);

    native public static int ReqQryInstrumentCommissionRate(
            int traderSessionId,
            CThostFtdcQryInstrumentCommissionRateField qryInstrumentCommissionRate,
            int requestId);

    native public static int ReqQryInstrumentMarginRate(
            int traderSessionId,
            CThostFtdcQryInstrumentMarginRateField qryInstrumentMarginRate,
            int requestId);

    native public static int ReqQryTradingAccount(
            int traderSessionId,
            CThostFtdcQryTradingAccountField qryTradingAccount, int requestId);

    native public static int ReqQryInvestorPositionDetail(
            int traderSessionId,
            CThostFtdcQryInvestorPositionDetailField qryInvestorPositionDetail,
            int requestId);

    native public static int SubscribeMarketData(int mdSessionid,
                                                 String[] instrumentID, int count);

    native public static int UnSubscribeMarketData(int mdSessionid,
                                                   String[] instrumentID, int count);
}
