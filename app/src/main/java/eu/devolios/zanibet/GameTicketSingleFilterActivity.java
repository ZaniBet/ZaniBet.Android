package eu.devolios.zanibet;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.MaterialIcons;
import com.kingfisher.easy_sharedpreference_library.SharedPreferencesManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.devolios.zanibet.adapter.GameTicketSingleFilterRecyclerAdapter;
import eu.devolios.zanibet.model.Competition;
import eu.devolios.zanibet.utils.Constants;
import eu.devolios.zanibet.utils.EmptyView;

public class GameTicketSingleFilterActivity extends AppCompatActivity implements GameTicketSingleFilterRecyclerAdapter.Listener {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.frameLayout)
    FrameLayout mFrameLayout;

    private GameTicketSingleFilterRecyclerAdapter mGameTicketSingleFilterRecyclerAdapter;
    private List<String> mFilteredLeagueList;
    private View mEmptyView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_ticket_single_filter);
        ButterKnife.bind(this, this);

        mToolbar.setTitle(getString(R.string.activity_title_league_filter));
        mToolbar.setNavigationIcon(new IconDrawable(getApplicationContext(),
                MaterialIcons.md_arrow_back).colorRes(R.color.colorWhite).actionBarSize());
        mToolbar.setNavigationOnClickListener(v -> onBackPressed());
        setSupportActionBar(mToolbar);

        mFilteredLeagueList = SharedPreferencesManager.getInstance().getValues(Constants.TICKET_SINGLE_FILTER_PREF, String[].class);
        if (mFilteredLeagueList == null) mFilteredLeagueList = new ArrayList<>();
        mGameTicketSingleFilterRecyclerAdapter = new GameTicketSingleFilterRecyclerAdapter(getApplicationContext(),
                mFilteredLeagueList, this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mGameTicketSingleFilterRecyclerAdapter);

        if (mEmptyView != null && mFrameLayout != null){
            if (mEmptyView.getParent() != null) mFrameLayout.removeView(mEmptyView);
        }

        mEmptyView = EmptyView.withDrawable(this, getString(R.string.empty_league_single_filter),
                ContextCompat.getDrawable(this, R.drawable.ic_filter_setting));

        if(mFilteredLeagueList.isEmpty()) mFrameLayout.addView(mEmptyView);
    }

    @Override
    public void removeFilter(String competitionId) {
        setResult(1);
        Competition.addForSingleTicket(competitionId);
        mFilteredLeagueList.remove(competitionId);
        mGameTicketSingleFilterRecyclerAdapter.notifyDataSetChanged();

        if (mFilteredLeagueList.isEmpty()) {
            if (mEmptyView != null && mFrameLayout != null) {
                if (mEmptyView.getParent() != null) mFrameLayout.removeView(mEmptyView);
            }

            mEmptyView = EmptyView.withDrawable(this, getString(R.string.empty_league_single_filter),
                    ContextCompat.getDrawable(this, R.drawable.ic_filter_setting));

            mFrameLayout.addView(mEmptyView);
        }
    }
}
