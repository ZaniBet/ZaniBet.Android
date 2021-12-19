package eu.devolios.zanibet.adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.crashlytics.android.Crashlytics;
import com.github.thunder413.datetimeutils.DateTimeUtils;

import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.devolios.zanibet.GlideApp;
import eu.devolios.zanibet.R;
import eu.devolios.zanibet.model.Fixture;
import eu.devolios.zanibet.model.Odds;
import eu.devolios.zanibet.utils.DateFormatter;

/**
 * Created by Gromat Luidgi on 26/01/2018.
 */

public class GameTicketCalendarListAdapter extends BaseAdapter {

    @BindView(R.id.dateTextView)
    AppCompatTextView mDateTextView;
    @BindView(R.id.homeTeamTextView)
    AppCompatTextView mHomeTeamTextView;
    @BindView(R.id.homeTeamImageView)
    ImageView mHomeTeamImageView;
    @BindView(R.id.awayTeamTextView)
    AppCompatTextView mAwayTeamTextView;
    @BindView(R.id.awayTeamImageView)
    ImageView mAwayTeamImageView;
    @BindView(R.id.oddsHomeTextView)
    AppCompatTextView mOddsHomeTextView;
    @BindView(R.id.oddsAwayTextView)
    AppCompatTextView mOddsAwayTextView;
    @BindView(R.id.oddsDrawTextView)
    AppCompatTextView mOddsDrawTextView;

    @BindView(R.id.playerOddsHomeTextView)
    AppCompatTextView mPlayerOddsHomeTextView;
    @BindView(R.id.playerOddsAwayTextView)
    AppCompatTextView mPlayerOddsAwayTextView;
    @BindView(R.id.playerOddsDrawTextView)
    AppCompatTextView mPlayerOddsDrawTextView;


    private Context mContext;
    private ArrayList<Fixture> mFixtureArrayList;

    public GameTicketCalendarListAdapter(Context context, ArrayList<Fixture> fixturesList){
        mContext = context;
        mFixtureArrayList = fixturesList;
    }

    @Override
    public int getCount() {
        return mFixtureArrayList.size();
    }

    @Override
    public Fixture getItem(int i) {
        return mFixtureArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View row = layoutInflater.inflate(R.layout.fixture_calendar_item, viewGroup, false);
        ButterKnife.bind(this, row);

        Fixture fixture = mFixtureArrayList.get(i);

        Date date = DateFormatter.formatMongoDate(fixture.getDate());
        mDateTextView.setText(DateTimeUtils.formatWithPattern(date, "EEEE dd MMM yyyy, HH:mm") + " UTC");

        if (fixture.getHomeTeam().getShortName() == null || fixture.getHomeTeam().getShortName().isEmpty()){
            mHomeTeamTextView.setText(fixture.getHomeTeam().getName());
        } else {
            mHomeTeamTextView.setText(fixture.getHomeTeam().getShortName());
        }

        if (fixture.getAwayTeam().getShortName() == null || fixture.getAwayTeam().getShortName().isEmpty()){
            mAwayTeamTextView.setText(fixture.getAwayTeam().getName());
        } else {
            mAwayTeamTextView.setText(fixture.getAwayTeam().getShortName());
        }

        GlideApp.with(mContext)
                .load(fixture.getHomeTeam().getLogo())
                .placeholder(R.drawable.zanibet_logo)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mHomeTeamImageView);

        GlideApp.with(mContext)
                .load(fixture.getAwayTeam().getLogo())
                .placeholder(R.drawable.zanibet_logo)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mAwayTeamImageView);

        if (fixture.getOdds() != null && fixture.getOdds().length > 0) {
            for (Odds odds : fixture.getOdds()){
                try {
                    if (odds.getBookmaker().equals("ZaniBet")) {
                        mOddsHomeTextView.setText(String.valueOf(odds.getOdds().getHomeTeam()));
                        mOddsAwayTextView.setText(String.valueOf(odds.getOdds().getAwayTeam()));
                        mOddsDrawTextView.setText(String.valueOf(odds.getOdds().getDraw()));
                    } else if (odds.getBookmaker().equals("Players")) {
                        mPlayerOddsHomeTextView.setText(String.valueOf(odds.getOdds().getHomeTeam()) + "%");
                        mPlayerOddsAwayTextView.setText(String.valueOf(odds.getOdds().getAwayTeam()) + "%");
                        mPlayerOddsDrawTextView.setText(String.valueOf(odds.getOdds().getDraw()) + "%");
                    }
                } catch (Exception e){
                    Crashlytics.logException(e);
                }
            }
        }

        return row;
    }
}
