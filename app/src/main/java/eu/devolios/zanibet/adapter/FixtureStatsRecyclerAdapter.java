package eu.devolios.zanibet.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.db.chart.animation.Animation;
import com.db.chart.model.BarSet;
import com.db.chart.renderer.XRenderer;
import com.db.chart.renderer.YRenderer;
import com.db.chart.view.BarChartView;
import com.github.thunder413.datetimeutils.DateTimeUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.devolios.zanibet.GlideApp;
import eu.devolios.zanibet.R;
import eu.devolios.zanibet.model.Competition;
import eu.devolios.zanibet.model.Fixture;
import eu.devolios.zanibet.model.GameTicket;
import eu.devolios.zanibet.model.Team;
import eu.devolios.zanibet.utils.DateFormatter;
import eu.devolios.zanibet.utils.GlideRoundTransformation;

/**
 * Created by Gromat Luidgi on 28/03/2018.
 */

public class FixtureStatsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final static int TITLE_HEADER_TYPE = 0;
    private final static int FORME_CHART_TYPE = 1;
    private final static int LAST_FIXTURE_HEADER_TYPE = 2;
    private final static int LAST_FIXTURE_TYPE = 3;

    private Context mContext;
    private Fixture mCurrentFixture;
    private GameTicket mGameTicket;
    private ArrayList<Fixture> mLastFixtureHomeTeam;
    private ArrayList<Fixture> mLastFixtureAwayTeam;


    public FixtureStatsRecyclerAdapter(Context context, GameTicket gameTicket, Fixture currentFixture,
                                       ArrayList<Fixture> lastFixtureHomeTeam, ArrayList<Fixture> lastFixtureAwayTeam){
        mContext = context;
        mGameTicket = gameTicket;
        mCurrentFixture = currentFixture;
        mLastFixtureHomeTeam = lastFixtureHomeTeam;
        mLastFixtureAwayTeam = lastFixtureAwayTeam;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == FORME_CHART_TYPE){
            view = LayoutInflater.from(mContext).inflate(R.layout.forme_chars_item, parent, false);
            return new FormeChartViewHolder(view, mContext);
        } else if (viewType == TITLE_HEADER_TYPE){
            view = LayoutInflater.from(mContext).inflate(R.layout.title_header_decoration, parent, false);
            return new TitleViewHolder(view);
        } else if (viewType == LAST_FIXTURE_HEADER_TYPE){
            view = LayoutInflater.from(mContext).inflate(R.layout.last_fixture_header_decoration, parent, false);
            return new LastFixtureHeaderViewHolder(view, mContext);
        } else if (viewType == LAST_FIXTURE_TYPE){
            view = LayoutInflater.from(mContext).inflate(R.layout.last_fixture_item, parent, false);
            return new LastFixtureViewHolder(view, mContext);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (position){
            case 0:
                ((TitleViewHolder) holder).bindTitle(mContext.getString(R.string.recent_form));
                break;
            case 1:
                ((FormeChartViewHolder) holder).bindFormeChart(mCurrentFixture.getHomeTeam());
                break;
            case 2:
                ((FormeChartViewHolder) holder).bindFormeChart(mCurrentFixture.getAwayTeam());
                break;
            case 3:
                ((TitleViewHolder) holder).bindTitle(mContext.getString(R.string.last_fixtures));
                break;
            case 4:
                ((LastFixtureHeaderViewHolder) holder).bindLastFixtureHeader(mCurrentFixture.getHomeTeam().getName(), mCurrentFixture.getHomeTeam().getLogo());
                break;
        }

        if (position == 5 + mLastFixtureHomeTeam.size()){
            ((LastFixtureHeaderViewHolder) holder).bindLastFixtureHeader(mCurrentFixture.getAwayTeam().getName(), mCurrentFixture.getAwayTeam().getLogo());
        } else if (position > 4 && position < 5+mLastFixtureHomeTeam.size()){
            ((LastFixtureViewHolder) holder).bindLastFixture( mLastFixtureHomeTeam.get(position-5), mCurrentFixture.getHomeTeam() );
        } else if (position > 4+mLastFixtureHomeTeam.size()){
            ((LastFixtureViewHolder) holder).bindLastFixture(mLastFixtureAwayTeam.get(position-mLastFixtureHomeTeam.size()-6), mCurrentFixture.getAwayTeam());
        }

    }

    @Override
    public int getItemCount() {
        if (mLastFixtureHomeTeam.isEmpty() || mLastFixtureAwayTeam.isEmpty()){
            return 0;
        }
        return 2 + 4 + mLastFixtureHomeTeam.size() + mLastFixtureAwayTeam.size();
    }

    @Override
    public int getItemViewType(int position) {
        switch (position){
            case 0:
                return TITLE_HEADER_TYPE;
            case 1:
                return FORME_CHART_TYPE;
            case 2:
                return FORME_CHART_TYPE;
            case 3:
                return TITLE_HEADER_TYPE;
            case 4:
                return LAST_FIXTURE_HEADER_TYPE;
        }

        if (position == mLastFixtureHomeTeam.size()+5){
            return LAST_FIXTURE_HEADER_TYPE;
        }

        return LAST_FIXTURE_TYPE;
    }

    static class TitleViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.titleTextView)
        TextView mTitleTextView;

        TitleViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bindTitle(String title){
            mTitleTextView.setText(title);
        }
    }

    static class LastFixtureHeaderViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.teamTextView)
        TextView mTeamTextView;
        @BindView(R.id.teamImageView)
        ImageView mTeamImageView;

        private Context mContext;

        LastFixtureHeaderViewHolder(View itemView, Context context) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext = context;
        }

        void bindLastFixtureHeader(String title, String imageUrl){
            mTeamTextView.setText(title);

            if (imageUrl != null && Patterns.WEB_URL.matcher(imageUrl).matches()){
                GlideApp.with(mContext)
                        .load(imageUrl)
                        .placeholder(R.drawable.zanibet_logo)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(mTeamImageView);
            } else {
                GlideApp.with(mContext)
                        .load(R.drawable.zanibet_logo)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(mTeamImageView);
            }

        }
    }

    static class LastFixtureViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.dateTextView)
        TextView mDateTextView;
        @BindView(R.id.competitionTextView)
        TextView mCompetitionTextView;
        @BindView(R.id.homeTeamTextView)
        TextView mHomeTeamTextView;
        @BindView(R.id.awayTeamTextView)
        TextView mAwayTeamTextView;
        @BindView(R.id.scoreTextView)
        TextView mScoreTextView;

        private Context mContext;

        LastFixtureViewHolder(View itemView, Context context) {
            super(itemView);
            mContext = context;
            ButterKnife.bind(this, itemView);
        }

        void bindLastFixture(Fixture fixture, Team team){
            Date openDate = DateFormatter.formatMongoDate(fixture.getDate());
            mDateTextView.setText(DateTimeUtils.formatWithPattern(openDate, "dd MMM yyyy HH:mm"));

            Competition competition = Competition.parseCompetition(fixture.getCompetition());
            mCompetitionTextView.setText(competition.getName());
            mHomeTeamTextView.setText(fixture.getHomeTeam().getShortName());
            mAwayTeamTextView.setText(fixture.getAwayTeam().getShortName());
            mScoreTextView.setText(mContext.getString(R.string.score_fixture,
                    fixture.getResult().getHomeScore(), fixture.getResult().getAwayScore()));

            if (fixture.getResult().getWinner() == 1 && fixture.getHomeTeam().get_id().equals(team.get_id())){
                mScoreTextView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPrimary100));
            } else if (fixture.getResult().getWinner() == 0 && fixture.getHomeTeam().get_id().equals(team.get_id())){
                mScoreTextView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorWhiteLynx));
            } else if (fixture.getResult().getWinner() == 2 && fixture.getHomeTeam().get_id().equals(team.get_id())){
                mScoreTextView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorRed100));
            }

            if (fixture.getResult().getWinner() == 1 && fixture.getAwayTeam().get_id().equals(team.get_id())){
                mScoreTextView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPrimary100));
            } else if (fixture.getResult().getWinner() == 0 && fixture.getAwayTeam().get_id().equals(team.get_id())){
                mScoreTextView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorWhiteLynx));
            } else if (fixture.getResult().getWinner() == 2 && fixture.getAwayTeam().get_id().equals(team.get_id())){
                mScoreTextView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorRed100));
            }

        }
    }

    static class FormeChartViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.teamImageView)
        ImageView mTeamImageView;
        @BindView(R.id.teamTextView)
        TextView mTeamTextView;
        @BindView(R.id.chart)
        BarChartView mBarChartView;

        private Context mContext;

        FormeChartViewHolder(View itemView, Context context) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext = context;
        }

        void bindFormeChart(Team team){
            mTeamTextView.setText(team.getName());
            if (team.getLogo() != null &&
                    Patterns.WEB_URL.matcher(team.getLogo()).matches()){
                GlideApp.with(mContext)
                        .load(team.getLogo())
                        .placeholder(R.drawable.zanibet_logo)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(mTeamImageView);
            } else {
                GlideApp.with(mContext)
                        .load(R.drawable.zanibet_logo)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(mTeamImageView);
            }

            try {
                if (mBarChartView.getData().isEmpty()) {
                    String[] mLabels = {"WIN", "DRAW", "LOST"};
                    float[][] mValues = {{0.0f, 0.0f, 0.0f}, {0.1f, 0.1f, 0.1f}};
                    //System.out.println("RECENT FORM " + team.getRecentForm());
                    if (team.getRecentForm() != null && !team.getRecentForm().isEmpty()){
                        float win = 0;
                        float draw = 0;
                        float lost = 0;
                        for (int i = 0; i < team.getRecentForm().length(); i++){
                            if (team.getRecentForm().charAt(i) == 'W'){
                                win += 1;
                            } else if (team.getRecentForm().charAt(i) == 'D') {
                                draw += 1;
                            } else {
                                lost += 1;
                            }
                        }
                        mValues = new float[][]{{win, draw, lost}, {win, draw, lost}};
                    }
                    int[] order = {0, 1, 2};

                    BarSet barSet = new BarSet(mLabels, mValues[0]);
                    barSet.setColor(Color.parseColor("#43A047"));
                    mBarChartView.addData(barSet);

                    mBarChartView.setXLabels(XRenderer.LabelPosition.OUTSIDE)
                            .setYLabels(YRenderer.LabelPosition.NONE)
                            .show(new Animation().inSequence(.5f, order));
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
