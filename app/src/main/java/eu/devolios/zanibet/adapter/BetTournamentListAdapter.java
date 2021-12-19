package eu.devolios.zanibet.adapter;

import android.content.Context;
import android.support.annotation.IdRes;
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

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.devolios.zanibet.GlideApp;
import eu.devolios.zanibet.R;
import eu.devolios.zanibet.model.Competition;
import eu.devolios.zanibet.model.Fixture;

/**
 * Created by Gromat Luidgi on 10/11/2017.
 */

public class BetTournamentListAdapter extends BaseAdapter {

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
    @BindView(R.id.homeTeamImageView)
    ImageView mHomeTeamImageView;
    @BindView(R.id.awayTeamImageView)
    ImageView mAwayTeamImageView;
    @BindView(R.id.pointsTextView)
    TextView mPointsTextView;
    @BindView(R.id.competitionTextView)
    TextView mCompetitionTextView;

    private Context mContext;
    private List<Fixture> mFixtureList;
    private HashMap<String, Integer> mBetsHashMap;
    private Listener mListener;

    public BetTournamentListAdapter(Context context, List<Fixture> fixtureList, HashMap<String, Integer> bets, Listener listener){
        mContext = context;
        mFixtureList = fixtureList;
        mBetsHashMap = bets;
        mListener = listener;
    }

    // Conserver la liste des paris sélectionnés
    public void updateBets(HashMap<String, Integer> bets){
        mBetsHashMap = bets;
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
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View row = layoutInflater.inflate(R.layout.bet_tournament_item, viewGroup, false);
        ButterKnife.bind(this, row);

        //countTextView.setText(String.valueOf(i+1));

        Fixture fixture = getItem(i);
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

        try {
            Competition competition = Competition.parseCompetition(fixture.getCompetition());
            mCompetitionTextView.setText(competition.getName() + " - " + competition.getCountry());
        } catch (Exception e){
            Crashlytics.logException(e);
        }

        mPointsTextView.setText(Html.fromHtml(mContext.getString(R.string.bet_zscore, fixture.getzScore())));

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

        betRadioGroup.setTag(fixture.getId());
        betRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                mListener.onBetChange(radioGroup);
            }
        });

        homeRadioButton.setTag(1);
        equalRadioButton.setTag(0);
        awayRadioButton.setTag(2);

        if (mBetsHashMap.containsKey(fixture.getId())) {
            if (mBetsHashMap.get(fixture.getId()) == null){
                betRadioGroup.clearCheck();
            } else {
                switch (mBetsHashMap.get(fixture.getId())){
                    case 0:
                        equalRadioButton.setChecked(true);
                        break;
                    case 1:
                        homeRadioButton.setChecked(true);
                        break;
                    case 2:
                        awayRadioButton.setChecked(true);
                        break;
                }
            }
        }

        return row;
    }

    public interface Listener {
        void onBetChange(RadioGroup radioGroup);
    }
}
