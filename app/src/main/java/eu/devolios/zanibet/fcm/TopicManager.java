package eu.devolios.zanibet.fcm;

import android.content.Context;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.kingfisher.easy_sharedpreference_library.SharedPreferencesManager;

import java.util.List;
import java.util.Locale;

import eu.devolios.zanibet.model.Competition;
import eu.devolios.zanibet.utils.AnnotationJSONConverterFactory;
import eu.devolios.zanibet.utils.Constants;
import eu.devolios.zanibet.ws.CompetitionService;
import eu.devolios.zanibet.ws.Injector;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Gromat Luidgi on 11/12/2017.
 */

public class TopicManager {

    public static void registerOpenTicketTopic(Context context){
        CompetitionService competitionService = Injector.provideCompetitionService(context,
                new AnnotationJSONConverterFactory(AnnotationJSONConverterFactory.JACKSON));

        competitionService.getCompetitionsTopic()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new SingleObserver<List<Competition>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(List<Competition> competitions) {
                        String locale = Locale.getDefault().getLanguage();
                        String baseTopic = "topic_open_gameticket_";
                        switch (locale){
                            case "fr":
                                baseTopic = "©";
                                break;
                            case "pt":
                                baseTopic = "topic_open_gameticket_pt_";
                                break;
                            case "en":
                                baseTopic = "topic_open_gameticket_en_";
                                break;
                        }

                        if (competitions != null){
                            for (Competition competition : competitions) {
                                try {
                                    String key = baseTopic + competition.getId().toLowerCase();
                                    //Log.d("COMPETITION", key);
                                    if (SharedPreferencesManager.getInstance().getValue(key, Boolean.class, true)) {
                                        FirebaseMessaging.getInstance().subscribeToTopic(key);
                                    } else {
                                        FirebaseMessaging.getInstance().unsubscribeFromTopic(key);
                                    }
                                } catch (Exception e){
                                    Crashlytics.logException(e);
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Crashlytics.logException(e);
                    }
                });
    }

    public static void registerGlobalTopic(){
        new Thread(() -> {
            String locale = Locale.getDefault().getLanguage();
            String singleTicketTopic = "single_gameticket_today";
            String openTournamentTopic = "topic_open_tournament";
            String dailyTicketTopic = "topic_open_gameticket_multileague";

            switch (locale){
                case "fr":
                    singleTicketTopic = "single_gameticket_today_fr";
                    openTournamentTopic = "topic_open_tournament_fr";
                    dailyTicketTopic = "topic_open_gameticket_fr_multileague";
                    break;
                case "pt":
                    singleTicketTopic = "single_gameticket_today_pt";
                    openTournamentTopic = "topic_open_tournament_pt";
                    dailyTicketTopic = "topic_open_gameticket_pt_multileague";
                    break;
                case "en":
                    singleTicketTopic = "single_gameticket_today_en";
                    openTournamentTopic = "topic_open_tournament_en";
                    dailyTicketTopic = "topic_open_gameticket_en_multileague";
                    break;
            }

            try {
                FirebaseMessaging.getInstance().subscribeToTopic(singleTicketTopic);
                FirebaseMessaging.getInstance().subscribeToTopic(openTournamentTopic);
                FirebaseMessaging.getInstance().subscribeToTopic(dailyTicketTopic);
            } catch (Exception e){
                Crashlytics.logException(e);
            }
        }).start();
    }

}
