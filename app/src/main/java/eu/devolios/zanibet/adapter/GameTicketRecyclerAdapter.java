package eu.devolios.zanibet.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.github.thunder413.datetimeutils.DateTimeUtils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.kingfisher.easy_sharedpreference_library.SharedPreferencesManager;
import com.rilixtech.materialfancybutton.MaterialFancyButton;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.devolios.zanibet.GlideApp;
import eu.devolios.zanibet.R;
import eu.devolios.zanibet.model.Competition;
import eu.devolios.zanibet.model.GameTicket;
import eu.devolios.zanibet.model.User;
import eu.devolios.zanibet.utils.Constants;
import eu.devolios.zanibet.utils.DateFormatter;
import eu.devolios.zanibet.utils.EmptyView;
import eu.devolios.zanibet.utils.GlideRoundTransformation;
import eu.devolios.zanibet.utils.RoundedTransformation;
/**
 * Created by Gromat Luidgi on 10/11/2017.
 */

public class GameTicketRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //enum TicketType { SINGLE, MULTI }

    private final static int TICKET_TYPE_MULTI = 1;
    private final static int TICKET_TYPE_SIMPLE = 2;
    private final static int TICKET_TYPE_TOURNAMENT = 3;

    private final static int FILTER_TYPE = 0;
    private final static int MULTI_MATCH_TYPE = 1;
    private final static int SINGLE_MATCH_TYPE = 2;
    private final static int ADS_TYPE = 3;
    private final static int SINGLE_HEADER_TYPE = 4;
    private final static int EMPTY_TYPE = 5;
    private final static int TOURNAMENT_TYPE = 6;
    private final static int TOURNAMENT_HEADER_TYPE = 7;

    private Context mContext;
    private List<GameTicket> mGameTicketList;
    private Listener mListener;
    private SingleTicketListener mSingleTicketListener;
    private TournamentTicketListener mTournamentTicketListener;
    private int mCurrentTicketType;
    private String mCurrentCompetitionFilter = "";
    private boolean mNetworkError = false;

    /* Constructeur ticket MULTI */
    public GameTicketRecyclerAdapter(Context context, List<GameTicket> gameTicketList, Listener listener){
        mContext = context;
        mGameTicketList = gameTicketList;
        mListener = listener;
        mCurrentTicketType = TICKET_TYPE_MULTI;
    }

    /* Constructeur ticket TOURNAMENT */
    public GameTicketRecyclerAdapter(Context context, List<GameTicket> gameTicketList, TournamentTicketListener listener){
        mContext = context;
        mGameTicketList = gameTicketList;
        mTournamentTicketListener = listener;
        mCurrentTicketType = TICKET_TYPE_TOURNAMENT;
    }

    /* Constructeur Ticket SIMPLE */
    public GameTicketRecyclerAdapter(Context context, List<GameTicket> gameTicketList, SingleTicketListener singleTicketListener){
        mContext = context;
        mGameTicketList = gameTicketList;
        mSingleTicketListener = singleTicketListener;
        mCurrentTicketType = TICKET_TYPE_SIMPLE;
    }


    public void setCurrentCompetitionFilter(String filter){
        mCurrentCompetitionFilter = filter;
    }

    public void setNetworkError(boolean value){
        mNetworkError = value;
    }

    @Override
    public int getItemViewType(int position) {

        if (position == 0 && mCurrentTicketType == TICKET_TYPE_SIMPLE){
            return SINGLE_HEADER_TYPE;
        } else if (position == 0 && mCurrentTicketType == TICKET_TYPE_TOURNAMENT){
            return TOURNAMENT_HEADER_TYPE;
        } else if (position == 1 && mGameTicketList.isEmpty() && mCurrentTicketType == TICKET_TYPE_SIMPLE){
            return EMPTY_TYPE;
        } else if ( position == 1 && mGameTicketList.isEmpty() && mCurrentTicketType == TICKET_TYPE_TOURNAMENT){
            return EMPTY_TYPE;
        }

        if (mCurrentTicketType == TICKET_TYPE_MULTI){
            return MULTI_MATCH_TYPE;
        } else if (mCurrentTicketType == TICKET_TYPE_SIMPLE){
            return SINGLE_MATCH_TYPE;
        } else if (mCurrentTicketType == TICKET_TYPE_TOURNAMENT){
            return TOURNAMENT_TYPE;
        } else {
            return MULTI_MATCH_TYPE;
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MULTI_MATCH_TYPE){
            View inflatedView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.game_ticket_card, parent, false);
            return new GameTicketViewHolder(inflatedView);
        } else if (viewType == SINGLE_MATCH_TYPE) {
            View inflatedView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.game_ticket_single_card, parent, false);
            return new GameTicketSingleViewHolder(inflatedView);
        } else if (viewType == ADS_TYPE){
            View inflatedView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.ads_banner_item, parent, false);
            return new AdsViewHolder(inflatedView);
        }  else if (viewType == SINGLE_HEADER_TYPE){
            View inflatedView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.game_ticket_single_list_header, parent, false);
            return new HeaderSingleViewHolder(inflatedView);
        } /*else if (viewType == FILTER_TYPE){
            View inflatedView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.filter_item, parent, false);
            return new CompetitionFilterViewHolder(inflatedView, parent.getContext());
        }*/ else if (viewType == EMPTY_TYPE){
            View inflatedView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.empty_view_drawable, parent, false);
            return new EmptyViewHolder(inflatedView);
        } else if (viewType == TOURNAMENT_TYPE){
            View inflatedView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.game_ticket_tournament_card, parent, false);
            return new GameTicketTournamentViewHolder(inflatedView);
        } else if (viewType == TOURNAMENT_HEADER_TYPE){
            View inflatedView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.game_ticket_tournament_list_header, parent, false);
            return new HeaderTournamentViewHolder(inflatedView);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof GameTicketViewHolder) {
            GameTicket gameTicket = mGameTicketList.get(position);
            ((GameTicketViewHolder) holder).bindGameTicket(mContext, gameTicket, mListener);
        } else if (holder instanceof  GameTicketSingleViewHolder){
            GameTicket gameTicket = mGameTicketList.get(position-1);
            ((GameTicketSingleViewHolder) holder).bindGameTicket(mContext, gameTicket, mSingleTicketListener);
        } else if (holder instanceof  AdsViewHolder){
            ((AdsViewHolder) holder).bindAds(mListener);
        } else if (holder instanceof  HeaderSingleViewHolder){
            ((HeaderSingleViewHolder) holder).bindHeader(mContext, mSingleTicketListener);
        } /*else if (holder instanceof  CompetitionFilterViewHolder){
            ((CompetitionFilterViewHolder) holder).bindCompetitionFilter(mSingleTicketListener, mCurrentCompetitionFilter);
        }*/ else if (holder instanceof  EmptyViewHolder){
            ((EmptyViewHolder) holder).bindEmpty(mContext, mCurrentCompetitionFilter, mCurrentTicketType);
        } else if (holder instanceof  HeaderTournamentViewHolder){
            ((HeaderTournamentViewHolder) holder).bindHeader(mContext, mTournamentTicketListener);
        }  else if (holder instanceof  GameTicketTournamentViewHolder){
            GameTicket gameTicket = mGameTicketList.get(position-1);
            ((GameTicketTournamentViewHolder) holder).bindGameTicket(mContext,gameTicket, mTournamentTicketListener);
        }
    }

    @Override
    public int getItemCount() {
        if (mNetworkError) return 0;

        if (mCurrentTicketType == TICKET_TYPE_MULTI){
            return mGameTicketList.size();
        }

        if (mCurrentTicketType == TICKET_TYPE_SIMPLE && mGameTicketList.isEmpty()){
            return mGameTicketList.size()+2;
        } else if (mCurrentTicketType == TICKET_TYPE_TOURNAMENT && mGameTicketList.isEmpty()){
            return mGameTicketList.size()+2;
        }

        return mGameTicketList.size()+1;
    }

    static class GameTicketViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.coverImageView)
        ImageView mCoverImageView;
        @BindView(R.id.competitionTextView)
        TextView mCompetitionTextView;
        @BindView(R.id.matchDayTextView)
        TextView mMatchDayTextView;
        @BindView(R.id.dateTextView)
        TextView mDateTextView;
        @BindView(R.id.countMatchTextView)
        TextView mCountMatchTextView;
        @BindView(R.id.cashTextView)
        TextView mCashTextView;
        @BindView(R.id.remainingPlayTextView)
        TextView mRemainingPlayTextView;
        @BindView(R.id.playButton)
        MaterialFancyButton mPlayButton;
        @BindView(R.id.calendarButton)
        MaterialFancyButton mCalendarButton;

        private GameTicket mGameTicket;
        private Listener mListener;
        private Context mContext;

        GameTicketViewHolder(View view){
            super(view);
            ButterKnife.bind(this, view);
        }

        void bindGameTicket(Context context, GameTicket gameTicket, Listener listener){
            mGameTicket = gameTicket;
            mListener = listener;
            mContext = context;

            mPlayButton.setOnClickListener(view -> mListener.gameTicketSelected(mGameTicket));

            mCalendarButton.setOnClickListener(view -> mListener.showCalendar(mGameTicket));

            if (gameTicket.getPicture() != null && Patterns.WEB_URL.matcher(gameTicket.getPicture()).matches()){
                GlideApp.with(context)
                        .load(gameTicket.getPicture())
                        .placeholder(R.drawable.ticket_tournament_placeholder)
                        .transforms(new CenterCrop(), new GlideRoundTransformation(12,0))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(mCoverImageView);
            } else {
                GlideApp.with(context)
                        .load(R.drawable.ticket_tournament_placeholder)
                        .transforms(new CenterCrop(), new GlideRoundTransformation(12,0))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(mCoverImageView);
            }

            Date startDate = DateFormatter.formatMongoDate(mGameTicket.getFixtures()[0].getDate());
            Date endDate = DateFormatter.formatMongoDate(mGameTicket.getFixtures()[mGameTicket.getFixtures().length-1].getDate());

            mCompetitionTextView.setText(mGameTicket.getName());
            mMatchDayTextView.setText(context.getString(R.string.matchday, mGameTicket.getMatchDay()));
            mDateTextView.setText(context.getString(R.string.date_range_ticket,
                    DateTimeUtils.formatWithPattern(startDate, "dd MMM yyyy HH:mm"),
                    DateTimeUtils.formatWithPattern(endDate, "dd MMM yyyy HH:mm") ));
            mCountMatchTextView.setText(context.getString(R.string.count_match, mGameTicket.getFixtures().length));
            mRemainingPlayTextView.setText(context.getString(R.string.ticket_playable_grid,
                    mGameTicket.getMaxNumberOfPlay() - mGameTicket.getNumberOfGrillePlay()));
            mCashTextView.setText(Html.fromHtml(context.getString(R.string.ticket_potential_cash, mGameTicket.getJackpot())));
        }
    }

    static class GameTicketTournamentViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.coverImageView)
        ImageView mCoverImageView;
        @BindView(R.id.ticketNameTextView)
        TextView mTicketNameTextView;

        @BindView(R.id.dateTextView)
        TextView mDateTextView;
        @BindView(R.id.countMatchTextView)
        TextView mCountMatchTextView;

        @BindView(R.id.rewardImageView)
        ImageView mRewardImageView;
        @BindView(R.id.rewardTextView)
        TextView mRewardTextView;

        @BindView(R.id.remainingPlayTextView)
        TextView mRemainingPlayTextView;
        @BindView(R.id.playCostTextView)
        TextView mHashCostTextView;
        @BindView(R.id.playersTextView)
        TextView mPlayersTextView;
        @BindView(R.id.playButton)
        MaterialFancyButton mPlayButton;


        private GameTicket mGameTicket;
        private TournamentTicketListener mListener;
        private Context mContext;

        GameTicketTournamentViewHolder(View view){
            super(view);
            ButterKnife.bind(this, view);
        }

        void bindGameTicket(Context context, GameTicket gameTicket, TournamentTicketListener listener){
            mGameTicket = gameTicket;
            mListener = listener;
            mContext = context;

            mPlayButton.setOnClickListener(view -> mListener.gameTicketSelected(mGameTicket));

            if (gameTicket.getPicture() != null && Patterns.WEB_URL.matcher(gameTicket.getPicture()).matches()){
                GlideApp.with(context)
                        .load(gameTicket.getPicture())
                        .placeholder(R.drawable.ticket_tournament_placeholder)
                        .transforms(new CenterCrop(), new GlideRoundTransformation(12,0))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(mCoverImageView);
            } else {
                GlideApp.with(context)
                        .load(R.drawable.ticket_tournament_placeholder)
                        .transforms(new CenterCrop(), new GlideRoundTransformation(12,0))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(mCoverImageView);
            }


            Date startDate = DateFormatter.formatMongoDate(mGameTicket.getFixtures()[0].getDate());
            Date endDate = DateFormatter.formatMongoDate(mGameTicket.getFixtures()[mGameTicket.getFixtures().length-1].getDate());

            mTicketNameTextView.setText(gameTicket.getName());
            mPlayersTextView.setText(Html.fromHtml(mContext.getString(R.string.players_in_tournament, gameTicket.getTournament().getTotalPlayers())));
            mHashCostTextView.setText(Html.fromHtml(mContext.getString(R.string.zanihash_cost, gameTicket.getTournament().getPlayCost())));
            mDateTextView.setText(Html.fromHtml(context.getString(R.string.date_range_ticket,
                    DateTimeUtils.formatWithPattern(startDate, "dd MMM yyyy HH:mm"),
                    DateTimeUtils.formatWithPattern(endDate, "dd MMM yyyy HH:mm") )));
            mCountMatchTextView.setText(Html.fromHtml(context.getString(R.string.count_match, mGameTicket.getFixtures().length)));
            mRemainingPlayTextView.setText(Html.fromHtml(context.getString(R.string.ticket_playable_grid,
                    mGameTicket.getMaxNumberOfPlay() - mGameTicket.getNumberOfGrillePlay())));

            if (gameTicket.getTournament().getRewardType().equals("ZaniHash")) {
                mRewardImageView.setImageResource(R.drawable.ic_zanihash_coin);
                mRewardTextView.setText(Html.fromHtml(context.getString(R.string.tournament_reward_zanihash,
                        gameTicket.getTournament().getPot(), gameTicket.getTournament().getTotalPlayersPaid())));
            } else {
                mRewardImageView.setImageResource(R.drawable.ic_zanicoin);
                mRewardTextView.setText(Html.fromHtml(context.getString(R.string.tournament_reward_zanicoin,
                        gameTicket.getTournament().getPot(), gameTicket.getTournament().getTotalPlayersPaid())));
            }

        }
    }

    static class GameTicketSingleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        @BindView(R.id.thumbnailImageView)
        ImageView mThumbnailImageView;
        @BindView(R.id.countryImageView)
        ImageView mCountryImageView;
        @BindView(R.id.dateTextView)
        TextView mDateTextView;
        @BindView(R.id.ticketNameTextView)
        TextView mTicketNameTextView;
        @BindView(R.id.competitionTextView)
        TextView mCompetitionTextView;
        /*@BindView(R.id.jetonTextView)
        TextView mJetonTextView;*/
        @BindView(R.id.indicatorImageView)
        ImageView mIndicatorImageView;
        @BindView(R.id.rewardTextView)
        TextView mRewardTextView;

        private GameTicket mGameTicket;
        private SingleTicketListener mListener;

        GameTicketSingleViewHolder(View view){
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);

        }

        void bindGameTicket(final Context context, final GameTicket gameTicket, SingleTicketListener listener){
            mGameTicket = gameTicket;
            mListener = listener;
            Competition competition = Competition.parseCompetition(mGameTicket.getCompetition());

            GlideApp.with(context)
                    .load(gameTicket.getThumbnail())
                    .placeholder(R.drawable.zanibet_logo)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(mThumbnailImageView);

            if (competition.getCountry() != null){
                int flag = context.getResources().getIdentifier(competition.getCountry().toLowerCase().replace(" ", "_") + "_flag_round",
                        "drawable", context.getPackageName());
                if (flag != 0){
                    mCountryImageView.setImageResource(flag);
                } else {
                    mCountryImageView.setImageResource(R.drawable.european_union_flag_round);
                }
            }


            Calendar calendar = Calendar.getInstance();
            calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date matchDate = DateFormatter.formatMongoDate(mGameTicket.getFixtures()[0].getDate());

            // Today
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            Date startOfDay = calendar.getTime();
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            Date endOfDay = calendar.getTime();
            // Tomorrow
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            Date endOfTomorrow = calendar.getTime();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            Date startOfTomorrow = calendar.getTime();

            if (matchDate.after(startOfDay) && matchDate.before(endOfDay)) {
                mDateTextView.setText(context.getString(R.string.today_date,
                        DateTimeUtils.formatWithPattern(matchDate, "HH:mm")));
            } else if (matchDate.after(startOfTomorrow) && matchDate.before(endOfTomorrow)){
                mDateTextView.setText(context.getString(R.string.tomorrow_date,
                        DateTimeUtils.formatWithPattern(matchDate, "HH:mm")));
            } else {
                mDateTextView.setText(
                        DateTimeUtils.formatWithPattern(matchDate, "EEE dd MMM yyyy, HH:mm") + " UTC");
            }
            mTicketNameTextView.setText(gameTicket.getName());
            mCompetitionTextView.setText(competition.getName());
            //mJetonTextView.setText(context.getString(R.string.cost_jeton, mGameTicket.getJeton()));

            if (mGameTicket.getNumberOfGrillePlay() > 0){
                mIndicatorImageView.setImageResource(R.drawable.checked_circle_green);
            } else {
                mIndicatorImageView.setImageResource(R.drawable.uncheck_circle);
            }

            int reward = (mGameTicket.getPointsPerBet()*mGameTicket.getBetsType().length)+mGameTicket.getBonus();
            mRewardTextView.setText(context.getString(R.string.grid_single_reward, reward));
        }

        @Override
        public void onClick(View view) {
            mListener.gameTicketSelected(mGameTicket);
        }

        @Override
        public boolean onLongClick(View v) {
            mListener.showGameTicketOptions(mGameTicket);
            return true;
        }
    }

    static class AdsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.adView)
        AdView mAdView;

        AdsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bindAds(Listener listener){
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
            //listener.showAds();
        }
    }

    static class HeaderSingleViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.moreJetonButton)
        MaterialFancyButton mMoreJetonButton;
        @BindView(R.id.jetonTextView)
        TextView mJetonTextView;
        @BindView(R.id.jetonDescTextView)
        TextView mJetonDescTextView;

        private Context mContext;
        private SingleTicketListener mSingleTicketListener;

        HeaderSingleViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bindHeader(Context context, SingleTicketListener listener){
            mContext = context;
            mSingleTicketListener = listener;
            mMoreJetonButton.setOnClickListener(view -> {
                if (mSingleTicketListener != null){
                    mSingleTicketListener.showAdsMission();
                }
            });

            mJetonTextView.setText(mContext.getString(R.string.title_jeton_count, User.currentUser().getJeton()));
            mJetonDescTextView.setText(mContext.getString(R.string.jeton_desc,
                    SharedPreferencesManager.getInstance().getValue(Constants.SETTING_FREE_JETON_PER_DAY, int.class, 5)));
        }
    }

    static class HeaderTournamentViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.moreZanihashButton)
        MaterialFancyButton mMoreZanihashButton;
        @BindView(R.id.zanihashTextView)
        TextView mZanihashTextView;
        @BindView(R.id.zanihashDescTextView)
        TextView mZanihashDescTextView;

        private Context mContext;
        private TournamentTicketListener mTournamentTicketListener;

        HeaderTournamentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bindHeader(Context context, TournamentTicketListener listener){
            mContext = context;
            mTournamentTicketListener = listener;
            mMoreZanihashButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mTournamentTicketListener != null){
                        mTournamentTicketListener.showZaniHashActivity();
                    }
                }
            });

            if (User.currentUser().getWallet() == null){
                mZanihashTextView.setText(mContext.getString(R.string.title_amount_zh, 0));
            } else {
                mZanihashTextView.setText(mContext.getString(R.string.title_amount_zh, User.currentUser().getWallet().getZaniHash()));
            }
            mZanihashDescTextView.setText(mContext.getString(R.string.zanihash_desc));
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

        void bindEmpty(Context context, String currentFilter, int type){
            Objects.requireNonNull(currentFilter);
            if (!currentFilter.isEmpty()){
                for (Competition comp : Competition.getCompetitions()){
                    if (comp.getId().equals(currentFilter)){
                        mMessageTextView.setText(Html.fromHtml(context.getString(R.string.empty_competition_gameticket_single,
                                comp.getName())));
                    }
                }
            } else {
                if (type == TICKET_TYPE_TOURNAMENT){
                    mIconView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_trophy));
                    mMessageTextView.setText(context.getString(EmptyView.EMPTY_GAMETICKET_TOURNAMENT));
                } else {
                    mIconView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.empty_single_ticket));
                    mMessageTextView.setText(context.getString(EmptyView.EMPTY_GAMETICKET_SINGLE));
                }
            }
        }
    }


    public interface Listener{
        void gameTicketSelected(GameTicket gameTicket);
        void showCalendar(GameTicket gameTicket);
    }

    public interface SingleTicketListener {
        void gameTicketSelected(GameTicket gameTicket);
        void showAdsMission();
        void showGameTicketOptions(GameTicket gameTicket);
        //void filterSelected(String filter);
    }

    public interface TournamentTicketListener {
        void gameTicketSelected(GameTicket gameTicket);
        void showZaniHashActivity();
    }

    /*static void selectSpinnerItemByValue(Spinner spnr, Competition value) {
        CompetitionSpinnerAdapter adapter = (CompetitionSpinnerAdapter) spnr.getAdapter();
        for (int position = 0; position < adapter.getCount(); position++) {
            Competition competition = adapter.getItem(position);
            assert competition != null;
            if(competition.get_id() != null && competition.get_id().equals(value.get_id())) {
                spnr.setSelection(position);
                return;
            }
        }
    }*/


}
