package eu.devolios.zanibet;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.mikepenz.iconics.Iconics;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.devolios.zanibet.adapter.BasicPagerAdapter;
import eu.devolios.zanibet.fragment.FixtureDetailsFragment;
import eu.devolios.zanibet.fragment.FixtureStatsFragment;
import eu.devolios.zanibet.fragment.GrilleSinglePlayFragment;
import eu.devolios.zanibet.fragment.GrilleSinglePlayedFragment;
import eu.devolios.zanibet.fragment.LeagueStandingFragment;
import eu.devolios.zanibet.fragment.gameticket.GameTicketSingleDetailsFragment;
import eu.devolios.zanibet.model.Competition;
import eu.devolios.zanibet.model.Fixture;
import eu.devolios.zanibet.model.GameTicket;
import eu.devolios.zanibet.model.Grille;
import eu.devolios.zanibet.model.User;
import eu.devolios.zanibet.utils.Constants;
import eu.devolios.zanibet.utils.DateFormatter;

/**
 * Created by Gromat Luidgi on 27/03/2018.
 */

public class GrilleSinglePlayPagerActivity  extends AppCompatActivity {
    @BindView(R.id.collapseToolbar)
    CollapsingToolbarLayout mCollapseToolbar;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tabLayout)
    TabLayout mTabLayout;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;

    @BindView(R.id.competitionTextView)
    TextView mCompetitionTextView;
    @BindView(R.id.dateTextView)
    TextView mDateTextView;
    @BindView(R.id.homeTeamImageView)
    ImageView mHomeTeamImageView;
    @BindView(R.id.awayTeamImageView)
    ImageView mAwayTeamImageView;
    @BindView(R.id.homeTeamTextView)
    TextView mHomeTeamTextView;
    @BindView(R.id.awayTeamTextView)
    TextView mAwayTeamTextView;
    @BindView(R.id.scoreTextView)
    TextView mScoreTextView;
    @BindView(R.id.adView)
    AdView mAdView;

    public static GameTicket mGameTicket;
    public static Grille mGrille;
    private BasicPagerAdapter mBasicPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grille_single_play_pager);
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


        Competition competition = new Competition();
        try {
            if (mGameTicket.getCompetition() instanceof String) {
                for (Competition comp : Competition.getCompetitions()) {
                    if (comp.getId().equals(mGameTicket.getCompetition())) {
                        competition = comp;
                        mGameTicket.setCompetition(comp);
                    }
                }
            } else {
                competition = Competition.parseCompetition(mGameTicket.getCompetition());
            }
        } catch (Exception e){
            Crashlytics.logException(e);
        }

        Fixture fixture = mGameTicket.getFixtures()[0];
        mCompetitionTextView.setText(competition.getName());

        Date resultDate = DateFormatter.formatMongoDate(fixture.getDate());
        mDateTextView.setText(DateTimeUtils.formatWithPattern(resultDate, "dd MMM yyyy HH:mm"));

        mHomeTeamTextView.setText(fixture.getHomeTeam().getName());
        if (fixture.getHomeTeam().getLogo() != null &&
                Patterns.WEB_URL.matcher(fixture.getHomeTeam().getLogo()).matches()){
            Picasso.with(this)
                    .load(fixture.getHomeTeam().getLogo())
                    .placeholder(R.drawable.zanibet_logo)
                    .into(mHomeTeamImageView);
        } else {
            Picasso.with(this)
                    .load(R.drawable.zanibet_logo)
                    .into(mHomeTeamImageView);
        }

        mAwayTeamTextView.setText(fixture.getAwayTeam().getName());
        if (fixture.getAwayTeam().getLogo() != null &&
                Patterns.WEB_URL.matcher(fixture.getAwayTeam().getLogo()).matches()){
            Picasso.with(this)
                    .load(fixture.getAwayTeam().getLogo())
                    .placeholder(R.drawable.zanibet_logo)
                    .into(mAwayTeamImageView);
        } else {
            Picasso.with(this)
                    .load(R.drawable.zanibet_logo)
                    .into(mAwayTeamImageView);
        }

        if (!fixture.getStatus().equals("soon") && !fixture.getStatus().equals("postphoned") && !fixture.getStatus().equals("canceled") && !mGameTicket.getStatus().equals("open") ){
            mScoreTextView.setText(getString(R.string.score_fixture, fixture.getResult().getHomeScore(),
                    fixture.getResult().getAwayScore()));
        }


        mBasicPagerAdapter = new BasicPagerAdapter(getSupportFragmentManager());
        if (mGameTicket.getNumberOfGrillePlay() > 0 || mGrille != null){
            mBasicPagerAdapter.addFragment(GrilleSinglePlayedFragment.newInstance(mGameTicket, mGrille), getString(R.string.play));
            try {
                AdRequest adRequest = new AdRequest.Builder().build();
                if ( Constants.SHOW_ADS ) mAdView.loadAd(adRequest);
            } catch (Exception e){
                Crashlytics.logException(e);
            }
        } else {
            mAdView.setVisibility(View.GONE);
            mBasicPagerAdapter.addFragment(GrilleSinglePlayFragment.newInstance(mGameTicket), getString(R.string.play));
        }

        mBasicPagerAdapter.addFragment(FixtureDetailsFragment.newInstance(mGameTicket), getString(R.string.tab_fixture_details));
        mBasicPagerAdapter.addFragment(GameTicketSingleDetailsFragment.newInstance(mGameTicket, mGrille), getString(R.string.tab_details_ticket));
        mBasicPagerAdapter.addFragment(FixtureStatsFragment.newInstance(mGameTicket), getString(R.string.tab_stats));
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mGrille != null) {
            getMenuInflater().inflate(R.menu.grille_single_played_menu, menu);
            menu.findItem(R.id.action_tweet).setIcon(
                    new IconicsDrawable(this)
                            .icon(FontAwesome.Icon.faw_twitter)
                            .colorRes(R.color.colorWhite)
                            .actionBar());
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_tweet:
                try {
                Uri uri;
                String locale = Locale.getDefault().getLanguage();
                Fixture fixture = mGameTicket.getFixtures()[0];
                if (locale.equals("fr")){
                    uri = Uri.parse("android.resource://eu.devolios.zanibet/drawable/share_grid_twitter_fr");
                } else if (locale.equals("pt")){
                    uri = Uri.parse("android.resource://eu.devolios.zanibet/drawable/share_grid_twitter_pt");
                } else if (locale.equals("es")){
                    uri = Uri.parse("android.resource://eu.devolios.zanibet/drawable/share_grid_twitter_es");
                } else {
                    uri = Uri.parse("android.resource://eu.devolios.zanibet/drawable/share_grid_twitter_en");
                }

                if (mGrille != null) {

                    String bet1 = "", bet2 = "", bet3 = "", bet4 = "";
                    for (int i = 0; i < mGrille.getBets().length; i++) {
                        if (mGrille.getBets()[i].getType().equals("1N2")) {
                            if (mGrille.getBets()[i].getResult() == 0) {
                                bet1 = "N";
                            } else if (mGrille.getBets()[i].getResult() == 1) {
                                bet1 = mGameTicket.getFixtures()[0].getHomeTeam().getName();
                            } else if (mGrille.getBets()[i].getResult() == 2) {
                                bet1 = mGameTicket.getFixtures()[0].getAwayTeam().getName();
                            }
                        } else if (mGrille.getBets()[i].getType().equals("LESS_MORE_GOAL")) {
                            if (mGrille.getBets()[i].getResult() == 0) {
                                bet2 = "-";
                            } else if (mGrille.getBets()[i].getResult() == 1) {
                                bet2 = "+";
                            }
                        } else if (mGrille.getBets()[i].getType().equals("BOTH_GOAL")) {
                            if (mGrille.getBets()[i].getResult() == 0) {
                                bet3 = "No";
                            } else if (mGrille.getBets()[i].getResult() == 1) {
                                bet3 = "Yes";
                            }
                        } else if (mGrille.getBets()[i].getType().equals("FIRST_GOAL")) {
                            bet4 = String.valueOf(mGrille.getBets()[i].getResult());
                            if (mGrille.getBets()[i].getResult() == 0) {
                                bet4 = "No goal";
                            } else if (mGrille.getBets()[i].getResult() == 1) {
                                bet4 = "1";
                            } else if (mGrille.getBets()[i].getResult() == 2) {
                                bet4 = "2";
                            }
                        }
                    }

                    /*TweetComposer.Builder builder = new TweetComposer.Builder(this)
                            .text(getString(R.string.tweet_single_grid,
                                    mGameTicket.getFixtures()[0].getHomeTeam().getName(),
                                    mGameTicket.getFixtures()[0].getAwayTeam().getName(),
                                    bet1, bet2, bet3, bet4,
                                    User.currentUser().getReferral().getInvitationCode()))
                            .image(uri);*/
                    String twitterHomeTeam = "";
                    String twitterAwayTeam = "";

                    if (fixture.getHomeTeam().getTwitter() != null && !fixture.getHomeTeam().getTwitter().isEmpty()){
                        twitterHomeTeam = fixture.getHomeTeam().getTwitter();
                    } else {
                        twitterHomeTeam = "#" + fixture.getHomeTeam().getName().replaceAll("\\s+","");
                    }

                    if (fixture.getHomeTeam().getTwitter() != null && !fixture.getHomeTeam().getTwitter().isEmpty()){
                        twitterAwayTeam = fixture.getAwayTeam().getTwitter();
                    } else {
                        twitterAwayTeam = "#" + fixture.getAwayTeam().getName().replaceAll("\\s+","");
                    }

                    TweetComposer.Builder builder = new TweetComposer.Builder(this)
                            .text(getString(R.string.tweet_single_grid1,
                                    twitterHomeTeam,
                                    twitterAwayTeam,
                                    bet1,
                                    User.currentUser().getReferral().getInvitationCode()))
                            .image(uri);
                    builder.show();
                }
                } catch(Exception e){
                    Crashlytics.logException(e);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onDestroy() {
        mGrille = null;
        mGameTicket = null;
        super.onDestroy();
    }

}
