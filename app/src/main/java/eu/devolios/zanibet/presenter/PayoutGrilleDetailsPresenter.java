package eu.devolios.zanibet.presenter;

import android.content.Context;

import com.kingfisher.easy_sharedpreference_library.SharedPreferencesManager;

import java.util.HashMap;

import eu.devolios.zanibet.model.ApiError;
import eu.devolios.zanibet.model.Payout;
import eu.devolios.zanibet.presenter.contract.PayoutGrilleDetailsContract;
import eu.devolios.zanibet.utils.Constants;
import eu.devolios.zanibet.ws.Injector;
import eu.devolios.zanibet.ws.PayoutService;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PayoutGrilleDetailsPresenter implements PayoutGrilleDetailsContract.Presenter {


    private PayoutGrilleDetailsContract.View mView;
    private Context mContext;
    private PayoutService mPayoutService;

    public PayoutGrilleDetailsPresenter(Context context, PayoutGrilleDetailsContract.View view){
        this.mContext = context;
        this.mView = view;
        this.mPayoutService = Injector.providePayoutService(context);
    }

    @Override
    public void updatePaymentMethod(Payout payout, String method) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), "{paymentMethod:"+ method + "}");
        mView.showUpdateLoading();
        mPayoutService.putPayout(payout.getId(), requestBody).enqueue(new Callback<Payout>() {
            @Override
            public void onResponse(Call<Payout> call, Response<Payout> response) {
                mView.hideUpdateLoading();
                if (response.isSuccessful()){
                    mView.onUpdatePaymentMethodSuccess(response.body());
                } else {
                    if (response.code() == 401)
                        SharedPreferencesManager.getInstance().putValue(Constants.IS_LOGGED_IN_PREF, false);
                    mView.onUpdatePaymentMethodError(ApiError.parseError(response));
                }
            }

            @Override
            public void onFailure(Call<Payout> call, Throwable t) {
                mView.hideUpdateLoading();
                mView.onUpdatePaymentMethodError(ApiError.networkError(mContext));
            }
        });
    }
}
