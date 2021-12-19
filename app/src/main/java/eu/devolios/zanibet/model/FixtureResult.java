package eu.devolios.zanibet.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by Gromat Luidgi on 03/12/2017.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class FixtureResult implements Serializable {

        private int winner;
        private int homeScore;
        private int awayScore;

        public FixtureResult(){
        }

        public int getWinner() {
            return this.winner;
        }

        public void setWinner(int winner) {
            this.winner = winner;
        }

        public int getHomeScore() {
            return this.homeScore;
        }

        public void setHomeScore(int homeScore) {
            this.homeScore = homeScore;
        }

        public int getAwayScore() {
            return this.awayScore;
        }

        public void setAwayScore(int awayScore) {
            this.awayScore = awayScore;
        }

}
