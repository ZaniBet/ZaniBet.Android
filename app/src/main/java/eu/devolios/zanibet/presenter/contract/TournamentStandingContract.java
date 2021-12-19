package eu.devolios.zanibet.presenter.contract;

import java.util.List;

import eu.devolios.zanibet.model.ApiError;
import eu.devolios.zanibet.model.Competition;
import eu.devolios.zanibet.model.GameTicket;
import eu.devolios.zanibet.model.GrilleStanding;
import eu.devolios.zanibet.model.LeagueStanding;

public interface TournamentStandingContract {

    interface View {
        void onLoadStanding(List<GrilleStanding> grilleStandingList);
        void showContentLoading();
        void hideContentLoading();
        void showContentError(ApiError apiError);
        void hideContentError();
    }

    interface Presenter {
        void loadStanding(GameTicket gameTicket);
    }

}
