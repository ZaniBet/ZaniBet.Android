package eu.devolios.zanibet.adapter;

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
import eu.devolios.zanibet.model.Fixture;
import eu.devolios.zanibet.model.Grille;

/**
 * Created by Gromat Luidgi on 11/11/2017.
 */

public class BetPlayedListAdapter extends BaseAdapter {

    //@BindView(eu.devolios.zanibet.R.id.countTextView)
    //TextView countTextView;
    @BindView(R.id.homeTeamTextView)
    TextView homeTextView;
    @BindView(R.id.awayTeamTextView)
    TextView awayTextView;
    @BindView(eu.devolios.zanibet.R.id.betRadioGroup)
    RadioGroup betRadioGroup;
    @BindView(eu.devolios.zanibet.R.id.homeRadioButton)
    RadioButton homeRadioButton;
    @BindView(eu.devolios.zanibet.R.id.equalRadioButton)
    RadioButton equalRadioButton;
    @BindView(eu.devolios.zanibet.R.id.awayRadioButton)
    RadioButton awayRadioButton;
    @BindView(R.id.pronoTextView)
    TextView mPronoTextView;
    @BindView(R.id.statusTextView)
    TextView mStatusTextView;
    @BindView(R.id.homeTeamImageView)
    ImageView mHomeTeamImageView;
    @BindView(R.id.awayTeamImageView)
    ImageView mAwayTeamImageView;
    @BindView(R.id.scoreTextView)
    TextView mScoreTextView;
    @BindView(R.id.warningImageView)
    ImageView mWarningImageView;

    private Context mContext;
    private List<Fixture> mFixtureList;
    private List<Bet> mBetList;
    private Grille mGrille;

    public BetPlayedListAdapter(Context context, List<Fixture> fixtureList, List<Bet> betList, Grille grille) {
        mContext = context;
        mFixtureList = fixtureList;
        mBetList = betList;
        mGrille = grille;
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
        View row = layoutInflater.inflate(R.layout.bet_played_item, viewGroup, false);
        ButterKnife.bind(this, row);

        Fixture fixture = getItem(index);

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
            if (fixture.getHomeTeam().getLogo() != null && Patterns.WEB_URL.matcher(fixture.getHomeTeam().getLogo() ).matches()) {
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

            if (fixture.getAwayTeam().getLogo() != null && Patterns.WEB_URL.matcher(fixture.getAwayTeam().getLogo() ).matches()) {
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

        mWarningImageView.setVisibility(View.GONE);
        for (int z = 0; z < mBetList.size(); z++){
            if (mBetList.get(z).getFixture().equals(fixture.getId())){
                switch (mBetList.get(z).getResult()){
                    case 0:
                        equalRadioButton.setEnabled(true);
                        equalRadioButton.setChecked(true);
                        if (mBetList.get(z).getStatus() != null && mBetList.get(z).getStatus().equals("canceled")){
                            mStatusTextView.setText(Html.fromHtml(mContext.getString(R.string.status_bet_canceled)));
                        }  else if (fixture.getResult().getWinner() != 0 && mGrille.getStatus().equals("loose")){
                            equalRadioButton.setBackgroundResource(R.drawable.bet1_bad);
                            mStatusTextView.setText(Html.fromHtml(mContext.getString(R.string.status_looser)));
                        } else if (fixture.getResult().getWinner() == 0 && !mGrille.getStatus().equals("waiting_result")){
                            mStatusTextView.setText(Html.fromHtml(mContext.getString(R.string.status_winner)));
                        } else if (fixture.getStatus().equals("postphoned")){
                            mStatusTextView.setText(Html.fromHtml(mContext.getString(R.string.status_bet_postphoned)));
                            mStatusTextView.setTextColor(mContext.getResources().getColor(R.color.colorRed800));
                            mWarningImageView.setVisibility(View.VISIBLE);
                        } else {
                            mStatusTextView.setText(Html.fromHtml(mContext.getString(R.string.result_unvailable)));
                        }
                        mPronoTextView.setText(Html.fromHtml(mContext.getString(R.string.your_prediction, "N")));
                        break;
                    case 1:
                        homeRadioButton.setEnabled(true);
                        homeRadioButton.setChecked(true);
                        if (mBetList.get(z).getStatus() != null && mBetList.get(z).getStatus().equals("canceled")){
                            mStatusTextView.setText(Html.fromHtml(mContext.getString(R.string.status_bet_canceled)));
                        }  else if (fixture.getResult().getWinner() != 1 && mGrille.getStatus().equals("loose")){
                            homeRadioButton.setBackgroundResource(R.drawable.bet1_bad);
                            mStatusTextView.setText(Html.fromHtml(mContext.getString(R.string.status_looser)));
                        } else if (fixture.getResult().getWinner() == 1 && !mGrille.getStatus().equals("waiting_result")){
                            mStatusTextView.setText(Html.fromHtml(mContext.getString(R.string.status_winner)));
                        } else if (fixture.getStatus().equals("postphoned")){
                            mStatusTextView.setText(Html.fromHtml(mContext.getString(R.string.status_bet_postphoned)));
                            mStatusTextView.setTextColor(mContext.getResources().getColor(R.color.colorRed800));
                            mWarningImageView.setVisibility(View.VISIBLE);
                        } else {
                            mStatusTextView.setText(Html.fromHtml(mContext.getString(R.string.result_unvailable)));
                        }
                        mPronoTextView.setText(Html.fromHtml(mContext.getString(R.string.your_prediction, "1")));
                        break;
                    case 2:
                        awayRadioButton.setEnabled(true);
                        awayRadioButton.setChecked(true);
                        if (mBetList.get(z).getStatus() != null && mBetList.get(z).getStatus().equals("canceled")){
                            mStatusTextView.setText(Html.fromHtml(mContext.getString(R.string.status_bet_canceled)));
                        }  else if (fixture.getResult().getWinner() != 2 && mGrille.getStatus().equals("loose")){
                            awayRadioButton.setBackgroundResource(R.drawable.bet1_bad);
                            mStatusTextView.setText(Html.fromHtml(mContext.getString(R.string.status_looser)));
                        } else if (fixture.getResult().getWinner() == 2 && !mGrille.getStatus().equals("waiting_result")){
                            mStatusTextView.setText(Html.fromHtml(mContext.getString(R.string.status_winner)));
                        } else if (fixture.getStatus().equals("postphoned")){
                            mStatusTextView.setText(Html.fromHtml(mContext.getString(R.string.status_bet_postphoned)));
                            mStatusTextView.setTextColor(mContext.getResources().getColor(R.color.colorRed800));
                            mWarningImageView.setVisibility(View.VISIBLE);
                        } else {
                            mStatusTextView.setText(Html.fromHtml(mContext.getString(R.string.result_unvailable)));
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