package eu.devolios.zanibet.presenter.contract;

import java.util.ArrayList;

import eu.devolios.zanibet.model.ApiError;
import eu.devolios.zanibet.model.Fixture;

public interface FixtureStatsContract {

    interface View {
        void onLoadStats(ArrayList<Fixture> lastHomeFixtures, ArrayList<Fixture> lastAwayFixtures);
        void showContentLoading();
        void hideContentLoading();
        void showContentError(ApiError apiError);
        void hideContentError();
    }

    interface Presenter {
        void loadStats(String fixtureId);
    }
}
