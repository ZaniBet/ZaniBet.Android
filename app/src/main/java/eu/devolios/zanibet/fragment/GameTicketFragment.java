package eu.devolios.zanibet.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.cleveroad.pulltorefresh.firework.FireworkyPullToRefreshLayout;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.devolios.zanibet.GameTicketCalendarActivity;
import eu.devolios.zanibet.GrilleMultiPlayPagerActivity;
import eu.devolios.zanibet.MainActivity;
import eu.devolios.zanibet.R;
import eu.devolios.zanibet.adapter.GameTicketRecyclerAdapter;
import eu.devolios.zanibet.model.ApiError;
import eu.devolios.zanibet.model.GameTicket;
import eu.devolios.zanibet.presenter.GameTicketPresenter;
import eu.devolios.zanibet.presenter.contract.GameTicketContract;
import eu.devolios.zanibet.utils.EmptyView;
import ru.alexbykov.nopaginate.paginate.Paginate;
import ru.alexbykov.nopaginate.paginate.PaginateBuilder;

/**
 * Created by Gromat Luidgi on 09/11/2017.
 */

public class GameTicketFragment extends BaseFragment implements GameTicketContract.View, GameTicketRecyclerAdapter.Listener {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.frameLayout)
    FrameLayout mFrameLayout;
    @BindView(R.id.pullToRefresh)
    FireworkyPullToRefreshLayout mPullToRefresh;

    public static boolean FORCE_REFRESH = false;

    private View mErrorView;
    private View mEmptyView;
    private View mLoadingView;

    private GameTicketPresenter mGameTicketPresenter;
    private GameTicketRecyclerAdapter mGameTicketRecyclerAdapter;
    private ArrayList<GameTicket> mGameTicketArrayList;

    private Paginate mPaginate;

    public static GameTicketFragment newInstance() {
        return new GameTicketFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGameTicketPresenter = new GameTicketPresenter(getActivity(), this);
        mGameTicketArrayList = new ArrayList<>();
        mGameTicketRecyclerAdapter = new GameTicketRecyclerAdapter(getActivity(), mGameTicketArrayList, this);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            Crashlytics.setString("last_ui_viewed", "GameTicketFragment");
            if (mZaniBetTracking != null) {
                try {
                    mZaniBetTracking.setCurrentScreen("Jouer", "GameTicketFragment");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.game_ticket_fragment, container, false);
        ButterKnife.bind(this, view);
        mLoadingView = inflater.inflate(R.layout.loading_overlay, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mGameTicketRecyclerAdapter);

        mPullToRefresh.setOnRefreshListener(() -> mPullToRefresh.post(() -> {
            mGameTicketPresenter.refresh();
            mPullToRefresh.setRefreshing(true);
        }));

        // Activer la pagination
        mPaginate = new PaginateBuilder()
                .with(mRecyclerView)
                .setOnLoadMoreListener(() -> {
                    if (isAdded() && isVisible()) {
                        if (mGameTicketArrayList.size() > 0) mGameTicketPresenter.loadMore();
                    }
                })
                .build();

        if (mGameTicketArrayList.isEmpty() || mGameTicketPresenter.shouldForceRefresh() || FORCE_REFRESH){
            FORCE_REFRESH = false;
            mGameTicketPresenter.refresh();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            if (mPaginate != null) mPaginate.unbind();
            if (mPullToRefresh!= null) {
                mPullToRefresh.setRefreshing(false);
                mPullToRefresh.destroyDrawingCache();
                mPullToRefresh.clearAnimation();
            }
        } catch (Exception e){
            Crashlytics.logException(e);
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        if (mPullToRefresh!= null) {
            mPullToRefresh.setRefreshing(false);
            mPullToRefresh.destroyDrawingCache();
            mPullToRefresh.clearAnimation();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mPullToRefresh!= null) {
            mPullToRefresh.setRefreshing(false);
            mPullToRefresh.destroyDrawingCache();
            mPullToRefresh.clearAnimation();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mGameTicketPresenter.refresh();
    }

    @Override
    public void addTickets(List<GameTicket> gameTickets) {
        mGameTicketArrayList.clear();
        mGameTicketArrayList.addAll(gameTickets);
        mGameTicketRecyclerAdapter.notifyDataSetChanged();

        if (isAdded()) {
            if (mGameTicketArrayList.size() == 0) {
                if (mEmptyView != null && mFrameLayout != null){
                    if (mEmptyView.getParent() != null) mFrameLayout.removeView(mEmptyView);
                }

                mEmptyView = EmptyView.withDrawable(getActivity(),
                        Objects.requireNonNull(getActivity()).getString(EmptyView.EMPTY_GAMETICKET),
                        ContextCompat.getDrawable(getActivity(), R.drawable.empty_gameticket));
                mFrameLayout.addView(mEmptyView);
            }
        }
    }

    @Override
    public void showContentLoading() {
        if (isAdded()) {
            try {
                if(mLoadingView != null && mLoadingView.getParent() != null)
                    ((ViewGroup)mLoadingView.getParent()).removeView(mLoadingView);

                if (mLoadingView != null && mFrameLayout != null) {
                    mFrameLayout.addView(mLoadingView);
                }
            } catch(Exception e){
                Crashlytics.logException(e);
            }
        }
    }

    @Override
    public void hideContentLoading() {
        if (isAdded()) {
            try {
                if(mLoadingView != null && mLoadingView.getParent() == null)
                    return;
                if (mLoadingView != null && mFrameLayout != null) {
                    mFrameLayout.removeView(mLoadingView);
                }
            } catch (Exception e){
                Crashlytics.logException(e);
            }
        }
    }

    @Override
    public void showContentError(ApiError apiError) {
        mGameTicketArrayList.clear();
        mGameTicketRecyclerAdapter.notifyDataSetChanged();

        if (isAdded()) {
            try {
                ((MainActivity) Objects.requireNonNull(getActivity())).checkLoggedIn();
            } catch (Exception e){
                Crashlytics.logException(e);
            }

            if (mErrorView != null && mFrameLayout != null){
                if (mErrorView.getParent() != null) mFrameLayout.removeView(mErrorView);
            }

            mErrorView = EmptyView.withDrawable(getActivity(),
                    apiError.getMessage(),
                    ContextCompat.getDrawable(getActivity(),
                            R.drawable.global_error));
            mFrameLayout.addView(mErrorView);
        }
    }


    @Override
    public void hideContentError() {
        if (isAdded()){
            if (mErrorView != null && mFrameLayout != null){
                if (mErrorView.getParent() != null) mFrameLayout.removeView(mErrorView);
            }
        }
    }

    @Override
    public void onRefresh() {
        if (mPullToRefresh != null && mPullToRefresh.isRefreshing()) {
            mPullToRefresh.postDelayed(() -> mPullToRefresh.setRefreshing(false), 400);
        }

    }

    @Override
    public void setPaginateLoading(boolean value) {
        if (isAdded()) {
            mPaginate.showLoading(value);
        }
    }

    @Override
    public void setNoPaginate(boolean value) {
        if (isAdded()) {
            mPaginate.setNoMoreItems(value);
        }
    }

    @Override
    public void gameTicketSelected(GameTicket gameTicket) {
        if (mFragmentNavigation != null){
            logTicketSelectedEvent(gameTicket);
            GrilleMultiPlayPagerActivity.mGameTicket = gameTicket;
            Intent intent = new Intent(getActivity(), GrilleMultiPlayPagerActivity.class);
            startActivityForResult(intent, 10);
        }
    }

    @Override
    public void showCalendar(GameTicket gameTicket) {
        if (isAdded()){
            GameTicketCalendarActivity.mGameTicket = gameTicket;
            Intent intent = new Intent(getActivity(), GameTicketCalendarActivity.class);
            startActivity(intent);
        }
    }

    private void logTicketSelectedEvent(GameTicket gameTicket){
        if (mZaniBetTracking != null){
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, gameTicket.getName() + " J" + gameTicket.getMatchDay());
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, gameTicket.getId());
            mZaniBetTracking.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        }
    }
}
