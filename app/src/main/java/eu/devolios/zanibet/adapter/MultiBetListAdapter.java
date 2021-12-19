package eu.devolios.zanibet.adapter;

import android.content.Context;
import android.support.annotation.IdRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.devolios.zanibet.R;
import eu.devolios.zanibet.model.BetType;
import eu.devolios.zanibet.model.Fixture;

/**
 * Created by Gromat Luidgi on 05/01/2018.
 */

public class MultiBetListAdapter extends BaseAdapter {

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

    private Context mContext;
    private BetType[] mBetsTypeArray;
    private HashMap<String, Integer> mBetsHashMap; // <type, bet>
    private MultiBetListAdapter.Listener mListener;
    private Fixture mFixture;

    public MultiBetListAdapter(Context context, BetType[] betList, HashMap<String, Integer> bets, Fixture fixture, MultiBetListAdapter.Listener listener) {
        mContext = context;
        mBetsTypeArray = betList;
        mBetsHashMap = bets;
        mFixture = fixture;
        mListener = listener;
    }

    public void updateBets(HashMap<String, Integer> bets){
        mBetsHashMap = bets;
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
        View row = layoutInflater.inflate(R.layout.bet_multi_item, viewGroup, false);
        ButterKnife.bind(this, row);

        //countTextView.setText(String.valueOf(i+1));

        String betType = getItem(i);
        switch (betType) {
            case "1N2":
                mDescriptionTextView.setText(mContext.getString(R.string.bet_basic_desc));
                mChoiceOneTextView.setText(mFixture.getHomeTeam().getName());
                mChoiceOneRadio.setTag(1);
                mChoiceTwoRadio.setTag(0);
                mChoiceTwoTextView.setText(mContext.getString(R.string.equal_score_match));

                mChoiceThreeRadio.setTag(2);
                mChoiceThreeTextView.setText(mFixture.getAwayTeam().getName());
                break;
            case "BOTH_GOAL":
                mDescriptionTextView.setText(mContext.getString(R.string.bet_both_goal_desc));
                mChoiceOneTextView.setText(mContext.getString(R.string.yes));
                mChoiceOneRadio.setTag(1);

                mChoiceTwoRadio.setTag(0);
                mChoiceTwoTextView.setText(mContext.getString(R.string.no));

                mChoiceThreeRadio.setVisibility(View.GONE);
                mChoiceThreeTextView.setVisibility(View.GONE);
                mSeparatorView.setVisibility(View.GONE);
                break;
            case "LESS_MORE_GOAL":
                mDescriptionTextView.setText(mContext.getString(R.string.bet_less_more_desc));
                mChoiceOneTextView.setText(mContext.getString(R.string.more_than_two));
                mChoiceOneRadio.setTag(1);

                mChoiceTwoRadio.setTag(0);
                mChoiceTwoTextView.setText(mContext.getString(R.string.less_than_two));

                mChoiceThreeRadio.setVisibility(View.GONE);
                mChoiceThreeTextView.setVisibility(View.GONE);
                mSeparatorView.setVisibility(View.GONE);
                break;
            case "FIRST_GOAL":
                mDescriptionTextView.setText(mContext.getString(R.string.bet_first_goal_desc));
                mChoiceOneTextView.setText(mFixture.getHomeTeam().getName());
                mChoiceOneRadio.setTag(1);

                mChoiceTwoRadio.setTag(0);
                mChoiceTwoTextView.setText(mContext.getString(R.string.no_goal_scored));

                mChoiceThreeRadio.setTag(2);
                mChoiceThreeTextView.setText(mFixture.getAwayTeam().getName());
                break;
        }


        mBetRadioGroup.setTag(betType);
        mBetRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                mListener.onBetChange(radioGroup);
            }
        });

        if (mBetsHashMap.containsKey(betType)) {
            if (mBetsHashMap.get(betType) == null) {
                mBetRadioGroup.clearCheck();
            } else {
                switch (mBetsHashMap.get(betType)) {
                    case 0:
                        mChoiceTwoRadio.setChecked(true);
                        break;
                    case 1:
                        mChoiceOneRadio.setChecked(true);
                        break;
                    case 2:
                        mChoiceThreeRadio.setChecked(true);
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
