package eu.devolios.zanibet.presenter.contract;

import com.facebook.login.LoginResult;
import eu.devolios.zanibet.model.ApiError;
import eu.devolios.zanibet.model.User;

/**
 * Created by Gromat Luidgi on 09/11/2017.
 */

public interface LoginContract {

    interface View {
        void onLogin(String method);
        void onLoadUser(User user);
        void showLoginLoading();
        void hideLoginLoading();
        void showLoginError(ApiError error);
    }

    interface Presenter {
        void login(String email, String password);
        void loginFacebook(LoginResult loginResult);
        void loadUser();

    }

}
