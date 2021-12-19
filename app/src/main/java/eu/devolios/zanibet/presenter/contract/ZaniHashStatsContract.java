package eu.devolios.zanibet.presenter.contract;

import java.util.List;

import eu.devolios.zanibet.model.ApiError;
import eu.devolios.zanibet.model.Transaction;

public interface ZaniHashStatsContract {

    interface View {
        void onLoadTransactions(List<Transaction> transactionList);
        void onLoadUser();
        void showContentError(ApiError apiError);
        void hideContentError();
        void showContentLoading();
        void hideContentLoading();
    }

    interface Presenter {
        void getTransactions();
        void loadUser();
    }

}
