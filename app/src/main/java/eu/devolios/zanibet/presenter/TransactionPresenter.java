package eu.devolios.zanibet.presenter;

import android.content.Context;

import eu.devolios.zanibet.presenter.contract.TransactionContract;
import eu.devolios.zanibet.ws.Injector;
import eu.devolios.zanibet.ws.TransactionService;

public class TransactionPresenter implements TransactionContract.Presenter {


    private Context mContext;
    private TransactionContract.View mView;
    private TransactionService mTransactionService;

    public TransactionPresenter(Context context, TransactionContract.View view){
        this.mContext = context;
        this.mView = view;
        this.mTransactionService = Injector.provideTransactionService(context);
    }

    @Override
    public void loadTransactions() {

    }
}
