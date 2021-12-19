package eu.devolios.zanibet.presenter.contract;

import eu.devolios.zanibet.model.ApiError;

public interface ZaniHashPresentationContract {

    interface View {

        void onEnableZaniAnalytics(boolean value);
        void showEnableZaniAnalyticsLoadingDialog();
        void hideEnableZaniAnalyticsLoadingDialog();
        void showEnableZaniAnalyticsErrorDialog(ApiError apiError);
    }

    interface Presenter {

        void enableZaniAnalytics(boolean value);

    }

}
