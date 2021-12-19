package eu.devolios.zanibet;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.MaterialIcons;
import com.kingfisher.easy_sharedpreference_library.SharedPreferencesManager;
import com.pixplicity.generate.Rate;
import com.rilixtech.materialfancybutton.MaterialFancyButton;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.devolios.zanibet.model.GameTicket;
import eu.devolios.zanibet.model.User;
import eu.devolios.zanibet.utils.Constants;

/**
 * Created by Gromat Luidgi on 15/11/2017.
 */

public class GrillePlayConfirmationActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.remainingPlayTextView)
    TextView mRemainingPlayTextView;
    @BindView(R.id.replayButton)
    MaterialFancyButton mReplayButton;
    @BindView(R.id.adView)
    AdView mAdView;
    //@BindView(R.id.backButton)
    //MaterialFancyButton mBackButton;
    private GameTicket mGameTicket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grille_play_confirmation);
        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();
        mGameTicket = (GameTicket) Objects.requireNonNull(bundle).getSerializable("gameticket");

        mToolbar.setTitle("");
        mToolbar.setNavigationIcon(new IconDrawable(getApplicationContext(), MaterialIcons.md_arrow_back).colorRes(R.color.colorWhite).actionBarSize());
        mToolbar.setNavigationOnClickListener(v -> {
            setResult(2);
            onBackPressed();
        });
        setSupportActionBar(mToolbar);

        if (mGameTicket.getNumberOfGrillePlay()+1 == mGameTicket.getMaxNumberOfPlay()){
            setResult(2);
            mReplayButton.setVisibility(View.GONE);
        }

        mRemainingPlayTextView.setText(getString(R.string.nb_played_grid, mGameTicket.getNumberOfGrillePlay()+1, mGameTicket.getMaxNumberOfPlay()));

        mReplayButton.setOnClickListener(view -> {
            setResult(1);
            finish();
        });

        int countPlay = SharedPreferencesManager.getInstance().getValue(Constants.COUNT_PLAY_PREF, Integer.class, 0);
        if (countPlay >= 19){
            try {
                Rate rate = new Rate.Builder(this)
                        .setTriggerCount(1)
                        .setMinimumInstallTime(1)
                        .setFeedbackAction(Uri.parse("mailto:contact@zanibet.com"))
                        .setMessage(getString(R.string.rating_message, User.currentUser().getUsername()))
                        .setNegativeButton(getString(R.string.btn_feedback))
                        .setNeverAgainText(getString(R.string.btn_dont_ask_again))
                        .setCancelButton(getString(R.string.btn_later))
                        .setPositiveButton(getString(R.string.btn_sure))
                        .build().count();
                rate.showRequest();
            } catch (Exception e){
                Crashlytics.logException(e);
            }
        }

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                mAdView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                mAdView.setVisibility(View.GONE);
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the user is about to return
                // to the app after tapping on an ad.
            }
        });

        try {
            if (Constants.SHOW_ADS) {
                AdRequest adRequest = new AdRequest.Builder().build();
                mAdView.loadAd(adRequest);
            }
        } catch (Exception e){
            Crashlytics.logException(e);
        }

        if (!BuildConfig.DEBUG) askLocationPermission();

    }


    @Override
    public void onBackPressed() {
        setResult(2);
        super.onBackPressed();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    private void askLocationPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                new MaterialDialog.Builder(this)
                        .titleColorRes(R.color.colorPrimary)
                        .positiveColorRes( R.color.colorPrimary)
                        .title(getString(R.string.dlg_title_location_permission))
                        .content(getString(R.string.dlg_content_location_permission))
                        .positiveText(getString(R.string.ok_exclamation))
                        .iconRes(R.drawable.ic_map_pointer)
                        .limitIconToDefaultSize()
                        .show();

            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        1);
            }
        }
    }

}
