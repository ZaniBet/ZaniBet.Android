package eu.devolios.zanibet.model;

import java.io.Serializable;

/**
 * Created by Gromat Luidgi on 26/01/2018.
 */

public class Odds implements Serializable {

    private String type;
    private String bookmaker;
    private OddsValue odds;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBookmaker() {
        return bookmaker;
    }

    public void setBookmaker(String bookmaker) {
        this.bookmaker = bookmaker;
    }

    public OddsValue getOdds() {
        return odds;
    }

    public void setOdds(OddsValue odds) {
        this.odds = odds;
    }
}

