package eu.devolios.zanibet.presenter.contract;

import eu.devolios.zanibet.model.ApiError;
import eu.devolios.zanibet.model.Nonce;
import eu.devolios.zanibet.model.SafetyResponse;

/**
 * Created by Gromat Luidgi on 16/11/2017.
 */

public interface LoadingContract {

    interface View {
        void onLoad();
        void isLoggedIn(boolean value);

        void onLoadUserError(ApiError apiError);
        void onLoadError(ApiError apiError);
        //void onInitSafety(Nonce nonce);
        //void onCheckSafety(boolean value);
        void onRegisterDevice();
    }

    interface Presenter {
        void load();
        //void initSafety();
        //void checkSafety(String safetyResponse);
        void registerDevice();
    }
}
