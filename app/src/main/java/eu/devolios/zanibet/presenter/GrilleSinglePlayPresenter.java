package eu.devolios.zanibet.presenter;

import android.content.Context;

import com.kingfisher.easy_sharedpreference_library.SharedPreferencesManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import eu.devolios.zanibet.R;
import eu.devolios.zanibet.model.ApiError;
import eu.devolios.zanibet.model.Bet;
import eu.devolios.zanibet.model.BetType;
import eu.devolios.zanibet.model.GameTicket;
import eu.devolios.zanibet.model.Grille;
import eu.devolios.zanibet.model.User;
import eu.devolios.zanibet.presenter.contract.GrilleSinglePlayContract;
import eu.devolios.zanibet.utils.AnnotationJSONConverterFactory;
import eu.devolios.zanibet.utils.Constants;
import eu.devolios.zanibet.ws.GrilleService;
import eu.devolios.zanibet.ws.Injector;
import eu.devolios.zanibet.ws.UserService;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;

import static eu.devolios.zanibet.utils.AnnotationJSONConverterFactory.JACKSON;

/**
 * Created by Gromat Luidgi on 10/11/2017.
 */

public class GrilleSinglePlayPresenter implements GrilleSinglePlayContract.Presenter {

    private Context mContext;
    private UserService mUserService;
    private GrilleService mGrilleService;
    private GrilleSinglePlayContract.View mView;
    private HashMap<String, Integer> mBetsHashMap;

    public GrilleSinglePlayPresenter(Context context, GrilleSinglePlayContract.View view){
        mContext = context;
        //mGameTicketService = Injector.provideGameTickerService(context,
          //      new AnnotationJSONConverterFactory(AnnotationJSONConverterFactory.JACKSON));
        mUserService = Injector.provideUserService(context);
        mGrilleService = Injector.provideGrilleService(context, new AnnotationJSONConverterFactory(JACKSON));
        mView = view;
        mBetsHashMap = new HashMap<>();
    }

    // Configurer une grille de paris vide
    @Override
    public void load(GameTicket gameTicket) {
        for (BetType betType:gameTicket.getBetsType()){
            mBetsHashMap.put(betType.getType(), null);
        }
        mView.onUpdateBet(mBetsHashMap);
    }

    // Mettre à jour une sélection dans la grille de paris
    @Override
    public void updateBet(String betType, int result) {
        mBetsHashMap.put(betType, result);
        mView.onUpdateBet(mBetsHashMap);
    }

    @Override
    public void playGrille(GameTicket gameTicket) {
        mView.showLoadingDialog(mContext.getString(R.string.processing), mContext.getString(R.string.dlg_content_processing_bet));

        // Vérifier que tous les paris sont complet
        if (mBetsHashMap.containsValue(null)) {
            mView.hideLoadingDialog();
            mView.showPlayTicketError(new ApiError(0,0,mContext.getString(R.string.incomplet_grid), mContext.getString(R.string.select_all_bet)));
            return;
        } else if (mBetsHashMap.size() < gameTicket.getBetsType().length){
            mView.hideLoadingDialog();
            mView.showPlayTicketError(new ApiError(0,0,mContext.getString(R.string.incomplet_grid), mContext.getString(R.string.select_all_bet)));
            return;
        }

        // Construire la grille de paris
        ArrayList<Bet> betsArrayList = new ArrayList();
        for(Map.Entry<String, Integer> entry : mBetsHashMap.entrySet()) {
            Bet bet = new Bet();
            bet.setType(entry.getKey());
            bet.setResult(entry.getValue());
            betsArrayList.add(bet);
        }

        Bet[] betArr = new Bet[mBetsHashMap.size()];

        // Construire une grille
        Grille grille = new Grille();
        grille.setInstanceId(SharedPreferencesManager.getInstance().getValue(Constants.INSTANCE_ID_PREF, String.class, ""));
        grille.setBets(betsArrayList.toArray(betArr));
        grille.setGameTicket(gameTicket);

        mGrilleService.postGrilleSingle(grille).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Grille>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Grille grille) {
                        mView.hideLoadingDialog();
                        int count = SharedPreferencesManager.getInstance().getValue(Constants.COUNT_PLAY_PREF,
                                Integer.class, 0);
                        int adsCount = SharedPreferencesManager.getInstance().getValue(Constants.COUNT_ADS_ACTION_PREF,
                                Integer.class, 0);
                        SharedPreferencesManager.getInstance().putValue(Constants.COUNT_PLAY_PREF, count+1);
                        SharedPreferencesManager.getInstance().putValue(Constants.COUNT_ADS_ACTION_PREF, adsCount+1);
                        loadUser();
                        mView.onPlayGrille(grille);

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
                        mView.hideLoadingDialog();
                        mView.showPlayTicketError(ApiError.parseRxError(mContext, e));
                        e.printStackTrace();
                    }
                });
    }

    private void loadUser(){
        mUserService.getCurrentUser()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<User>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(User user) {
                        User.saveUser(user);

                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });

    }
}
