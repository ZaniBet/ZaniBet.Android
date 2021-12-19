package eu.devolios.zanibet.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.mopub.common.MoPub;
import com.mopub.common.MoPubReward;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubRewardedVideoListener;
import com.mopub.mobileads.MoPubRewardedVideoManager;
import com.mopub.mobileads.MoPubRewardedVideos;
import com.rilixtech.materialfancybutton.MaterialFancyButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.devolios.zanibet.GrillePlayConfirmationActivity;
import eu.devolios.zanibet.R;
import eu.devolios.zanibet.adapter.BetListAdapter;
import eu.devolios.zanibet.model.ApiError;
import eu.devolios.zanibet.model.Fixture;
import eu.devolios.zanibet.model.GameTicket;
import eu.devolios.zanibet.model.Grille;
import eu.devolios.zanibet.model.User;
import eu.devolios.zanibet.presenter.GrillePlayPresenter;
import eu.devolios.zanibet.presenter.contract.GrillePlayContract;
import eu.devolios.zanibet.safety.Safety;
import eu.devolios.zanibet.utils.AdsUnit;

/**
 * Created by Gromat Luidgi on 09/11/2017.
 */

public class GrilleMultiPlayFragment extends BaseFragment implements GrillePlayContract.View, BetListAdapter.Listener {

    @BindView(R.id.listView)
    ListView mListView;

    private MaterialFancyButton mPlayButton;
    private GrillePlayPresenter mGrillePlayPresenter;
    private GameTicket mGameTicket;
    private BetListAdapter mBetListAdapter;
    private Grille mGrille;
    private FirebaseAnalytics mFirebaseAnalytics;

    private boolean mAdsWatched = false;

    public static GrilleMultiPlayFragment newInstance(GameTicket gameTicket) {
        GrilleMultiPlayFragment fragment = new GrilleMultiPlayFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("gameticket", gameTicket);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mGameTicket = (GameTicket) bundle.getSerializable("gameticket");
        } else {
            return;
        }

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(Objects.requireNonNull(getActivity()));

        mGrillePlayPresenter = new GrillePlayPresenter(getActivity(), this);
        List<Fixture> mFixtureList = new ArrayList<>(Arrays.asList(mGameTicket.getFixtures()));
        HashMap<String, Integer> mBetsHashMap = new HashMap<>();
        mBetListAdapter = new BetListAdapter(getActivity(), mFixtureList, mBetsHashMap, this);
        mGrillePlayPresenter.load(mGameTicket);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.grille_multi_play_fragment, container, false);
        ButterKnife.bind(this, view);
        mFirebaseAnalytics.setCurrentScreen(Objects.requireNonNull(getActivity()), "Jouer une grille", "GrilleMatchdayPlayFragment");

        mListView.addHeaderView(initListHeaderView(inflater));
        mListView.addFooterView(initListFooter(inflater));
        mListView.setAdapter(mBetListAdapter);

        MoPubRewardedVideoListener mRewardedVideoListener = new MoPubRewardedVideoListener() {
            @Override
            public void onRewardedVideoLoadSuccess(@NonNull String adUnitId) {
                // Called when the video for the given adUnitId has loaded. At this point you should
                // be able to call MoPubRewardedVideos.showRewardedVideo(String) to show the video.
                try {
                    if (isAdded()) {
                        if (mPlayButton != null) {
                            mPlayButton.setText(getString(R.string.validate_grid));
                            mPlayButton.setEnabled(true);
                        }
                    }
                } catch (Exception e) {
                    Crashlytics.logException(e);
                }
            }

            @Override
            public void onRewardedVideoLoadFailure(@NonNull String adUnitId, @NonNull MoPubErrorCode errorCode) {
                // Called when a video fails to load for the given adUnitId.
                // The provided error code will provide more insight into the reason for the failure to load.
                Crashlytics.log(errorCode.toString());

                try {
                    if (mPlayButton != null && isAdded()) {
                        mPlayButton.setText(getString(R.string.validate_grid));
                        mPlayButton.setEnabled(false);
                    }
                } catch (Exception e) {
                    Crashlytics.logException(e);
                }
                loadRewardVideo();
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
                // Called when a rewarded video is closed. At this point your application should resume.
                try {
                    if (isAdded()) {
                        if (mPlayButton != null) {
                            mPlayButton.setText(getString(R.string.loading_video));
                            mPlayButton.setEnabled(false);
                        }
                    }
                } catch (Exception e) {
                    Crashlytics.logException(e);
                }

                if (mGrillePlayPresenter != null && mAdsWatched)
                    mGrillePlayPresenter.validateGrille();

            }

            @Override
            public void onRewardedVideoCompleted(@NonNull Set<String> adUnitIds, @NonNull MoPubReward reward) {
                // Called when a rewarded video is completed and the user should be rewarded.
                // You can query the reward object with boolean isSuccessful(), String getLabel(), and int getAmount().
                mAdsWatched = true;
            }
        };

        MoPubRewardedVideos.setRewardedVideoListener(mRewardedVideoListener);


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Crashlytics.setString("last_ui_viewed", "GrilleMultiPlayFragment");
        loadRewardVideo();
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void loadRewardVideo() {
        if (MoPub.isSdkInitialized() && !MoPubRewardedVideoManager.hasVideo(AdsUnit.PLAY_MULTI_GRID_UNIT_ID)) {
            MoPubRewardedVideoManager.RequestParameters requestParameters =
                    new MoPubRewardedVideoManager.RequestParameters(null, null, null, User.currentUser().getId());
            MoPubRewardedVideos.loadRewardedVideo(AdsUnit.PLAY_MULTI_GRID_UNIT_ID, requestParameters);
        } else if (!MoPub.isSdkInitialized()) {
            Handler handler = new Handler();
            Runnable runnableCode = new Runnable() {
                @Override
                public void run() {
                    if (!MoPub.isSdkInitialized()){
                        handler.postDelayed(this, 2000);
                    } else {
                        MoPubRewardedVideoManager.RequestParameters requestParameters =
                                new MoPubRewardedVideoManager.RequestParameters(null, null, null, User.currentUser().getId());
                        MoPubRewardedVideos.loadRewardedVideo(AdsUnit.PLAY_MULTI_GRID_UNIT_ID, requestParameters);
                    }
                }
            };
            handler.post(runnableCode);
        }
    }

    private View initListHeaderView(LayoutInflater inflater) {
        @SuppressLint("InflateParams") View headerView = inflater.inflate(R.layout.title_header_decoration, null);
        TextView titleTextView = headerView.findViewById(R.id.titleTextView);
        titleTextView.setText(Objects.requireNonNull(getActivity()).getString(R.string.select_prediction));
        return headerView;
    }

    private View initListFooter(LayoutInflater inflater) {
        @SuppressLint("InflateParams") View footerView = inflater.inflate(R.layout.bet_list_footer, null);
        MaterialFancyButton resetButton = footerView.findViewById(R.id.resetButton);
        MaterialFancyButton flashButton = footerView.findViewById(R.id.flashButton);
        mPlayButton = footerView.findViewById(R.id.playButton);

        resetButton.setOnClickListener(view -> {
            logResetGrille();
            mGrillePlayPresenter.clearBets();
        });

        flashButton.setOnClickListener(view -> {
            logFlashGrille();
            mGrillePlayPresenter.flashGrille();
        });

        mPlayButton.setOnClickListener(view -> {
            if (Safety.checkVPN(getActivity())) return;

            if (mGameTicket.getNumberOfGrillePlay() >= mGameTicket.getMaxNumberOfPlay()) {
                try {
                    new MaterialDialog.Builder(Objects.requireNonNull(getActivity()))
                            .titleGravity(GravityEnum.CENTER)
                            .contentGravity(GravityEnum.CENTER)
                            .title(getActivity().getString(R.string.err_title_oups))
                            .titleColorRes(R.color.colorRed800)
                            .negativeColorRes(R.color.colorRed800)
                            .content(getActivity().getString(R.string.grille_play_limit))
                            .negativeText(getActivity().getString(R.string.ok_exclamation))
                            .iconRes(R.drawable.ico_stop)
                            .show();
                } catch (Exception e) {
                    Crashlytics.logException(e);
                }
            } else if (MoPubRewardedVideoManager.hasVideo(AdsUnit.PLAY_MULTI_GRID_UNIT_ID)) {
                try {
                    mPlayButton.setEnabled(false);
                    mPlayButton.setText(Objects.requireNonNull(getActivity()).getString(R.string.launching_video));
                } catch (Exception e) {
                    Crashlytics.logException(e);
                }
                mGrillePlayPresenter.playGrille(mGameTicket);
            } else {
                try {
                    mPlayButton.setEnabled(false);
                    mPlayButton.setText(Objects.requireNonNull(getActivity()).getString(R.string.no_ads_available));
                    loadRewardVideo();
                    new MaterialDialog.Builder(getActivity())
                            .titleGravity(GravityEnum.CENTER)
                            .contentGravity(GravityEnum.CENTER)
                            .title(getActivity().getString(R.string.err_title_oups))
                            .titleColorRes(R.color.colorRed800)
                            .negativeColorRes(R.color.colorRed800)
                            .content(getActivity().getString(R.string.err_no_ads_available))
                            .negativeText(getActivity().getString(R.string.ok_exclamation))
                            .iconRes(R.drawable.ico_stop)
                            .show();
                } catch (Exception e) {
                    Crashlytics.logException(e);
                }
            }
        });


        mPlayButton.setText(Objects.requireNonNull(getActivity()).getString(R.string.loading_video));
        mPlayButton.setEnabled(false);

        if (MoPubRewardedVideoManager.hasVideo(AdsUnit.PLAY_MULTI_GRID_UNIT_ID)) {
            mPlayButton.setText(getString(R.string.validate_grid));
            mPlayButton.setEnabled(true);
        }

        return footerView;
    }


    // Presenter Callback
    @Override
    public void onLoad() {

    }

    @Override
    public void onPlayGrille(Grille grille) {
        mGrille = grille;
        logValidateGridEvent();
        MoPubRewardedVideos.showRewardedVideo(AdsUnit.PLAY_MULTI_GRID_UNIT_ID);
    }

    @Override
    public void showPlayTicketError(ApiError error) {
        if (isAdded()) {
            if (mPlayButton != null) mPlayButton.setEnabled(true);

            if (error.getCode() == -1) {
                mPlayButton.setText(Objects.requireNonNull(getActivity()).getString(R.string.no_ads_available));
            }

            try {
                new MaterialDialog.Builder(getActivity())
                        .titleGravity(GravityEnum.CENTER)
                        .contentGravity(GravityEnum.CENTER)
                        .title(error.getTitle())
                        .titleColorRes(R.color.colorRed800)
                        .negativeColorRes(R.color.colorRed800)
                        .content(error.getMessage())
                        .negativeText(getActivity().getString(R.string.ok_exclamation))
                        .iconRes(R.drawable.ico_stop)
                        .show();
            } catch (Exception e) {
                Crashlytics.logException(e);
            }
        }
    }

    @Override
    public void onUpdateBet(HashMap<String, Integer> bets) {
        mBetListAdapter.updateBets(bets);
    }

    @Override
    public void onClearBets() {
        mBetListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFlashTicket() {
        mBetListAdapter.notifyDataSetChanged();
    }


    @Override
    public void onValidateGrille() {
        try {
            Intent intent = new Intent(getActivity(), GrillePlayConfirmationActivity.class);
            intent.putExtra("gameticket", mGameTicket);
            startActivityForResult(intent, 100);
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    @Override
    public void showValidateTicketError(ApiError error) {

    }

    // Adapter callback
    @Override
    public void onBetChange(RadioGroup radioGroup) {
        RadioButton radioButton = radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());
        if (radioButton != null)
            mGrillePlayPresenter.updateBet(radioGroup.getTag().toString(), (int) radioButton.getTag());
        //Log.d("BET CHANGE", "Fixture : " + radioGroup.getTag() + " - Result : " + radioButton.getTag());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 100:
                if (resultCode == 1) {
                    // replay
                    try {
                        if (mPlayButton != null)
                            mPlayButton.setText(Objects.requireNonNull(getActivity()).getString(R.string.loading_video));
                        if (mPlayButton != null) mPlayButton.setEnabled(false);
                        if (mGameTicket != null)
                            mGameTicket.setNumberOfGrillePlay(mGameTicket.getNumberOfGrillePlay() + 1);
                        mGrillePlayPresenter.clearBets();
                        loadRewardVideo();
                    } catch (Exception e) {
                        Crashlytics.logException(e);
                    }
                } else if (resultCode == 2) {
                    Objects.requireNonNull(getActivity()).finish();
                }
                break;
        }
    }

    private void logValidateGridEvent() {
        if (mFirebaseAnalytics != null && mGrille != null && mGameTicket != null) {
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, mGrille.get_id());
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, mGameTicket.getName() + " J" + mGameTicket.getMatchDay());
            bundle.putInt("number_validate_grid", mGameTicket.getNumberOfGrillePlay());
            mFirebaseAnalytics.logEvent("validate_grid", bundle);
        }
    }

    private void logFlashGrille() {
        if (mFirebaseAnalytics != null && mGameTicket != null) {
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, mGameTicket.getId());
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, mGameTicket.getName() + " J" + mGameTicket.getMatchDay());
            bundle.putInt("number_validate_grid", mGameTicket.getNumberOfGrillePlay());
            mFirebaseAnalytics.logEvent("flash_grid", bundle);
        }
    }

    private void logResetGrille() {
        if (mFirebaseAnalytics != null && mGameTicket != null) {
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, mGameTicket.getId());
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, mGameTicket.getName() + " J" + mGameTicket.getMatchDay());
            bundle.putInt("number_validate_grid", mGameTicket.getNumberOfGrillePlay());
            mFirebaseAnalytics.logEvent("reset_grid", bundle);
        }
    }
}
