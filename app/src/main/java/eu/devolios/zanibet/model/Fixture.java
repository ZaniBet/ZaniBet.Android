package eu.devolios.zanibet.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by Gromat Luidgi on 10/11/2017.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Fixture implements Serializable{

    private String _id;
    private String date;
    private Venue venue;
    private int matchDay;
    private Team homeTeam;
    private Team awayTeam;
    private FixtureResult result;
    private String status;
    private Object competition;
    private Odds[] odds;
    private int zScore;

    public static List<Fixture> convertArrayFromMap(Object object){
        Moshi moshi = new Moshi.Builder().build();
        Type type = Types.newParameterizedType(List.class, Fixture.class);
        JsonAdapter<List<Fixture>> jsonAdapter = moshi.adapter(type);
        return jsonAdapter.fromJsonValue(object);
    }

    public Fixture(){
    }

    public Fixture(String id){
        _id = id;
    }

    public String getId() {
        return this._id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getMatchDay() {
        return this.matchDay;
    }

    public void setMatchDay(int matchDay) {
        this.matchDay = matchDay;
    }

    public Team getHomeTeam() {
        return this.homeTeam;
    }

    public void setHomeTeam(Team homeTeam) {
        this.homeTeam = homeTeam;
    }

    public Team getAwayTeam() {
        return this.awayTeam;
    }

    public void setAwayTeam(Team awayTeam) {
        this.awayTeam = awayTeam;
    }

    public FixtureResult getResult() {
        return this.result;
    }

    public void setResult(FixtureResult result) {
        this.result = result;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getCompetition() {
        return competition;
    }

    public void setCompetition(Object competition) {
        this.competition = competition;
    }

    public Odds[] getOdds() {
        return odds;
    }

    public void setOdds(Odds[] odds) {
        this.odds = odds;
    }

    public Venue getVenue() {
        return venue;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
    }

    public int getzScore() {
        return zScore;
    }

    public void setzScore(int zScore) {
        this.zScore = zScore;
    }
}
