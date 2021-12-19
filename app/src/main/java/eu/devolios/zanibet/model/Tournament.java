package eu.devolios.zanibet.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Tournament implements Serializable {

    private int level; // 1 à 6
    private int totalPlayers; // Nombre de joueurs participant au tournoi
    private int totalPlayersPaid; // Nombre de joueur
    private int playCost;
    private int fees;
    private int pot;
    private int sharing; // Pourcentage de joueur payé
    private String rewardType;

    public int getTotalPlayers() {
        return totalPlayers;
    }

    public void setTotalPlayers(int totalPlayers) {
        this.totalPlayers = totalPlayers;
    }

    public int getTotalPlayersPaid() {
        return totalPlayersPaid;
    }

    public void setTotalPlayersPaid(int totalPlayersPaid) {
        this.totalPlayersPaid = totalPlayersPaid;
    }

    public int getPlayCost() {
        return playCost;
    }

    public void setPlayCost(int playCost) {
        this.playCost = playCost;
    }

    public int getFees() {
        return fees;
    }

    public void setFees(int fees) {
        this.fees = fees;
    }

    public int getPot() {
        return pot;
    }

    public void setPot(int pot) {
        this.pot = pot;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getSharing() {
        return sharing;
    }

    public void setSharing(int sharing) {
        this.sharing = sharing;
    }

    public String getRewardType() {
        return rewardType;
    }

    public void setRewardType(String rewardType) {
        this.rewardType = rewardType;
    }
}
