package eu.devolios.zanibet.presenter.contract;

import eu.devolios.zanibet.model.ApiError;

/**
 * Created by Gromat Luidgi on 11/03/2018.
 */

public interface InviteFriendsContract {

    interface View {
        void showLoadingDialog();
        void hideLoadingDialog();
        void showUpdateInvitationCodeSuccess(String code);
        void showUpdateInvitationCodeError(ApiError apiError);
    }

    interface Presenter {
        void setCustomInvitationCode(String code);
    }

}
