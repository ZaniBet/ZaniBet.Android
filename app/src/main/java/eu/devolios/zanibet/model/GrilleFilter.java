package eu.devolios.zanibet.model;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class GrilleFilter {

    // Filtres type
    public static final String TYPE_ALL_PENDING = "waiting_result";
    public static final String TYPE_MULTI_LOST = "";
    public static final String TYPE_MULTI_JACKPOT = "";
    public static final String TYPE_SIMPLE = "";
    public static final String TYPE_TOURNAMENT = "";

    // Filtres status
    public static final String STATUS_PENDING = "waiting_result";
    public static final String STATUS_LOST = "loose";
    public static final String STATUS_WIN = "win";
    public static final String STATUS_FREE = "free";

    // Filtres récompenses
    public static final String REWARD_ZANICOIN = "ZaniCoin";
    public static final String REWARD_ZANIHASH = "ZaniHash";

    private String status;
    private String reward;

    public GrilleFilter(@Status String status, @Reward String reward){
        this.status = status;
        this.reward = reward;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReward() {
        return reward;
    }

    public void setReward(String reward) {
        this.reward = reward;
    }

    @StringDef({ STATUS_PENDING, STATUS_LOST, STATUS_WIN })
    @Retention(RetentionPolicy.SOURCE)
    @interface Status{
    }

    @StringDef({ REWARD_ZANICOIN, REWARD_ZANIHASH })
    @Retention(RetentionPolicy.SOURCE)
    @interface Reward{
    }
}



