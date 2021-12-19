package eu.devolios.zanibet;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.crashlytics.android.Crashlytics;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.MaterialIcons;
import com.kingfisher.easy_sharedpreference_library.SharedPreferencesManager;
import com.mopub.common.MoPub;
import com.mopub.common.MoPubReward;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubRewardedVideoListener;
import com.mopub.mobileads.MoPubRewardedVideoManager;
import com.mopub.mobileads.MoPubRewardedVideos;
import com.pollfish.interfaces.PollfishSurveyCompletedListener;
import com.pollfish.main.PollFish;
import com.rilixtech.materialfancybutton.MaterialFancyButton;

import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.devolios.zanibet.fragment.GameTicketSingleFragment;
import eu.devolios.zanibet.model.ApiError;
import eu.devolios.zanibet.model.User;
import eu.devolios.zanibet.presenter.AdsMissionPresenter;
import eu.devolios.zanibet.presenter.contract.AdsMissionContract;
import eu.devolios.zanibet.safety.Safety;
import eu.devolios.zanibet.utils.AdsUnit;
import eu.devolios.zanibet.utils.Constants;

public class AdsMissionActivity extends AppCompatActivity implements AdsMissionContract.View {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.pollfishButton)
    MaterialFancyButton mPollfishButton;
    //@BindView(R.id.adscendButton)
    //MaterialFancyButton mAdscendButton;
    @BindView(R.id.moreJetonButton)
    MaterialFancyButton mMoreJetonButton;
    @BindView(R.id.jetonAdsTitleTextView)
    TextView mJetonAdsTitleTextView;
    @BindView(R.id.jetonAdsLimit)
    TextView mJetonAdsLimitTextView;
    @BindView(R.id.main_content)
    RelativeLayout mRelativeLayout;
    @BindView(R.id.pollfishLayout)
    LinearLayout mPollFishLayout;
    @BindView(R.id.pollfishDescTextView)
    TextView mPollfishDecTextView;

    private AdsMissionPresenter mAdsMissionPresenter;
    private MaterialDialog mLoadingDialog;
    private MoPubRewardedVideoListener mRewardedVideoListener;
    private CountDownTimer mCountDownTimer;

    private View mLoadingView;
    private int mCurrentJeton;
    private boolean mAdsWatched = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ads_mission);
        ButterKnife.bind(this);

        mToolbar.setTitle(getString(R.string.title_jeton_task));
        mToolbar.setNavigationIcon(new IconDrawable(getApplicationContext(), MaterialIcons.md_arrow_back).colorRes(R.color.colorWhite).actionBarSize());
        mToolbar.setNavigationOnClickListener(v -> onBackPressed());
        setSupportActionBar(mToolbar);

        mCurrentJeton = User.currentUser().getJeton();
        mJetonAdsTitleTextView.setText(getString(R.string.get_n_jeton,
                SharedPreferencesManager.getInstance().getValue(Constants.SETTING_JETON_PER_VIDEO, int.class, 3)));
        mJetonAdsLimitTextView.setText(getString(R.string.get_jeton_limit,
                SharedPreferencesManager.getInstance().getValue(Constants.SETTING_JETON_VIDEO_ADS_PERIOD, int.class, 2)));
        mAdsMissionPresenter = new AdsMissionPresenter(this, this);
        mLoadingView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.loading_overlay, mRelativeLayout, false);
        mMoreJetonButton.setText(getString(R.string.loading_video));
        mMoreJetonButton.setEnabled(false);
        mPollfishDecTextView.setText(Html.fromHtml(getString(R.string.ads_pollfish_content)));


        loadRewardedVideo();
        MoPub.onCreate(this);

        mRewardedVideoListener = new MoPubRewardedVideoListener() {
            @Override
            public void onRewardedVideoLoadSuccess(@NonNull String adUnitId) {
                // Called when the video for the given adUnitId has loaded. At this point you should
                // be able to call MoPubRewardedVideos.showRewardedVideo(String) to show the video.
                try {
                    if (mMoreJetonButton != null) {
                        mMoreJetonButton.setText(getString(R.string.watch_a_video));
                        mMoreJetonButton.setEnabled(true);
                    }
                } catch (Exception e){
                    Crashlytics.logException(e);
                }
            }

            @Override
            public void onRewardedVideoLoadFailure(@NonNull String adUnitId, @NonNull MoPubErrorCode errorCode) {
                // Called when a video fails to load for the given adUnitId.
                // The provided error code will provide more insight into the reason for the failure to load.
                try {
                    mMoreJetonButton.setText(getString(R.string.no_ads_available));
                    mMoreJetonButton.setEnabled(false);
                } catch (Exception e){
                    Crashlytics.logException(e);
                }
            }

            @Override
            public void onRewardedVideoStarted(@NonNull String adUnitId) {
                // Called when a rewarded video starts playing.
            }

            @Override
            public void onRewardedVideoPlaybackError(@NonNull String adUnitId, @NonNull MoPubErrorCode errorCode) {
                //  Called when there is an error during video playback.
            }

            @Override
            public void onRewardedVideoClicked(@NonNull String adUnitId) {

            }

            @Override
            public void onRewardedVideoClosed(@NonNull String adUnitId) {
                if (mAdsWatched){
                    mAdsWatched = false;
                    try {
                        new MaterialDialog.Builder(AdsMissionActivity.this)
                                .title(getString(R.string.youha))
                                .titleColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent))
                                .positiveColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark))
                                .content(getString(R.string.jeton_added, SharedPreferencesManager.getInstance().getValue(Constants.SETTING_JETON_PER_VIDEO, int.class, 3)))
                                .positiveText(getString(R.string.ok_exclamation))
                                .onPositive((dialog, which) -> {
                                    if (mMoreJetonButton != null)
                                        mMoreJetonButton.setEnabled(false);
                                })
                                .iconRes(R.drawable.ic_zani_chips)
                                .limitIconToDefaultSize()
                                .show();
                    } catch (Exception e){
                        Crashlytics.logException(e);
                    }
                }
            }

            @Override
            public void onRewardedVideoCompleted(@NonNull Set<String> adUnitIds, @NonNull MoPubReward reward) {
                if (reward.isSuccessful()){
                    //User.currentUser().setJeton(User.currentUser().getJeton() + SharedPreferencesManager.getInstance().getValue(Constants.SETTING_JETON_VIDEO_ADS_PERIOD, int.class, 3));
                    mAdsWatched = true;
                    mAdsMissionPresenter.getUser();
                    GameTicketSingleFragment.FORCE_REFRESH = true;
                }
            }
        };

        MoPubRewardedVideos.setRewardedVideoListener(mRewardedVideoListener);

        mPollfishButton.setText(getString(R.string.loading_poll));
        mPollfishButton.setEnabled(false);

        mPollfishButton.setOnClickListener(view -> {
            if (Safety.checkVPN(getApplicationContext())) return;
            /*if (mLoadingView != null && mRelativeLayout != null) {
                mRelativeLayout.addView(mLoadingView);
            }*/
            PollFish.show();

        });

        /*mAdscendButton.setOnClickListener(view -> {
            if (Safety.checkVPN(getApplicationContext())) return;
            Intent intent = OffersActivity.getIntentForOfferWall(AdsMissionActivity.this, Constants.ADSCEND_PUBLISHER_ID,
                    Constants.ADSCEND_ADWALL_ID, User.currentUser().getId());
            startActivity(intent);
        });*/


        mMoreJetonButton.setOnClickListener(view -> {
            if (Safety.checkVPN(getApplicationContext())) return;
            mMoreJetonButton.setEnabled(false);
            mCurrentJeton = User.currentUser().getJeton();
            mMoreJetonButton.setText(getString(R.string.launching_video));
            mAdsMissionPresenter.getMoreJeton();
        });

    }

    @Override
    protected void onDestroy() {
        if (mCountDownTimer != null) mCountDownTimer.cancel();
        if (mRewardedVideoListener != null) mRewardedVideoListener = null;
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MoPub.onResume(this);
        PollFish.initWith(this, new PollFish.ParamsBuilder(Constants.POLLFISH_KEY)
                .requestUUID(User.currentUser().getId())
                .releaseMode(true)
                .customMode(true)
                .pollfishUserNotEligibleListener(() -> {
                    try {
                        new MaterialDialog.Builder(AdsMissionActivity.this)
                                .title(getString(R.string.dlg_title_pollfish_not_eligible))
                                .titleColorRes(R.color.colorRed800)
                                .negativeColorRes(R.color.colorRed800)
                                .content(getString(R.string.dlg_content_pollfish_not_eligible))
                                .negativeText(getString(R.string.ok_exclamation))
                                .iconRes(R.drawable.ico_stop)
                                .limitIconToDefaultSize()
                                .show();
                    } catch(Exception e){
                        Crashlytics.logException(e);
                    }
                })
                .pollfishSurveyReceivedListener((b, i) -> {
                    runOnUiThread(() -> {
                        mPollfishButton.setEnabled(true);
                        float chips = (float)i/100;
                        int pollConversion = SharedPreferencesManager.getInstance().getValue(Constants.SETTING_POLL_CONVERSION, int.class, 0);
                        if (i == 0){
                            mPollfishButton.setText(getString(R.string.discovery_poll));
                        } else {
                            mPollfishButton.setText(getString(R.string.poll_chips_earning, Math.round(chips*pollConversion) ));
                        }
                    });
                })
                .pollfishSurveyNotAvailableListener(() -> runOnUiThread(() -> {
                    mPollfishButton.setEnabled(false);
                    mPollfishButton.setText(getString(R.string.no_poll_available));
                }))
                .pollfishSurveyCompletedListener((b, i) -> {
                    try {

                        new MaterialDialog.Builder(AdsMissionActivity.this)
                                .title(getString(R.string.youha))
                                .titleColorRes(R.color.colorAccent)
                                .positiveColorRes(R.color.colorPrimaryDark)
                                .content(getString(R.string.dlg_content_pollfish_success))
                                .positiveText(getString(R.string.ok_exclamation))
                                .iconRes(R.drawable.ic_zani_chips)
                                .limitIconToDefaultSize()
                                .show();
                    } catch(Exception e){
                        Crashlytics.logException(e);
                    }
                })
                .build());
        PollFish.hide();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MoPub.onPause(this);
    }

    @Override
    public void onJetonRequest() {
        mCurrentJeton = User.currentUser().getJeton();
        MoPubRewardedVideos.showRewardedVideo(AdsUnit.GET_MORE_CHIPS_UNIT_ID);

    }

    @Override
    public void showLoadingDialog() {
        try {
            mLoadingDialog = new MaterialDialog.Builder(this)
                    .title(getString(R.string.loading))
                    .content(getString(R.string.please_wait))
                    .progress(true, 0)
                    .show();
        } catch (Exception e){
            Crashlytics.logException(e);
        }

    }

    @Override
    public void hideLoadingDialog() {
        try {
            if (mLoadingDialog != null) mLoadingDialog.dismiss();
        } catch (Exception e){
            Crashlytics.logException(e);
        }
    }

    @Override
    public void showErrorDialog(ApiError apiError) {
        try {
            mMoreJetonButton.setText(getString(R.string.watch_a_video));
            mMoreJetonButton.setEnabled(true);
            new MaterialDialog.Builder(this)
                    .title(getString(R.string.err_title_oups))
                    .titleColor(ContextCompat.getColor(this, R.color.colorRed800))
                    .negativeColor(ContextCompat.getColor(this, R.color.colorRed800))
                    .content(apiError.getMessage())
                    .negativeText(getString(R.string.ok_exclamation))
                    .iconRes(R.drawable.ico_stop)
                    .limitIconToDefaultSize()
                    .show();
        } catch (Exception e){
            Crashlytics.logException(e);
        }
    }

    @Override
    public void onLoadUser() {

    }

    private void loadRewardedVideo(){
        if (!MoPubRewardedVideoManager.hasVideo(AdsUnit.GET_MORE_CHIPS_UNIT_ID)) {
            MoPubRewardedVideoManager.RequestParameters requestParameters =
                    new MoPubRewardedVideoManager.RequestParameters(null, null, null, User.currentUser().getId());
            MoPubRewardedVideos.loadRewardedVideo(AdsUnit.GET_MORE_CHIPS_UNIT_ID, requestParameters);
        } else {
            if (mMoreJetonButton != null) {
                mMoreJetonButton.setText(getString(R.string.watch_a_video));
                mMoreJetonButton.setEnabled(true);
            }
        }
    }

}

