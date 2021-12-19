package eu.devolios.zanibet.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Wallet implements Serializable {

    private int zaniCoin;
    private int zaniHash;
    private int chips;
    private int totalZaniCoin;
    private int totalZaniHash;

    public int getZaniCoin() {
        return zaniCoin;
    }

    public void setZaniCoin(int zaniCoin) {
        this.zaniCoin = zaniCoin;
    }

    public int getTotalZaniCoin() {
        return totalZaniCoin;
    }

    public void setTotalZaniCoin(int totalZaniCoin) {
        this.totalZaniCoin = totalZaniCoin;
    }

    public int getZaniHash() {
        return zaniHash;
    }

    public void setZaniHash(int zaniHash) {
        this.zaniHash = zaniHash;
    }

    public int getTotalZaniHash() {
        return totalZaniHash;
    }

    public void setTotalZaniHash(int totalZaniHash) {
        this.totalZaniHash = totalZaniHash;
    }

    public int getChips() {
        return chips;
    }

    public void setChips(int chips) {
        this.chips = chips;
    }
}
