package eu.devolios.zanibet.presenter.contract;

import java.util.HashMap;

import eu.devolios.zanibet.model.ApiError;
import eu.devolios.zanibet.model.GameTicket;
import eu.devolios.zanibet.model.Grille;

/**
 * Created by Gromat Luidgi on 09/11/2017.
 */

public interface GrilleSinglePlayContract {

    interface View {
        void onLoad();
        void onUpdateBet(HashMap<String, Integer> bets);
        void onPlayGrille(Grille grille);
        void showPlayTicketError(ApiError error);
        void showLoadingDialog(String title, String content);
        void hideLoadingDialog();
    }

    interface Presenter {
        void load(GameTicket gameTicket);
        void updateBet(String fixtureId, int result);
        void playGrille(GameTicket gameTicket);
    }
}
