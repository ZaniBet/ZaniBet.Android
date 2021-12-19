package eu.devolios.zanibet.presenter.contract;

import java.util.List;

import eu.devolios.zanibet.model.Fixture;
import eu.devolios.zanibet.model.GameTicket;

/**
 * Created by Gromat Luidgi on 26/01/2018.
 */

public interface GameTicketCalendarContract {

    interface Presenter{
        void loadFixtures(GameTicket gameTicket);
    }

    interface View {
        void onLoadFixtures(List<Fixture> fixtureList);

        void showContentLoading();
        void hideContentLoading();

        void showContentError();
        void hideContentError();
    }

}
