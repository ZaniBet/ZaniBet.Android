package eu.devolios.zanibet.presenter;

import android.content.Context;
import android.support.annotation.NonNull;

import com.facebook.login.LoginResult;
import eu.devolios.zanibet.model.ApiError;
import eu.devolios.zanibet.model.FacebookOauthBody;
import eu.devolios.zanibet.model.OauthBody;
import eu.devolios.zanibet.model.Token;
import eu.devolios.zanibet.model.User;
import eu.devolios.zanibet.presenter.contract.RegisterContract;
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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Gromat Luidgi on 11/11/2017.
 */

public class RegisterPresenter implements RegisterContract.Presenter {

    private RegisterContract.View mView;
    private UserService mUserService;
    private AuthService mAuthService;
    private Context mContext;

    public RegisterPresenter(Context context, RegisterContract.View view){
        mContext = context;
        mView = view;
        mUserService = Injector.provideUserService(context);
        mAuthService = Injector.provideAuthService(context);
    }

    @Override
    public void signup(String pseudo, String email, String password) {
        User user = new User();
        user.setUsername(pseudo);
        user.setEmail(email);
        user.setPassword(password);
        user.setLocale(Locale.getDefault().getLanguage());

        mView.showSignupLoading();
        mUserService.createUser(user).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                mView.hideSignupLoading();
                if (response.isSuccessful()){
                    User.saveUser(response.body());
                    mView.onSignup();
                } else {
                    mView.showSignupError(ApiError.parseError(response));
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                mView.hideSignupLoading();
                mView.showSignupError(ApiError.networkError(mContext));
            }
        });
    }

    @Override
    public void signupFacebook(LoginResult loginResult) {
        mView.showSignupLoading();
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
                        mView.onLogin();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.hideSignupLoading();
                        mView.showSignupError(ApiError.parseRxError(mContext, e));
                    }
                });
    }

    @Override
    public void login(String email, String password) {
        final OauthBody oauthBody = new OauthBody(email, password);
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
                        mView.onLogin();
                    }

                    @Override
                    public void onError(Throwable e) {
                        User.clearUserPreference();
                        mView.showLoginError(ApiError.parseRxError(mContext, e));
                    }
                });
    }


}
