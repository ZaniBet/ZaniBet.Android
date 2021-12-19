package eu.devolios.zanibet.presenter;

import android.content.Context;

import com.crashlytics.android.Crashlytics;

import eu.devolios.zanibet.model.ApiError;
import eu.devolios.zanibet.model.User;
import eu.devolios.zanibet.presenter.contract.ProfileDetailsContract;
import eu.devolios.zanibet.ws.Injector;
import eu.devolios.zanibet.ws.UserService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Gromat Luidgi on 16/11/2017.
 */

public class ProfileDetailsPresenter implements ProfileDetailsContract.Presenter {

    private Context mContext;
    private UserService mUserService;
    private ProfileDetailsContract.View mView;

    public ProfileDetailsPresenter(Context context, ProfileDetailsContract.View view){
        mContext = context;
        mView = view;
        mUserService = Injector.provideUserService(context);
    }

    @Override
    public void updateProfile(User user) {
        mView.showUpdateLoading();
        mUserService.updatePaiement( user).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                mView.hideUpdateLoading();
                if (response.isSuccessful()){
                    User.saveUser(response.body());
                    mView.onUpdateProfile(response.body());
                } else {
                    mView.showUpdateError(ApiError.parseError(response));
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Crashlytics.logException(t);
                mView.hideUpdateLoading();
                mView.showUpdateError(ApiError.networkError(mContext));
            }
        });
    }
}
