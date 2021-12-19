package eu.devolios.zanibet.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.devolios.zanibet.R;
import eu.devolios.zanibet.adapter.LeagueStandingListAdapter;
import eu.devolios.zanibet.adapter.TournamentStandingListAdapter;
import eu.devolios.zanibet.model.ApiError;
import eu.devolios.zanibet.model.Competition;
import eu.devolios.zanibet.model.GameTicket;
import eu.devolios.zanibet.model.GrilleStanding;
import eu.devolios.zanibet.model.LeagueStanding;
import eu.devolios.zanibet.presenter.LeagueStandingPresenter;
import eu.devolios.zanibet.presenter.TournamentStandingPresenter;
import eu.devolios.zanibet.presenter.contract.LeagueStandingContract;
import eu.devolios.zanibet.presenter.contract.TournamentStandingContract;
import eu.devolios.zanibet.utils.EmptyView;

/**
 * Created by Gromat Luidgi on 27/03/2018.
 */

public class TournamentStandingFragment extends Fragment implements TournamentStandingContract.View {

    @BindView(R.id.standingListView)
    ListView mStandingListView;
    @BindView(R.id.frameLayout)
    FrameLayout mFrameLayout;

    private TournamentStandingPresenter mTournamentStandingPresenter;
    private TournamentStandingListAdapter mTournamentStandingListAdapter;
    private GameTicket mGameTicket;
    private ArrayList<GrilleStanding> mGrilleStandingList;

    private View mErrorView;
    private View mLoadingView;

    public static TournamentStandingFragment newInstance(GameTicket gameTicket){
        TournamentStandingFragment tournamentStandingFragment = new TournamentStandingFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("gameticket", gameTicket);
        tournamentStandingFragment.setArguments(bundle);
        return tournamentStandingFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGameTicket = (GameTicket) Objects.requireNonNull(getArguments()).getSerializable("gameticket");
        mTournamentStandingPresenter = new TournamentStandingPresenter(getActivity(), this);
        mGrilleStandingList = new ArrayList<>();
        mTournamentStandingListAdapter = new TournamentStandingListAdapter(getActivity(), mGrilleStandingList, mGameTicket);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tournament_standing_fragment, container, false);
        ButterKnife.bind(this, view);

        mLoadingView = inflater.inflate(R.layout.loading_overlay, container, false);
        mStandingListView.addHeaderView(inflater.inflate(R.layout.tournament_standing_header_item, null, false));
        mStandingListView.setAdapter(mTournamentStandingListAdapter);
        mTournamentStandingPresenter.loadStanding(mGameTicket);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onLoadStanding(List<GrilleStanding> grilleStandings) {
        mGrilleStandingList.clear();
        mGrilleStandingList.addAll(grilleStandings);
        mTournamentStandingListAdapter.notifyDataSetChanged();
    }

    @Override
    public void showContentLoading() {
        if (isAdded()) {
            if (mLoadingView != null && mFrameLayout != null) {
                if (mLoadingView.getParent() == null) mFrameLayout.addView(mLoadingView);
            }
        }
    }

    @Override
    public void hideContentLoading() {
        if (isAdded()) {
            if (mLoadingView != null && mFrameLayout != null) {
                if (mLoadingView.getParent() != null) mFrameLayout.removeView(mLoadingView);
            }
        }
    }

    @Override
    public void showContentError(ApiError error) {
        mGrilleStandingList.clear();
        mTournamentStandingListAdapter.notifyDataSetChanged();
        if (isAdded()) {
            if (mErrorView != null && mFrameLayout != null){
                if (mErrorView.getParent() != null){
                    mFrameLayout.removeView(mErrorView);
                };
            }

            mErrorView = EmptyView.withDrawable(getActivity(),
                    error.getMessage(),
                    ContextCompat.getDrawable(Objects.requireNonNull(getActivity()), R.drawable.ic_champion_round));
            mFrameLayout.addView(mErrorView);
        }
    }


    @Override
    public void hideContentError() {
        if (isAdded()){
            if (mErrorView != null && mFrameLayout != null){
               if (mErrorView.getParent() != null){
                   mFrameLayout.removeView(mErrorView);
               }
            }
        }
    }
}
