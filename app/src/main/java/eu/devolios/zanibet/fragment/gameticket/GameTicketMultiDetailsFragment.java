package eu.devolios.zanibet.fragment.gameticket;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.github.thunder413.datetimeutils.DateTimeUtils;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.devolios.zanibet.R;
import eu.devolios.zanibet.fragment.BaseFragment;
import eu.devolios.zanibet.model.Competition;
import eu.devolios.zanibet.model.GameTicket;
import eu.devolios.zanibet.model.Grille;
import eu.devolios.zanibet.utils.DateFormatter;

/**
 * Created by Gromat Luidgi on 27/03/2018.
 */

public class GameTicketMultiDetailsFragment extends BaseFragment {

    @BindView(R.id.statusTextView)
    TextView mStatusTextView;
    @BindView(R.id.countryTextView)
    TextView mCountryTextView;
    @BindView(R.id.divisionTextView)
    TextView mDivisionTextView;
    @BindView(R.id.openDateTextView)
    TextView mOpenDateTextView;
    @BindView(R.id.limitDateTextView)
    TextView mLimitDateTextView;
    @BindView(R.id.resultDateTextView)
    TextView mResultDateTextView;
    @BindView(R.id.zanicoinTextView)
    TextView mZanicoinTextView;
    @BindView(R.id.cashTextView)
    TextView mCashTextView;
    @BindView(R.id.bonusTextView)
    TextView mBonusTextView;
    @BindView(R.id.rulesTextView)
    TextView mRulesTextView;
    @BindView(R.id.pronoWinTextView)
    TextView mPronoWinTextView;
    @BindView(R.id.alertTextView)
    TextView mAlertTextView;
    @BindView(R.id.videoHelpTextView)
    TextView mVideoHelpTextView;


    private GameTicket mGameTicket;
    private Grille mGrille;
    private Competition mCompetition;

    public static GameTicketMultiDetailsFragment newInstance(GameTicket gameTicket, Grille grille){
        GameTicketMultiDetailsFragment gameTicketMultiDetailsFragment = new GameTicketMultiDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("gameticket", gameTicket);
        bundle.putSerializable("grille", grille);

        gameTicketMultiDetailsFragment.setArguments(bundle);
        return gameTicketMultiDetailsFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGameTicket = (GameTicket) getArguments().getSerializable("gameticket");
        mGrille = (Grille) getArguments().getSerializable("grille");
        //mCompetition = Competition.convertFromMap(mGameTicket.getCompetition());
        mCompetition = Competition.parseCompetition(mGameTicket.getCompetition());
        /*try {
            if (mGameTicket.getCompetition() instanceof String) {
                for (Competition comp : Competition.getCompetitions()) {
                    if (comp.get_id().equals(mGameTicket.getCompetition())) {
                        mCompetition = comp;
                        mGameTicket.setCompetition(comp);
                    }
                }
            } else {
                mCompetition = Competition.convertFromMap(mGameTicket.getCompetition());
            }
        } catch (Exception e){
            Crashlytics.logException(e);
        }*/
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.game_ticket_multi_details_fragment, container, false);
        ButterKnife.bind(this, view);

        //Fixture fixture = mGameTicket.getFixtures()[0];

        if (mGameTicket.getStatus().equals("close")) {
            mStatusTextView.setText(getActivity().getString(R.string.ticket_status_close));
        } else if (mGameTicket.getStatus().equals("open")) {
            mStatusTextView.setText(getActivity().getString(R.string.ticket_status_open));
        } else if (mGameTicket.getStatus().equals("waiting_result")) {
            mStatusTextView.setText(getActivity().getString(R.string.ticket_status_waiting_result));
        } else if (mGameTicket.getStatus().equals("ended") && mGrille.getStatus() != null && !mGrille.getStatus().equals("waiting_result")){
            mStatusTextView.setText(getActivity().getString(R.string.ticket_proceed));
        } else if (mGameTicket.getStatus().equals("ended")) {
            mStatusTextView.setText(getActivity().getString(R.string.ticket_status_ended));
        } else if (mGameTicket.getStatus().equals("canceled")) {
            mStatusTextView.setText(getActivity().getString(R.string.ticket_status_canceled));
        }

        try {
            mCountryTextView.setText(mCompetition.getCountry());
            mDivisionTextView.setText(String.valueOf(mCompetition.getDivision()));
        } catch (Exception e){
            Crashlytics.logException(e);
        }

        Date openDate = DateFormatter.formatMongoDate(mGameTicket.getOpenDate());
        mOpenDateTextView.setText(DateTimeUtils.formatWithPattern(openDate, "dd MMM yyyy HH:mm"));

        Date limitDate = DateFormatter.formatMongoDate(mGameTicket.getLimitDate());
        mLimitDateTextView.setText(DateTimeUtils.formatWithPattern(limitDate, "dd MMM yyyy HH:mm"));

        Date resultDate = DateFormatter.formatMongoDate(mGameTicket.getResultDate());
        mResultDateTextView.setText(DateTimeUtils.formatWithPattern(resultDate, "dd MMM yyyy HH:mm"));

        if (mGrille != null && mGrille.getStatus().equals("win")){
            mCashTextView.setText(getActivity().getString(R.string.grille_earn, mGrille.getPayout().getAmount()));
            mPronoWinTextView.setText(Html.fromHtml(getActivity().getString(R.string.status_prono_win, mGrille.getNumberOfBetsWin(), mGrille.getBets().length)));
        } else if (mGrille != null && mGrille.getStatus().equals("loose")){
            mCashTextView.setText(getActivity().getString(R.string.grille_earn, mGrille.getPayout().getAmount()));
            mPronoWinTextView.setText(Html.fromHtml(getActivity().getString(R.string.status_prono_win, mGrille.getNumberOfBetsWin(), mGrille.getBets().length)));
        } else if (mGrille != null && mGrille.getStatus().equals("canceled")){
            mCashTextView.setText(getActivity().getString(R.string.grille_earn, mGrille.getPayout().getAmount()));
            mPronoWinTextView.setText(Html.fromHtml(getActivity().getString(R.string.status_prono_win, mGrille.getNumberOfBetsWin(), mGrille.getBets().length)));
        } else {
            mCashTextView.setText(getActivity().getString(R.string.reward_share, mGameTicket.getJackpot()));
            mPronoWinTextView.setText(Html.fromHtml(getActivity().getString(R.string.status_prono_unavailable)));
        }

        mZanicoinTextView.setText(getActivity().getString(R.string.zanicoin_earn_per_bet, mGameTicket.getPointsPerBet()));
        mBonusTextView.setText(getActivity().getString(R.string.bonus_activation, mGameTicket.getBonus(), mGameTicket.getBonusActivation()));
        if(mGrille != null && mGameTicket.getStatus().equals("ended")){
            if (mGrille.getStatus().equals("loose") || mGrille.getStatus().equals("win") || mGrille.getStatus().equals("canceled")){
                mZanicoinTextView.setText(getActivity().getString(R.string.amount_zanicoins, mGrille.getPayout().getPoint()));

            }
        }

        mAlertTextView.setText(Html.fromHtml(getActivity().getString(R.string.matchday_rules_alert)));
        mVideoHelpTextView.setText(Html.fromHtml(getActivity().getString(R.string.video_ads_help)));
        mRulesTextView.setText(Html.fromHtml(getActivity().getString(R.string.ticket_multi_rules)));
        return view;
    }
}
