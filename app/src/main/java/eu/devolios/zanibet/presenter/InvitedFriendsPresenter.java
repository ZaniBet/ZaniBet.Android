package eu.devolios.zanibet.presenter;

import android.content.Context;

import com.kingfisher.easy_sharedpreference_library.SharedPreferencesManager;

import java.util.List;

import eu.devolios.zanibet.model.Transaction;
import eu.devolios.zanibet.model.User;
import eu.devolios.zanibet.presenter.contract.InvitedFriendsContract;
import eu.devolios.zanibet.utils.Constants;
import eu.devolios.zanibet.ws.Injector;
import eu.devolios.zanibet.ws.TransactionService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Gromat Luidgi on 12/03/2018.
 */

public class InvitedFriendsPresenter implements InvitedFriendsContract.Presenter {

    private TransactionService mTransactionService;
    private InvitedFriendsContract.View mView;

    public InvitedFriendsPresenter(Context context, InvitedFriendsContract.View view){
        mTransactionService = Injector.provideTransactionService(context);
        mView = view;
    }

    @Override
    public void getTransactions() {
        mView.showContentLoading();
        mTransactionService.getReferralTransaction().enqueue(new Callback<List<Transaction>>() {
            @Override
            public void onResponse(Call<List<Transaction>> call, Response<List<Transaction>> response) {
                mView.hideContentLoading();
                if (response.isSuccessful()){
                    mView.onLoadTransactions(response.body());
                } else {
                    if (response.code() == 401)
                        SharedPreferencesManager.getInstance().putValue(Constants.IS_LOGGED_IN_PREF, false);
                }
            }

            @Override
            public void onFailure(Call<List<Transaction>> call, Throwable t) {
                mView.hideContentLoading();
                mView.showContentError();
            }
        });
    }
}
