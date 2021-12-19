package eu.devolios.zanibet.presenter;

import android.content.Context;

import eu.devolios.zanibet.model.ApiError;
import eu.devolios.zanibet.model.User;
import eu.devolios.zanibet.presenter.contract.ForgotPasswordContract;
import eu.devolios.zanibet.ws.AuthService;
import eu.devolios.zanibet.ws.Injector;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Gromat Luidgi on 11/11/2017.
 */

public class ForgotPasswordPresenter implements ForgotPasswordContract.Presenter {

    private Context mContext;
    private ForgotPasswordContract.View mView;
    private AuthService mAuthService;

    public ForgotPasswordPresenter(Context context, ForgotPasswordContract.View view){
        mContext = context;
        mView = view;
        mAuthService = Injector.provideAuthService(context);
    }


    @Override
    public void resetPassword(String email) {

        User user = new User();
        user.setEmail(email);

        mView.showResetPasswordLoading();
        mAuthService.putResetPassword(user).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                mView.hideResetPasswordLoading();
                if (response.isSuccessful()){
                    mView.onResetPassword();
                } else {
                    mView.showResetPasswordError(ApiError.parseError(response));
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                mView.hideResetPasswordLoading();
                mView.showResetPasswordError(ApiError.networkError(mContext));
            }
        });
    }
}
