package eu.devolios.zanibet.presenter;

import android.content.Context;
import android.support.annotation.NonNull;

import eu.devolios.zanibet.model.ApiError;
import eu.devolios.zanibet.model.User;
import eu.devolios.zanibet.presenter.contract.AccountDetailsContract;
import eu.devolios.zanibet.ws.Injector;
import eu.devolios.zanibet.ws.UserService;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Gromat Luidgi on 16/11/2017.
 */

public class AccountDetailsPresenter implements AccountDetailsContract.Presenter {

    private Context mContext;
    private UserService mUserService;
    private AccountDetailsContract.View mView;

    public AccountDetailsPresenter(Context context, AccountDetailsContract.View view){
        mContext = context;
        mView = view;
        mUserService = Injector.provideUserService(context);
    }
    @Override
    public void updateAccount(User user) {
        mView.showUpdateLoading();
        mUserService.updateAccount(user).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                mView.hideUpdateLoading();
                if (response.isSuccessful()){
                    User.saveUser(response.body());
                    mView.onUpdateAccount();
                } else {
                    mView.showUpdateError(ApiError.parseError(response));
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                mView.hideUpdateLoading();
                mView.showUpdateError(ApiError.networkError(mContext));
            }
        });
    }

    @Override
    public void removeAccount() {
        mView.showRemoveLoading();
        mUserService.removeAccount().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                mView.hideRemoveLoading();
                if (response.isSuccessful()){
                    mView.onRemoveAccount();
                } else {
                    mView.showUpdateError(ApiError.parseError(response));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                mView.hideRemoveLoading();
                mView.showUpdateError(ApiError.networkError(mContext));
            }
        });
    }
}
