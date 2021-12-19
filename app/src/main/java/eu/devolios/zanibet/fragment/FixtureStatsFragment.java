package eu.devolios.zanibet.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.gms.common.api.Api;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.devolios.zanibet.R;
import eu.devolios.zanibet.adapter.FixtureStatsRecyclerAdapter;
import eu.devolios.zanibet.fragment.pager.GrilleGroupPagerFragment;
import eu.devolios.zanibet.model.ApiError;
import eu.devolios.zanibet.model.Fixture;
import eu.devolios.zanibet.model.GameTicket;
import eu.devolios.zanibet.presenter.FixtureStatsPresenter;
import eu.devolios.zanibet.presenter.contract.FixtureStatsContract;
import eu.devolios.zanibet.utils.EmptyView;

/**
 * Created by Gromat Luidgi on 27/03/2018.
 */

public class FixtureStatsFragment extends Fragment implements FixtureStatsContract.View {

    private final static int FULL_SPAN = 2;
    private final static int SINGLE_SPAN = 1;

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.frameLayout)
    FrameLayout mFrameLayout;

    private View mErrorView;
    private View mLoadingView;
    private View mEmptyView;

    private FixtureStatsPresenter mFixtureStatsPresenter;
    private GameTicket mGameTicket;
    private FixtureStatsRecyclerAdapter mFixtureStatsRecyclerAdapter;
    private ArrayList<Fixture> mLastHomeTeamFixture;
    private ArrayList<Fixture> mLastAwayTeamFixture;

    public static FixtureStatsFragment newInstance(GameTicket gameTicket) {
        FixtureStatsFragment fixtureStatsFragment = new FixtureStatsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("gameticket", gameTicket);
        fixtureStatsFragment.setArguments(bundle);
        return fixtureStatsFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFixtureStatsPresenter = new FixtureStatsPresenter(getActivity(), this);
        mGameTicket = (GameTicket) getArguments().getSerializable("gameticket");
        Fixture currentFixture = mGameTicket.getFixtures()[0];
        mLastHomeTeamFixture = new ArrayList<>();
        mLastAwayTeamFixture = new ArrayList<>();
        mFixtureStatsRecyclerAdapter = new FixtureStatsRecyclerAdapter(getActivity(), mGameTicket, currentFixture, mLastHomeTeamFixture, mLastAwayTeamFixture);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fixture_stats_fragment, container, false);
        ButterKnife.bind(this, view);

        mLoadingView = inflater.inflate(R.layout.loading_overlay, container, false);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), FULL_SPAN);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {

                switch (position) {
                    case 0:
                        return FULL_SPAN;
                    case 1:
                        return SINGLE_SPAN;
                    case 2:
                        return SINGLE_SPAN;
                    case 3:
                        return FULL_SPAN;
                    case 4:
                        return FULL_SPAN;
                    default:
                        return FULL_SPAN;
                }

            }
        });

        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setAdapter(mFixtureStatsRecyclerAdapter);
        mFixtureStatsPresenter.loadStats(mGameTicket.getFixtures()[0].getId());
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onLoadStats(ArrayList<Fixture> lastHomeFixtures, ArrayList<Fixture> lastAwayFixtures) {
        mLastHomeTeamFixture.clear();
        mLastAwayTeamFixture.clear();

        mLastHomeTeamFixture.addAll(lastHomeFixtures);
        mLastAwayTeamFixture.addAll(lastAwayFixtures);
        mFixtureStatsRecyclerAdapter.notifyDataSetChanged();

        if (isAdded()) {
            if (mLastAwayTeamFixture.isEmpty() || mLastHomeTeamFixture.isEmpty()) {
                if (mEmptyView != null && mFrameLayout != null){
                    if (mEmptyView.getParent() != null) mFrameLayout.removeView(mEmptyView);
                }

                mEmptyView = EmptyView.withDrawable(getActivity(),
                        getString(EmptyView.EMPTY_FIXTURE_STATS),
                        ContextCompat.getDrawable(getActivity(), R.drawable.ic_scoreboard));
                mFrameLayout.addView(mEmptyView);
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
                if (mLoadingView.getParent() != null) mFrameLayout.removeView(mLoadingView);
            }
        }
    }


    @Override
    public void showContentError(ApiError apiError) {

    }

    @Override
    public void hideContentError() {

    }
}
