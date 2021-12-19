package eu.devolios.zanibet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.analytics.FirebaseAnalytics;

import eu.devolios.zanibet.model.ApiError;
import eu.devolios.zanibet.model.User;
import eu.devolios.zanibet.presenter.LoadingPresenter;
import eu.devolios.zanibet.presenter.contract.LoadingContract;

public class LoadingActivity extends AppCompatActivity implements LoadingContract.View {

    private LoadingContract.Presenter mLoadingPresenter;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        Crashlytics.setString("last_ui_viewed", "LoadingActivity");

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mLoadingPresenter = new LoadingPresenter(getApplicationContext(), this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mLoadingPresenter != null){
            //mLoadingPresenter.checkIsLoggedIn();
            mLoadingPresenter.load();
        } else {
            mLoadingPresenter = new LoadingPresenter(getApplicationContext(), this);
            //mLoadingPresenter.checkIsLoggedIn();
            mLoadingPresenter.load();
        }
    }

    @Override
    public void onLoad() {
        if (mFirebaseAnalytics != null) mFirebaseAnalytics.setUserProperty("user", User.currentUser().getId());
        mLoadingPresenter.registerDevice();
    }

    @Override
    public void isLoggedIn(boolean value) {
        if (!value){
            User.clearUserPreference();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    @Override
    public void onLoadError(ApiError apiError) {
        runOnUiThread(() -> {
            try {
                new MaterialDialog.Builder(this)
                        .titleGravity(GravityEnum.CENTER)
                        .contentGravity(GravityEnum.CENTER)
                        .title(apiError.getTitle())
                        .titleColorRes(R.color.colorRed800)
                        .negativeColorRes(R.color.colorRed800)
                        .content(Html.fromHtml(apiError.getMessage()))
                        .negativeText(getString(R.string.ok_exclamation))
                        .onNegative((dialog, which) -> finish())
                        .iconRes(R.drawable.ico_stop)
                        .show();
            } catch (Exception e) {
                Crashlytics.logException(e);
                finish();
            }
        });
    }

    @Override
    public void onRegisterDevice() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


    @Override
    public void onLoadUserError(ApiError apiError) {
        runOnUiThread(() -> {
            try {
                new MaterialDialog.Builder(LoadingActivity.this)
                        .titleGravity(GravityEnum.CENTER)
                        .contentGravity(GravityEnum.CENTER)
                        .title(apiError.getTitle())
                        .titleColorRes(R.color.colorRed800)
                        .negativeColorRes(R.color.colorRed800)
                        .content(Html.fromHtml(apiError.getMessage()))
                        .negativeText(getString(R.string.ok_exclamation))
                        .onNegative((dialog, which) -> finish())
                        .iconRes(R.drawable.ico_stop)
                        .show();
            } catch (Exception e){
                Crashlytics.logException(e);
            }
        });
    }
}
