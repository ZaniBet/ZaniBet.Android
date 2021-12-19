package eu.devolios.zanibet.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by Gromat Luidgi on 26/01/2018.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OddsValue implements Serializable{
    private float homeTeam;
    private float draw;
    private float awayTeam;
    private float negative = -1;
    private float positive = -1;

    private OddsValue(){

    }

    public float getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(float homeTeam) {
        this.homeTeam = homeTeam;
    }

    public float getDraw() {
        return draw;
    }

    public void setDraw(float draw) {
        this.draw = draw;
    }

    public float getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(float awayTeam) {
        this.awayTeam = awayTeam;
    }

    public float getNegative() {
        return negative;
    }

    public void setNegative(float negative) {
        this.negative = negative;
    }

    public float getPositive() {
        return positive;
    }

    public void setPositive(float positive) {
        this.positive = positive;
    }
}
