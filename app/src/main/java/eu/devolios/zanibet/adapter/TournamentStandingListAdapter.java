package eu.devolios.zanibet.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.devolios.zanibet.R;
import eu.devolios.zanibet.model.Fixture;
import eu.devolios.zanibet.model.GameTicket;
import eu.devolios.zanibet.model.GrilleStanding;
import eu.devolios.zanibet.model.User;

/**
 * Created by Gromat Luidgi on 10/11/2017.
 */

public class TournamentStandingListAdapter extends BaseAdapter {


    @BindView(R.id.positionTextView)
    TextView mPositionTextView;
    @BindView(R.id.playerTextView)
    TextView mPlayerTextView;
    @BindView(R.id.pointsTextView)
    TextView mPointsTextView;
    @BindView(R.id.coinsTextView)
    TextView mCoinsTextView;

    private Context mContext;
    private List<GrilleStanding> mGrilleStandngList;
    private GameTicket mGameTicket;

    public TournamentStandingListAdapter(Context context, ArrayList<GrilleStanding> grilleStandings, GameTicket gameTicket){
        mContext = context;
        mGrilleStandngList = grilleStandings;
        mGameTicket = gameTicket;
    }

    @Override
    public int getCount() {
        return mGrilleStandngList.size();
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
        View row = layoutInflater.inflate(R.layout.tournament_standing_item, viewGroup, false);
        ButterKnife.bind(this, row);

        boolean shouldHighlight = false;
        GrilleStanding grilleStanding = mGrilleStandngList.get(i);
        if (grilleStanding.getId().equals(User.currentUser().getUsername())) shouldHighlight = true;

        try {
            mPositionTextView.setText(String.valueOf(grilleStanding.getRank()));
            mPlayerTextView.setText(grilleStanding.getId());
            mPointsTextView.setText(String.valueOf(grilleStanding.getPoints()));

            if (mGameTicket.getTournament().getRewardType().equals("ZaniCoin")) {
                mCoinsTextView.setText(String.valueOf(grilleStanding.getCoin() + " ZC"));
            } else if (mGameTicket.getTournament().getRewardType().equals("ZaniHash")){
                mCoinsTextView.setText(String.valueOf(grilleStanding.getCoin() + " ZH"));
            }
            if (shouldHighlight) row.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPrimary50));

            if(!mGameTicket.getStatus().equals("ended")){
                mPointsTextView.setText("-");
            }
        } catch(Exception e){
            Crashlytics.logException(e);
        }

        return row;
    }
}
