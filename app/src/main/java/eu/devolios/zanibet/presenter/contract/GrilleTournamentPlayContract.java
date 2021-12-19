package eu.devolios.zanibet.presenter.contract;

import java.util.HashMap;

import eu.devolios.zanibet.model.ApiError;
import eu.devolios.zanibet.model.GameTicket;
import eu.devolios.zanibet.model.Grille;

/**
 * Created by Gromat Luidgi on 09/11/2017.
 */

public interface GrilleTournamentPlayContract {

    interface View {
        void onLoad();
        void onUpdateBet(HashMap<String, Integer> bets);
        void onClearBets();
        void onFlashTicket();
        void onPlayGrille(Grille grille);
        void showPlayTicketError(ApiError error);
    }

    interface Presenter {
        void load(GameTicket gameTicket);
        void updateBet(String fixtureId, int result);
        void clearBets();
        void flashGrille();
        void playGrille(GameTicket gameTicket);
    }
}
