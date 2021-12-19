package eu.devolios.zanibet.presenter;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.kingfisher.easy_sharedpreference_library.SharedPreferencesManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import eu.devolios.zanibet.model.ApiError;
import eu.devolios.zanibet.model.GameTicket;
import eu.devolios.zanibet.model.User;
import eu.devolios.zanibet.presenter.contract.GameTicketSingleContract;
import eu.devolios.zanibet.utils.AnnotationJSONConverterFactory;
import eu.devolios.zanibet.utils.Constants;
import eu.devolios.zanibet.ws.GameTicketService;
import eu.devolios.zanibet.ws.Injector;
import eu.devolios.zanibet.ws.UserService;
import io.reactivex.Observer;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;

/**
 * Created by Gromat Luidgi on 05/01/2018.
 */

public class GameTicketSinglePresenter implements GameTicketSingleContract.Presenter{

    private final static int FORCE_REFRESH_THRESOLD = 5;

    private Context mContext;
    private GameTicketService mGameTicketService;
    private GameTicketSingleContract.View mView;
    private UserService mUserService;
    private ArrayList<GameTicket> mGameTicketArrayList;

    private int mLimit = 5;
    private int mCurrentPage = 0;
    private List<String> mCompetitionFilter;
    private long mLastRefresh = 0;

    public GameTicketSinglePresenter(Context context, GameTicketSingleContract.View view){
        mContext = context;
        mView = view;
        mUserService = Injector.provideUserService(context);
        mGameTicketService = Injector.provideGameTickerService(context,
                new AnnotationJSONConverterFactory(AnnotationJSONConverterFactory.JACKSON));
        mGameTicketArrayList = new ArrayList<>();
        mCompetitionFilter = new ArrayList<>();
    }

    public boolean shouldForceRefresh(){
        long currentTime = System.currentTimeMillis();
        if (currentTime > (mLastRefresh+((1000*60)*FORCE_REFRESH_THRESOLD))){
            Log.d("ZaniBet", "Should force refresh single gameticket");
            mCurrentPage = 1;
            return true;
        }
        return false;
    }

    @Override
    public void loadMore() {
        mView.hideContentError();
        mView.setPaginateLoading(true);

        if (SharedPreferencesManager.getInstance().getValues(Constants.TICKET_SINGLE_FILTER_PREF, String[].class) != null){
            mCompetitionFilter.clear();
            mCompetitionFilter.addAll(SharedPreferencesManager.getInstance().getValues(Constants.TICKET_SINGLE_FILTER_PREF, String[].class));
        }

        String filter = "";
        for (String ft : mCompetitionFilter){
            filter += ft +";";
        }

        mGameTicketService.getGameTickets("SINGLE", mCurrentPage, mLimit, filter)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<GameTicket>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<GameTicket> gameTickets) {
                        if (gameTickets.isEmpty() && mGameTicketArrayList.size() > 0){
                            mView.setPaginateLoading(false);
                            mView.setNoPaginate(true);
                        } else if (gameTickets.isEmpty()) {
                            mView.setPaginateLoading(false);
                            mView.setNoPaginate(true);
                            mView.onLoadGameTicket(mGameTicketArrayList);
                        } else {
                            mCurrentPage++;
                            mGameTicketArrayList.addAll(gameTickets);
                            mView.onLoadGameTicket(mGameTicketArrayList);
                            mView.setNoPaginate(false);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof HttpException){
                            switch(((HttpException) e).code()){
                                case 401:
                                    SharedPreferencesManager.getInstance().putValue(Constants.IS_LOGGED_IN_PREF, false);
                                    break;
                            }
                        }

                        mView.setPaginateLoading(false);
                        mView.setNoPaginate(true);
                        mView.showContentError(ApiError.parseRxError(mContext, e));
                    }

                    @Override
                    public void onComplete() {
                        mView.setPaginateLoading(false);
                        if (mGameTicketArrayList.isEmpty()){
                            mView.setNoPaginate(true);
                        }
                    }
                });
    }


    @Override
    public void loadUser() {
        new Handler().postDelayed(() -> mUserService.getCurrentUser()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<User>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(User user) {
                        User.saveUser(user);
                        mView.onLoadUser();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                }), 20);
    }

    @Override
    public void refresh() {
        mView.showContentLoading();

        if (SharedPreferencesManager.getInstance().getValues(Constants.TICKET_SINGLE_FILTER_PREF, String[].class) != null){
            mCompetitionFilter.clear();
            mCompetitionFilter.addAll(SharedPreferencesManager.getInstance().getValues(Constants.TICKET_SINGLE_FILTER_PREF, String[].class));
        }

        String filter = "";
        for (String ft : mCompetitionFilter){
            filter += ft +";";
        }

        if (mGameTicketArrayList.isEmpty()) mCurrentPage = 1;
        mGameTicketService.getGameTickets("SINGLE", 0, mCurrentPage*mLimit, filter)
                .delay(2, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<GameTicket>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<GameTicket> gameTickets) {
                        mGameTicketArrayList.clear();
                        mGameTicketArrayList.addAll(gameTickets);
                        mView.onLoadGameTicket(mGameTicketArrayList);
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof HttpException){
                            switch(((HttpException) e).code()){
                                case 401:
                                    SharedPreferencesManager.getInstance().putValue(Constants.IS_LOGGED_IN_PREF, false);
                                    break;
                            }
                        }
                        mView.hideContentLoading();
                        mView.setPaginateLoading(false);
                        mView.setNoPaginate(true);
                        mView.showContentError(ApiError.parseRxError(mContext, e));
                    }

                    @Override
                    public void onComplete() {
                        mLastRefresh = System.currentTimeMillis();
                        mView.hideContentLoading();
                        mView.setNoPaginate(false);
                        if(mGameTicketArrayList.isEmpty()){
                            mView.setNoPaginate(true);
                        }
                    }
                });
    }
}
