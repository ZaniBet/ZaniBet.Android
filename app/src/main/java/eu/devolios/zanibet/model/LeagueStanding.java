package eu.devolios.zanibet.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LeagueStanding implements Serializable {

    private Team team;
    private int position;
    private int points;
    private int goalDifference;
    private String recentForm;
    private Stats overall;
    private Stats home;
    private Stats away;

    public LeagueStanding(){

    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getGoalDifference() {
        return goalDifference;
    }

    public void setGoalDifference(int goalDifference) {
        this.goalDifference = goalDifference;
    }

    public String getRecentForm() {
        return recentForm;
    }

    public void setRecentForm(String recentForm) {
        this.recentForm = recentForm;
    }

    public Stats getOverall() {
        return overall;
    }

    public void setOverall(Stats overall) {
        this.overall = overall;
    }

    public Stats getHome() {
        return home;
    }

    public void setHome(Stats home) {
        this.home = home;
    }

    public Stats getAway() {
        return away;
    }

    public void setAway(Stats away) {
        this.away = away;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public class Stats implements  Serializable {
        private int gamesPlayed;
        private int won;
        private int draw;
        private int lost;
        private int goals;
        private int goalsAgainst;

        public Stats(){

        }

        public int getGamesPlayed() {
            return gamesPlayed;
        }

        public void setGamesPlayed(int gamesPlayed) {
            this.gamesPlayed = gamesPlayed;
        }

        public int getWon() {
            return won;
        }

        public void setWon(int won) {
            this.won = won;
        }

        public int getDraw() {
            return draw;
        }

        public void setDraw(int draw) {
            this.draw = draw;
        }

        public int getLost() {
            return lost;
        }

        public void setLost(int lost) {
            this.lost = lost;
        }

        public int getGoals() {
            return goals;
        }

        public void setGoals(int goals) {
            this.goals = goals;
        }

        public int getGoalsAgainst() {
            return goalsAgainst;
        }

        public void setGoalsAgainst(int goalsAgainst) {
            this.goalsAgainst = goalsAgainst;
        }
    }
}
