package eu.devolios.zanibet.presenter.contract;

import java.util.ArrayList;
import java.util.List;

import eu.devolios.zanibet.model.ApiError;
import eu.devolios.zanibet.model.Transaction;

public interface TransactionContract {

    interface View {
        void onLoadTransactions(List<Transaction> transactions);
        void showContentLoading();
        void hideContentLoading();
        void showContentError(ApiError apiError);
        void hideContentError();
    }

    interface Presenter {
        void loadTransactions();
    }

}
