package eu.devolios.zanibet.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.Serializable;

/**
 * Created by Gromat Luidgi on 09/11/2017.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Grille implements Serializable {

    private String _id;
    private String createdAt;
    private String updatedAt;
    private String reference;
    private Object gameTicket;
    private Bet[] bets;
    private String status;
    private int numberOfBetsWin;
    private GrillePayout payout;
    private GrilleStanding standing;
    private String picture;
    private String type;
    private String instanceId;
    private String advertisingId;

    public Grille() {

    }

    public static Grille convertFromMap(Object object){
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<Grille> jsonAdapter = moshi.adapter(Grille.class);
        return jsonAdapter.fromJsonValue(object);
    }

    public String get_id() {
        return this._id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Object getGameTicket() {
        return this.gameTicket;
    }

    public void setGameTicket(Object gameTicket) {
        this.gameTicket = gameTicket;
    }

    /*public Object getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }*/

    public Bet[] getBets() {
        return this.bets;
    }

    public void setBets(Bet[] bets) {
        this.bets = bets;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getNumberOfBetsWin() {
        return this.numberOfBetsWin;
    }

    public void setNumberOfBetsWin(int numberOfBetsWin) {
        this.numberOfBetsWin = numberOfBetsWin;
    }

    public GrillePayout getPayout() {
        return this.payout;
    }

    public void setPayout(GrillePayout payout) {
        this.payout = payout;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public String getAdvertisingId() {
        return advertisingId;
    }

    public void setAdvertisingId(String advertisingId) {
        this.advertisingId = advertisingId;
    }

    public GrilleStanding getStanding() {
        return standing;
    }

    public void setStanding(GrilleStanding grilleStanding) {
        this.standing = grilleStanding;
    }
}
