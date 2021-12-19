package eu.devolios.zanibet.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import eu.devolios.zanibet.R;
import eu.devolios.zanibet.model.Payout;
import eu.devolios.zanibet.utils.DateFormatter;
import com.joanzapata.iconify.widget.IconTextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Gromat Luidgi on 15/11/2017.
 */

public class PayoutRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private Listener mListener;
    private List<Payout> mPayoutList;

    public PayoutRecyclerAdapter(Context context, Listener listener, List<Payout> payoutList){
        mContext = context;
        mListener = listener;
        mPayoutList = payoutList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.payout_item, parent, false);
        return new PayoutViewHolder(inflatedView);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof PayoutViewHolder) {
            Payout payout = mPayoutList.get(position);
            ((PayoutViewHolder) holder).bindPayout(mContext, mListener, payout);
        }
    }

    @Override
    public int getItemCount() {
        return mPayoutList.size();
    }

    static class PayoutViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.iconImageView)
        ImageView mIconImageView;
        @BindView(R.id.payoutTypeTextView)
        TextView mPayoutTypeTextView;
        @BindView(R.id.payoutAmountTextView)
        TextView mPayoutAmountTextView;
        @BindView(R.id.payoutDateTextView)
        TextView mPayoutDateTextView;
        @BindView(R.id.statusTextView)
        TextView mStatusTextView;

        private Context mContext;
        private Payout mPayout;
        private Listener mListener;

        public PayoutViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        public void bindPayout(Context context, Listener listener, Payout payout){
            mContext = context;
            mListener = listener;
            mPayout = payout;

            if (payout.getKind().equals("Reward")){
                //mIconImageView.setImageResource(R.drawable.ico_gift);
                /*if (mPayout.getDescription() == null) {
                    mPayoutTypeTextView.setText(context.getString(R.string.payout_for_reward));
                } else {
                    mPayoutTypeTextView.setText(mPayout.getDescription());
                }*/
                mPayoutTypeTextView.setText(context.getString(R.string.payout_for_reward));
            } else if (payout.getKind().equals("Grille")){
                //mIconImageView.setImageResource(R.drawable.bet_confirmation);
                /*if (mPayout.getDescription() == null) {
                    mPayoutTypeTextView.setText(context.getString(R.string.payout_for_grid));
                } else {
                    mPayoutTypeTextView.setText(mPayout.getDescription().toUpperCase());
                }*/

                mPayoutTypeTextView.setText(context.getString(R.string.payout_for_grid));
            }

            if (payout.getStatus().equals("waiting_paiement")){
                mStatusTextView.setText(context.getString(R.string.status_payout_waiting_paiement));
                mIconImageView.setImageResource(R.drawable.ic_payout_pending);
            } else if (payout.getStatus().equals("paid")){
                mStatusTextView.setText(context.getString(R.string.status_payout_paid));
                mIconImageView.setImageResource(R.drawable.ic_payout_success);
            } else if (payout.getStatus().equals("verification")){
                mStatusTextView.setText(context.getString(R.string.status_payout_verification));
                mIconImageView.setImageResource(R.drawable.ic_payout_verification);
            } else if (payout.getStatus().equals("fraud")){
                mStatusTextView.setText(context.getString(R.string.status_payout_fraud));
                mIconImageView.setImageResource(R.drawable.ic_payout_error);
            } else if (payout.getStatus().equals("missing_data")){
                mStatusTextView.setText(context.getString(R.string.status_payout_missing_data));
                mIconImageView.setImageResource(R.drawable.ic_payout_missing_data);
            } else if (payout.getStatus().equals("canceled")){
                mStatusTextView.setText(context.getString(R.string.status_payout_canceled));
                mIconImageView.setImageResource(R.drawable.ic_payout_error);
            } else if (payout.getStatus().equals("invalid_data")){
                mStatusTextView.setText(context.getString(R.string.status_payout_invalid_data));
                mIconImageView.setImageResource(R.drawable.ic_payout_error);
            } else if (payout.getStatus().equals("invalid_paypal")){
                mStatusTextView.setText(context.getString(R.string.status_payout_invalid_paypal));
                mIconImageView.setImageResource(R.drawable.ic_payout_error);
            } else if (payout.getStatus().equals("invalid_bitcoin")){
                mStatusTextView.setText(context.getString(R.string.status_payout_invalid_bitcoin));
                mIconImageView.setImageResource(R.drawable.ic_payout_error);
            } else {
                mStatusTextView.setText(payout.getStatus());
                mIconImageView.setImageResource(R.drawable.ic_payout_error);
            }

            if (payout.getInvoice() != null) mPayoutAmountTextView.setText(context.getString(R.string.amount_float, payout.getInvoice().getAmount()));
            mPayoutDateTextView.setText(DateFormatter.parseDateForLocal(DateFormatter.formatMongoDate(payout.getCreatedAt())));
        }

        @Override
        public void onClick(View view) {
            mListener.payoutSelected(mPayout);
        }
    }

    public interface Listener{
        void payoutSelected(Payout payout);
    }
}
