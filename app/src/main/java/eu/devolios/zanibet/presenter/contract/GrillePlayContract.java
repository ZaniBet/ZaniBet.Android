package eu.devolios.zanibet.presenter.contract;

import eu.devolios.zanibet.model.ApiError;
import eu.devolios.zanibet.model.GameTicket;
import eu.devolios.zanibet.model.Grille;

import java.util.HashMap;

/**
 * Created by Gromat Luidgi on 09/11/2017.
 */

public interface GrillePlayContract {

    interface View {
        void onLoad();
        void onUpdateBet(HashMap<String, Integer> bets);
        void onClearBets();
        void onFlashTicket();
        void onPlayGrille(Grille grille);
        void onValidateGrille();


        void showPlayTicketError(ApiError error);
        void showValidateTicketError(ApiError error);
    }

    interface Presenter {
        void load(GameTicket gameTicket);
        void updateBet(String fixtureId, int result);
        void clearBets();
        void flashGrille();
        void playGrille(GameTicket gameTicket);
        void validateGrille();
        void cancelGrille(Grille grille);
    }
}
