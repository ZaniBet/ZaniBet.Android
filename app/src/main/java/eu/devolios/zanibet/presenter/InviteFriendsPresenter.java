package eu.devolios.zanibet.presenter;

import android.content.Context;
import android.support.annotation.NonNull;

import com.crashlytics.android.Crashlytics;

import org.json.JSONObject;

import eu.devolios.zanibet.model.ApiError;
import eu.devolios.zanibet.presenter.contract.InviteFriendsContract;
import eu.devolios.zanibet.ws.Injector;
import eu.devolios.zanibet.ws.UserService;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InviteFriendsPresenter implements InviteFriendsContract.Presenter {

    private Context mContext;
    private InviteFriendsContract.View mView;
    private UserService mUserService;

    public InviteFriendsPresenter(Context context, InviteFriendsContract.View view){
        mContext = context;
        mView = view;
        mUserService = Injector.provideUserService(context);
    }

    @Override
    public void setCustomInvitationCode(String code) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("invitationCode", code);

            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());

            mView.showLoadingDialog();
            mUserService.setCustomInvitationCode(requestBody).enqueue(new Callback<String>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    mView.hideLoadingDialog();
                    if (response.isSuccessful()){
                        mView.showUpdateInvitationCodeSuccess(response.body());
                    } else {
                        mView.showUpdateInvitationCodeError(ApiError.parseError(response));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    mView.hideLoadingDialog();
                    mView.showUpdateInvitationCodeError(ApiError.networkError(mContext));
                }
            });
        } catch (Exception e){
            Crashlytics.logException(e);
        }
    }
}
