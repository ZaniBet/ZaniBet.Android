package eu.devolios.zanibet.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.Serializable;

/**
 * Created by Gromat Luidgi on 09/11/2017.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class GameTicket<T> implements Serializable {

    @JsonProperty("_id")
    private String _id;

    private String type;
    private String name;
    private String cover;
    private String picture;
    private String thumbnail;
    private int jackpot;
    private String openDate;
    private String limitDate;
    private String resultDate;
    private Fixture[] fixtures;
    private BetType[] betsType;
    private Object competition;
    private int pointsPerBet;
    private int bonus;
    private int bonusActivation;
    //private Bonuses[] bonuses;
    private String status;
    private int matchDay;
    private int jeton;

    //@SerializedName("maxNumberOfPlay")
    private int maxNumberOfPlay;
    private int numberOfGrillePlay;
    private Tournament tournament;

    @SuppressWarnings("unused")
    public GameTicket(){
    }

    public static GameTicket convertFromMap(Object object){
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<GameTicket> jsonAdapter = moshi.adapter(GameTicket.class);
        return jsonAdapter.fromJsonValue(object);
    }

    public String getId() {
        return this._id;
    }

    public void setId(String _id) {
        this._id = _id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getJackpot() {
        return this.jackpot;
    }

    public void setJackpot(int jackpot) {
        this.jackpot = jackpot;
    }

    public Fixture[] getFixtures() {
        return this.fixtures;
    }

    public void setFixtures(Fixture[] fixtures) {
        this.fixtures = fixtures;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getMaxNumberOfPlay() {
        return this.maxNumberOfPlay;
    }

    public void setMaxNumberOfPlay(int maxNumberOfPlay) {
        this.maxNumberOfPlay = maxNumberOfPlay;
    }

    public String getOpenDate() {
        return this.openDate;
    }

    public void setOpenDate(String openDate) {
        this.openDate = openDate;
    }

    public String getLimitDate() {
        return this.limitDate;
    }

    public void setLimitDate(String limitDate) {
        this.limitDate = limitDate;
    }

    public String getResultDate() {
        return this.resultDate;
    }

    public void setResultDate(String resultDate) {
        this.resultDate = resultDate;
    }

    public String getCover() {
        return this.cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public int getNumberOfGrillePlay() {
        return this.numberOfGrillePlay;
    }

    public void setNumberOfGrillePlay(int numberOfGrillePlay) {
        this.numberOfGrillePlay = numberOfGrillePlay;
    }

    public String getThumbnail() {
        return this.thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public int getMatchDay() {
        return this.matchDay;
    }

    public void setMatchDay(int matchDay) {
        this.matchDay = matchDay;
    }

    public int getPointsPerBet() {
        return pointsPerBet;
    }

    public void setPointsPerBet(int pointsPerBet) {
        this.pointsPerBet = pointsPerBet;
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

    public Object getCompetition() {
        return competition;
    }

    public void setCompetition(Object competition) {
        this.competition = competition;
    }

    public int getJeton() {
        return jeton;
    }

    public void setJeton(int jeton) {
        this.jeton = jeton;
    }

    public BetType[] getBetsType() {
        return betsType;
    }

    public void setBetsType(BetType[] betsType) {
        this.betsType = betsType;
    }

    public int getBonus() {
        return bonus;
    }

    public void setBonus(int bonus) {
        this.bonus = bonus;
    }

    public int getBonusActivation() {
        return bonusActivation;
    }

    public void setBonusActivation(int bonusActivation) {
        this.bonusActivation = bonusActivation;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

    /*public Bonuses[] getBonuses() {
        return bonuses;
    }

    public void setBonuses(Bonuses[] bonuses) {
        this.bonuses = bonuses;
    }

    class Bonuses {
        private String _id;
        private int threshold;
        private int bonus;
    }*/
}
