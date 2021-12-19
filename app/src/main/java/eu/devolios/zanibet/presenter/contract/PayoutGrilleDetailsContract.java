package eu.devolios.zanibet.presenter.contract;

import java.util.HashMap;

import eu.devolios.zanibet.model.ApiError;
import eu.devolios.zanibet.model.Payout;
import okhttp3.RequestBody;

public interface PayoutGrilleDetailsContract {

    interface View {
        void showUpdateLoading();
        void hideUpdateLoading();
        void onUpdatePaymentMethodSuccess(Payout payout);
        void onUpdatePaymentMethodError(ApiError apiError);
    }

    interface Presenter {

        void updatePaymentMethod(Payout payout, String paymentMethod);

    }

}
