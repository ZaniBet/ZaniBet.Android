package eu.devolios.zanibet.presenter.contract;

import eu.devolios.zanibet.model.ApiError;

/**
 * Created by Gromat Luidgi on 02/03/2018.
 */

public interface AdsMissionContract {

    interface View {
        void onJetonRequest();
        void showLoadingDialog();
        void hideLoadingDialog();
        void showErrorDialog(ApiError error);
        void onLoadUser();
    }

    interface Presenter {
        void getMoreJeton();
        void getUser();
    }

}
