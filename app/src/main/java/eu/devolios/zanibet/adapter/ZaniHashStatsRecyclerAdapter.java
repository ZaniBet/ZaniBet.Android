package eu.devolios.zanibet.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.github.thunder413.datetimeutils.DateTimeUtils;

import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.devolios.zanibet.R;
import eu.devolios.zanibet.model.Transaction;
import eu.devolios.zanibet.model.User;
import eu.devolios.zanibet.utils.DateFormatter;

/**
 * Created by Gromat Luidgi on 11/03/2018.
 */

public class ZaniHashStatsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    private final static int EMPTY_TYPE = 0;
    private final static int ZANIHASH_STATS_TYPE = 1;
    private final static int TRANSACTION_TYPE = 2;


    private Context mContext;
    private ArrayList<Transaction> mTransactionList;
    //private GrilleRecyclerAdapter.Listener mListener;

    public ZaniHashStatsRecyclerAdapter(Context context, ArrayList<Transaction> transactionList){
        mContext = context;
        mTransactionList = transactionList;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return ZANIHASH_STATS_TYPE;
        }

        if (mTransactionList.isEmpty() && position == 1){
            return EMPTY_TYPE;
        }

        return TRANSACTION_TYPE;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView;
        switch (viewType){
            case EMPTY_TYPE:
                inflatedView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.empty_view_drawable, parent, false);
                return new ZaniHashStatsRecyclerAdapter.EmptyViewHolder(inflatedView);
            case ZANIHASH_STATS_TYPE:
                inflatedView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.zanihash_stats_item, parent, false);
                return new ZaniHashStatsRecyclerAdapter.StatsViewHolder(inflatedView);
            case TRANSACTION_TYPE:
                inflatedView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.zanihash_transaction_item, parent, false);
                return new ZaniHashStatsRecyclerAdapter.TransactionViewHolder(inflatedView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ZaniHashStatsRecyclerAdapter.StatsViewHolder) {
            ((ZaniHashStatsRecyclerAdapter.StatsViewHolder) holder).bindStats(mContext);
        } else if (holder instanceof ZaniHashStatsRecyclerAdapter.TransactionViewHolder) {
            Transaction transaction = mTransactionList.get(position-1);
            ((ZaniHashStatsRecyclerAdapter.TransactionViewHolder) holder).bindTransaction(mContext, transaction);
        } else if (holder instanceof ZaniHashStatsRecyclerAdapter.EmptyViewHolder) {
            ((ZaniHashStatsRecyclerAdapter.EmptyViewHolder) holder).bindEmpty(mContext);
        }
    }

    @Override
    public int getItemCount() {
        if (mTransactionList.isEmpty()){
            return 2;
        }

        return mTransactionList.size()+1;
    }

    static class StatsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.zanihashTextView)
        TextView mZaniHashTextView;
        @BindView(R.id.totalZanihashTextView)
        TextView mTotalZaniHashTextView;


        public StatsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        public void bindStats(Context context){
            try {
                mZaniHashTextView.setText(Html.fromHtml(context.getString(R.string.zanihash_wallet,
                        User.currentUser().getWallet().getZaniHash())));
                mTotalZaniHashTextView.setText(Html.fromHtml(context.getString(R.string.zanihash_since_registration,
                        User.currentUser().getWallet().getTotalZaniHash())));
            } catch (Exception e){
                Crashlytics.logException(e);
            }
        }

        @Override
        public void onClick(View view) {
        }
    }

    static class TransactionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.referenceImageView)
        ImageView mReferenceImageView;
        @BindView(R.id.dateTextView)
        TextView mDateTextView;
        @BindView(R.id.descriptionTextView)
        TextView mDescriptionTextView;
        @BindView(R.id.usernameTextView)
        TextView mUsernameTextView;
        @BindView(R.id.zanicoinTextView)
        TextView mZanicoinTextView;

        private Context mContext;
        private Transaction mTransaction;

        public TransactionViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        public void bindTransaction(Context context, Transaction transaction){
            mContext = context;
            mTransaction = transaction;

            Date createdAt = DateFormatter.formatMongoDate(transaction.getCreatedAt());

            mDateTextView.setText(DateTimeUtils.formatWithPattern(createdAt, "dd MMM yyyy HH:mm:ss") + " UTC");
            mZanicoinTextView.setText(context.getString(R.string.transaction_reward_zh, transaction.getAmount()));

            if (transaction.getSourceKind().equals("ZaniAnalytics")) {
                mDescriptionTextView.setText(mContext.getString(R.string.analytics_reward));
                mUsernameTextView.setText(transaction.getSourceRef());
                mReferenceImageView.setImageResource(R.drawable.ic_mine);
            } else if (transaction.getSourceKind().equals("Reward")) {
                mDescriptionTextView.setText(mContext.getString(R.string.reward_buy));
                mUsernameTextView.setText(transaction.getDescription());
                mReferenceImageView.setImageResource(R.drawable.ic_zani_chips);
            } else {
                mDescriptionTextView.setText(transaction.getDescription());
                mUsernameTextView.setText(transaction.getSourceRef());
            }
        }

        @Override
        public void onClick(View view) {
        }
    }

    static class EmptyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.imageView)
        ImageView mIconView;
        @BindView(R.id.message_textview)
        TextView mMessageTextView;

        public EmptyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindEmpty(Context context){
            mMessageTextView.setText(Html.fromHtml(context.getString(R.string.empty_zanihash_transaction)));
            mIconView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_zanihash_coin));
        }
    }
}
