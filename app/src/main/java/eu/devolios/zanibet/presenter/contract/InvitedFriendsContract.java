package eu.devolios.zanibet.presenter.contract;

import java.util.List;

import eu.devolios.zanibet.model.Transaction;

/**
 * Created by Gromat Luidgi on 11/03/2018.
 */

public interface InvitedFriendsContract {

    interface View {
        void onLoadTransactions(List<Transaction> transactionList);
        void showContentError();
        void showContentLoading();
        void hideContentLoading();
    }

    interface Presenter {
        void getTransactions();
    }

}
