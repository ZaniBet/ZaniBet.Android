package eu.devolios.zanibet.presenter;

import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.kingfisher.easy_sharedpreference_library.SharedPreferencesManager;

import eu.devolios.zanibet.model.ApiError;
import eu.devolios.zanibet.model.Help;
import eu.devolios.zanibet.model.User;
import eu.devolios.zanibet.presenter.contract.HelpContract;
import eu.devolios.zanibet.utils.Constants;
import eu.devolios.zanibet.ws.HelpService;
import eu.devolios.zanibet.ws.Injector;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Gromat Luidgi on 12/11/2017.
 */

public class HelpPresenter implements HelpContract.Presenter {

    private HelpContract.View mView;
    private HelpService mHelpService;
    private Context mContext;

    public HelpPresenter(Context context, HelpContract.View view){
        mContext = context;
        mView = view;
        mHelpService = Injector.provideHelpService(mContext);
    }

    @Override
    public void load() {
        mView.showLoadingContent();
        mHelpService.getHelps().enqueue(new Callback<List<Help>>() {
            @Override
            public void onResponse(Call<List<Help>> call, Response<List<Help>> response) {
                mView.hideLoadingContent();
                if (response.isSuccessful()){
                    mView.addHelps(response.body());
                } else {
                    if (response.code() == 401)
                        SharedPreferencesManager.getInstance().putValue(Constants.IS_LOGGED_IN_PREF, false);
                    mView.showContentError(ApiError.parseError(response));
                }
            }

            @Override
            public void onFailure(Call<List<Help>> call, Throwable t) {
                Crashlytics.logException(t);
                mView.hideLoadingContent();
                mView.showContentError(ApiError.networkError(mContext));
            }
        });
    }
}
