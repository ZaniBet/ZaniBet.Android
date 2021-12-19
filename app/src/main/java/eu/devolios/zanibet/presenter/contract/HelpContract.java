package eu.devolios.zanibet.presenter.contract;

import eu.devolios.zanibet.model.ApiError;
import eu.devolios.zanibet.model.Help;

import java.util.List;

/**
 * Created by Gromat Luidgi on 09/11/2017.
 */

public interface HelpContract {

    interface View {
        void addHelps(List<Help> helps);
        void showLoadingContent();
        void hideLoadingContent();
        void showContentError(ApiError apiError);
        void hideContentError();
    }

    interface Presenter {
        void load();
    }
}
