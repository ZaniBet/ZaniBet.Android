package eu.devolios.zanibet.presenter.contract;

import eu.devolios.zanibet.model.ApiError;
import eu.devolios.zanibet.model.Payout;
import eu.devolios.zanibet.model.Reward;

import java.util.List;

/**
 * Created by Gromat Luidgi on 09/11/2017.
 */

public interface RewardContract {

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
