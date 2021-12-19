package eu.devolios.zanibet.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.crashlytics.android.Crashlytics;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.devolios.zanibet.GlideApp;
import eu.devolios.zanibet.R;
import eu.devolios.zanibet.model.Bet;
import eu.devolios.zanibet.model.Competition;
import eu.devolios.zanibet.model.Fixture;
import eu.devolios.zanibet.model.Grille;

/**
 * Created by Gromat Luidgi on 11/11/2017.
 */

public class BetTournamentPlayedListAdapter extends BaseAdapter {

    @BindView(R.id.homeTeamTextView)
    TextView homeTextView;
    @BindView(R.id.awayTeamTextView)
    TextView awayTextView;
    @BindView(R.id.betRadioGroup)
    RadioGroup betRadioGroup;
    @BindView(R.id.homeRadioButton)
    RadioButton homeRadioButton;
    @BindView(R.id.equalRadioButton)
    RadioButton equalRadioButton;
    @BindView(R.id.awayRadioButton)
    RadioButton awayRadioButton;
    @BindView(R.id.pronoTextView)
    TextView mPronoTextView;
    @BindView(R.id.pointsTextView)
    TextView mPointsTextView;
    @BindView(R.id.homeTeamImageView)
    ImageView mHomeTeamImageView;
    @BindView(R.id.awayTeamImageView)
    ImageView mAwayTeamImageView;
    @BindView(R.id.scoreTextView)
    TextView mScoreTextView;
    @BindView(R.id.competitionTextView)
    TextView mCompetitionTextView;


    private Context mContext;
    private List<Fixture> mFixtureList;
    private List<Bet> mBetList;
    private Grille mGrille;

    public BetTournamentPlayedListAdapter(Context context, List<Fixture> fixtureList, List<Bet> betList) {
        mContext = context;
        mFixtureList = fixtureList;
        mBetList = betList;
    }

    public void setGrille(Grille grille) {
        this.mGrille = grille;
    }

    @Override
    public int getCount() {
        return mFixtureList.size();
    }

    @Override
    public Fixture getItem(int i) {
        return mFixtureList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int index, View view, ViewGroup viewGroup) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);

        @SuppressLint("ViewHolder") View row = layoutInflater.inflate(R.layout.bet_tournament_played_item, viewGroup, false);
        ButterKnife.bind(this, row);

        //countTextView.setText(String.valueOf(index+1));
        if (mGrille == null) return row;

        Fixture fixture = getItem(index);

        try {
            Competition competition = Competition.parseCompetition(fixture.getCompetition());
            mCompetitionTextView.setText(competition.getName() + " - " + competition.getCountry());
        } catch (Exception e){
            Crashlytics.logException(e);
        }



        try {
            if (mGrille.getStatus().equals("win") || mGrille.getStatus().equals("loose")) {
                mScoreTextView.setText(fixture.getResult().getHomeScore() + " - " + fixture.getResult().getAwayScore());
            }
        } catch (Exception e){
            mScoreTextView.setText(" - ");
            Crashlytics.logException(e);
        }

        if (fixture.getHomeTeam().getShortName() == null || fixture.getHomeTeam().getShortName().isEmpty()) {
            homeTextView.setText(fixture.getHomeTeam().getName());
        } else {
            homeTextView.setText(fixture.getHomeTeam().getShortName());
        }

        if (fixture.getAwayTeam().getShortName() == null || fixture.getAwayTeam().getShortName().isEmpty()) {
            awayTextView.setText(fixture.getAwayTeam().getName());
        } else {
            awayTextView.setText(fixture.getAwayTeam().getShortName());
        }

        try {
            if (fixture.getHomeTeam().getLogo() != null && Patterns.WEB_URL.matcher(fixture.getHomeTeam().getLogo()).matches()) {
                GlideApp.with(mContext)
                        .load(fixture.getHomeTeam().getLogo())
                        .placeholder(R.drawable.zanibet_logo)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(mHomeTeamImageView);
            } else {
                GlideApp.with(mContext)
                        .load(R.drawable.zanibet_logo)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(mHomeTeamImageView);
            }

            if (fixture.getAwayTeam().getLogo() != null && Patterns.WEB_URL.matcher(fixture.getAwayTeam().getLogo()).matches()) {
                GlideApp.with(mContext)
                        .load(fixture.getAwayTeam().getLogo())
                        .placeholder(R.drawable.zanibet_logo)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(mAwayTeamImageView);
            } else {
                GlideApp.with(mContext)
                        .load(R.drawable.zanibet_logo)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(mAwayTeamImageView);
            }
        } catch (Exception e){
            Crashlytics.logException(e);
        }

        equalRadioButton.setEnabled(false);
        homeRadioButton.setEnabled(false);
        awayRadioButton.setEnabled(false);


        for (int z = 0; z < mBetList.size(); z++){
            if (mBetList.get(z).getFixture().equals(fixture.getId())){
                switch (mBetList.get(z).getResult()){
                    case 0:
                        equalRadioButton.setEnabled(true);
                        equalRadioButton.setChecked(true);
                        if (mBetList.get(z).getStatus() != null && mBetList.get(z).getStatus().equals("canceled")){
                            mPointsTextView.setText(Html.fromHtml(mContext.getString(R.string.points_earned, fixture.getzScore() )));
                        }  else if (fixture.getResult().getWinner() != 0 && mGrille.getStatus().equals("loose")){
                            equalRadioButton.setBackgroundResource(R.drawable.bet1_bad);
                            mPointsTextView.setText(Html.fromHtml(mContext.getString(R.string.points_earned, 0)));
                        } else if (fixture.getResult().getWinner() != 0 && mGrille.getStatus().equals("win")){
                            equalRadioButton.setBackgroundResource(R.drawable.bet1_bad);
                            mPointsTextView.setText(Html.fromHtml(mContext.getString(R.string.points_earned, 0)));
                        } else if (fixture.getResult().getWinner() == 0 && mGrille.getStatus().equals("waiting_result")){
                            mPointsTextView.setText(Html.fromHtml(mContext.getString(R.string.result_unvailable)));
                        }  else {
                            mPointsTextView.setText(Html.fromHtml(mContext.getString(R.string.points_earned, fixture.getzScore())));
                        }
                        mPronoTextView.setText(Html.fromHtml(mContext.getString(R.string.your_prediction, "N")));
                        break;
                    case 1:
                        homeRadioButton.setEnabled(true);
                        homeRadioButton.setChecked(true);
                        if (mBetList.get(z).getStatus() != null && mBetList.get(z).getStatus().equals("canceled")){
                            mPointsTextView.setText(Html.fromHtml(mContext.getString(R.string.points_earned, fixture.getzScore() )));
                        } else if (fixture.getResult().getWinner() != 1 && mGrille.getStatus().equals("loose")){
                            homeRadioButton.setBackgroundResource(R.drawable.bet1_bad);
                            mPointsTextView.setText(Html.fromHtml(mContext.getString(R.string.points_earned, 0)));
                        } else if (fixture.getResult().getWinner() != 1 && mGrille.getStatus().equals("win")){
                            homeRadioButton.setBackgroundResource(R.drawable.bet1_bad);
                            mPointsTextView.setText(Html.fromHtml(mContext.getString(R.string.points_earned, 0)));
                        } else if (fixture.getResult().getWinner() == 1 && mGrille.getStatus().equals("waiting_result")){
                            mPointsTextView.setText(Html.fromHtml(mContext.getString(R.string.result_unvailable)));
                        } else {
                            mPointsTextView.setText(Html.fromHtml(mContext.getString(R.string.points_earned, fixture.getzScore())));
                        }
                        mPronoTextView.setText(Html.fromHtml(mContext.getString(R.string.your_prediction, "1")));
                        break;
                    case 2:
                        awayRadioButton.setEnabled(true);
                        awayRadioButton.setChecked(true);
                        if (mBetList.get(z).getStatus() != null && mBetList.get(z).getStatus().equals("canceled")){
                            mPointsTextView.setText(Html.fromHtml(mContext.getString(R.string.points_earned, fixture.getzScore() )));
                        }  else if (fixture.getResult().getWinner() != 2 && mGrille.getStatus().equals("loose")){
                            awayRadioButton.setBackgroundResource(R.drawable.bet1_bad);
                            mPointsTextView.setText(Html.fromHtml(mContext.getString(R.string.points_earned, 0)));
                        } else if (fixture.getResult().getWinner() != 2 && mGrille.getStatus().equals("win")){
                            awayRadioButton.setBackgroundResource(R.drawable.bet1_bad);
                            mPointsTextView.setText(Html.fromHtml(mContext.getString(R.string.points_earned,0)));
                        } else if (fixture.getResult().getWinner() == 2 && mGrille.getStatus().equals("waiting_result")){
                            mPointsTextView.setText(Html.fromHtml(mContext.getString(R.string.result_unvailable)));
                        } else {
                            mPointsTextView.setText(Html.fromHtml(mContext.getString(R.string.points_earned, fixture.getzScore())));
                        }

                        mPronoTextView.setText(Html.fromHtml(mContext.getString(R.string.your_prediction, "2")));
                        break;

                }

                if (mGrille.getStatus().equals("loose") || mGrille.getStatus().equals("win")){
                    switch(fixture.getResult().getWinner()){
                        case 0:
                            equalRadioButton.setBackgroundResource(R.drawable.bet_on);
                            //equalRadioButton.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimaryLight));
                            break;
                        case 1:
                            homeRadioButton.setBackgroundResource(R.drawable.bet_on);
                            //homeRadioButton.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimaryLight));
                            break;
                        case 2:
                            awayRadioButton.setBackgroundResource(R.drawable.bet_on);
                            //awayRadioButton.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimaryLight));
                            break;
                    }
                }

            }
        }
        return row;
    }
}