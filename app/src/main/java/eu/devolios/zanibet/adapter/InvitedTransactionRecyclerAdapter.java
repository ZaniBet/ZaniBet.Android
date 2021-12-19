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

import com.github.thunder413.datetimeutils.DateTimeUtils;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.joanzapata.iconify.fonts.MaterialIcons;

import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.devolios.zanibet.R;
import eu.devolios.zanibet.model.Competition;
import eu.devolios.zanibet.model.Grille;
import eu.devolios.zanibet.model.Help;
import eu.devolios.zanibet.model.Transaction;
import eu.devolios.zanibet.model.User;
import eu.devolios.zanibet.utils.DateFormatter;
import eu.devolios.zanibet.utils.EmptyView;

/**
 * Created by Gromat Luidgi on 11/03/2018.
 */

public class InvitedTransactionRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    private final static int EMPTY_TYPE = 0;
    private final static int INVITED_STATS_TYPE = 1;
    private final static int TRANSACTION_TYPE = 2;


    private Context mContext;
    private ArrayList<Transaction> mTransactionList;
    //private GrilleRecyclerAdapter.Listener mListener;

    public InvitedTransactionRecyclerAdapter(Context context, ArrayList<Transaction> transactionList){
        mContext = context;
        mTransactionList = transactionList;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return INVITED_STATS_TYPE;
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
                return new InvitedTransactionRecyclerAdapter.EmptyViewHolder(inflatedView);
            case INVITED_STATS_TYPE:
                inflatedView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.invite_stats_item, parent, false);
                return new InvitedTransactionRecyclerAdapter.StatsViewHolder(inflatedView);
            case TRANSACTION_TYPE:
                inflatedView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.invite_transaction_item, parent, false);
                return new InvitedTransactionRecyclerAdapter.TransactionViewHolder(inflatedView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof InvitedTransactionRecyclerAdapter.StatsViewHolder) {
            ((InvitedTransactionRecyclerAdapter.StatsViewHolder) holder).bindStats(mContext);
        } else if (holder instanceof InvitedTransactionRecyclerAdapter.TransactionViewHolder) {
            Transaction transaction = mTransactionList.get(position-1);
            ((InvitedTransactionRecyclerAdapter.TransactionViewHolder) holder).bindTransaction(mContext, transaction);
        } else if (holder instanceof InvitedTransactionRecyclerAdapter.EmptyViewHolder) {
            ((InvitedTransactionRecyclerAdapter.EmptyViewHolder) holder).bindEmpty(mContext);
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

        @BindView(R.id.amountInvitedTextView)
        TextView mAmountInvitedTextView;
        @BindView(R.id.amountTransactionTextView)
        TextView mAmountTransactionTextView;
        @BindView(R.id.totalEarningTextView)
        TextView mTotalEarningTextView;

        private Context mContext;

        public StatsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        public void bindStats(Context context){
            mContext = context;
            mAmountInvitedTextView.setText(Html.fromHtml(context.getString(R.string.amount_invited,
                    User.currentUser().getReferral().getTotalReferred())));
            mAmountTransactionTextView.setText(Html.fromHtml(context.getString(R.string.amount_transaction,
                    User.currentUser().getReferral().getTotalTransaction())));
            mTotalEarningTextView.setText(Html.fromHtml(context.getString(R.string.invitation_earning,
                    User.currentUser().getReferral().getTotalCoin())));
        }

        @Override
        public void onClick(View view) {
        }
    }

    static class TransactionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        //@BindView(R.id.thumbnailImageView)
        //ImageView mThumbnailImageView;
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
            mDescriptionTextView.setText(transaction.getDescription());
            mZanicoinTextView.setText(context.getString(R.string.transaction_reward_zc, transaction.getAmount()));
            mUsernameTextView.setText(transaction.getSourceRef());
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
            mMessageTextView.setText(Html.fromHtml(context.getString(R.string.empty_referral_transaction)));
            mIconView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_zanicoin));
        }
    }
}
