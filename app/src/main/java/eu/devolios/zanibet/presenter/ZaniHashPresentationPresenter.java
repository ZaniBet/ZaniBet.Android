package eu.devolios.zanibet.presenter;

import android.content.Context;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import eu.devolios.zanibet.model.ApiError;
import eu.devolios.zanibet.presenter.contract.ZaniHashPresentationContract;
import eu.devolios.zanibet.ws.Injector;
import eu.devolios.zanibet.ws.UserService;
import eu.devolios.zanibet.ws.ZaniHashService;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class ZaniHashPresentationPresenter implements ZaniHashPresentationContract.Presenter {

    private ZaniHashPresentationContract.View mView;
    private Context mContext;
    private UserService mUserService;
    private ZaniHashService mZaniHashService;

    public ZaniHashPresentationPresenter(Context context, ZaniHashPresentationContract.View view){
        this.mContext = context;
        this.mUserService = Injector.provideUserService(context);
        this.mZaniHashService = Injector.provideZaniHashService(context);
        this.mView = view;
    }

    @Override
    public void enableZaniAnalytics(boolean value) {
        mZaniHashService.putEnabled(value)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mView.showEnableZaniAnalyticsLoadingDialog();
                    }

                    @Override
                    public void onSuccess(ResponseBody responseBody) {
                        mView.hideEnableZaniAnalyticsLoadingDialog();
                        try {
                            JsonFactory factory = new JsonFactory();
                            JsonParser parser = factory.createParser(responseBody.bytes());
                            while(!parser.isClosed()) {
                                JsonToken jsonToken = parser.nextToken();
                                if(JsonToken.FIELD_NAME.equals(jsonToken)) {
                                    mView.onEnableZaniAnalytics(parser.nextBooleanValue());
                                }
                            }
                        } catch (Exception e){
                            e.printStackTrace();
                            mView.showEnableZaniAnalyticsErrorDialog(ApiError.networkError(mContext));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.hideEnableZaniAnalyticsLoadingDialog();
                        mView.showEnableZaniAnalyticsErrorDialog(ApiError.parseRxError(mContext, e));
                    }
                });
    }
}
