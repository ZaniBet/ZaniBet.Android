package eu.devolios.zanibet.fragment;

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

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.devolios.zanibet.R;
import eu.devolios.zanibet.adapter.TransactionRecyclerAdapter;
import eu.devolios.zanibet.adapter.ZaniHashStatsRecyclerAdapter;
import eu.devolios.zanibet.model.ApiError;
import eu.devolios.zanibet.model.Transaction;
import eu.devolios.zanibet.presenter.TransactionPresenter;
import eu.devolios.zanibet.presenter.ZaniHashStatsPresenter;
import eu.devolios.zanibet.presenter.contract.TransactionContract;
import eu.devolios.zanibet.presenter.contract.ZaniHashStatsContract;
import eu.devolios.zanibet.utils.EmptyView;

public class TransactionFragment extends BaseFragment implements TransactionContract.View {

    @BindView(R.id.frameLayout)
    FrameLayout mFrameLayout;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    private TransactionPresenter mTransactionPresenter;
    private TransactionRecyclerAdapter mTransactionRecyclerAdapter;
    private ArrayList<Transaction> mTransactionArrayList;

    private View mLoadingView;
    private View mEmptyView;
    private View mErrorView;

    public static TransactionFragment newInstance(){
        TransactionFragment transactionFragment = new TransactionFragment();
        return transactionFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTransactionPresenter = new TransactionPresenter(getActivity(), this);
        mTransactionArrayList = new ArrayList<>();
        mTransactionRecyclerAdapter = new TransactionRecyclerAdapter(getActivity(), mTransactionArrayList);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.transaction_fragment, container, false);
        ButterKnife.bind(this, view);

        mLoadingView = inflater.inflate(R.layout.loading_overlay, container, false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mTransactionRecyclerAdapter);

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onLoadTransactions(List<Transaction> transactionList) {
        mTransactionArrayList.clear();
        mTransactionArrayList.addAll(transactionList);
        mTransactionRecyclerAdapter.notifyDataSetChanged();
    }


    @Override
    public void showContentError(ApiError apiError) {
        mTransactionArrayList.clear();
        mTransactionRecyclerAdapter.notifyDataSetChanged();

        if (isAdded()) {
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
    public void showContentLoading() {
        if (isAdded()) {
            try {
                if(mLoadingView != null && mFrameLayout != null) {
                    if (mLoadingView.getParent() != null) mFrameLayout.removeView(mLoadingView);
                }

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
                if(mLoadingView != null && mFrameLayout != null) {
                    if (mLoadingView.getParent() != null) mFrameLayout.removeView(mLoadingView);
                }
            } catch (Exception e){
                Crashlytics.logException(e);
            }
        }
    }

}
