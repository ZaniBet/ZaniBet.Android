package eu.devolios.zanibet.presenter;

import android.content.Context;
import android.os.Handler;

import eu.devolios.zanibet.model.ApiError;
import eu.devolios.zanibet.model.User;
import eu.devolios.zanibet.presenter.contract.AdsMissionContract;
import eu.devolios.zanibet.ws.Injector;
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
 * Created by Gromat Luidgi on 02/03/2018.
 */

public class AdsMissionPresenter implements AdsMissionContract.Presenter {

    private Context mContext;
    private UserService mUserService;
    private AdsMissionContract.View mView;

    public AdsMissionPresenter(Context context, AdsMissionContract.View view){
        this.mContext = context;
        this.mView = view;
        this.mUserService = Injector.provideUserService(context);
    }

    @Override
    public void getMoreJeton() {
        mView.showLoadingDialog();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mUserService.getMoreJeton().enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()){
                            mView.hideLoadingDialog();
                            mView.onJetonRequest();
                        } else {
                            mView.hideLoadingDialog();
                            mView.showErrorDialog(ApiError.parseError(response));
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        mView.hideLoadingDialog();
                        mView.showErrorDialog(ApiError.networkError(mContext));
                    }
                });
            }
        }, 2000);
    }

    @Override
    public void getUser() {
        mView.showLoadingDialog();

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
                        mView.hideLoadingDialog();
                        mView.onLoadUser();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.hideLoadingDialog();
                    }
                });
    }
}
