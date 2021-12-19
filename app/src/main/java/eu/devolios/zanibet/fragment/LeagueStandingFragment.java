package eu.devolios.zanibet.fragment;

import android.os.Bundle;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.devolios.zanibet.R;
import eu.devolios.zanibet.adapter.LeagueStandingListAdapter;
import eu.devolios.zanibet.model.ApiError;
import eu.devolios.zanibet.model.Competition;
import eu.devolios.zanibet.model.GameTicket;
import eu.devolios.zanibet.model.LeagueStanding;
import eu.devolios.zanibet.presenter.LeagueStandingPresenter;
import eu.devolios.zanibet.presenter.contract.LeagueStandingContract;
import eu.devolios.zanibet.utils.EmptyView;

/**
 * Created by Gromat Luidgi on 27/03/2018.
 */

public class LeagueStandingFragment extends Fragment implements LeagueStandingContract.View {

    @BindView(R.id.standingListView)
    ListView mStandingListView;
    //@BindView(R.id.filterButton)
    //MultiStateToggleButton mFilterToggleButton;
    @BindView(R.id.frameLayout)
    FrameLayout mFrameLayout;

    private LeagueStandingPresenter mLeagueStandingPresenter;
    private LeagueStandingListAdapter mLeagueStandingListAdapter;
    private GameTicket mGameTicket;
    private ArrayList<LeagueStanding> mLeagueStandingList;

    private View mErrorView;
    private View mLoadingView;

    public static LeagueStandingFragment newInstance(GameTicket gameTicket){
        LeagueStandingFragment leagueStandingFragment = new LeagueStandingFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("gameticket", gameTicket);
        leagueStandingFragment.setArguments(bundle);
        return leagueStandingFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGameTicket = (GameTicket) getArguments().getSerializable("gameticket");
        mLeagueStandingPresenter = new LeagueStandingPresenter(getActivity(), this);
        mLeagueStandingList = new ArrayList<>();
        mLeagueStandingListAdapter = new LeagueStandingListAdapter(getActivity(), mGameTicket.getFixtures()[0], mLeagueStandingList, 0);
        if (mGameTicket.getType().equals("MATCHDAY")){
            mLeagueStandingListAdapter.setmHightlight(false);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.league_standing_fragment, container, false);
        ButterKnife.bind(this, view);

        mLoadingView = inflater.inflate(R.layout.loading_overlay, container, false);
        mStandingListView.addHeaderView(inflater.inflate(R.layout.league_standing_header_item, null, false));
        mStandingListView.setAdapter(mLeagueStandingListAdapter);

        try {
            Competition competition = Competition.parseCompetition(mGameTicket.getCompetition());
            mLeagueStandingPresenter.loadStanding(competition);
        } catch(Exception e){
            Crashlytics.logException(e);
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onLoadStanding(List<LeagueStanding> leagueStandingList) {
        mLeagueStandingList.clear();
        mLeagueStandingList.addAll(leagueStandingList);
        mLeagueStandingListAdapter.notifyDataSetChanged();

        if (isAdded()){
            if (mLeagueStandingList.isEmpty()) {
                View emptyView = EmptyView.withDrawable(getActivity(),
                        getString(EmptyView.EMPTY_LEAGUE_STANDING),
                        ContextCompat.getDrawable(getActivity(), R.drawable.ic_podium));
                mFrameLayout.addView(emptyView);
            }
        }
    }

    @Override
    public void showContentLoading() {
        if (isAdded()) {
            if (mLoadingView != null && mFrameLayout != null) {
                mFrameLayout.addView(mLoadingView);
            }
        }
    }

    @Override
    public void hideContentLoading() {
        if (isAdded()) {
            if (mLoadingView != null && mFrameLayout != null) {
                mFrameLayout.removeView(mLoadingView);
            }
        }
    }

    @Override
    public void showContentError(ApiError error) {
        mLeagueStandingList.clear();
        mLeagueStandingListAdapter.notifyDataSetChanged();
        if (isAdded()) {
            try {

            } catch (Exception e){
                Crashlytics.logException(e);
            }

            if (mErrorView != null){
                if (mErrorView.getParent() != null) return;
            }

            mErrorView = EmptyView.withDrawable(getActivity(),
                    error.getMessage(),
                    ContextCompat.getDrawable(getActivity(), R.drawable.global_error));
            mFrameLayout.addView(mErrorView);
        }
    }


    @Override
    public void hideContentError() {
        if (isAdded()){
            if (mErrorView != null && mFrameLayout != null){
                try {
                    mFrameLayout.removeView(mErrorView);
                } catch (Exception e){
                    Crashlytics.logException(e);
                }
            }
        }
    }
}
