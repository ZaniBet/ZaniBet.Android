package eu.devolios.zanibet.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by Gromat Luidgi on 26/12/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GrilleGroup implements Serializable {

    private GameTicket gameTicket;
    private Grille[] grilles;

    public GrilleGroup(){

    }

    public GameTicket getGameTicket() {
        return gameTicket;
    }

    public void setGameTicket(GameTicket gameTicket) {
        this.gameTicket = gameTicket;
    }

    public Grille[] getGrilles() {
        return grilles;
    }

    public void setGrilles(Grille[] grilles) {
        this.grilles = grilles;
    }
}
