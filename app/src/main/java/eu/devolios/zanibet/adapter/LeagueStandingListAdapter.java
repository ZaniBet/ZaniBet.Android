package eu.devolios.zanibet.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.devolios.zanibet.GlideApp;
import eu.devolios.zanibet.R;
import eu.devolios.zanibet.model.Fixture;
import eu.devolios.zanibet.model.LeagueStanding;

/**
 * Created by Gromat Luidgi on 10/11/2017.
 */

public class LeagueStandingListAdapter extends BaseAdapter {

    @BindView(R.id.teamImageView)
    ImageView mTeamImageView;
    @BindView(R.id.positionTextView)
    TextView mPositionTextView;
    @BindView(R.id.teamTextView)
    TextView mTeamTextView;
    @BindView(R.id.gamesTextView)
    TextView mGamesTextView;
    @BindView(R.id.diffTextView)
    TextView mDiffGameTextView;
    @BindView(R.id.pointsTextView)
    TextView mPointsTextView;


    private Context mContext;
    private List<LeagueStanding> mLeagueStandingList;
    private Fixture mCurrentFixture;
    private int mActiveFilter;
    private boolean mHightlight = true;

    public LeagueStandingListAdapter(Context context, Fixture currentFixture, ArrayList<LeagueStanding> leagueStandingArrayList, int activeFilter) {
        mContext = context;
        mCurrentFixture = currentFixture;
        mLeagueStandingList = leagueStandingArrayList;
        mActiveFilter = activeFilter;
    }

    public void setActiveFilter(int mActiveFilter) {
        this.mActiveFilter = mActiveFilter;
    }


    public void setmHightlight(boolean mHightlight) {
        this.mHightlight = mHightlight;
    }

    @Override
    public int getCount() {
        return mLeagueStandingList.size();
    }

    @Override
    public Fixture getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        @SuppressLint("ViewHolder") View row = layoutInflater.inflate(R.layout.league_standing_item, viewGroup, false);
        ButterKnife.bind(this, row);

        LeagueStanding leagueStanding = mLeagueStandingList.get(i);
        try {
            if (mHightlight) {
                if (leagueStanding.getTeam().get_id().equals(mCurrentFixture.getHomeTeam().get_id())) {
                    row.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPrimary100));
                } else if (leagueStanding.getTeam().get_id().equals(mCurrentFixture.getAwayTeam().get_id())) {
                    row.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPrimary100));
                }
            }


            if (leagueStanding.getTeam() != null && leagueStanding.getTeam().getLogo() != null) {
                if (Patterns.WEB_URL.matcher(leagueStanding.getTeam().getLogo()).matches()) {
                    GlideApp.with(mContext)
                            .load(leagueStanding.getTeam().getLogo())
                            .placeholder(R.drawable.zanibet_logo)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(mTeamImageView);
                }  else {
                    GlideApp.with(mContext)
                            .load(R.drawable.zanibet_logo)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(mTeamImageView);
                }
            } else {
                GlideApp.with(mContext)
                        .load(R.drawable.zanibet_logo)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(mTeamImageView);
            }

            switch (mActiveFilter) {
                case 0:
                    LeagueStanding.Stats stats = leagueStanding.getOverall();
                    mTeamTextView.setText(leagueStanding.getTeam().getShortName());
                    mPositionTextView.setText(String.valueOf(leagueStanding.getPosition()));
                    mGamesTextView.setText(String.valueOf(stats.getGamesPlayed()));
                    mPointsTextView.setText(String.valueOf(leagueStanding.getPoints()));
                    mDiffGameTextView.setText(String.valueOf(leagueStanding.getGoalDifference()));
                    break;
                case 1:
                    LeagueStanding.Stats homeStats = leagueStanding.getHome();
                    mTeamTextView.setText(leagueStanding.getTeam().getShortName());
                    mPositionTextView.setText(String.valueOf(leagueStanding.getPosition()));
                    mGamesTextView.setText(String.valueOf(homeStats.getGamesPlayed()));

                    int points = 0;
                    int diff = 0;

                    points += homeStats.getWon() * 3;
                    points += homeStats.getDraw();
                    mPointsTextView.setText(String.valueOf(points));


                    break;
                case 2:
                    break;
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
        }

        return row;
    }
}
