package eu.devolios.zanibet.presenter;

import android.content.Context;

import com.kingfisher.easy_sharedpreference_library.SharedPreferencesManager;

import eu.devolios.zanibet.model.ApiError;
import eu.devolios.zanibet.model.Payout;
import eu.devolios.zanibet.model.User;
import eu.devolios.zanibet.presenter.contract.PayoutContract;
import eu.devolios.zanibet.utils.Constants;
import eu.devolios.zanibet.ws.Injector;
import eu.devolios.zanibet.ws.PayoutService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Gromat Luidgi on 15/11/2017.
 */

public class PayoutPresenter implements PayoutContract.Presenter {

    private Context mContext;
    private PayoutService mPayoutService;
    private PayoutContract.View mView;
    private List<Payout> mPayoutList;

    public PayoutPresenter(Context context, PayoutContract.View view){
        mContext = context;
        mView = view;
        mPayoutService = Injector.providePayoutService(context);
    }

    @Override
    public void load() {
        mView.showContentLoading();
        mPayoutService.getPayouts().enqueue(new Callback<List<Payout>>() {
            @Override
            public void onResponse(Call<List<Payout>> call, Response<List<Payout>> response) {
                mView.hideContentLoading();
                if(response.isSuccessful()){
                    mView.addPayouts(response.body());
                } else {
                    if (response.code() == 401)
                        SharedPreferencesManager.getInstance().putValue(Constants.IS_LOGGED_IN_PREF, false);
                    mView.showContentError(ApiError.parseError(response));
                }
            }

            @Override
            public void onFailure(Call<List<Payout>> call, Throwable t) {
                t.printStackTrace();
                mView.hideContentLoading();
                mView.showContentError(ApiError.networkError(mContext));
            }
        });
    }
}
