package eu.devolios.zanibet.presenter;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.crashlytics.android.Crashlytics;
import com.kingfisher.easy_sharedpreference_library.SharedPreferencesManager;

import eu.devolios.zanibet.ProfileDetailsActivity;
import eu.devolios.zanibet.R;
import eu.devolios.zanibet.model.ApiError;
import eu.devolios.zanibet.model.Bet;
import eu.devolios.zanibet.model.Fixture;
import eu.devolios.zanibet.model.GameTicket;
import eu.devolios.zanibet.model.Grille;
import eu.devolios.zanibet.presenter.contract.GrillePlayContract;
import eu.devolios.zanibet.utils.AnnotationJSONConverterFactory;
import eu.devolios.zanibet.utils.Constants;
import eu.devolios.zanibet.ws.GameTicketService;
import eu.devolios.zanibet.ws.GrilleService;
import eu.devolios.zanibet.ws.Injector;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.HttpException;

import static eu.devolios.zanibet.utils.AnnotationJSONConverterFactory.JACKSON;

/**
 * Created by Gromat Luidgi on 10/11/2017.
 */

public class GrillePlayPresenter implements GrillePlayContract.Presenter {

    private Context mContext;
    private GameTicketService mGameTicketService;
    private GrilleService mGrilleService;
    private GrillePlayContract.View mView;
    private HashMap<String, Integer> mBetsHashMap;

    public GrillePlayPresenter(Context context, GrillePlayContract.View view){
        mContext = context;
        mGameTicketService = Injector.provideGameTickerService(context,
                new AnnotationJSONConverterFactory(AnnotationJSONConverterFactory.JACKSON));
        mGrilleService = Injector.provideGrilleService(context, new AnnotationJSONConverterFactory(JACKSON));
        mView = view;
        mBetsHashMap = new HashMap<>();
    }

    // Configurer une grille de paris vide
    @Override
    public void load(GameTicket gameTicket) {
        for (Fixture fixture:gameTicket.getFixtures()){
            mBetsHashMap.put(fixture.getId(), null);
        }
        mView.onUpdateBet(mBetsHashMap);
    }

    // Mettre à jour une sélection dans la grille de paris
    @Override
    public void updateBet(String fixtureId, int result) {
        mBetsHashMap.put(fixtureId, result);
        mView.onUpdateBet(mBetsHashMap);
    }

    // Effacer la grille de paris
    @Override
    public void clearBets() {
        for (HashMap.Entry<String, Integer> entry:mBetsHashMap.entrySet()){
            entry.setValue(null);
        }
        mView.onUpdateBet(mBetsHashMap);
        mView.onClearBets();
    }

    @Override
    public void flashGrille() {
        for (HashMap.Entry<String, Integer> entry:mBetsHashMap.entrySet()){
            entry.setValue((int) (Math.random() * 3));
        }

        mView.onUpdateBet(mBetsHashMap);
        mView.onFlashTicket();
    }

    @Override
    public void playGrille(GameTicket gameTicket) {
        // Vérifier que tous les paris sont complet
        if (mBetsHashMap.containsValue(null)) {
            mView.showPlayTicketError(new ApiError(0,0,mContext.getString(R.string.incomplet_grid), mContext.getString(R.string.select_all_bet)));
            return;
        } else if (mBetsHashMap.size() < gameTicket.getFixtures().length){
            mView.showPlayTicketError(new ApiError(0,0,mContext.getString(R.string.incomplet_grid), mContext.getString(R.string.select_all_bet)));
            return;
        }

        // Construire la grille de paris
        ArrayList<Bet> betsArrayList = new ArrayList();
        for(Map.Entry<String, Integer> entry : mBetsHashMap.entrySet()) {
            Bet bet = new Bet();
            bet.setFixture(entry.getKey());
            bet.setResult(entry.getValue());
            betsArrayList.add(bet);
        }

        Bet[] betArr = new Bet[mBetsHashMap.size()];

        // Construire une grille
        String adsId = SharedPreferencesManager.getInstance().getValue(Constants.ADS_ID_PREF, String.class, "");
        String instanceId = SharedPreferencesManager.getInstance().getValue(Constants.INSTANCE_ID_PREF, String.class, "");

        Grille grille = new Grille();
        grille.setInstanceId(instanceId);
        grille.setBets(betsArrayList.toArray(betArr));
        grille.setGameTicket(gameTicket);
        grille.setAdvertisingId(adsId);

        mGrilleService.postGrille(grille)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Grille>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Grille grille) {
                        int count = SharedPreferencesManager.getInstance().getValue(Constants.COUNT_PLAY_PREF,
                                Integer.class, 0);
                        int countMulti = SharedPreferencesManager.getInstance().getValue(Constants.COUNT_MULTI_PLAY_PREF,
                                Integer.class, 0);

                        SharedPreferencesManager.getInstance().putValue(Constants.COUNT_MULTI_PLAY_PREF, countMulti+1);
                        SharedPreferencesManager.getInstance().putValue(Constants.COUNT_PLAY_PREF, count+1);

                        /*if (countMulti > 1){
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(System.currentTimeMillis());
                            calendar.add(Calendar.MILLISECOND, 10000);

                            NotifyMe.Builder notifyMe = new NotifyMe.Builder(mContext);
                            notifyMe.title(R.string.notif_title_complete_payment);
                            notifyMe.content(R.string.notif_content_complete_payment);
                            notifyMe.time(calendar);
                            notifyMe.delay(5000);
                            notifyMe.build();
                        }*/

                        /*try {
                            if (countMulti > 1) {
                                NotificationManager notificationManager =
                                        (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext)
                                        .setSmallIcon(R.mipmap.ic_launcher)
                                        .setContentTitle(mContext.getString(R.string.notif_title_complete_payment))
                                        .setContentText(mContext.getString(R.string.notif_content_complete_payment));

                                Intent notificationIntent = new Intent(mContext, ProfileDetailsActivity.class);
                                PendingIntent contentIntent =
                                        PendingIntent.getActivity(mContext, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                notificationBuilder.setContentIntent(contentIntent);
                                Notification notification = notificationBuilder.build();
                                notification.flags |= Notification.FLAG_AUTO_CANCEL;
                                notification.defaults |= Notification.DEFAULT_SOUND;
                                notificationManager.notify(0, notification);
                            }
                        } catch (Exception e){
                            e.printStackTrace();
                            Crashlytics.logException(e);
                        }*/

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

                        mView.showPlayTicketError(ApiError.parseRxError(mContext, e));
                    }
                });
    }

    @Override
    public void validateGrille() {
        mView.onValidateGrille();
    }

    @Override
    public void cancelGrille(Grille grille) {
        mGrilleService.cancelGrille(grille.get_id()).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(ResponseBody responseBody) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }

}
