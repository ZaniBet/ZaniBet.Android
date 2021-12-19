package eu.devolios.zanibet.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by Gromat Luidgi on 12/03/2018.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Referral {

    private String invitationCode;
    private int invitationCodeEditAttempt;
    private String referrerCode;
    private int totalReferred;
    private int totalReferredToday;
    private int totalReferredMonth;
    private int totalCoin;
    private int totalCoinMultiTicketPlay;
    private int totalCoinSimpleTicketPlay;
    private int totalJeton;
    private int totalTransaction;
    private int invitationBonus;
    private int coinRewardPercent;
    private int jetonReward;
    // Partner
    private int coinPerMultiTicketPlay;
    private int coinPerSimpleTicketPlay;

    public String getInvitationCode() {
        return invitationCode;
    }

    public int getInvitationCodeEditAttempt() {
        return invitationCodeEditAttempt;
    }

    public String getReferrerCode() {
        return referrerCode;
    }

    public int getTotalReferred() {
        return totalReferred;
    }

    public int getTotalCoin() {
        return totalCoin;
    }

    public int getTotalJeton() {
        return totalJeton;
    }

    public int getTotalTransaction() {
        return totalTransaction;
    }

    public int getInvitationBonus() {
        return invitationBonus;
    }

    public int getCoinRewardPercent() {
        return coinRewardPercent;
    }

    public int getJetonReward() {
        return jetonReward;
    }

    public int getCoinPerMultiTicketPlay() {
        return coinPerMultiTicketPlay;
    }

    public int getCoinPerSimpleTicketPlay() {
        return coinPerSimpleTicketPlay;
    }

    public int getTotalReferredToday() {
        return totalReferredToday;
    }

    public int getTotalReferredMonth() {
        return totalReferredMonth;
    }

    public int getTotalCoinMultiTicketPlay() {
        return totalCoinMultiTicketPlay;
    }

    public int getTotalCoinSimpleTicketPlay() {
        return totalCoinSimpleTicketPlay;
    }

    public void setInvitationCode(String invitationCode) {
        this.invitationCode = invitationCode;
    }

    public void setInvitationCodeEditAttempt(int invitationCodeEditAttempt) {
        this.invitationCodeEditAttempt = invitationCodeEditAttempt;
    }

    public void setReferrerCode(String referrerCode) {
        this.referrerCode = referrerCode;
    }

    public void setTotalReferred(int totalReferred) {
        this.totalReferred = totalReferred;
    }

    public void setTotalReferredToday(int totalReferredToday) {
        this.totalReferredToday = totalReferredToday;
    }

    public void setTotalReferredMonth(int totalReferredMonth) {
        this.totalReferredMonth = totalReferredMonth;
    }

    public void setTotalCoin(int totalCoin) {
        this.totalCoin = totalCoin;
    }

    public void setTotalCoinMultiTicketPlay(int totalCoinMultiTicketPlay) {
        this.totalCoinMultiTicketPlay = totalCoinMultiTicketPlay;
    }

    public void setTotalCoinSimpleTicketPlay(int totalCoinSimpleTicketPlay) {
        this.totalCoinSimpleTicketPlay = totalCoinSimpleTicketPlay;
    }

    public void setTotalJeton(int totalJeton) {
        this.totalJeton = totalJeton;
    }

    public void setTotalTransaction(int totalTransaction) {
        this.totalTransaction = totalTransaction;
    }

    public void setInvitationBonus(int invitationBonus) {
        this.invitationBonus = invitationBonus;
    }

    public void setCoinRewardPercent(int coinRewardPercent) {
        this.coinRewardPercent = coinRewardPercent;
    }

    public void setJetonReward(int jetonReward) {
        this.jetonReward = jetonReward;
    }

    public void setCoinPerMultiTicketPlay(int coinPerMultiTicketPlay) {
        this.coinPerMultiTicketPlay = coinPerMultiTicketPlay;
    }

    public void setCoinPerSimpleTicketPlay(int coinPerSimpleTicketPlay) {
        this.coinPerSimpleTicketPlay = coinPerSimpleTicketPlay;
    }

}
