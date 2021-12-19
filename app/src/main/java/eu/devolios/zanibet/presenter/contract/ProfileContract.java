package eu.devolios.zanibet.presenter.contract;

import eu.devolios.zanibet.model.ApiError;
import eu.devolios.zanibet.model.User;
import eu.devolios.zanibet.presenter.ProfilePresenter;

import java.util.List;

/**
 * Created by Gromat Luidgi on 09/11/2017.
 */

public interface ProfileContract {

    interface View {
        void addMenus(List<ProfilePresenter.Menu> menuList);
        void onLoad(User user);
        void showLoadError(ApiError apiError);
    }

    interface Presenter {
        void load();
    }

}
