package eu.devolios.zanibet.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.crashlytics.android.Crashlytics;
import com.rilixtech.materialfancybutton.MaterialFancyButton;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.devolios.zanibet.GlideApp;
import eu.devolios.zanibet.R;
import eu.devolios.zanibet.model.Competition;

public class GameTicketSingleFilterRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final static int EMPTY_TYPE = 0;
    private final static int LEAGUE_TYPE = 1;

    private Context mContext;
    private List<String> mFilteredLeague;
    private GameTicketSingleFilterRecyclerAdapter.Listener mListener;

    public GameTicketSingleFilterRecyclerAdapter(Context context, List<String> filteredLeagueList, GameTicketSingleFilterRecyclerAdapter.Listener listener) {
        mContext = context;
        mFilteredLeague = filteredLeagueList;
        mListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        /*if (mFilteredLeague.isEmpty()) {
            return EMPTY_TYPE;
        }*/
        return LEAGUE_TYPE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case EMPTY_TYPE:
                view = LayoutInflater.from(mContext).inflate(R.layout.empty_view_drawable, parent, false);
                return new EmptyViewHolder(view);
            case LEAGUE_TYPE:
                view = LayoutInflater.from(mContext).inflate(R.layout.league_filter_item, parent, false);
                return new LeagueViewHolder(view);
            default:
                view = LayoutInflater.from(mContext).inflate(R.layout.empty_view_drawable, parent, false);
                return new EmptyViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof LeagueViewHolder) {
            ((LeagueViewHolder) holder).bindLeague(mContext, mFilteredLeague.get(position), mListener);
        } else if (holder instanceof EmptyViewHolder) {
            ((EmptyViewHolder) holder).bindEmpty(mContext);
        }
    }

    @Override
    public int getItemCount() {
        /*if (mFilteredLeague.isEmpty()){
            return 1;
        }*/
        return mFilteredLeague.size();
    }


    static class LeagueViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.leagueImageView)
        ImageView mLeagueImageView;
        @BindView(R.id.competitionTextView)
        AppCompatTextView mCompetitionTextView;
        @BindView(R.id.countryTextView)
        AppCompatTextView mCountryTextView;
        @BindView(R.id.countryImageView)
        ImageView mCountryImageView;
        @BindView(R.id.removeButton)
        MaterialFancyButton mRemoveButton;

        public LeagueViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindLeague(Context context, String competitionId, Listener listener) {
            try {
                Competition competition = Competition.getForId(competitionId);
                GlideApp.with(context)
                        .load(competition.getLogo())
                        .placeholder(R.drawable.zanibet_logo)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(mLeagueImageView);
                mCompetitionTextView.setText(competition.getName());
                mCountryTextView.setText(competition.getCountry());
                if (competition.getCountry() != null) mCountryImageView.setImageResource(context.getResources()
                        .getIdentifier(competition.getCountry().toLowerCase() + "_flag_round", "drawable", context.getPackageName()));
                mRemoveButton.setOnClickListener(v -> listener.removeFilter(competitionId));
            } catch (Exception e){
                Crashlytics.logException(e);
            }
        }
    }

    static class EmptyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.imageView)
        ImageView mIconView;
        @BindView(R.id.message_textview)
        TextView mMessageTextView;

        EmptyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bindEmpty(Context context) {
            mMessageTextView.setText(context.getString(R.string.empty_league_single_filter));
        }
    }

    public interface Listener {
        void removeFilter(String competitionId);
    }
}
