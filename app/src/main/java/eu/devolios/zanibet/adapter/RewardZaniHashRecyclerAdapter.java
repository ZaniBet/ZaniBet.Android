package eu.devolios.zanibet.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.devolios.zanibet.R;
import eu.devolios.zanibet.model.Reward;

/**
 * Created by Gromat Luidgi on 10/11/2017.
 */

public class RewardZaniHashRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private ArrayList<Reward> mRewardArrayList;
    private Listener mListener;

    public RewardZaniHashRecyclerAdapter(Context context, ArrayList<Reward> rewardList, Listener listener){
        mContext = context;
        mRewardArrayList = rewardList;
        mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.reward_item, parent, false);
        return new RewardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Reward reward = mRewardArrayList.get(position);
        ((RewardViewHolder) holder).bindReward(reward,mListener, mContext);
    }

    @Override
    public int getItemCount() {
        return mRewardArrayList.size();
    }


    static class RewardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.rewardLayout)
        LinearLayout mRewardLayout;
        @BindView(R.id.rewardAmountTextView)
        TextView mRewardAmountTextView;
        @BindView(R.id.rewardPriceTextView)
        TextView mRewardPriceTextView;
        @BindView(R.id.rewardImageView)
        ImageView mRewardImageView;

        private Reward mReward;
        private Listener mListener;

        public RewardViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        public void bindReward(Reward reward, Listener listener, Context context){
            mReward = reward;
            mListener = listener;

            if (reward.getName().contains("ZaniCoin")){
                mRewardImageView.setImageResource(R.drawable.ic_zanicoin);
            } else {
                mRewardImageView.setImageResource(R.drawable.ic_zani_chips);
            }

            mRewardAmountTextView.setText(context.getString(R.string.shop_reward_zanihash, reward.getValue(), mReward.getName()
                    .replace("Jetons", context.getString(R.string.jetons))));
            mRewardPriceTextView.setText(context.getString(R.string.shop_reward_zh, mReward.getPrice()));
        }

        @Override
        public void onClick(View view) {
            mListener.onRewardSelected(mReward);
        }
    }

    public interface Listener{
        void onRewardSelected(Reward reward);
    }
}
