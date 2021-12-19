package eu.devolios.zanibet.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.devolios.zanibet.GrilleTournamentPlayPagerActivity;
import eu.devolios.zanibet.MainActivity;
import eu.devolios.zanibet.R;
import eu.devolios.zanibet.adapter.GameTicketRecyclerAdapter;
import eu.devolios.zanibet.analytics.ZaniAnalyticsPagerActivity;
import eu.devolios.zanibet.model.ApiError;
import eu.devolios.zanibet.model.GameTicket;
import eu.devolios.zanibet.presenter.GameTicketTournamentPresenter;
import eu.devolios.zanibet.presenter.contract.GameTicketTournamentContract;
import eu.devolios.zanibet.utils.EmptyView;
import ru.alexbykov.nopaginate.paginate.Paginate;
import ru.alexbykov.nopaginate.paginate.PaginateBuilder;

/**
 * Created by Gromat Luidgi on 09/11/2017.
 */

public class GameTicketTournamentFragment extends BaseFragment implements GameTicketTournamentContract.View, GameTicketRecyclerAdapter.TournamentTicketListener {

    @BindView(R.id.tournamentRecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.main_content)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.fab)
    FloatingActionButton mFloadingButton;

    public static boolean FORCE_REFRESH = false;

    private View mErrorView;
    private View mLoadingView;

    private GameTicketTournamentPresenter mGameTicketPresenter;
    private GameTicketRecyclerAdapter mGameTicketRecyclerAdapter;
    private ArrayList<GameTicket> mGameTicketArrayList;

    private Paginate mPaginate;


    public static GameTicketTournamentFragment newInstance() {
        return new GameTicketTournamentFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGameTicketPresenter = new GameTicketTournamentPresenter(getActivity(), this);
        mGameTicketArrayList = new ArrayList<>();
        mGameTicketRecyclerAdapter = new GameTicketRecyclerAdapter(getActivity(), mGameTicketArrayList, this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.game_ticket_tournament_fragment, container, false);
        ButterKnife.bind(this, view);

        ((MainActivity) Objects.requireNonNull(getActivity())).updateTitle(getActivity().getString(R.string.app_name));

        mLoadingView = inflater.inflate(R.layout.loading_overlay, container, false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mGameTicketRecyclerAdapter);


        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                if (dy > 0 ||dy<0 && mFloadingButton.isShown())
                    mFloadingButton.hide();
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                if (newState == RecyclerView.SCROLL_STATE_IDLE){
                    mFloadingButton.show();
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        mFloadingButton.setOnClickListener(view1 -> mGameTicketPresenter.refresh());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Crashlytics.setString("last_ui_viewed", "GameTicketFragment");

        // Activer la pagination
        mPaginate = new PaginateBuilder()
                .with(mRecyclerView)
                .setOnLoadMoreListener(() -> {
                    if (isAdded() && isVisible()) {
                        if(mGameTicketArrayList.size() > 0)
                            mGameTicketPresenter.loadMore();
                    }
                })
                .build();

        if (mGameTicketArrayList.isEmpty() || FORCE_REFRESH){
            FORCE_REFRESH = false;
            mPaginate.setNoMoreItems(true);
            mGameTicketPresenter.refresh();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            if (mPaginate != null) mPaginate.unbind();
            if (mRecyclerView != null) mRecyclerView.clearOnScrollListeners();
        } catch (Exception e){
            Crashlytics.logException(e);
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            mGameTicketPresenter.loadUser();
            mGameTicketPresenter.refresh();
        }
    }

    @Override
    public void addTickets(List<GameTicket> gameTickets) {
        mGameTicketArrayList.clear();
        mGameTicketArrayList.addAll(gameTickets);
        mGameTicketRecyclerAdapter.setNetworkError(false);
        mGameTicketRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void showContentLoading() {
        if (isAdded()) {
            try {
                if(mLoadingView != null && mLoadingView.getParent() != null)
                    ((ViewGroup)mLoadingView.getParent()).removeView(mLoadingView);

                if (mLoadingView != null && mCoordinatorLayout != null) {
                    mCoordinatorLayout.addView(mLoadingView);
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
                if (mLoadingView != null && mCoordinatorLayout != null) {
                    mCoordinatorLayout.removeView(mLoadingView);
                }
            } catch (Exception e){
                Crashlytics.logException(e);
            }
        }
    }

    @Override
    public void showContentError(ApiError apiError) {
        mGameTicketArrayList.clear();
        mGameTicketRecyclerAdapter.setNetworkError(true);
        mGameTicketRecyclerAdapter.notifyDataSetChanged();

        if (isAdded()) {
            try {
                ((MainActivity) Objects.requireNonNull(getActivity())).checkLoggedIn();
            } catch (Exception e){
                Crashlytics.logException(e);
            }

            if (mErrorView != null && mCoordinatorLayout != null){
                if (mErrorView.getParent() != null) mCoordinatorLayout.removeView(mErrorView);
            }

            mErrorView = EmptyView.withDrawable(getActivity(),
                    apiError.getMessage(),
                    ContextCompat.getDrawable(getActivity(),
                            R.drawable.global_error));
            mCoordinatorLayout.addView(mErrorView);
        }
    }


    @Override
    public void hideContentError() {
        if (isAdded()){
            if (mErrorView != null && mCoordinatorLayout != null){
                mCoordinatorLayout.removeView(mErrorView);
            }
        }
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoadUser() {
        if (isAdded()) {
            if (mGameTicketRecyclerAdapter != null)
                mGameTicketRecyclerAdapter.notifyItemChanged(0);
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
            //logTicketSelectedEvent(gameTicket);
            GrilleTournamentPlayPagerActivity.mGameTicket = gameTicket;
            Intent intent = new Intent(getActivity(), GrilleTournamentPlayPagerActivity.class);
            startActivityForResult(intent, 10);
        }
    }

    @Override
    public void showZaniHashActivity() {
        Intent intent = new Intent(getActivity(), ZaniAnalyticsPagerActivity.class);
        startActivity(intent);
    }

    /*private void logTicketSelectedEvent(GameTicket gameTicket){
        if (gameTicket != null && mFirebaseAnalytics != null){
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, gameTicket.getName() + " J" + gameTicket.getMatchDay());
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, gameTicket.getId());
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        }
    }*/
}
