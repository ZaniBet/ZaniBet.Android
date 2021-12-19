package eu.devolios.zanibet.presenter.contract;

import eu.devolios.zanibet.model.ApiError;
import eu.devolios.zanibet.model.User;

/**
 * Created by Gromat Luidgi on 16/11/2017.
 */

public interface ProfileDetailsContract {

    interface View {
        void onUpdateProfile(User user);
        void showUpdateLoading();
        void hideUpdateLoading();
        void showUpdateError(ApiError apiError);
    }

    interface Presenter {
        void updateProfile(User user);
    }

}
