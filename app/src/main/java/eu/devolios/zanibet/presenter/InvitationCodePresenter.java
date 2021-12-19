package eu.devolios.zanibet.presenter;

import android.content.Context;
import android.support.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.kingfisher.easy_sharedpreference_library.SharedPreferencesManager;

import org.json.JSONObject;

import eu.devolios.zanibet.model.ApiError;
import eu.devolios.zanibet.model.User;
import eu.devolios.zanibet.presenter.contract.InvitationCodeContract;
import eu.devolios.zanibet.utils.Constants;
import eu.devolios.zanibet.ws.Injector;
import eu.devolios.zanibet.ws.UserService;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Gromat Luidgi on 12/03/2018.
 */

public class InvitationCodePresenter implements InvitationCodeContract.Presenter {

    private UserService mUserService;
    private InvitationCodeContract.View mView;
    private Context mContext;

    public InvitationCodePresenter(Context context, InvitationCodeContract.View view){
        mContext = context;
        mUserService = Injector.provideUserService(context);
        mView = view;
    }

    @Override
    public void validateCode(String code) {

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("invitationCode", code);
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());

            mView.showLoadingDialog();
            mUserService.setInvitationCode(requestBody).enqueue(new Callback<Integer>() {
                @Override
                public void onResponse(@NonNull Call<Integer> call, @NonNull Response<Integer> response) {
                    mView.hideLoadingDialog();
                    if (response.isSuccessful()) {
                        SharedPreferencesManager.getInstance().putValue(Constants.INVITATION_CODE_ACTIVE_PREF, false);
                        User user = User.currentUser();
                        user.setPoint(user.getPoint() + response.body());
                        User.saveUser(user);

                        mView.onValidateCode(response.body());
                    } else {
                        if (response.code() == 401)
                            SharedPreferencesManager.getInstance().putValue(Constants.IS_LOGGED_IN_PREF, false);
                        mView.showErrorDialog(ApiError.parseError(response));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Integer> call, @NonNull Throwable t) {
                    mView.hideLoadingDialog();
                    mView.showErrorDialog(ApiError.networkError(mContext));
                }
            });
        } catch (Exception e){
            Crashlytics.logException(e);
        }
    }
}
