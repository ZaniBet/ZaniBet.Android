package eu.devolios.zanibet.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import eu.devolios.zanibet.PayoutGrilleDetailsActivity;
import eu.devolios.zanibet.PayoutRewardDetailsActivity;
import eu.devolios.zanibet.utils.EmptyView;
import eu.devolios.zanibet.MainActivity;
import eu.devolios.zanibet.R;
import eu.devolios.zanibet.adapter.PayoutRecyclerAdapter;
import eu.devolios.zanibet.model.ApiError;
import eu.devolios.zanibet.model.Payout;
import eu.devolios.zanibet.presenter.contract.PayoutContract;
import eu.devolios.zanibet.presenter.PayoutPresenter;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.joanzapata.iconify.widget.IconTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Gromat Luidgi on 09/11/2017.
 */

public class PayoutFragment extends BaseFragment implements PayoutContract.View, PayoutRecyclerAdapter.Listener {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.frameLayout)
    FrameLayout mFrameLayout;

    private PayoutPresenter mPayoutPresenter;
    private PayoutRecyclerAdapter mPayoutRecyclerAdapter;
    private ArrayList<Payout> mPayoutArrayList;
    private FirebaseAnalytics mFirebaseAnalytics;

    private View mErrorView;
    private View mLoadingView;

    public static PayoutFragment newInstance(){
        PayoutFragment payoutFragment = new PayoutFragment();
        return payoutFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
        mPayoutPresenter = new PayoutPresenter(getActivity(), this);
        mPayoutArrayList = new ArrayList<Payout>();
        mPayoutRecyclerAdapter = new PayoutRecyclerAdapter(getActivity(), this, mPayoutArrayList);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.payout_fragment, container, false);
        ButterKnife.bind(this, view);
        ((MainActivity)getActivity()).updateTitle(getString(R.string.title_payout_history));
        mFirebaseAnalytics.setCurrentScreen(getActivity(), "Historique Paiements", "PayoutFragment");

        mLoadingView = inflater.inflate(R.layout.loading_overlay, container, false);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(Objects.requireNonNull(getActivity()),
                linearLayoutManager.getOrientation());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mPayoutRecyclerAdapter);
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mPayoutPresenter.load();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mPayoutArrayList.isEmpty()){
            mPayoutPresenter.load();
        }
    }

    @Override
    public void addPayouts(List<Payout> payoutList) {
        mPayoutArrayList.clear();
        mPayoutArrayList.addAll(payoutList);
        mPayoutRecyclerAdapter.notifyDataSetChanged();

        if (isAdded()) {
            if (mPayoutArrayList.size() == 0) {
                View emptyView = EmptyView.withDrawable(getActivity(), getActivity().getString(EmptyView.EMPTY_PAYOUT),
                        ContextCompat.getDrawable(getActivity(), R.drawable.empty_payout));
                mFrameLayout.addView(emptyView);
            }
        }
    }

    @Override
    public void showContentLoading() {
        if (isAdded()) {
            try {
                if (mLoadingView != null && mFrameLayout != null) {
                    if (mLoadingView.getParent() == null) mFrameLayout.addView(mLoadingView);
                }
            } catch (Exception e){
                Crashlytics.logException(e);
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
        if (isAdded()) {
            try {
                ((MainActivity) Objects.requireNonNull(getActivity())).checkLoggedIn();
            } catch (Exception e){
                Crashlytics.logException(e);
            }
            View errorView = EmptyView.withDrawable(getActivity(),
                    apiError.getMessage(),
                    ContextCompat.getDrawable(getActivity(), R.drawable.global_error));
            mFrameLayout.addView(errorView);
        }
    }

    @Override
    public void payoutSelected(Payout payout) {
        if (payout.getKind().equals("Reward") && payout.getInvoice() != null){
            Intent intent = new Intent(getActivity(), PayoutRewardDetailsActivity.class);
            intent.putExtra("payout", payout);
            startActivity(intent);
        } else if (payout.getKind().equals("Grille") && payout.getInvoice() != null){
            Intent intent = new Intent(getActivity(), PayoutGrilleDetailsActivity.class);
            intent.putExtra("payout", payout);
            startActivity(intent);
        }
    }
}
