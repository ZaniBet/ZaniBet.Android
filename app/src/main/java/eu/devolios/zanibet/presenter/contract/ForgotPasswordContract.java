package eu.devolios.zanibet.presenter.contract;

import eu.devolios.zanibet.model.ApiError;

/**
 * Created by Gromat Luidgi on 09/11/2017.
 */

public interface ForgotPasswordContract {

    interface View {
        void showResetPasswordLoading();
        void hideResetPasswordLoading();
        void onResetPassword();
        void showResetPasswordError(ApiError error);
    }

    interface Presenter {
        void resetPassword(String email);
    }
}
