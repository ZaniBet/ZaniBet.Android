package eu.devolios.zanibet.presenter.contract;

import eu.devolios.zanibet.model.ApiError;

/**
 * Created by Gromat Luidgi on 12/03/2018.
 */

public interface InvitationCodeContract {

    interface View {
        void showLoadingDialog();
        void hideLoadingDialog();
        void onValidateCode(int bonus);
        void showErrorDialog(ApiError apiError);
    }

    interface Presenter {
        void validateCode(String code);
    }
}
