package eu.devolios.zanibet.fragment.gameticket;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.thunder413.datetimeutils.DateTimeUtils;

import java.util.Date;
import java.util.Objects;

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

public class GameTicketSingleDetailsFragment extends BaseFragment {

    @BindView(R.id.statusTextView)
    TextView mStatusTextView;
    @BindView(R.id.openDateTextView)
    TextView mOpenDateTextView;
    @BindView(R.id.limitDateTextView)
    TextView mLimitDateTextView;
    @BindView(R.id.resultDateTextView)
    TextView mResultDateTextView;
    @BindView(R.id.jetonTextView)
    TextView mJetonTextView;
    @BindView(R.id.zanicoinTextView)
    TextView mZanicoinTextView;
    @BindView(R.id.bonusTextView)
    TextView mBonusTextView;
    @BindView(R.id.rulesTextView)
    TextView mRulesTextView;

    private GameTicket mGameTicket;
    private Grille mGrille;

    public static GameTicketSingleDetailsFragment newInstance(GameTicket gameTicket, Grille grille){
        GameTicketSingleDetailsFragment gameTicketSingleDetailsFragment = new GameTicketSingleDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("gameticket", gameTicket);
        bundle.putSerializable("grille", grille);

        gameTicketSingleDetailsFragment.setArguments(bundle);
        return gameTicketSingleDetailsFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGameTicket = (GameTicket) Objects.requireNonNull(getArguments()).getSerializable("gameticket");
        mGrille = (Grille) getArguments().getSerializable("grille");

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.game_ticket_details_fragment, container, false);
        ButterKnife.bind(this, view);

        //Fixture fixture = mGameTicket.getFixtures()[0];

        if (mGameTicket.getStatus().equals("close")) {
            mStatusTextView.setText(Objects.requireNonNull(getActivity()).getString(R.string.ticket_status_close));
        } else if (mGameTicket.getStatus().equals("open")) {
            mStatusTextView.setText(Objects.requireNonNull(getActivity()).getString(R.string.ticket_status_open));
        } else if (mGameTicket.getStatus().equals("waiting_result")) {
            mStatusTextView.setText(Objects.requireNonNull(getActivity()).getString(R.string.ticket_status_waiting_result));
        } else if (mGameTicket.getStatus().equals("ended") && mGrille.getStatus() != null && !mGrille.getStatus().equals("waiting_result")){
            mStatusTextView.setText(Objects.requireNonNull(getActivity()).getString(R.string.ticket_proceed));
        } else if (mGameTicket.getStatus().equals("ended")) {
            mStatusTextView.setText(Objects.requireNonNull(getActivity()).getString(R.string.ticket_status_ended));
        } else if (mGameTicket.getStatus().equals("canceled")) {
            mStatusTextView.setText(Objects.requireNonNull(getActivity()).getString(R.string.ticket_status_canceled));
        }

        Date openDate = DateFormatter.formatMongoDate(mGameTicket.getOpenDate());
        mOpenDateTextView.setText(DateTimeUtils.formatWithPattern(openDate, "dd MMM yyyy HH:mm"));

        Date limitDate = DateFormatter.formatMongoDate(mGameTicket.getLimitDate());
        mLimitDateTextView.setText(DateTimeUtils.formatWithPattern(limitDate, "dd MMM yyyy HH:mm"));

        Date resultDate = DateFormatter.formatMongoDate(mGameTicket.getResultDate());
        mResultDateTextView.setText(DateTimeUtils.formatWithPattern(resultDate, "dd MMM yyyy HH:mm"));


        mJetonTextView.setText(getActivity().getString(R.string.cost_jeton, mGameTicket.getJeton()));

        mZanicoinTextView.setText(getActivity().getString(R.string.zanicoin_earn_per_bet, mGameTicket.getPointsPerBet()));
        mBonusTextView.setText(getActivity().getString(R.string.zanicoin_bonus, mGameTicket.getBonus()));
        if(mGrille != null && mGameTicket.getStatus().equals("ended")){
            if (mGrille.getStatus().equals("loose") || mGrille.getStatus().equals("win")
                    || mGrille.getStatus().equals("free") || mGrille.getStatus().equals("refund")){
                mZanicoinTextView.setText(getActivity().getString(R.string.amount_zanicoins, mGrille.getPayout().getPoint()));

            }
        }

        mRulesTextView.setText(Html.fromHtml(getActivity().getString(R.string.ticket_single_rules)));
        return view;
    }
}
