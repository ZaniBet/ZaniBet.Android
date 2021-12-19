package eu.devolios.zanibet.fragment.gameticket;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.thunder413.datetimeutils.DateTimeUtils;

import org.w3c.dom.Text;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.devolios.zanibet.R;
import eu.devolios.zanibet.fragment.BaseFragment;
import eu.devolios.zanibet.model.GameTicket;
import eu.devolios.zanibet.model.Grille;
import eu.devolios.zanibet.utils.DateFormatter;

/**
 * Created by Gromat Luidgi on 27/03/2018.
 */

public class GameTicketTournamentDetailsFragment extends BaseFragment {

    @BindView(R.id.statusTextView)
    TextView mStatusTextView;
    @BindView(R.id.openDateTextView)
    TextView mOpenDateTextView;
    @BindView(R.id.limitDateTextView)
    TextView mLimitDateTextView;
    @BindView(R.id.resultDateTextView)
    TextView mResultDateTextView;
    @BindView(R.id.amountPlayersTextView)
    TextView mAmountPlayersTextView;
    @BindView(R.id.potTextView)
    TextView mPotTextView;
    @BindView(R.id.paidPlayersTextView)
    TextView mPaidPlayersTextView;
    @BindView(R.id.feesTextView)
    TextView mFeesTextView;
    @BindView(R.id.playCostTextView)
    TextView mPlayCostTextView;
    @BindView(R.id.rulesTextView)
    TextView mRulesTextView;
    @BindView(R.id.levelTextView)
    TextView mLevelTextView;
    @BindView(R.id.sharingTextView)
    TextView mSharingTextView;
    @BindView(R.id.bonusTextView)
    TextView mBonusTextView;
    @BindView(R.id.bonusActivationTextView)
    TextView mBonusActivationTextView;

    private GameTicket mGameTicket;
    private Grille mGrille;

    public static GameTicketTournamentDetailsFragment newInstance(GameTicket gameTicket, Grille grille){
        GameTicketTournamentDetailsFragment gameTicketSingleDetailsFragment = new GameTicketTournamentDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("gameticket", gameTicket);
        bundle.putSerializable("grille", grille);

        gameTicketSingleDetailsFragment.setArguments(bundle);
        return gameTicketSingleDetailsFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGameTicket = (GameTicket) getArguments().getSerializable("gameticket");
        mGrille = (Grille) getArguments().getSerializable("grille");

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.game_ticket_tournament_details_fragment, container, false);
        ButterKnife.bind(this, view);

        //Fixture fixture = mGameTicket.getFixtures()[0];
        mPlayCostTextView.setText(getActivity().getString(R.string.amount_zanihash,
                mGameTicket.getTournament().getPlayCost()));

        mFeesTextView.setText(getActivity().getString(R.string.amount_zanihash,
                mGameTicket.getTournament().getFees()));

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

        Date openDate = DateFormatter.formatMongoDate(mGameTicket.getOpenDate());
        mOpenDateTextView.setText(DateTimeUtils.formatWithPattern(openDate, "dd MMM yyyy HH:mm"));

        Date limitDate = DateFormatter.formatMongoDate(mGameTicket.getLimitDate());
        mLimitDateTextView.setText(DateTimeUtils.formatWithPattern(limitDate, "dd MMM yyyy HH:mm"));

        Date resultDate = DateFormatter.formatMongoDate(mGameTicket.getResultDate());
        mResultDateTextView.setText(DateTimeUtils.formatWithPattern(resultDate, "dd MMM yyyy HH:mm"));

        mAmountPlayersTextView.setText(Html.fromHtml(getActivity().getString(R.string.amount_players,
                mGameTicket.getTournament().getTotalPlayers())));

        if (mGameTicket.getTournament().getRewardType().equals("ZaniHash")) {
            mPotTextView.setText(getActivity().getString(R.string.pot_amount_zanihash,
                    mGameTicket.getTournament().getPot()+mGameTicket.getJackpot()));
            mBonusTextView.setText(getString(R.string.amount_zanihash, mGameTicket.getBonus()));
            mBonusActivationTextView.setText(Html.fromHtml(getString(R.string.tournament_zanihash_bonus_activation, mGameTicket.getBonusActivation(), mGameTicket.getBonus())));
            mRulesTextView.setText(Html.fromHtml(getActivity().getString(R.string.ticket_tournament_rules)));
        } else {
            mPotTextView.setText(getActivity().getString(R.string.pot_amount,
                    mGameTicket.getTournament().getPot()+mGameTicket.getJackpot()));
            mBonusTextView.setText(getString(R.string.amount_zanicoins, mGameTicket.getBonus()));
            mBonusActivationTextView.setText(Html.fromHtml(getString(R.string.tournament_bonus_activation, mGameTicket.getBonusActivation(), mGameTicket.getBonus())));
            mRulesTextView.setText(Html.fromHtml(getActivity().getString(R.string.ticket_tournament_rules)));
        }

        mPaidPlayersTextView.setText(Html.fromHtml(getActivity().getString(R.string.amount_paid_players,
                mGameTicket.getTournament().getTotalPlayersPaid())));
        mLevelTextView.setText(String.valueOf(mGameTicket.getTournament().getLevel()));
        mSharingTextView.setText(String.format("%d%%", mGameTicket.getTournament().getSharing()));

        return view;
    }
}
