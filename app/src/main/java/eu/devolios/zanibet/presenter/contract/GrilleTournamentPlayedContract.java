package eu.devolios.zanibet.presenter.contract;

import eu.devolios.zanibet.model.GameTicket;
import eu.devolios.zanibet.model.Grille;

/**
 * Created by Gromat Luidgi on 09/11/2017.
 */

public interface GrilleTournamentPlayedContract {

    interface View {
        void onLoadGrille(Grille grille);
        void showContentLoading();
        void hideContentLoading();
    }

     interface Presenter {
         void loadGrille(GameTicket gameTicket);
     }
}
