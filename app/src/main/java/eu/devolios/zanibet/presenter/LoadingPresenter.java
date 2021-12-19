package eu.devolios.zanibet.presenter;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;

import com.facebook.AccessToken;
import com.kingfisher.easy_sharedpreference_library.SharedPreferencesManager;

import java.util.List;

import eu.devolios.zanibet.analytics.ZaniAnalyticsConf;
import eu.devolios.zanibet.model.ApiError;
import eu.devolios.zanibet.model.Competition;
import eu.devolios.zanibet.model.Device;
import eu.devolios.zanibet.model.FacebookOauthBody;
import eu.devolios.zanibet.model.Nonce;
import eu.devolios.zanibet.model.Setting;
import eu.devolios.zanibet.model.Token;
import eu.devolios.zanibet.model.User;
import eu.devolios.zanibet.presenter.contract.LoadingContract;
import eu.devolios.zanibet.utils.AnnotationJSONConverterFactory;
import eu.devolios.zanibet.ws.AuthService;
import eu.devolios.zanibet.ws.CompetitionService;
import eu.devolios.zanibet.ws.DataService;
import eu.devolios.zanibet.ws.Injector;
import eu.devolios.zanibet.ws.UserService;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * Created by Gromat Luidgi on 16/11/2017.
 */

public class LoadingPresenter implements  LoadingContract.Presenter {

    private Context mContext;
    private LoadingContract.View mView;
    private UserService mUserService;
    private AuthService mAuthService;
    private CompetitionService mCompetitionService;
    private DataService mDataService;

    public LoadingPresenter(Context context, LoadingContract.View view){
        mContext = context;
        mView = view;
        mUserService = Injector.provideUserService(context);
        mAuthService = Injector.provideAuthService(context);
        mCompetitionService = Injector.provideCompetitionService(context,
                new AnnotationJSONConverterFactory(AnnotationJSONConverterFactory.JACKSON));
        mDataService = Injector.provideDataService(context);

        ZaniAnalyticsConf.setAvailable(true);
    }

    @Override
    public void load() {
        Single<User> getUserRequest = mUserService.getCurrentUser();
        Single<List<Competition>> getCompetitionRequest = mCompetitionService.getCompetitions();
        Single<Setting[]> getSettingsRequest = mDataService.getSettings();

        if (AccessToken.getCurrentAccessToken() != null && !AccessToken.getCurrentAccessToken().getToken().isEmpty()) {
            FacebookOauthBody facebookOauthBody = new FacebookOauthBody();
            facebookOauthBody.setAccessToken(AccessToken.getCurrentAccessToken().getToken());
            Single<Token> loginRequest = mAuthService.postLoginWithFacebook(facebookOauthBody);
            loginRequest.doOnError(throwable -> {
                ApiError apiError = ApiError.parseRxError(mContext, throwable);
                if (apiError.getCode() == 401) {
                    User.clearUserPreference();
                    mView.isLoggedIn(false);
                } else {
                    mView.onLoadUserError(apiError);
                }
            }).flatMap(token -> {
                User.setAccessToken(token.getAccessToken());
                return getUserRequest;
            }).flatMap(user -> {
                User.saveUser(user);
                return getCompetitionRequest;
            }).flatMap(competitionList -> {
                Competition.saveCompetitions(competitionList);
                return getSettingsRequest;
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<Setting[]>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onSuccess(Setting[] settings) {
                            for (Setting setting : settings){
                                SharedPreferencesManager.getInstance().putValue(setting.getSetting(), setting.getValue());
                            }
                            mView.onLoad();
                        }

                        @Override
                        public void onError(Throwable e) {
                            mView.onLoadError(ApiError.parseRxError(mContext, e));
                        }
                    });
        } else {
            getUserRequest.doOnError(throwable -> {
                ApiError apiError = ApiError.parseRxError(mContext, throwable);
                if (apiError.getCode() == 401) {
                    mView.isLoggedIn(false);
                } else {
                    mView.onLoadUserError(apiError);
                }

            }).flatMap(user -> {
                User.saveUser(user);
                return getCompetitionRequest;
            }).flatMap(competitionList -> {
                Competition.saveCompetitions(competitionList);
                return getSettingsRequest;
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<Setting[]>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onSuccess(Setting[] settings) {
                            for (Setting setting : settings){
                                SharedPreferencesManager.getInstance().putValue(setting.getSetting(), setting.getValue());
                            }
                            mView.onLoad();
                        }

                        @Override
                        public void onError(Throwable e) {
                            mView.onLoadError(ApiError.parseRxError(mContext, e));
                        }
                    });
        }
    }


    @Override
    public void registerDevice() {
        Device device = new Device();
        device.setManufacturer(Build.MANUFACTURER);
        device.setBrand(Build.BRAND);
        device.setModel(Build.MODEL);
        device.setSdk(Build.VERSION.SDK_INT);
        device.setVersionIncremental(Build.VERSION.INCREMENTAL);
        device.setVersionRelease(Build.VERSION.RELEASE);
        device.setPackageName(mContext.getPackageName());

        try {
            Signature[] sigs = mContext.getPackageManager()
                    .getPackageInfo(mContext.getPackageName(), PackageManager.GET_SIGNATURES)
                    .signatures;
            device.setSignature(sigs[0].toCharsString());
        } catch (Exception e){
            device.setSignature("");
        }

        mUserService.registerDevice(device).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(ResponseBody responseBody) {
                        mView.onRegisterDevice();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.onRegisterDevice();
                    }
                });
    }

    //@Override
    /*public void initSafety() {
        mUserService.getNonce().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Nonce>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Nonce nonce) {
                        mView.onInitSafety(nonce);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.onInitSafety(null);
                    }
                });

    }*/


}
