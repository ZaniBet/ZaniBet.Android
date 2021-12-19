package eu.devolios.zanibet.presenter;

import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.api.Api;
import com.kingfisher.easy_sharedpreference_library.SharedPreferencesManager;

import java.util.List;

import eu.devolios.zanibet.model.ApiError;
import eu.devolios.zanibet.model.Transaction;
import eu.devolios.zanibet.model.User;
import eu.devolios.zanibet.presenter.contract.InvitedFriendsContract;
import eu.devolios.zanibet.presenter.contract.ZaniHashStatsContract;
import eu.devolios.zanibet.utils.Constants;
import eu.devolios.zanibet.ws.Injector;
import eu.devolios.zanibet.ws.TransactionService;
import eu.devolios.zanibet.ws.UserService;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Gromat Luidgi on 12/03/2018.
 */

public class ZaniHashStatsPresenter implements ZaniHashStatsContract.Presenter {

    private Context mContext;
    private TransactionService mTransactionService;
    private UserService mUserService;
    private ZaniHashStatsContract.View mView;

    public ZaniHashStatsPresenter(Context context, ZaniHashStatsContract.View view){
        mContext = context;
        mView = view;
        mTransactionService = Injector.provideTransactionService(context);
        mUserService = Injector.provideUserService(context);
    }

    @Override
    public void getTransactions() {
        mView.showContentLoading();
        mTransactionService.getZaniHashTransaction()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<Transaction>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(List<Transaction> transactions) {
                        mView.hideContentLoading();
                        mView.onLoadTransactions(transactions);
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

}
