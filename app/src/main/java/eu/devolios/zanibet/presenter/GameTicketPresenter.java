package eu.devolios.zanibet.presenter;

import android.content.Context;
import android.util.Log;

import com.kingfisher.easy_sharedpreference_library.SharedPreferencesManager;

import eu.devolios.zanibet.model.ApiError;
import eu.devolios.zanibet.model.GameTicket;
import eu.devolios.zanibet.model.User;
import eu.devolios.zanibet.presenter.contract.GameTicketContract;
import eu.devolios.zanibet.utils.AnnotationJSONConverterFactory;
import eu.devolios.zanibet.utils.Constants;
import eu.devolios.zanibet.ws.GameTicketService;
import eu.devolios.zanibet.ws.Injector;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;

/**
 * Created by Gromat Luidgi on 09/11/2017.
 */

public class GameTicketPresenter implements GameTicketContract.Presenter {

    private static int FORCE_REFRESH_THRESOLD = 5; // Minutes

    private Context mContext;
    private GameTicketService mGameTicketService;
    private GameTicketContract.View mView;
    private ArrayList<GameTicket> mTicketArrayList;

    private long mLastRefresh = 0;
    private int mLimit = 3;
    private int mCurrentPage = 0;

    public GameTicketPresenter(Context context, GameTicketContract.View view){
        mContext = context;
        mView = view;
        mGameTicketService = Injector.provideGameTickerService(context,
                new AnnotationJSONConverterFactory(AnnotationJSONConverterFactory.JACKSON));
        mTicketArrayList = new ArrayList<>();
    }

    public boolean shouldForceRefresh(){
        long currentTime = System.currentTimeMillis();
        if (currentTime > (mLastRefresh+((1000*60)*FORCE_REFRESH_THRESOLD))){
            Log.d("GameTicketPresenter", "Force refresh");
            mCurrentPage = 1;
            return true;
        }
        return false;
    }

    @Override
    public void refresh() {
        mView.hideContentError();
        mView.showContentLoading();

        boolean filter = SharedPreferencesManager.getInstance().getValue(Constants.TICKET_MULTI_FILTER_PREF, Boolean.class, false);

        if (mTicketArrayList.isEmpty()){
            mCurrentPage = 1;
        }

        mGameTicketService.getMatchdayGameTickets( "MATCHDAY", 0, mCurrentPage*mLimit, filter)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<GameTicket>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<GameTicket> gameTickets) {
                        mTicketArrayList.clear();
                        mTicketArrayList.addAll(gameTickets);
                        mView.addTickets(mTicketArrayList);
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof HttpException){
                            switch(((HttpException) e).code()){
                                case 401:
                                    User.clearUserPreference();
                                    break;
                            }
                        }

                        mView.hideContentLoading();
                        mView.onRefresh();
                        mView.setNoPaginate(true);
                        mView.showContentError(ApiError.parseRxError(mContext, e));
                    }

                    @Override
                    public void onComplete() {
                        mLastRefresh = System.currentTimeMillis();
                        mView.hideContentLoading();
                        mView.onRefresh();
                        mView.setNoPaginate(false);
                    }
                });
    }

    @Override
    public void loadMore() {
        if (mTicketArrayList.isEmpty()){
            mCurrentPage = 0;
        }

        mView.hideContentError();
        mView.setPaginateLoading(true);

        boolean filter = SharedPreferencesManager.getInstance().getValue(Constants.TICKET_MULTI_FILTER_PREF, Boolean.class, false);

        mGameTicketService.getMatchdayGameTickets( "MATCHDAY", mCurrentPage, mLimit, filter)
                //.delay(600, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<GameTicket>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<GameTicket> gameTickets) {
                        if (gameTickets.isEmpty() && mTicketArrayList.size() > 0) {
                            mView.setNoPaginate(true);
                        } else {
                            mTicketArrayList.addAll(gameTickets);
                            mCurrentPage++;
                            mView.addTickets(mTicketArrayList);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof HttpException){
                            switch(((HttpException) e).code()){
                                case 401:
                                    User.clearUserPreference();
                                    break;
                            }
                        }

                        mCurrentPage = 0;
                        mView.setPaginateLoading(false);
                        mView.setNoPaginate(true);
                        mView.showContentError(ApiError.parseRxError(mContext, e));
                    }

                    @Override
                    public void onComplete() {
                        mView.setPaginateLoading(false);
                        if (mTicketArrayList.isEmpty()){
                            mView.setNoPaginate(true);
                        }
                    }
                });
    }

}
