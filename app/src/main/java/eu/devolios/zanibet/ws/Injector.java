package eu.devolios.zanibet.ws;

import android.content.Context;

import eu.devolios.zanibet.utils.AnnotationJSONConverterFactory;
import eu.devolios.zanibet.utils.Constants;

import java.io.File;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;

import static eu.devolios.zanibet.utils.AnnotationJSONConverterFactory.*;

/**
 * Created by Gromat Luidgi on 29/07/2017.
 */

public class Injector {


    public static Retrofit provideJacksonRetrofit(Context context, String baseUrl){
        return new Retrofit.Builder()
                .baseUrl( baseUrl )
                .client( provideOkHttpClient(context) )
                .addConverterFactory(JacksonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // <- add this
                .build();
    }

    private static Retrofit provideGSONRetrofit (Context context, String baseUrl)
    {
        return new Retrofit.Builder()
                .baseUrl( baseUrl )
                .client( provideOkHttpClient(context) )
                .addConverterFactory( GsonConverterFactory.create() )
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // <- add this
                .build();
    }


    private static Retrofit provideMoshiRetrofit (Context context, String baseUrl)
    {
        return new Retrofit.Builder()
                .baseUrl( baseUrl )
                .client( provideOkHttpClient(context) )
                .addConverterFactory( MoshiConverterFactory.create() )
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // <- add this
                .build();
    }

    private static OkHttpClient provideOkHttpClient (Context context)
    {
        int cacheSize = 30 * 1024 * 1024; // 10 MiB
        Cache cache = new Cache(new File(context.getApplicationInfo().dataDir), cacheSize);

        return new OkHttpClient.Builder()
                .addInterceptor(new AuthenticationInterceptor())
                //.cache(cache)
                .build();
    }

    public static UserService provideUserService (Context context)
    {
        return provideMoshiRetrofit(context, Constants.BASE_URL + "v4/" ).create(UserService.class);
    }

    public static GameTicketService provideGameTickerService (Context context,
                                                              AnnotationJSONConverterFactory annotationJSONConverterFactory){
        switch (annotationJSONConverterFactory.getFactory()){
            case JACKSON:
                return provideJacksonRetrofit(context, Constants.BASE_URL + "v6/").create(GameTicketService.class);
            case MOSHI:
                return provideMoshiRetrofit(context, Constants.BASE_URL + "v6/").create(GameTicketService.class);
            default:
                return provideJacksonRetrofit(context, Constants.BASE_URL + "v6/").create(GameTicketService.class);
        }
    }

    public static GrilleService provideGrilleService (Context context,
                                                      AnnotationJSONConverterFactory annotationJSONConverterFactory){
        switch (annotationJSONConverterFactory.getFactory()){
            case JACKSON:
                return provideJacksonRetrofit(context, Constants.BASE_URL + "v6/").create(GrilleService.class);
            case MOSHI:
                return provideMoshiRetrofit(context, Constants.BASE_URL + "v6/").create(GrilleService.class);
            default:
                return provideJacksonRetrofit(context, Constants.BASE_URL + "v6/").create(GrilleService.class);
        }
    }

    public static RewardService provideRewardService (Context context){
        return provideMoshiRetrofit(context, Constants.BASE_URL + "v3/").create(RewardService.class);
    }

    public static AuthService provideAuthService (Context context){
        return provideMoshiRetrofit(context, Constants.BASE_URL + "v1/").create(AuthService.class);
    }

    public static TransactionService provideTransactionService (Context context){
        return provideJacksonRetrofit(context, Constants.BASE_URL).create(TransactionService.class);
    }

    public static HelpService provideHelpService (Context context){
        return provideMoshiRetrofit(context, Constants.BASE_URL).create(HelpService.class);
    }

    public static PayoutService providePayoutService(Context context){
        return provideMoshiRetrofit(context, Constants.BASE_URL + "v2/").create(PayoutService.class);
    }

    public static CompetitionService provideCompetitionService(Context context,
                                                               AnnotationJSONConverterFactory annotationJSONConverterFactory){
        switch (annotationJSONConverterFactory.getFactory()){
            case JACKSON:
                return provideJacksonRetrofit(context, Constants.BASE_URL + "v1/").create(CompetitionService.class);
            case MOSHI:
                return provideMoshiRetrofit(context, Constants.BASE_URL + "v1/").create(CompetitionService.class);
            default:
                return provideJacksonRetrofit(context, Constants.BASE_URL + "v1/").create(CompetitionService.class);
        }    }

    public static DataService provideDataService(Context context){
        return provideMoshiRetrofit(context, Constants.BASE_URL).create(DataService.class);
    }

    public static ZaniHashService provideZaniHashService(Context context){
        return provideJacksonRetrofit(context, Constants.BASE_URL).create(ZaniHashService.class);
    }
}


