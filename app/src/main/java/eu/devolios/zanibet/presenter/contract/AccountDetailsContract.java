package eu.devolios.zanibet.presenter.contract;

import eu.devolios.zanibet.model.ApiError;
import eu.devolios.zanibet.model.User;

/**
 * Created by Gromat Luidgi on 16/11/2017.
 */

public interface AccountDetailsContract {

    interface View {
        void onUpdateAccount();
        void showUpdateLoading();
        void hideUpdateLoading();
        void showRemoveLoading();
        void hideRemoveLoading();
        void showUpdateError(ApiError apiError);
        void onRemoveAccount();
    }

    interface Presenter {
        void updateAccount(User user);
        void removeAccount();
    }
}
