package eu.devolios.zanibet.presenter.contract;

import java.util.List;

import eu.devolios.zanibet.model.ApiError;
import eu.devolios.zanibet.model.Competition;
import eu.devolios.zanibet.model.LeagueStanding;

public interface LeagueStandingContract {

    interface View {
        void onLoadStanding(List<LeagueStanding> leagueStandingList);
        void showContentLoading();
        void hideContentLoading();
        void showContentError(ApiError apiError);
        void hideContentError();
    }

    interface Presenter {

        void loadStanding(Competition competition);

    }

}
