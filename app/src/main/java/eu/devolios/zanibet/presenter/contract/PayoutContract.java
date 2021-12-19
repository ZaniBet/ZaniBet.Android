package eu.devolios.zanibet.presenter.contract;

import eu.devolios.zanibet.model.ApiError;
import eu.devolios.zanibet.model.Payout;

import java.util.List;

/**
 * Created by Gromat Luidgi on 15/11/2017.
 */

public interface PayoutContract {

    interface View {
        void addPayouts(List<Payout> payoutList);
        void showContentLoading();
        void hideContentLoading();
        void showContentError(ApiError apiError);
    }

    interface Presenter {
        void load();

    }
}
