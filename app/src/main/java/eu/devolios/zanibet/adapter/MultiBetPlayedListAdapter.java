package eu.devolios.zanibet.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.devolios.zanibet.R;
import eu.devolios.zanibet.model.Bet;
import eu.devolios.zanibet.model.BetType;
import eu.devolios.zanibet.model.Fixture;
import eu.devolios.zanibet.model.GameTicket;
import eu.devolios.zanibet.model.Odds;

/**
 * Created by Gromat Luidgi on 05/01/2018.
 */

public class  MultiBetPlayedListAdapter extends BaseAdapter {

    @BindView(R.id.descriptionTextView)
    TextView mDescriptionTextView;
    @BindView(R.id.choiceOneRadio)
    RadioButton mChoiceOneRadio;
    @BindView(R.id.choiceOneTextView)
    TextView mChoiceOneTextView;

    @BindView(R.id.choiceTwoRadio)
    RadioButton mChoiceTwoRadio;
    @BindView(R.id.choiceTwoTextView)
    TextView mChoiceTwoTextView;

    @BindView(R.id.choiceThreeRadio)
    RadioButton mChoiceThreeRadio;
    @BindView(R.id.choiceThreeTextView)
    TextView mChoiceThreeTextView;
    @BindView(R.id.betRadioGroup)
    RadioGroup mBetRadioGroup;

    @BindView(R.id.separatorView)
    View mSeparatorView;

    @BindView(R.id.choiceOneOddDescTextView)
    TextView mChoiceOneOddDescTextView;
    @BindView(R.id.choiceOneOddTextView)
    TextView mChoiceOneOddTextView;

    @BindView(R.id.choiceTwoOddDescTextView)
    TextView mChoiceTwoOddDescTextView;
    @BindView(R.id.choiceTwoOddTextView)
    TextView mChoiceTwoOddTextView;

    @BindView(R.id.choiceThreeLayout)
    LinearLayout mChoiceThreeLayout;
    @BindView(R.id.choiceThreeOddDescTextView)
    TextView mChoiceThreeOddDescTextView;
    @BindView(R.id.choiceThreeOddTextView)
    TextView mChoiceThreeOddTextView;

    private Context mContext;
    private GameTicket mGameTicket;
    private BetType[] mBetsTypeArray;
    private List<Bet> mBetList;
    private Fixture mFixture;

    public MultiBetPlayedListAdapter(Context context, GameTicket gameTicket, List<Bet> bets, Fixture fixture) {
        mContext = context;
        mGameTicket = gameTicket;
        mBetList = bets; // Liste des paris joués par l'utilisateur
        mFixture = fixture;
        mBetsTypeArray = gameTicket.getBetsType(); // Utiliser pour générer la vue
    }

    @Override
    public int getCount() {
        return mBetsTypeArray.length;
    }

    @Override
    public String getItem(int i) {
        return mBetsTypeArray[i].getType();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);

        View row = layoutInflater.inflate(R.layout.bet_multi_odd_item, viewGroup, false);
        ButterKnife.bind(this, row);

        mChoiceOneRadio.setEnabled(false);
        mChoiceTwoRadio.setEnabled(false);
        mChoiceThreeRadio.setEnabled(false);

        String betType = getItem(i);
        switch (betType) {
            case "1N2":
                mDescriptionTextView.setText(mContext.getString(R.string.bet_basic_desc));
                mChoiceOneTextView.setText(mFixture.getHomeTeam().getName());
                mChoiceTwoTextView.setText(mContext.getString(R.string.equal_score_match));
                mChoiceThreeTextView.setText(mFixture.getAwayTeam().getName());

                if (mFixture.getOdds() != null && mFixture.getOdds().length > 0) {
                    for (Odds odds : mFixture.getOdds()){
                        try {
                           if (odds.getBookmaker().equals("Players") && odds.getType().equals("1N2")) {
                                mChoiceOneOddTextView.setText(String.valueOf(odds.getOdds().getHomeTeam()) + "%");
                                mChoiceThreeOddTextView.setText(String.valueOf(odds.getOdds().getAwayTeam()) + "%");
                                mChoiceTwoOddTextView.setText(String.valueOf(odds.getOdds().getDraw()) + "%");
                            }
                        } catch (Exception e){
                            Crashlytics.logException(e);
                        }
                    }
                }

                break;
            case "BOTH_GOAL":
                mDescriptionTextView.setText(mContext.getString(R.string.bet_both_goal_desc));
                mChoiceOneTextView.setText(mContext.getString(R.string.yes));
                mChoiceOneOddDescTextView.setText(mContext.getString(R.string.yes));

                mChoiceTwoTextView.setText(mContext.getString(R.string.no));
                mChoiceTwoOddDescTextView.setText(mContext.getString(R.string.no));

                mChoiceThreeRadio.setVisibility(View.GONE);
                mChoiceThreeTextView.setVisibility(View.GONE);
                mSeparatorView.setVisibility(View.GONE);
                mChoiceThreeLayout.setVisibility(View.GONE);

                if (mFixture.getOdds() != null && mFixture.getOdds().length > 0) {
                    for (Odds odds : mFixture.getOdds()){
                        try {
                            if (odds.getBookmaker().equals("Players-Single") && odds.getType().equals("BOTH_GOAL")) {
                                mChoiceOneOddTextView.setText(String.valueOf(odds.getOdds().getPositive()) + "%");
                                mChoiceTwoOddTextView.setText(String.valueOf(odds.getOdds().getNegative()) + "%");
                            }
                        } catch (Exception e){
                            Crashlytics.logException(e);
                        }
                    }
                }
                break;
            case "LESS_MORE_GOAL":
                mDescriptionTextView.setText(mContext.getString(R.string.bet_less_more_desc));
                mChoiceOneTextView.setText(mContext.getString(R.string.more_than_two));
                mChoiceOneOddDescTextView.setText(mContext.getString(R.string.more));

                mChoiceTwoTextView.setText(mContext.getString(R.string.less_than_two));
                mChoiceTwoOddDescTextView.setText(mContext.getString(R.string.less));

                mChoiceThreeRadio.setVisibility(View.GONE);
                mChoiceThreeTextView.setVisibility(View.GONE);
                mSeparatorView.setVisibility(View.GONE);
                mChoiceThreeLayout.setVisibility(View.GONE);

                if (mFixture.getOdds() != null && mFixture.getOdds().length > 0) {
                    for (Odds odds : mFixture.getOdds()){
                        try {
                            if (odds.getBookmaker().equals("Players-Single") && odds.getType().equals("LESS_MORE_GOAL")) {
                                mChoiceOneOddTextView.setText(String.valueOf(odds.getOdds().getPositive()) + "%");
                                mChoiceTwoOddTextView.setText(String.valueOf(odds.getOdds().getNegative()) + "%");
                            }
                        } catch (Exception e){
                            Crashlytics.logException(e);
                        }
                    }
                }
                break;
            case "FIRST_GOAL":
                mDescriptionTextView.setText(mContext.getString(R.string.bet_first_goal_desc));
                mChoiceOneTextView.setText(mFixture.getHomeTeam().getName());

                mChoiceTwoTextView.setText(mContext.getString(R.string.no_goal_scored));
                mChoiceThreeTextView.setText(mFixture.getAwayTeam().getName());

                if (mFixture.getOdds() != null && mFixture.getOdds().length > 0) {
                    for (Odds odds : mFixture.getOdds()){
                        try {
                            if (odds.getBookmaker().equals("Players-Single") && odds.getType().equals("FIRST_GOAL")) {
                                mChoiceOneOddTextView.setText(String.valueOf(odds.getOdds().getHomeTeam()) + "%");
                                mChoiceThreeOddTextView.setText(String.valueOf(odds.getOdds().getAwayTeam()) + "%");
                                mChoiceTwoOddTextView.setText(String.valueOf(odds.getOdds().getDraw()) + "%");
                            }
                        } catch (Exception e){
                            Crashlytics.logException(e);
                        }
                    }
                }
                break;
        }

        int index = 0;
        for (Bet bet : mBetList){
            if (bet.getType().equals(betType)){
                switch(bet.getResult()){
                    case 0:
                        mChoiceTwoRadio.setEnabled(true);
                        mChoiceTwoRadio.setChecked(true);
                        if (mGameTicket.getBetsType()[index].getResult() != 0){
                            //mChoiceTwoRadio.setBackgroundColor(mContext.getResources().getColor(R.color.colorRed800));
                        }
                        break;
                    case 1:
                        mChoiceOneRadio.setEnabled(true);
                        mChoiceOneRadio.setChecked(true);
                        if (mGameTicket.getBetsType()[index].getResult() != 1){
                            //mChoiceOneRadio.setBackgroundColor(mContext.getResources().getColor(R.color.colorRed800));
                        }
                        break;
                    case 2:
                        mChoiceThreeRadio.setEnabled(true);
                        mChoiceThreeRadio.setChecked(true);
                        if (mGameTicket.getBetsType()[index].getResult() != 2){
                            //mChoiceThreeRadio.button(mContext.getResources().getColor(R.color.colorRed800));
                        }
                        break;
                }
            }
            index++;
        }

        return row;
    }
}
