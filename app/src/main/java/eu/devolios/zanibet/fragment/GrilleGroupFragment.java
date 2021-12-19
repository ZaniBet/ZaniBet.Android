package eu.devolios.zanibet.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.cleveroad.pulltorefresh.firework.FireworkyPullToRefreshLayout;
import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.devolios.zanibet.GrilleSinglePlayPagerActivity;
import eu.devolios.zanibet.GrilleTournamentPlayPagerActivity;
import eu.devolios.zanibet.MainActivity;
import eu.devolios.zanibet.R;
import eu.devolios.zanibet.adapter.GrilleGroupRecyclerAdapter;
import eu.devolios.zanibet.fragment.pager.GrilleGroupPagerFragment;
import eu.devolios.zanibet.model.ApiError;
import eu.devolios.zanibet.model.GameTicket;
import eu.devolios.zanibet.model.GrilleGroup;
import eu.devolios.zanibet.presenter.GrilleGroupPresenter;
import eu.devolios.zanibet.presenter.contract.GrilleGroupContract;
import eu.devolios.zanibet.utils.EmptyView;
import ru.alexbykov.nopaginate.paginate.Paginate;
import ru.alexbykov.nopaginate.paginate.PaginateBuilder;

/**
 * Created by Gromat Luidgi on 10/11/2017.
 */

public class GrilleGroupFragment extends BaseFragment implements GrilleGroupContract.View, GrilleGroupRecyclerAdapter.Listener {

    @BindView(R.id.frameLayout)
    FrameLayout mFrameLayout;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;
    @BindView(R.id.pullToRefresh)
    FireworkyPullToRefreshLayout mPullToRefresh;
    @BindView(R.id.fab)
    FloatingActionButton mFloatingActionButton;

    private GrilleGroupRecyclerAdapter mGrilleRecyclerAdapter;
    private GrilleGroupPresenter mGrilleListPresenter;
    private ArrayList<GrilleGroup> mGrilleGroupArrayList;
    private Paginate mPaginate;
    private View mErrorView;
    private View mEmptyView;

    private int mFragmentType;


    public static GrilleGroupFragment newInstance(int fragmentType) {
        GrilleGroupFragment grilleListFragment = new GrilleGroupFragment();
        Bundle args = new Bundle();
        args.putInt("fragmentType", fragmentType);
        grilleListFragment.setArguments(args);
        return grilleListFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assert getArguments() != null;
        mFragmentType = getArguments().getInt("fragmentType");
        String status = "";
        switch (mFragmentType) {
            case GrilleGroupPagerFragment.CURRENT_GRILLE:
                status = "waiting_result";
                break;
            case GrilleGroupPagerFragment.LOOSE_GRILLE:
                status = "loose";
                break;
            case GrilleGroupPagerFragment.WIN_GRILLE:
                status = "win";
                break;
            case GrilleGroupPagerFragment.SIMPLE_GRILLE:
                status = "single";
                break;
            case GrilleGroupPagerFragment.TOURNAMENT_GRILLE:
                status = "tournament";
                break;
        }
        mGrilleListPresenter = new GrilleGroupPresenter(getActivity(), this, status);
        mGrilleGroupArrayList = new ArrayList<>();
        mGrilleRecyclerAdapter = new GrilleGroupRecyclerAdapter(getActivity(), mGrilleGroupArrayList, this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.grille_group_fragment, container, false);
        ButterKnife.bind(this, view);

        switch (mFragmentType) {
            case GrilleGroupPagerFragment.CURRENT_GRILLE:
                mFloatingActionButton.setVisibility(View.GONE);
                break;
            case GrilleGroupPagerFragment.LOOSE_GRILLE:
                mFloatingActionButton.setVisibility(View.GONE);
                break;
            case GrilleGroupPagerFragment.WIN_GRILLE:
                mFloatingActionButton.setVisibility(View.GONE);
                break;
            case GrilleGroupPagerFragment.SIMPLE_GRILLE:
                mFloatingActionButton.setVisibility(View.GONE);
                break;
            case GrilleGroupPagerFragment.TOURNAMENT_GRILLE:
                mFloatingActionButton.setVisibility(View.GONE);
                /*mFloatingActionButton.setOnClickListener(v -> {
                    GrilleTournamentFilterFragment dialogFrag = GrilleTournamentFilterFragment.newInstance();
                    dialogFrag.setParentFab(mFloatingActionButton);
                    dialogFrag.show(getChildFragmentManager(), dialogFrag.getTag());
                });

                mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                        if (dy > 0 ||dy<0 && mFloatingActionButton.isShown())
                            mFloatingActionButton.hide();
                    }

                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                        if (newState == RecyclerView.SCROLL_STATE_IDLE){
                            mFloatingActionButton.show();
                        }
                        super.onScrollStateChanged(recyclerView, newState);
                    }
                });*/
                break;
        }


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Crashlytics.setString("last_ui_viewed", "GrilleGroupFragment");
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(Objects.requireNonNull(getActivity()),
                mLinearLayoutManager.getOrientation());

        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mGrilleRecyclerAdapter);
        mGrilleRecyclerAdapter.notifyDataSetChanged();
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        // Activer la pagination
        mPaginate = new PaginateBuilder()
                .with(mRecyclerView)
                .setOnLoadMoreListener(() -> {
                    if (isAdded() && isVisible() && mGrilleGroupArrayList.size() > 0) {
                        mGrilleListPresenter.loadMore();
                    }
                })
                .build();

        // Activer le pull to refresh
        mPullToRefresh.setOnRefreshListener(() -> mPullToRefresh.post(() -> {
            mGrilleListPresenter.refresh();
            mPullToRefresh.setRefreshing(true);
        }));

        if (mGrilleGroupArrayList.isEmpty()) {
            mGrilleListPresenter.refresh();
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mPullToRefresh != null) {
            mPullToRefresh.setRefreshing(false);
            mPullToRefresh.destroyDrawingCache();
            mPullToRefresh.clearAnimation();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mPullToRefresh != null) {
            mPullToRefresh.setRefreshing(false);
            mPullToRefresh.destroyDrawingCache();
            mPullToRefresh.clearAnimation();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            if (mPaginate != null) mPaginate.unbind();
            if (mPullToRefresh != null) {
                mPullToRefresh.setRefreshing(false);
                mPullToRefresh.destroyDrawingCache();
                mPullToRefresh.clearAnimation();
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    @Override
    public void addGroupGrilles(List<GrilleGroup> grilles) {
        mGrilleGroupArrayList.clear();
        mGrilleGroupArrayList.addAll(grilles);
        mGrilleRecyclerAdapter.notifyDataSetChanged();

        if (grilles.size() == 0) {
            if (isAdded()) {
                mPaginate.setNoMoreItems(true);
                if (mEmptyView != null && mFrameLayout != null) {
                    if (mEmptyView.getParent() != null) mFrameLayout.removeView(mEmptyView);
                }

                switch (mFragmentType) {
                    case GrilleGroupPagerFragment.CURRENT_GRILLE:
                        mEmptyView = EmptyView.withDrawable(getActivity(), getString(EmptyView.EMPTY_GRILLE_WAITING),
                                ContextCompat.getDrawable(Objects.requireNonNull(getActivity()), R.drawable.empty_pending_grid));
                        mFrameLayout.addView(mEmptyView);
                        break;
                    case GrilleGroupPagerFragment.LOOSE_GRILLE:
                        mEmptyView = EmptyView.withDrawable(getActivity(), getString(EmptyView.EMPTY_GRILLE_LOOSE),
                                ContextCompat.getDrawable(Objects.requireNonNull(getActivity()), R.drawable.empty_losing_grid));
                        mFrameLayout.addView(mEmptyView);
                        break;
                    case GrilleGroupPagerFragment.SIMPLE_GRILLE:
                        mEmptyView = EmptyView.withDrawable(getActivity(), getString(EmptyView.EMPTY_GRILLE_SIMPLE),
                                ContextCompat.getDrawable(Objects.requireNonNull(getActivity()), R.drawable.empty_simple_grid));
                        mFrameLayout.addView(mEmptyView);
                        break;
                    case GrilleGroupPagerFragment.WIN_GRILLE:
                        mEmptyView = EmptyView.withDrawable(getActivity(), getString(EmptyView.EMPTY_GRILLE_WIN),
                                ContextCompat.getDrawable(Objects.requireNonNull(getActivity()), R.drawable.empty_winning_grid));
                        mFrameLayout.addView(mEmptyView);
                        break;
                    case GrilleGroupPagerFragment.TOURNAMENT_GRILLE:
                        mEmptyView = EmptyView.withDrawable(getActivity(), getString(EmptyView.EMPTY_GRILLE_TOURNAMENT),
                                ContextCompat.getDrawable(Objects.requireNonNull(getActivity()), R.drawable.ic_trophy2));
                        mFrameLayout.addView(mEmptyView);
                        break;
                }
            }
        }
    }

    @Override
    public void showContentError(ApiError error) {
        mGrilleGroupArrayList.clear();
        mGrilleRecyclerAdapter.notifyDataSetChanged();
        if (isAdded()) {
            try {
                ((MainActivity) Objects.requireNonNull(getActivity())).checkLoggedIn();
            } catch (Exception e) {
                Crashlytics.logException(e);
            }

            if (mErrorView != null) {
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
        if (isAdded()) {
            if (mErrorView != null && mFrameLayout != null) {
                try {
                    mFrameLayout.removeView(mErrorView);
                } catch (Exception e) {
                    Crashlytics.logException(e);
                }
            }
        }
    }

    @Override
    public void togglePaginateLoading(boolean value) {
        if (isAdded()) {
            mPaginate.showLoading(value);
        }
    }

    @Override
    public void disablePaginate() {
        if (isAdded()) {
            mPaginate.setNoMoreItems(true);
        }
    }

    @Override
    public void onRefresh() {
        if (mPullToRefresh != null && mPullToRefresh.isRefreshing()) {
            mPullToRefresh.postDelayed(() -> {
                //mIsRefreshing = false;
                mPaginate.setNoMoreItems(false);
                mPullToRefresh.setRefreshing(false);
            }, 400);
        }

    }

    @Override
    public void onGrilleSelected(GrilleGroup grille) {
        try {
            GameTicket gameTicket = GameTicket.convertFromMap(grille.getGrilles()[0].getGameTicket());

            switch (grille.getGrilles()[0].getType()) {
                case "SIMPLE": {
                    if (gameTicket.getFixtures().length == 0)
                        return;
                    GrilleSinglePlayPagerActivity.mGameTicket = gameTicket;
                    GrilleSinglePlayPagerActivity.mGrille = grille.getGrilles()[0];
                    Intent intent = new Intent(getActivity(), GrilleSinglePlayPagerActivity.class);
                    startActivity(intent);
                    break;
                }
                case "TOURNAMENT": {
                    if (gameTicket.getFixtures().length == 0)
                        return;
                    GrilleTournamentPlayPagerActivity.mGameTicket = gameTicket;
                    GrilleTournamentPlayPagerActivity.mGrille = grille.getGrilles()[0];
                    Intent intent = new Intent(getActivity(), GrilleTournamentPlayPagerActivity.class);
                    startActivity(intent);
                    break;
                }
                default:
                    if (mFragmentNavigation != null)
                        mFragmentNavigation.pushFragment(GrilleListFragment.newInstance(grille.getGrilles()));
                    break;
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    // AAH Fragment Callbacks
    /*@Override
    public void onResult(Object result) {
        Log.d("k9res", "onResult: " + result.toString());
        Log.d("GrilleGroupFragment", "Filter callback !");
    }*/
}
