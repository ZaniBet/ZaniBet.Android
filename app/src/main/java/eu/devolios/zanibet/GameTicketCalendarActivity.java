package eu.devolios.zanibet;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.MaterialIcons;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.devolios.zanibet.adapter.GameTicketCalendarListAdapter;
import eu.devolios.zanibet.model.Fixture;
import eu.devolios.zanibet.model.GameTicket;
import eu.devolios.zanibet.presenter.contract.GameTicketCalendarContract;
import eu.devolios.zanibet.utils.Constants;

public class GameTicketCalendarActivity extends AppCompatActivity implements GameTicketCalendarContract.View{

    @BindView(R.id.listView)
    ListView mListView;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.adView)
    AdView mAdView;

    public static GameTicket mGameTicket;

    private ArrayList<Fixture> mFixtureArrayList;
    private GameTicketCalendarListAdapter mGameTicketCalendarListAdapter;
    private View mHeaderView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_ticket_calendar);
        ButterKnife.bind(this, this);

        if (mGameTicket == null){
            finish();
            return;
        }

        if (mGameTicket.getName() != null)
            mToolbar.setTitle(getString(R.string.title_gameticket_calendar, mGameTicket.getName().replace(" Jackpot", ""), mGameTicket.getMatchDay()));

        mToolbar.setNavigationIcon(new IconDrawable(getApplicationContext(), MaterialIcons.md_arrow_back).colorRes(R.color.colorWhite).actionBarSize());
        mToolbar.setNavigationOnClickListener(v -> onBackPressed());
        setSupportActionBar(mToolbar);

        mFixtureArrayList = new ArrayList<>();
        mFixtureArrayList.addAll(Arrays.asList(mGameTicket.getFixtures()));
        mGameTicketCalendarListAdapter = new GameTicketCalendarListAdapter(getApplicationContext(), mFixtureArrayList);
        mListView.addHeaderView(initListHeaderView(getLayoutInflater()));
        mListView.setAdapter(mGameTicketCalendarListAdapter);

        try {
            AdRequest adRequest = new AdRequest.Builder().build();
            if ( Constants.SHOW_ADS ) mAdView.loadAd(adRequest);
        } catch (Exception e){
            Crashlytics.logException(e);
        }
    }

    @Override
    protected void onDestroy() {
        mGameTicket = null;
        super.onDestroy();
    }

    private View initListHeaderView(LayoutInflater inflater) {
        mHeaderView = inflater.inflate(R.layout.fixture_calendar_list_header, mListView, false);
        return mHeaderView;
    }


    @Override
    public void onLoadFixtures(List<Fixture> fixtureList) {

    }

    @Override
    public void showContentLoading() {

    }

    @Override
    public void hideContentLoading() {

    }

    @Override
    public void showContentError() {

    }

    @Override
    public void hideContentError() {

    }
}
