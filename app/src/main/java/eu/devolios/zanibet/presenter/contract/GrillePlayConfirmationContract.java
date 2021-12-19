package eu.devolios.zanibet.presenter.contract;

/**
 * Created by Gromat Luidgi on 15/11/2017.
 */

public interface GrillePlayConfirmationContract {

    interface View {
        void onLoad();
        void showAskRating();
    }

    interface Presenter {
        void load();
    }

}
