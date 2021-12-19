package eu.devolios.zanibet.presenter.contract;

import java.util.List;

import eu.devolios.zanibet.model.ApiError;
import eu.devolios.zanibet.model.GrilleGroup;

/**
 * Created by Gromat Luidgi on 09/11/2017.
 */

public interface GrilleGroupContract {

    interface View {
        void addGroupGrilles(List<GrilleGroup> grilles);

        void showContentError(ApiError error);
        void hideContentError();

        void togglePaginateLoading(boolean value);
        void disablePaginate();

        void onRefresh();
    }

    interface Presenter {
        void loadMore();
        void refresh();
    }

}
