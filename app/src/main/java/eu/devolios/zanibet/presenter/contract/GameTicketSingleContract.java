package eu.devolios.zanibet.presenter.contract;

import java.util.List;

import eu.devolios.zanibet.model.ApiError;
import eu.devolios.zanibet.model.GameTicket;

/**
 * Created by Gromat Luidgi on 05/01/2018.
 */

public interface GameTicketSingleContract {

    interface View {
        void onLoadGameTicket(List<GameTicket> gameTicketList);

        void showContentError(ApiError apiError);
        void hideContentError();

        void showContentLoading();
        void hideContentLoading();

        void onLoadUser();

        void setPaginateLoading(boolean value);
        void setNoPaginate(boolean value);
    }

    interface Presenter {
        void loadMore();
        void loadUser();
        void refresh();
    }

}
