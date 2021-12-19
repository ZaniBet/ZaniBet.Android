package eu.devolios.zanibet.presenter;

import android.content.Context;

import java.util.List;

import eu.devolios.zanibet.presenter.contract.GameTicketSingleFilterContract;

/**
 * Created by Gromat Luidgi on 26/03/2018.
 */

public class GameTicketSingleFilterPresenter implements GameTicketSingleFilterContract.Presenter {

    private Context mContext;
    private GameTicketSingleFilterContract.View mView;

    public GameTicketSingleFilterPresenter(Context context, GameTicketSingleFilterContract.View view){
        mContext = context;
        mView = view;
    }

    @Override
    public void loadFilter() {

    }

    @Override
    public void addFilter(String id) {

    }

    @Override
    public void addAllFilter(List<String> ids) {

    }

    @Override
    public void removeFilter(String id) {

    }

    @Override
    public void removeAllFilter() {

    }

    @Override
    public void saveFilter() {

    }
}
