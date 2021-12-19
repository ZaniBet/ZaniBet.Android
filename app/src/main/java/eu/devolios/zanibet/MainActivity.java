package eu.devolios.zanibet;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.arch.lifecycle.ProcessLifecycleOwner;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.crashlytics.android.Crashlytics;
import com.github.thunder413.datetimeutils.DateTimeUtils;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.MaterialIcons;
import com.kingfisher.easy_sharedpreference_library.SharedPreferencesManager;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.context.IconicsContextWrapper;
import com.mopub.common.MoPub;
import com.mopub.common.SdkConfiguration;
import com.mopub.common.privacy.ConsentDialogListener;
import com.mopub.common.privacy.ConsentStatusChangeListener;
import com.mopub.common.privacy.PersonalInfoManager;
import com.mopub.mobileads.AdColonyRewardedVideo;
import com.mopub.mobileads.AppLovinRewardedVideo;
import com.mopub.mobileads.GooglePlayServicesInterstitial;
import com.mopub.mobileads.GooglePlayServicesRewardedVideo;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubInterstitial;
import com.mopub.mobileads.UnityRewardedVideo;
import com.ncapdevi.fragnav.FragNavController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.devolios.zanibet.analytics.ZaniAlarmReceiver;
import eu.devolios.zanibet.analytics.ZaniAnalyticsConf;
import eu.devolios.zanibet.analytics.ZaniBetJobService;
import eu.devolios.zanibet.analytics.ZaniBetService;
import eu.devolios.zanibet.fcm.TopicManager;
import eu.devolios.zanibet.fragment.BaseFragment;
import eu.devolios.zanibet.fragment.GameTicketFragment;
import eu.devolios.zanibet.fragment.GameTicketSingleFragment;
import eu.devolios.zanibet.fragment.GameTicketTournamentFragment;
import eu.devolios.zanibet.fragment.ProfileFragment;
import eu.devolios.zanibet.fragment.RewardFragment;
import eu.devolios.zanibet.fragment.pager.GameTicketPagerFragment;
import eu.devolios.zanibet.fragment.pager.GrilleGroupPagerFragment;
import eu.devolios.zanibet.fragment.pager.RewardPagerFragment;
import eu.devolios.zanibet.model.User;
import eu.devolios.zanibet.safety.Safety;
import eu.devolios.zanibet.utils.AdsUnit;
import eu.devolios.zanibet.utils.Constants;
import eu.devolios.zanibet.utils.DateFormatter;
import eu.devolios.zanibet.utils.FragmentHistory;
import eu.devolios.zanibet.utils.ZaniBetTracking;
import eu.devolios.zanibet.ws.Injector;
import eu.devolios.zanibet.ws.UserService;
import io.fabric.sdk.android.services.common.Crash;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements FragNavController.TransactionListener, FragNavController.RootFragmentListener, BaseFragment.FragmentNavigation, LifecycleObserver, ZaniBetTracking {

    @BindView(R.id.bottomBar)
    AHBottomNavigation mBottonBar;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    private final int INDEX_TICKETS = FragNavController.TAB1;
    private final int INDEX_GRILLE = FragNavController.TAB2;
    private final int INDEX_REWARDS = FragNavController.TAB3;
    private final int INDEX_PROFILE = FragNavController.TAB4;

    private FirebaseAnalytics mFirebaseAnalytics;
    private FragNavController mFragNavController;
    private FragmentHistory mFragmentHistory;
    private MoPubInterstitial mMoPubInterstitial;
    private PersonalInfoManager mPersonalInfoManager;
    private JobScheduler mJobScheduler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mJobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);

        if (mToolbar != null)
            mToolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorWhite));
        setSupportActionBar(mToolbar);

        Safety.setupInstanceId();
        Safety.setupAdvertiserId(getApplicationContext());

        // Initialiser les prestataires publicitaire
        if (!MoPub.isSdkInitialized()) {
            Bundle admobMediationBundle = new Bundle();
            admobMediationBundle.putString("npa", "1");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                List<String> networksToInit = new ArrayList<String>();
                networksToInit.add(AdColonyRewardedVideo.class.getName());
                networksToInit.add(UnityRewardedVideo.class.getName());
                networksToInit.add(GooglePlayServicesRewardedVideo.class.getName());
                networksToInit.add(AppLovinRewardedVideo.class.getName());

                SdkConfiguration sdkConfiguration = new SdkConfiguration.Builder(AdsUnit.PLAY_MULTI_GRID_UNIT_ID)
                        .withNetworksToInit(networksToInit)
                        .withMediationSettings(
                                new GooglePlayServicesInterstitial.GooglePlayServicesMediationSettings(admobMediationBundle),
                                new GooglePlayServicesRewardedVideo.GooglePlayServicesMediationSettings(admobMediationBundle))
                        .build();

                MoPub.initializeSdk(this, sdkConfiguration, () -> {
                    mMoPubInterstitial = new MoPubInterstitial(MainActivity.this, AdsUnit.PAUSE_UNIT_ID);
                    mPersonalInfoManager = MoPub.getPersonalInformationManager();
                    if (mPersonalInfoManager != null) {
                        mPersonalInfoManager.subscribeConsentStatusChangeListener(mConsentStatusChangeListener);
                    }

                });
            } else {
                MoPub.initializeSdk(this, new SdkConfiguration.Builder(AdsUnit.PLAY_MULTI_GRID_UNIT_ID)
                        .withMediationSettings(
                        new GooglePlayServicesInterstitial.GooglePlayServicesMediationSettings(admobMediationBundle),
                        new GooglePlayServicesRewardedVideo.GooglePlayServicesMediationSettings(admobMediationBundle))
                        .build(), () -> {
                    mMoPubInterstitial = new MoPubInterstitial(MainActivity.this, AdsUnit.PAUSE_UNIT_ID);
                    mPersonalInfoManager = MoPub.getPersonalInformationManager();
                    if (mPersonalInfoManager != null) {
                        mPersonalInfoManager.subscribeConsentStatusChangeListener(mConsentStatusChangeListener);
                    }
                });
            }
        }
        //mPersonalInfoManager.subscribeConsentStatusChangeListener(mConsentStatusChangeListener);

        List<Fragment> fragments = new ArrayList<>(4);
        fragments.add(GameTicketPagerFragment.newInstance());
        fragments.add(GrilleGroupPagerFragment.newInstance());
        fragments.add(RewardPagerFragment.newInstance());
        fragments.add(ProfileFragment.newInstance());

        mFragmentHistory = new FragmentHistory();
        mFragNavController = FragNavController.newBuilder(savedInstanceState, getSupportFragmentManager(), R.id.container)
                .transactionListener(this)
                .rootFragmentListener(this, 4)
                .rootFragments(fragments)
                .build();

        initBottomBar();

        showWelcomeDialog();
        initFcmTopic();

        //ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(IconicsContextWrapper.wrap(newBase));
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onMoveToBackground() {
        //Log.d("MainActivity", "App move to background !");
        ComponentName serviceComponent = new ComponentName(getApplicationContext(), ZaniBetJobService.class);
        JobInfo.Builder builder = new JobInfo.Builder(666, serviceComponent);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        builder.setRequiresDeviceIdle(false);
        builder.setRequiresCharging(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            builder.setRequiresBatteryNotLow(true);
        }

        //builder.setBackoffCriteria(TimeUnit.MINUTES.toMillis(20),JobInfo.BACKOFF_POLICY_LINEAR);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            builder.setMinimumLatency(3000);
            builder.setOverrideDeadline((1000*60)*15);
            if (mJobScheduler != null) mJobScheduler.schedule(builder.build());
        } else {
            //builder.setPeriodic(3000);
            startService(new Intent(this, ZaniBetService.class));
        }

        GameTicketSingleFragment.FORCE_REFRESH = true;
        GameTicketFragment.FORCE_REFRESH = true;
        GameTicketTournamentFragment.FORCE_REFRESH = true;
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onMoveToForeground() {
        //Log.d("MainActivity", "App move to foreground !");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mJobScheduler != null) mJobScheduler.cancelAll();
        } else {
            stopService(new Intent(this, ZaniBetService.class));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mMoPubInterstitial != null && !mMoPubInterstitial.isReady()) mMoPubInterstitial.load();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            stopService(new Intent(getApplicationContext(), ZaniBetService.class));

    }

    @Override
    protected void onDestroy() {
        if (mMoPubInterstitial != null) mMoPubInterstitial.destroy();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        switch (mBottonBar.getCurrentItem()) {
            case INDEX_TICKETS:
                getMenuInflater().inflate(R.menu.main_menu, menu);
                menu.findItem(R.id.action_help).setIcon(
                        new IconDrawable(this, MaterialIcons.md_help)
                                .colorRes(R.color.colorWhite)
                                .actionBarSize());
                /*menu.findItem(R.id.action_filter).setIcon(
                        new IconDrawable(this, MaterialIcons.md_filter_list)
                                .colorRes(R.color.colorWhite)
                                .actionBarSize());*/
                break;
            default:
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case android.R.id.home:
                try {
                    mFragNavController.popFragment();
                } catch (Exception e) {
                    Crashlytics.logException(e);
                }
                return true;
            case R.id.action_help:
                intent = new Intent(getApplicationContext(), HelpActivity.class);
                startActivity(intent);
                return true;
            /*case R.id.action_filter:
                intent = new Intent(getApplicationContext(), GameTicketFilterPagerActivity.class);
                startActivity(intent);
                return true;*/
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onBackPressed() {
        if (!mFragNavController.isRootFragment()) {
            mFragNavController.popFragment();
        } else {
            if (mFragmentHistory.isEmpty()) {
                super.onBackPressed();
            } else {
                if (mFragmentHistory.getStackSize() > 1) {
                    int position = mFragmentHistory.popPrevious();
                    mFragNavController.switchTab(position);
                    updateTabSelection(position);
                    mFragmentHistory.emptyStack();
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mFragNavController != null) {
            mFragNavController.onSaveInstanceState(outState);
        }
    }

    @Override
    public Fragment getRootFragment(int index) {
        switch (index) {
            case INDEX_TICKETS:
                return GameTicketPagerFragment.newInstance();
            case INDEX_GRILLE:
                return GrilleGroupPagerFragment.newInstance();
            case INDEX_REWARDS:
                return RewardFragment.newInstance();
            case INDEX_PROFILE:
                return ProfileFragment.newInstance();
        }
        throw new IllegalStateException("Need to send an index that we know");
    }

    @Override
    public void onTabTransaction(Fragment fragment, int i) {
        if (getSupportActionBar() != null && mFragNavController != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(!mFragNavController.isRootFragment());
        }
    }

    @Override
    public void onFragmentTransaction(Fragment fragment, FragNavController.TransactionType transactionType) {
        if (getSupportActionBar() != null && mFragNavController != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(!mFragNavController.isRootFragment());
        }
    }


    private void initBottomBar(){

        Drawable itemOneIcon = new IconicsDrawable(this)
                .icon(FontAwesome.Icon.faw_gamepad)
                .sizeDp(24);
        Drawable itemTwoIcon = new IconicsDrawable(this)
                .icon(FontAwesome.Icon.faw_futbol2)
                .sizeDp(24);
        Drawable itemThreeIcon = new IconicsDrawable(this)
                .icon(FontAwesome.Icon.faw_gift)
                .sizeDp(24);
        Drawable itemFourIcon = new IconicsDrawable(this)
                .icon(FontAwesome.Icon.faw_user_circle2)
                .sizeDp(24);

        AHBottomNavigationItem itemOne =
                new AHBottomNavigationItem(getString(R.string.tab_play), itemOneIcon, ContextCompat.getColor(this, R.color.colorPrimaryDark));
        AHBottomNavigationItem itemTwo =
                new AHBottomNavigationItem(getString(R.string.tab_grilles), itemTwoIcon, ContextCompat.getColor(this, R.color.colorPrimaryDark));
        AHBottomNavigationItem itemThree =
                new AHBottomNavigationItem(getString(R.string.tab_shop), itemThreeIcon, ContextCompat.getColor(this, R.color.colorPrimaryDark));
        AHBottomNavigationItem itemFour =
                new AHBottomNavigationItem(getString(R.string.tab_profile), itemFourIcon, ContextCompat.getColor(this, R.color.colorPrimaryDark));

        mBottonBar.addItem(itemOne);
        mBottonBar.addItem(itemTwo);
        mBottonBar.addItem(itemThree);
        mBottonBar.addItem(itemFour);

        mBottonBar.setAccentColor(Color.parseColor("#4CAF50"));
        mBottonBar.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
        mBottonBar.setTitleTypeface(ResourcesCompat.getFont(this, R.font.helveticaneue_roman));

        mBottonBar.setOnTabSelectedListener((position, wasSelected) -> {
            try {
                supportInvalidateOptionsMenu();
                switch (position) {
                    case INDEX_TICKETS:
                        if (wasSelected) {
                            mFragNavController.clearStack();
                        } else {
                            mFragmentHistory.push(INDEX_TICKETS);
                            mFragNavController.switchTab(INDEX_TICKETS);
                        }
                        break;
                    case INDEX_GRILLE:
                        if (wasSelected) {
                            mFragNavController.clearStack();
                        } else {
                            mFragmentHistory.push(INDEX_GRILLE);
                            mFragNavController.switchTab(INDEX_GRILLE);
                        }
                        break;
                    case INDEX_REWARDS:
                        if (wasSelected) {
                            mFragNavController.clearStack();
                        } else {
                            mFragmentHistory.push(INDEX_REWARDS);
                            mFragNavController.switchTab(INDEX_REWARDS);
                        }
                        break;
                    case INDEX_PROFILE:
                        if (wasSelected) {
                            mFragNavController.clearStack();
                        } else {
                            mFragmentHistory.push(INDEX_PROFILE);
                            mFragNavController.switchTab(INDEX_PROFILE);
                        }
                        break;
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
            }
            return true;
        });
    }

    private void initFcmTopic() {

        /*ActivityManager.MemoryInfo memoryInfo = getAvailableMemory();
        if (!memoryInfo.lowMemory) {
            TopicManager.registerOpenTicketTopic(getApplicationContext());
        }*/

        TopicManager.registerGlobalTopic();

        /*new Thread(() -> {
        try {
            String token = FirebaseInstanceId.getInstance().getToken();
            UserService userService = Injector.provideUserService(getApplicationContext());
            User user = new User();

            if (token != null) {
                user.setFcmToken(token);
                userService.updateFcmToken(user).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {

                    }
                });

            }

            // Update locale
            String locale = Locale.getDefault().getLanguage();
            user.setLocale(locale);
            userService.updateExtra(user).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {

                }
            });
        } catch (Exception e){
            Crashlytics.logException(e);
        }
       // }).run();*/
    }

    private ActivityManager.MemoryInfo getAvailableMemory() {
        ActivityManager activityManager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        assert activityManager != null;
        activityManager.getMemoryInfo(memoryInfo);
        return memoryInfo;
    }

    private void updateTabSelection(int currentTab) {
        for (int i = 0; i < mBottonBar.getItemsCount(); i++) {
            if (currentTab != i) {
                //bottomBarTab.setSelected(false);
            } else {
                mBottonBar.setCurrentItem(currentTab);
            }
        }
    }

    private void showWelcomeDialog() {
        try {
            Date createdAt = DateFormatter.formatMongoDate(User.currentUser().getCreatedAt());
            if (DateTimeUtils.isToday(createdAt)) {
                if (!SharedPreferencesManager.getInstance().getValue(Constants.WELCOME_SHOWED_PREF, Boolean.class, false)) {
                    SharedPreferencesManager.getInstance().putValue(Constants.WELCOME_SHOWED_PREF, true);

                    new MaterialDialog.Builder(this)
                            .titleGravity(GravityEnum.CENTER)
                            .contentGravity(GravityEnum.CENTER)
                            .title(getString(R.string.dlg_title_welcome))
                            .titleColor(ContextCompat.getColor(this, R.color.colorAccent))
                            .positiveColor(ContextCompat.getColor(this, R.color.colorAccent))
                            .content(Html.fromHtml(getString(R.string.dlg_content_welcome,
                                    SharedPreferencesManager.getInstance().getValue(Constants.SETTING_WELCOME_ZANICOIN_REWARD, int.class, 2400))))
                            .positiveText(getString(R.string.ok_exclamation))
                            .onAny((dialog, which) -> {
                                loadGdpr();
                            })
                            .iconRes(R.drawable.ico_welcome)
                            .show();
                } else {
                    loadGdpr();
                }
            } else {
                loadGdpr();
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    private void loadGdpr() {
        try {
            if (mPersonalInfoManager != null && !mPersonalInfoManager.canCollectPersonalInformation()
                    && mPersonalInfoManager.gdprApplies()) {
                mPersonalInfoManager.loadConsentDialog(mConsentDialogListener);
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    private ConsentStatusChangeListener mConsentStatusChangeListener = (oldConsentStatus, newConsentStatus, canCollectPersonalInformation) -> {
        if (!canCollectPersonalInformation) {
            try {
                new MaterialDialog.Builder(this)
                        .contentGravity(GravityEnum.CENTER)
                        .content(getString(R.string.consent_warning))
                        .negativeText(getString(R.string.ok_exclamation))
                        .negativeColorRes(R.color.colorRed800)
                        .neutralText(getString(R.string.btn_dont_ask_again))
                        .onNeutral((dialog, which) -> SharedPreferencesManager.getInstance().putValue(Constants.GDPR_NEVER_ASK_CONSENT_PREF, true))
                        .iconRes(R.drawable.ic_siren)
                        .limitIconToDefaultSize()
                        .show();
            } catch (Exception e) {
                Crashlytics.logException(e);
            }
        }
    };

    private ConsentDialogListener mConsentDialogListener = new ConsentDialogListener() {
        @Override
        public void onConsentDialogLoaded() {
            if (mPersonalInfoManager != null &&
                    !SharedPreferencesManager.getInstance().getValue(Constants.GDPR_NEVER_ASK_CONSENT_PREF, Boolean.class, false)) {
                mPersonalInfoManager.showConsentDialog();
            }
        }

        @Override
        public void onConsentDialogLoadFailed(@NonNull MoPubErrorCode moPubErrorCode) {

        }
    };

    public void checkLoggedIn() {
        if (!SharedPreferencesManager.getInstance().getValue(Constants.IS_LOGGED_IN_PREF, Boolean.class, false)) {
            User.clearUserPreference();
            Intent intent = new Intent(this, DispatchActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }

    public void updateTitle(String title) {
        if (mToolbar != null) {
            mToolbar.setTitle(title);
        }
    }

    public void showInterstitial() {
        if (mMoPubInterstitial != null && mMoPubInterstitial.isReady()){
            SharedPreferencesManager.getInstance().putValue(Constants.COUNT_ADS_ACTION_PREF, 0);
            mMoPubInterstitial.show();
        }
    }

    @Override
    public void pushFragment(Fragment fragment) {
        if (mFragNavController != null) {
            mFragNavController.pushFragment(fragment);
        }
    }

    @Override
    public void replaceFragment(Fragment fragment) {
        if (mFragNavController != null) {
            mFragNavController.replaceFragment(fragment);
        }
    }

    @Override
    public void popFragment() {
        if (mFragNavController != null) {
            if (!mFragNavController.isRootFragment()) {
                mFragNavController.popFragment();
            }
        }
    }

    @Override
    public void setCurrentScreen(String screenName, String activityName) {
        mFirebaseAnalytics.setCurrentScreen(this, screenName, activityName);
    }

    @Override
    public void logEvent(String event, Bundle bundle) {
        mFirebaseAnalytics.logEvent(event, bundle);
    }
}