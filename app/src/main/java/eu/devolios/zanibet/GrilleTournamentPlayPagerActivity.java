package eu.devolios.zanibet;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.github.thunder413.datetimeutils.DateTimeUtils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.MaterialIcons;
import com.mopub.common.MoPub;
import com.mopub.mobileads.MoPubRewardedVideoManager;
import com.mopub.mobileads.MoPubRewardedVideos;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.devolios.zanibet.adapter.BasicPagerAdapter;
import eu.devolios.zanibet.fragment.GrilleMultiPlayFragment;
import eu.devolios.zanibet.fragment.GrilleMultiPlayedFragment;
import eu.devolios.zanibet.fragment.GrilleTournamentPlayFragment;
import eu.devolios.zanibet.fragment.GrilleTournamentPlayedFragment;
import eu.devolios.zanibet.fragment.LeagueStandingFragment;
import eu.devolios.zanibet.fragment.TournamentStandingFragment;
import eu.devolios.zanibet.fragment.gameticket.GameTicketMultiDetailsFragment;
import eu.devolios.zanibet.fragment.gameticket.GameTicketTournamentDetailsFragment;
import eu.devolios.zanibet.model.GameTicket;
import eu.devolios.zanibet.model.Grille;
import eu.devolios.zanibet.model.User;
import eu.devolios.zanibet.utils.AdsUnit;
import eu.devolios.zanibet.utils.Constants;
import eu.devolios.zanibet.utils.DateFormatter;

public class GrilleTournamentPlayPagerActivity extends AppCompatActivity {
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
    @BindView(R.id.potTextView)
    TextView mPotTextView;
    @BindView(R.id.dateTextView)
    TextView mDateTextView;
    @BindView(R.id.adView)
    AdView mAdView;

    public static GameTicket mGameTicket;
    public static Grille mGrille;

    private BasicPagerAdapter mBasicPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grille_tournament_play_pager);
        ButterKnife.bind(this, this);

        if (mGameTicket == null){
            finish();
            return;
        }

        mCollapseToolbar.setTitle(mGameTicket.getName());
        mCollapseToolbar.setTitleEnabled(true);
        mCollapseToolbar.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
        mCollapseToolbar.setCollapsedTitleTextColor(getResources().getColor(R.color.colorWhite));

        mToolbar.setTitle("");
        mToolbar.setNavigationIcon(new IconDrawable(getApplicationContext(), MaterialIcons.md_arrow_back).colorRes(R.color.colorWhite).actionBarSize());
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        setSupportActionBar(mToolbar);

        mTicketNameTextView.setText(mGameTicket.getName());
        mPotTextView.setText(getString(R.string.matchday, mGameTicket.getMatchDay()));

        Date startDate = DateFormatter.formatMongoDate(mGameTicket.getLimitDate());
        Date resultDate = DateFormatter.formatMongoDate(mGameTicket.getResultDate());
        mDateTextView.setText(DateTimeUtils.formatWithPattern(startDate, "dd MMM yyyy") +
                " - " + DateTimeUtils.formatWithPattern(resultDate, "dd MMM yyyy"));

        mBasicPagerAdapter = new BasicPagerAdapter(getSupportFragmentManager());
        if (mGameTicket.getNumberOfGrillePlay() > 0 || mGrille != null) {
            mBasicPagerAdapter.addFragment(GrilleTournamentPlayedFragment.newInstance(mGameTicket), getString(R.string.your_grid));
            try {
                AdRequest adRequest = new AdRequest.Builder().build();
                if ( Constants.SHOW_ADS ) mAdView.loadAd(adRequest);
            } catch (Exception e){
                Crashlytics.logException(e);
            }

        } else {
            mBasicPagerAdapter.addFragment(GrilleTournamentPlayFragment.newInstance(mGameTicket), getString(R.string.play));
            mAdView.setVisibility(View.GONE);
        }

        mBasicPagerAdapter.addFragment(GameTicketTournamentDetailsFragment.newInstance(mGameTicket, mGrille), getString(R.string.tab_details_tournament));
        mBasicPagerAdapter.addFragment(TournamentStandingFragment.newInstance(mGameTicket), getString(R.string.tab_standing));

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
    }

    public AdView getAdView() {
        return mAdView;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
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
