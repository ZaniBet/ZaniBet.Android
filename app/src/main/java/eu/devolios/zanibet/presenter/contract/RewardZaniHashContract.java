package eu.devolios.zanibet.presenter.contract;

import java.util.List;

import eu.devolios.zanibet.model.ApiError;
import eu.devolios.zanibet.model.Payout;
import eu.devolios.zanibet.model.Reward;

/**
 * Created by Gromat Luidgi on 09/11/2017.
 */

public interface RewardZaniHashContract {

    interface View {
        void addRewards(List<Reward> rewardList);
        void showContentLoading();
        void hideContentLoading();
        void showContentError(ApiError apiError);
        void onLoadUser();

        void onCreatePayout();
        void showLoadingDialog();
        void hideLoadingDialog();
        void showCreatePayoutError(ApiError apiError);
    }

    interface Presenter {
        void load();
        void loadUser();
        void createPayout(Reward reward);
    }
}
