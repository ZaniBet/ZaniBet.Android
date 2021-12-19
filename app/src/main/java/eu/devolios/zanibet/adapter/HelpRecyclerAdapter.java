package eu.devolios.zanibet.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import eu.devolios.zanibet.R;
import eu.devolios.zanibet.model.Help;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.joanzapata.iconify.fonts.MaterialIcons;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Gromat Luidgi on 15/11/2017.
 */

public class HelpRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private Listener mListener;
    private List<Help> mHelpList;

    public HelpRecyclerAdapter(Context context, Listener listener, List<Help> helpList){
        mContext = context;
        mListener = listener;
        mHelpList = helpList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.help_item, parent, false);
        return new HelpRecyclerAdapter.HelpViewHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HelpRecyclerAdapter.HelpViewHolder) {
            Help help = mHelpList.get(position);
            ((HelpRecyclerAdapter.HelpViewHolder) holder).bindHelp(mContext, mListener, help);
        }
    }

    @Override
    public int getItemCount() {
        return mHelpList.size();
    }



    static class HelpViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.iconImageView)
        ImageView mIconImageView;
        @BindView(R.id.titleTextView)
        TextView mTitleTextView;
        @BindView(R.id.captionTextView)
        TextView mCaptionTextView;

        private Help mHelp;
        private Context mContext;
        private Listener mListener;

        public HelpViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        public void bindHelp(Context context, Listener listener, Help help){
            mContext = context;
            mListener = listener;
            mHelp = help;

            String[] icon = help.getIcon().split("_");
            IconDrawable iconDrawable;
            if (icon[0].equals("fa")){
                iconDrawable = new IconDrawable(mContext, FontAwesomeIcons.valueOf(help.getIcon())).colorRes(R.color.colorPrimaryDark).sizeDp(40);
            } else {
                iconDrawable = new IconDrawable(mContext, MaterialIcons.valueOf(help.getIcon())).colorRes(R.color.colorPrimaryDark).sizeDp(40);
            }
            mIconImageView.setImageDrawable(iconDrawable);
            mTitleTextView.setText(mHelp.getSubject());
            mCaptionTextView.setText(mHelp.getCaption());
        }

        @Override
        public void onClick(View view) {
            mListener.helpSelected(mHelp);
        }
    }

    public interface Listener {
        void helpSelected(Help help);
    }
}
