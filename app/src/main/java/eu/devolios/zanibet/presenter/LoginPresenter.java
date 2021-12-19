package eu.devolios.zanibet.presenter;

import android.content.Context;

import com.facebook.login.LoginResult;
import eu.devolios.zanibet.model.ApiError;
import eu.devolios.zanibet.model.FacebookOauthBody;
import eu.devolios.zanibet.presenter.contract.LoginContract;
import eu.devolios.zanibet.model.OauthBody;
import eu.devolios.zanibet.model.Token;
import eu.devolios.zanibet.model.User;
import eu.devolios.zanibet.utils.Constants;
import eu.devolios.zanibet.ws.AuthService;
import eu.devolios.zanibet.ws.Injector;
import eu.devolios.zanibet.ws.UserService;
import com.kingfisher.easy_sharedpreference_library.SharedPreferencesManager;

import java.util.Locale;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Gromat Luidgi on 11/11/2017.
 */

public class LoginPresenter implements LoginContract.Presenter {

    private Context mContext;
    private LoginContract.View mView;
    private AuthService mAuthService;
    private UserService mUserService;

    public LoginPresenter(Context context, LoginContract.View view){
        mContext = context;
        mView = view;
        mAuthService = Injector.provideAuthService(context);
        mUserService = Injector.provideUserService(context);
    }


    @Override
    public void login(String email, String password) {
        final OauthBody oauthBody = new OauthBody(email, password);

        mView.showLoginLoading();
        mAuthService.postLogin(oauthBody).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Token>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Token token) {
                        SharedPreferencesManager.getInstance().putValue(Constants.ACCESS_TOKEN_PREF, token.getAccessToken());
                        SharedPreferencesManager.getInstance().putValue(Constants.LAST_LOGIN_EMAIL_PREF, oauthBody.getUsername());
                        mView.onLogin("Basic");
                    }

                    @Override
                    public void onError(Throwable e) {
                        User.clearUserPreference();
                        mView.hideLoginLoading();
                        mView.showLoginError(ApiError.parseHttpError(mContext, e));
                    }
                });
    }

    @Override
    public void loginFacebook(LoginResult loginResult) {
        mView.showLoginLoading();
        FacebookOauthBody facebookOauthBody = new FacebookOauthBody();
        facebookOauthBody.setAccessToken(loginResult.getAccessToken().getToken());
        facebookOauthBody.setLocale(Locale.getDefault().getLanguage());

        mAuthService.postLoginWithFacebook(facebookOauthBody).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Token>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Token token) {
                        User.setAccessToken(token.getAccessToken());
                        mView.onLogin("Facebook");
                    }

                    @Override
                    public void onError(Throwable e) {
                        User.clearUserPreference();
                        mView.hideLoginLoading();
                        mView.showLoginError(ApiError.parseRxError(mContext, e));
                    }
                });

    }

    @Override
    public void loadUser() {

        mUserService.getCurrentUser()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<User>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(User user) {
                        User.saveUser(user);
                        mView.hideLoginLoading();
                        mView.onLoadUser(user);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.hideLoginLoading();
                        mView.showLoginError(ApiError.parseRxError(mContext, e));
                    }
                });
    }
}
