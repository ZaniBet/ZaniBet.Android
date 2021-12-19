package eu.devolios.zanibet.presenter.contract;

import com.facebook.login.LoginResult;
import eu.devolios.zanibet.model.ApiError;

/**
 * Created by Gromat Luidgi on 09/11/2017.
 */

public interface RegisterContract {
    interface View {
        void showSignupLoading();
        void hideSignupLoading();
        void onSignup();
        void showSignupError(ApiError error);
        void onLogin();
        void showLoginError(ApiError apiError);
    }

    interface Presenter {
        void signup(String pseudo, String email, String password);
        void signupFacebook(LoginResult loginResult);
        void login(String email, String password);
    }
}
