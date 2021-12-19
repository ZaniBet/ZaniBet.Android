package eu.devolios.zanibet.presenter;

import eu.devolios.zanibet.presenter.contract.GrilleDetailsContract;

/**
 * Created by Gromat Luidgi on 11/11/2017.
 */

public class GrilleDetailsPresenter implements GrilleDetailsContract.Presenter {

    private GrilleDetailsContract.View mView;

    public GrilleDetailsPresenter(GrilleDetailsContract.View view){
        mView = view;
    }

    @Override
    public void load() {

    }
}
