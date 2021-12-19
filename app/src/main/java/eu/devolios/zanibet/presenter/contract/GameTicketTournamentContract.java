package eu.devolios.zanibet.presenter.contract;

import java.util.List;

import eu.devolios.zanibet.model.ApiError;
import eu.devolios.zanibet.model.GameTicket;

/**
 * Created by Gromat Luidgi on 09/11/2017.
 */

public interface GameTicketTournamentContract {

    interface View {
        void addTickets(List<GameTicket> gameTickets);
        void showContentLoading();
        void hideContentLoading();
        void showContentError(ApiError apiError);
        void hideContentError();
        void onRefresh();
        void onLoadUser();


        void setPaginateLoading(boolean value);
        void setNoPaginate(boolean value);
    }

    interface Presenter {
        void refresh();
        void loadMore();
        void loadUser();

    }

}
