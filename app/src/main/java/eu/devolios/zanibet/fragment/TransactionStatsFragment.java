package eu.devolios.zanibet.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.devolios.zanibet.R;
import eu.devolios.zanibet.presenter.TransactionPresenter;
import eu.devolios.zanibet.presenter.TransactionStatsPresenter;
import eu.devolios.zanibet.presenter.contract.TransactionStatsContract;

public class TransactionStatsFragment extends BaseFragment implements TransactionStatsContract.View {


    @BindView(R.id.zanicoinTextView)
    TextView mZaniCoinTextView;
    @BindView(R.id.totalZanicoinTextView)
    TextView mTotalZaniCoinTextView;
    @BindView(R.id.zanihashTextView)
    TextView mZaniHashTextView;
    @BindView(R.id.totalZanihashTextView)
    TextView mTotalZaniHashTextView;
    @BindView(R.id.jetonTextView)
    TextView mJetonTextView;

    public static TransactionStatsFragment newInstance(){
        return new TransactionStatsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.transaction_stats_fragment, container, false);
        ButterKnife.bind(this, view);


        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

}
