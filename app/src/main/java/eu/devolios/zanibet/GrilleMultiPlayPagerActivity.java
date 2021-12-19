package eu.devolios.zanibet;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.crashlytics.android.Crashlytics;
import com.github.thunder413.datetimeutils.DateTimeUtils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.MaterialIcons;
import com.kingfisher.easy_sharedpreference_library.SharedPreferencesManager;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;
import com.mopub.common.MoPub;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.devolios.zanibet.adapter.BasicPagerAdapter;
import eu.devolios.zanibet.fragment.GrilleMultiPlayFragment;
import eu.devolios.zanibet.fragment.GrilleMultiPlayedFragment;
import eu.devolios.zanibet.fragment.LeagueStandingFragment;
import eu.devolios.zanibet.fragment.gameticket.GameTicketMultiDetailsFragment;
import eu.devolios.zanibet.model.Competition;
import eu.devolios.zanibet.model.GameTicket;
import eu.devolios.zanibet.model.Grille;
import eu.devolios.zanibet.utils.Constants;
import eu.devolios.zanibet.utils.DateFormatter;
import io.fabric.sdk.android.services.common.Crash;

public class GrilleMultiPlayPagerActivity extends AppCompatActivity {
    @BindView(R.id.collapseToolbar)
    CollapsingToolbarLayout mCollapseToolbar;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tabLayout)
    TabLayout mTabLayout;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    @BindView(R.id.leagueImageView)
    ImageView mLeagueImageView;
    @BindView(R.id.ticketNameTextView)
    TextView mTicketNameTextView;
    @BindView(R.id.matchDayTextView)
    TextView mMatchDayTextView;
    @BindView(R.id.dateTextView)
    TextView mDateTextView;
    @BindView(R.id.adView)
    AdView mAdView;

    public static GameTicket mGameTicket;
    public static Grille mGrille;

    private BasicPagerAdapter mBasicPagerAdapter;
    boolean mNotifyEnabled = false;
    String mBaseFcmTopic = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grille_multi_play_pager);
        ButterKnife.bind(this, this);

        if (mGameTicket == null){
            finish();
            return;
        }

        MoPub.onCreate(this);

        mCollapseToolbar.setTitle(mGameTicket.getName());
        mCollapseToolbar.setTitleEnabled(true);
        mCollapseToolbar.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
        mCollapseToolbar.setCollapsedTitleTextColor(getResources().getColor(R.color.colorWhite));

        mToolbar.setTitle("");
        mToolbar.setNavigationIcon(new IconDrawable(getApplicationContext(),
                MaterialIcons.md_arrow_back).colorRes(R.color.colorWhite).actionBarSize());
        mToolbar.setNavigationOnClickListener(v -> onBackPressed());
        setSupportActionBar(mToolbar);

        mTicketNameTextView.setText(mGameTicket.getName());
        mMatchDayTextView.setText(getString(R.string.matchday, mGameTicket.getMatchDay()));

        Date startDate = DateFormatter.formatMongoDate(mGameTicket.getLimitDate());
        Date resultDate = DateFormatter.formatMongoDate(mGameTicket.getResultDate());
        mDateTextView.setText(DateTimeUtils.formatWithPattern(startDate, "dd MMM yyyy") +
                " - " + DateTimeUtils.formatWithPattern(resultDate, "dd MMM yyyy"));

        mBasicPagerAdapter = new BasicPagerAdapter(getSupportFragmentManager());
        if (mGrille == null) {
            mBasicPagerAdapter.addFragment(GrilleMultiPlayFragment.newInstance(mGameTicket), getString(R.string.play));
            mAdView.setVisibility(View.GONE);
        } else {
            mBasicPagerAdapter.addFragment(GrilleMultiPlayedFragment.newInstance(mGameTicket, mGrille), getString(R.string.your_grid));
            try {
                AdRequest adRequest = new AdRequest.Builder().build();
                if ( Constants.SHOW_ADS ) mAdView.loadAd(adRequest);
            } catch (Exception e){
                Crashlytics.logException(e);
            }
        }

        mBasicPagerAdapter.addFragment(GameTicketMultiDetailsFragment.newInstance(mGameTicket, mGrille), getString(R.string.tab_details_ticket));
        mBasicPagerAdapter.addFragment(LeagueStandingFragment.newInstance(mGameTicket), getString(R.string.tab_standing));

        mViewPager.setAdapter(mBasicPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        String locale = Locale.getDefault().getLanguage();
        mBaseFcmTopic = "topic_open_gameticket_";
        switch (locale){
            case "fr":
                mBaseFcmTopic = "topic_open_gameticket_fr_";
                break;
            case "pt":
                mBaseFcmTopic = "topic_open_gameticket_pt_";
                break;
            case "en":
                mBaseFcmTopic = "topic_open_gameticket_en_";
                break;
        }

        try {
            Competition competition = Competition.parseCompetition(mGameTicket.getCompetition());
            mBaseFcmTopic = mBaseFcmTopic + competition.getId().toLowerCase();
            mNotifyEnabled = SharedPreferencesManager.getInstance()
                    .getValue(mBaseFcmTopic, Boolean.class, false);
        } catch (Exception e){
            if (mGameTicket != null) Crashlytics.setString("gameticket", mGameTicket.getName());
            Crashlytics.logException(e);
        }

    }

    public AdView getAdView() {
        return mAdView;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.multi_grid_played_menu, menu);

            if (!mNotifyEnabled) {
                menu.findItem(R.id.action_notify).setIcon(
                        new IconicsDrawable(this)
                                .icon(MaterialDesignIconic.Icon.gmi_notifications_add)
                                .colorRes(R.color.colorWhite)
                                .actionBar());
            } else {
                menu.findItem(R.id.action_notify).setIcon(
                        new IconicsDrawable(this)
                                .icon(MaterialDesignIconic.Icon.gmi_notifications_active)
                                .colorRes(R.color.colorWhite)
                                .actionBar());
            }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_notify:
                if (mNotifyEnabled){
                    new MaterialDialog.Builder(this)
                            .title(getString(R.string.dlg_title_unsubscribe_notify_multi))
                            .content(getString(R.string.dlg_content_unsubscribe_notify_multi, mGameTicket.getName()))
                            .positiveColorRes(R.color.colorPrimary)
                            .positiveText(getString(R.string.ok_exclamation))
                            .negativeColorRes(R.color.colorRed800)
                            .negativeText(getString(R.string.cancel))
                            .onPositive((dialog, which) -> {
                                FirebaseMessaging.getInstance().unsubscribeFromTopic(mBaseFcmTopic);
                                mNotifyEnabled = false;
                                SharedPreferencesManager.getInstance().putValue(mBaseFcmTopic, mNotifyEnabled);
                                item.setIcon(
                                        new IconicsDrawable(this)
                                                .icon(MaterialDesignIconic.Icon.gmi_notifications_add)
                                                .colorRes(R.color.colorWhite)
                                                .actionBar());
                            })
                            .show();
                } else {
                    new MaterialDialog.Builder(this)
                            .title(getString(R.string.dlg_title_subscribe_notify_multi))
                            .content(getString(R.string.dlg_content_subscribe_notify_multi, mGameTicket.getName()))
                            .positiveColorRes(R.color.colorPrimary)
                            .positiveText(getString(R.string.ok_exclamation))
                            .negativeColorRes(R.color.colorRed800)
                            .negativeText(getString(R.string.cancel))
                            .onPositive((dialog, which) -> {
                                FirebaseMessaging.getInstance().subscribeToTopic(mBaseFcmTopic);
                                mNotifyEnabled = true;
                                SharedPreferencesManager.getInstance().putValue(mBaseFcmTopic, mNotifyEnabled);
                                item.setIcon(
                                        new IconicsDrawable(this)
                                                .icon(MaterialDesignIconic.Icon.gmi_notifications_active)
                                                .colorRes(R.color.colorWhite)
                                                .actionBar());
                            })
                            .show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MoPub.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MoPub.onPause(this);
    }

    @Override
    protected void onDestroy() {
        mGrille = null;
        mGameTicket = null;
        if (mAdView != null){
            mAdView.destroy();
        }
        super.onDestroy();
    }


}
