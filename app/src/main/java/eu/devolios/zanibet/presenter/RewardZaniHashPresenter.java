package eu.devolios.zanibet.presenter;

import android.content.Context;

import com.kingfisher.easy_sharedpreference_library.SharedPreferencesManager;

import java.util.List;

import eu.devolios.zanibet.model.ApiError;
import eu.devolios.zanibet.model.Payout;
import eu.devolios.zanibet.model.Reward;
import eu.devolios.zanibet.model.User;
import eu.devolios.zanibet.presenter.contract.RewardContract;
import eu.devolios.zanibet.presenter.contract.RewardZaniHashContract;
import eu.devolios.zanibet.utils.Constants;
import eu.devolios.zanibet.ws.Injector;
import eu.devolios.zanibet.ws.PayoutService;
import eu.devolios.zanibet.ws.RewardService;
import eu.devolios.zanibet.ws.UserService;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Gromat Luidgi on 11/11/2017.
 */

public class RewardZaniHashPresenter implements RewardZaniHashContract.Presenter {

    private Context mContext;
    private RewardZaniHashContract.View mView;
    private RewardService mRewardService;
    private PayoutService mPayoutService;
    private UserService mUserService;

    public RewardZaniHashPresenter(Context context, RewardZaniHashContract.View view){
        mContext = context;
        mView = view;
        mRewardService = Injector.provideRewardService(context);
        mUserService = Injector.provideUserService(context);
        mPayoutService = Injector.providePayoutService(context);
    }

    @Override
    public void load() {
        mView.showContentLoading();
        mRewardService.getRewardsZaniHash()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<Reward>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(List<Reward> rewards) {
                        mView.hideContentLoading();
                        mView.addRewards(rewards);

                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.hideContentLoading();
                        mView.showContentError(ApiError.parseRxError(mContext, e));
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
                        mView.onLoadUser();
                    }

                    @Override
                    public void onError(Throwable e) {
                    }
                });
    }

    @Override
    public void createPayout(Reward reward) {
        mView.showLoadingDialog();
        mPayoutService.postPayoutReward(reward).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                mView.hideLoadingDialog();
                if (response.isSuccessful()){
                    mView.onCreatePayout();
                } else {
                    if (response.code() == 401)
                        SharedPreferencesManager.getInstance().putValue(Constants.IS_LOGGED_IN_PREF, false);
                    mView.showCreatePayoutError(ApiError.parseError(response));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                mView.hideLoadingDialog();
                mView.showCreatePayoutError(ApiError.networkError(mContext));
            }
        });
    }
}
