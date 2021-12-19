package eu.devolios.zanibet.presenter.contract;

import java.util.List;

/**
 * Created by Gromat Luidgi on 26/03/2018.
 */

public interface GameTicketSingleFilterContract {

    interface View {
        void showLoadingDialog();
        void filterLoaded();
        void filterSaved();
    }

    interface Presenter {
        void loadFilter();
        void addFilter(String id);
        void addAllFilter(List<String> ids);
        void removeFilter(String id);
        void removeAllFilter();
        void saveFilter();
    }

}
