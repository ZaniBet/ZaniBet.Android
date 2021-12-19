package eu.devolios.zanibet.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.devolios.zanibet.R;
import eu.devolios.zanibet.adapter.InvitedTransactionRecyclerAdapter;
import eu.devolios.zanibet.adapter.PayoutRecyclerAdapter;
import eu.devolios.zanibet.model.Payout;
import eu.devolios.zanibet.model.Transaction;
import eu.devolios.zanibet.presenter.InvitedFriendsPresenter;
import eu.devolios.zanibet.presenter.PayoutPresenter;
import eu.devolios.zanibet.presenter.contract.InvitedFriendsContract;

/**
 * Created by Gromat Luidgi on 11/03/2018.
 */

public class InvitedFriendsFragment extends BaseFragment implements InvitedFriendsContract.View {

    @BindView(R.id.frameLayout)
    FrameLayout mFrameLayout;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    private View mLoadingView;

    private InvitedFriendsPresenter mInvitedFriendsPresenter;
    private InvitedTransactionRecyclerAdapter mInvitedTransactionRecyclerAdapter;
    private ArrayList<Transaction> mTransactionArrayList;

    public static InvitedFriendsFragment newInstance(){
        InvitedFriendsFragment invitedFriendsFragment = new InvitedFriendsFragment();
        return invitedFriendsFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInvitedFriendsPresenter = new InvitedFriendsPresenter(getActivity(), this);
        mTransactionArrayList = new ArrayList<>();
        mInvitedTransactionRecyclerAdapter = new InvitedTransactionRecyclerAdapter(getActivity(), mTransactionArrayList);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.invited_friends_fragment, container, false);
        ButterKnife.bind(this, view);

        mLoadingView = inflater.inflate(R.layout.loading_overlay, container, false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mInvitedTransactionRecyclerAdapter);
        mInvitedTransactionRecyclerAdapter.notifyDataSetChanged();
        mInvitedFriendsPresenter.getTransactions();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onLoadTransactions(List<Transaction> transactionList) {
        if (!transactionList.isEmpty()){
            mTransactionArrayList.clear();
            mTransactionArrayList.addAll(transactionList);
            mInvitedTransactionRecyclerAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void showContentError() {

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
}
