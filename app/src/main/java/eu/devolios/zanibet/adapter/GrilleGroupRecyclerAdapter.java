package eu.devolios.zanibet.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.crashlytics.android.Crashlytics;
import com.github.thunder413.datetimeutils.DateTimeStyle;
import com.github.thunder413.datetimeutils.DateTimeUtils;

import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.devolios.zanibet.GlideApp;
import eu.devolios.zanibet.R;
import eu.devolios.zanibet.model.Competition;
import eu.devolios.zanibet.model.GameTicket;
import eu.devolios.zanibet.model.Grille;
import eu.devolios.zanibet.model.GrilleGroup;
import eu.devolios.zanibet.utils.DateFormatter;
import eu.devolios.zanibet.utils.GlideRoundTransformation;

/**
 * Created by Gromat Luidgi on 11/11/2017.
 */

public class GrilleGroupRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final static int ITEM_LOADING = 0;
    private final static int WAITING_RESULT_TYPE = 1;
    private final static int LOOSE_TYPE = 2;
    private final static int WIN_TYPE = 3;
    private final static int SINGLE_TYPE = 4;
    private final static int SINGLE_TYPE_DONE = 5;
    private final static int TOURNAMENT_PENDING_TYPE = 6;
    private final static int TOURNAMENT_DONE_TYPE = 7;


    private Context mContext;
    private ArrayList<GrilleGroup> mGrilleGroupList;
    private Listener mListener;

    public GrilleGroupRecyclerAdapter(Context context, ArrayList<GrilleGroup> grilleGroupList, Listener listener){
        mContext = context;
        mGrilleGroupList = grilleGroupList;
        mListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        GrilleGroup grilleGroup = mGrilleGroupList.get(position);
        if (mGrilleGroupList.get(position) == null){
            return ITEM_LOADING;
        } else if (grilleGroup.getGrilles()[0].getStatus().equals("win")
                && grilleGroup.getGrilles()[0].getType().equals("MULTI")){
            return WIN_TYPE;
        } else if (grilleGroup.getGrilles()[0].getStatus().equals("loose")
                && grilleGroup.getGrilles()[0].getType().equals("MULTI")){
            return LOOSE_TYPE;
        } else if (grilleGroup.getGrilles()[0].getStatus().equals("waiting_result")
                && grilleGroup.getGrilles()[0].getType().equals("MULTI")) {
            return WAITING_RESULT_TYPE;
        } else if (grilleGroup.getGrilles()[0].getStatus().equals("waiting_result")
                && grilleGroup.getGrilles()[0].getType().equals("SIMPLE")){
            return SINGLE_TYPE;
        } else if (grilleGroup.getGrilles()[0].getStatus().equals("waiting_result")
                && grilleGroup.getGrilles()[0].getType().equals("TOURNAMENT")){
            return TOURNAMENT_PENDING_TYPE;
        } else if (grilleGroup.getGrilles()[0].getType().equals("TOURNAMENT")){
            return TOURNAMENT_DONE_TYPE;
        } else {
            return SINGLE_TYPE_DONE;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflatedView;
        switch (viewType){
            case WAITING_RESULT_TYPE:
                inflatedView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.grille_group_card_pending, parent, false);
                return new GrilleViewHolder(inflatedView);
            case LOOSE_TYPE:
                inflatedView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.grille_group_card_lost, parent, false);
                return new GrilleLooseViewHolder(inflatedView);
            case WIN_TYPE:
                inflatedView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.grille_group_card_win, parent, false);
                return new GrilleWinViewHolder(inflatedView);
            case SINGLE_TYPE:
                inflatedView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.grille_group_single_card_pending, parent, false);
                return new GrilleSingleViewHolder(inflatedView);
            case SINGLE_TYPE_DONE:
                inflatedView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.grille_group_single_card, parent, false);
                return new GrilleSingleDoneViewHolder(inflatedView);
            case TOURNAMENT_PENDING_TYPE:
                inflatedView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grille_group_tournament_card_pending, parent, false);
                return new GrilleTournamentPendingViewHolder(inflatedView);
            case TOURNAMENT_DONE_TYPE:
                inflatedView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grille_group_tournament_card_done, parent, false);
                return new GrilleTournamentDoneViewHolder(inflatedView);
            case ITEM_LOADING:
                inflatedView = LayoutInflater.from(parent.getContext()).inflate(R.layout.load_more_item, parent, false);
                return new LoadingViewHolder(inflatedView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof GrilleViewHolder) {
            GrilleGroup grille = mGrilleGroupList.get(position);
            ((GrilleViewHolder) holder).bindGrille(mContext, grille, mListener);
        } else if (holder instanceof GrilleLooseViewHolder) {
            GrilleGroup grille = mGrilleGroupList.get(position);
            ((GrilleLooseViewHolder) holder).bindGrille(mContext, grille, mListener);
        } else if (holder instanceof GrilleWinViewHolder) {
            GrilleGroup grille = mGrilleGroupList.get(position);
            ((GrilleWinViewHolder) holder).bindGrille(mContext, grille, mListener);
        } else if (holder instanceof GrilleSingleViewHolder) {
            GrilleGroup grille = mGrilleGroupList.get(position);
            ((GrilleSingleViewHolder) holder).bindGrille(mContext, grille, mListener);
        } else if (holder instanceof GrilleSingleDoneViewHolder) {
            GrilleGroup grille = mGrilleGroupList.get(position);
            ((GrilleSingleDoneViewHolder) holder).bindGrille(mContext, grille, mListener);
        } else if (holder instanceof GrilleTournamentPendingViewHolder) {
            GrilleGroup grille = mGrilleGroupList.get(position);
            ((GrilleTournamentPendingViewHolder) holder).bindGrille(mContext, grille, mListener);
        } else if (holder instanceof GrilleTournamentDoneViewHolder) {
            GrilleGroup grille = mGrilleGroupList.get(position);
            ((GrilleTournamentDoneViewHolder) holder).bindGrille(mContext, grille, mListener);
        }
    }

    @Override
    public int getItemCount() {
        return mGrilleGroupList.size();
    }

    static class LoadingViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.progressBar1)
        ProgressBar mProgressBar;

        LoadingViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, itemView);
        }
    }


    static class GrilleSingleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.coverImageView)
        ImageView mCoverImageView;
        @BindView(R.id.competitionTextView)
        TextView mCompetitionTextView;
        @BindView(R.id.matchDayTextView)
        TextView mMatchDayTextView;
        @BindView(R.id.resultDateTextView)
        TextView mResultDateTextView;
        @BindView(R.id.zanicoinTextView)
        TextView mZanicoinTextView;
        @BindView(R.id.competitionLayout)
        LinearLayout mCompetitionLayout;
        @BindView(R.id.countryImageView)
        ImageView mCountryImageView;
        @BindView(R.id.countryTextView)
        TextView mCountryTextView;


        private GrilleGroup mGrilleGroup;
        private Listener mListener;

        GrilleSingleViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        void bindGrille(Context context, GrilleGroup grilleGroup, Listener listener){
            mGrilleGroup = grilleGroup;
            mListener = listener;

            GameTicket gameTicket = mGrilleGroup.getGameTicket();
            int potentialZanicoins = 0;
            potentialZanicoins = (gameTicket.getPointsPerBet() * gameTicket.getBetsType().length)+gameTicket.getBonus();
            Date resultDate = DateFormatter.formatMongoDate(gameTicket.getResultDate());

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

            mCompetitionTextView.setText(gameTicket.getName().replace(" Jackpot", ""));
            mMatchDayTextView.setText(context.getString(R.string.matchday, gameTicket.getMatchDay()));
            mResultDateTextView.setText(context.getString(R.string.result_at, DateTimeUtils.formatWithPattern(resultDate, "dd MMM yyyy HH:mm")));
            mZanicoinTextView.setText(context.getString(R.string.amount_zanicoins, potentialZanicoins));

            try {
                Competition competition = Competition.parseCompetition(gameTicket.getFixtures()[0].getCompetition());
                mCountryTextView.setText(competition.getCountry());
                int flag = context.getResources().getIdentifier(competition.getCountry().toLowerCase().replace(" ", "_") + "_flag_round",
                        "drawable", context.getPackageName());
                if (flag != 0){
                    mCountryImageView.setImageResource(flag);
                } else {
                    mCountryImageView.setImageResource(R.drawable.world_flag_round);
                }
            } catch (Exception e){
                Crashlytics.logException(e);
                mCompetitionLayout.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View view) {
            mListener.onGrilleSelected(mGrilleGroup);
        }
    }

    static class GrilleSingleDoneViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.coverImageView)
        ImageView mCoverImageView;
        @BindView(R.id.competitionTextView)
        TextView mCompetitionTextView;
        @BindView(R.id.matchDayTextView)
        TextView mMatchDayTextView;
        @BindView(R.id.pronoWinTextView)
        TextView mPronoWinTextView;
        @BindView(R.id.zanicoinTextView)
        TextView mZanicoinTextView;
        @BindView(R.id.competitionLayout)
        LinearLayout mCompetitionLayout;
        @BindView(R.id.countryImageView)
        ImageView mCountryImageView;
        @BindView(R.id.countryTextView)
        TextView mCountryTextView;

        private GrilleGroup mGrilleGroup;
        private Listener mListener;

        GrilleSingleDoneViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        void bindGrille(Context context, GrilleGroup grilleGroup, Listener listener){
            mGrilleGroup = grilleGroup;
            mListener = listener;

            GameTicket gameTicket = mGrilleGroup.getGameTicket();
            int earnedCoins = mGrilleGroup.getGrilles()[0].getPayout().getPoint();

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

            mCompetitionTextView.setText(gameTicket.getName().replace(" Jackpot", ""));
            mMatchDayTextView.setText(context.getString(R.string.matchday, gameTicket.getMatchDay()));
            mPronoWinTextView.setText(context.getString(R.string.pari_win_count, mGrilleGroup.getGrilles()[0].getNumberOfBetsWin(), gameTicket.getBetsType().length));
            mZanicoinTextView.setText(context.getString(R.string.amount_zanicoins, earnedCoins));

            try {
                Competition competition = Competition.parseCompetition(gameTicket.getFixtures()[0].getCompetition());
                mCountryTextView.setText(competition.getCountry());
                int flag = context.getResources().getIdentifier(competition.getCountry().toLowerCase().replace(" ", "_") + "_flag_round",
                        "drawable", context.getPackageName());
                if (flag != 0){
                    mCountryImageView.setImageResource(flag);
                } else {
                    mCountryImageView.setImageResource(R.drawable.world_flag_round);
                }
            } catch (Exception e){
                Crashlytics.logException(e);
                mCompetitionLayout.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View view) {
            mListener.onGrilleSelected(mGrilleGroup);
        }
    }

    static class GrilleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.coverImageView)
        ImageView mCoverImageView;
        @BindView(R.id.competitionTextView)
        TextView mCompetitionTextView;
        @BindView(R.id.matchDayTextView)
        TextView mMatchDayTextView;
        @BindView(R.id.grillesDescTextView)
        TextView mGrillesDescTextView;
        @BindView(R.id.remainingPlayTextView)
        TextView mRemainingPlayTextView;
        @BindView(R.id.zanicoinTextView)
        TextView mZanicoinTextView;
        @BindView(R.id.cashTextView)
        TextView mCashTextView;

        private GrilleGroup mGrilleGroup;
        private Listener mListener;

        public GrilleViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        public void bindGrille(Context context, GrilleGroup grilleGroup, Listener listener){
            mGrilleGroup = grilleGroup;
            mListener = listener;

            GameTicket gameTicket = mGrilleGroup.getGameTicket();
            Date resultDate = DateFormatter.formatMongoDate(gameTicket.getResultDate());
            int potentialZanicoins = gameTicket.getPointsPerBet() * (gameTicket.getFixtures().length*gameTicket.getMaxNumberOfPlay());
            potentialZanicoins += gameTicket.getBonus() * gameTicket.getMaxNumberOfPlay();
            potentialZanicoins += gameTicket.getMaxNumberOfPlay()*1000;

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

            try {
                mCompetitionTextView.setText(gameTicket.getName().replace(" Jackpot", ""));
                mMatchDayTextView.setText(context.getString(R.string.matchday, gameTicket.getMatchDay()));
                mGrillesDescTextView.setText(context.getString(R.string.pending_grid_desc, grilleGroup.getGrilles().length, gameTicket.getMatchDay(), gameTicket.getName().replace(" Jackpot", ""), DateTimeUtils.formatWithPattern(resultDate, "dd MMM yyyy HH:mm:ss")));
                mRemainingPlayTextView.setText(context.getString(R.string.remaining_grille, (gameTicket.getMaxNumberOfPlay() - grilleGroup.getGrilles().length)));
                mCashTextView.setText(context.getString(R.string.grille_paypal, gameTicket.getJackpot()));
                mZanicoinTextView.setText(context.getString(R.string.amount_zanicoins_group, potentialZanicoins));
            } catch (Exception e){
                if (mGrilleGroup.getGrilles()[0] != null)
                    Crashlytics.setString("GrilleGroup", mGrilleGroup.getGrilles()[0].get_id());
                Crashlytics.logException(e);
            }
        }

        @Override
        public void onClick(View view) {
            mListener.onGrilleSelected(mGrilleGroup);
        }
    }

    static class GrilleLooseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.coverImageView)
        ImageView mCoverImageView;
        @BindView(R.id.competitionTextView)
        TextView mCompetitionTextView;
        @BindView(R.id.matchDayTextView)
        TextView mMatchDayTextView;
        @BindView(R.id.grillesDescTextView)
        TextView mGrillesDescTextView;
        @BindView(R.id.remainingPlayTextView)
        TextView mRemainingPlayTextView;
        @BindView(R.id.zanicoinTextView)
        TextView mZanicoinTextView;
        @BindView(R.id.pronoWinTextView)
        TextView mPronoWinTextView;
        @BindView(R.id.pronoLostTextView)
        TextView mPronoLostTextView;

        private GrilleGroup mGrilleGroup;
        private Listener mListener;

        GrilleLooseViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        void bindGrille(Context context, GrilleGroup grilleGroup, Listener listener){
            mGrilleGroup = grilleGroup;
            mListener = listener;

            GameTicket gameTicket = mGrilleGroup.getGameTicket();
            int pronoWin = 0;
            int pronoLost = 0;
            int zanicoins = 0;
            for (Grille grille: grilleGroup.getGrilles()){
                pronoWin += grille.getNumberOfBetsWin();
                zanicoins += grille.getPayout().getPoint();
            }

            pronoLost = (gameTicket.getFixtures().length * mGrilleGroup.getGrilles().length) - pronoWin;

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

            try {
                mCompetitionTextView.setText(gameTicket.getName().replace(" Jackpot", ""));
                mMatchDayTextView.setText(context.getString(R.string.matchday, gameTicket.getMatchDay()));
                mGrillesDescTextView.setText(context.getString(R.string.lost_grid_desc, gameTicket.getMatchDay()));

                mRemainingPlayTextView.setText(context.getString(R.string.play_grille, mGrilleGroup.getGrilles().length));
                mPronoLostTextView.setText(context.getString(R.string.prono_lost, pronoLost));
                mPronoWinTextView.setText(context.getString(R.string.pronos_win, pronoWin));
                mZanicoinTextView.setText(context.getString(R.string.amount_zanicoins_group, zanicoins));
            } catch (Exception e){
               /* if (mGrilleGroup.getGrilles()[0] != null)
                    Crashlytics.setString("GrilleGroup", mGrilleGroup.getGrilles()[0].get_id());*/
                Crashlytics.logException(e);
            }
        }

        @Override
        public void onClick(View view) {
            mListener.onGrilleSelected(mGrilleGroup);
        }
    }

    static class GrilleWinViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.coverImageView)
        ImageView mCoverImageView;
        @BindView(R.id.competitionTextView)
        TextView mCompetitionTextView;
        @BindView(R.id.matchDayTextView)
        TextView mMatchDayTextView;
        @BindView(R.id.grillesDescTextView)
        TextView mGrillesDescTextView;
        @BindView(R.id.remainingPlayTextView)
        TextView mRemainingPlayTextView;
        @BindView(R.id.zanicoinTextView)
        TextView mZanicoinTextView;
        @BindView(R.id.pronoWinTextView)
        TextView mPronoWinTextView;
        @BindView(R.id.cashTextView)
        TextView mCashTextView;

        private GrilleGroup mGrilleGroup;
        private Listener mListener;

        GrilleWinViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        void bindGrille(Context context, GrilleGroup grilleGroup, Listener listener){
            mGrilleGroup = grilleGroup;
            mListener = listener;

            GameTicket gameTicket = mGrilleGroup.getGameTicket();
            int pronoWin = 0;
            int zanicoins = 0;

            double cash = 0;
            for (Grille grille: grilleGroup.getGrilles()){
                pronoWin += grille.getNumberOfBetsWin();
                cash += grille.getPayout().getAmount();
                zanicoins += grille.getPayout().getPoint();
            }

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

            try {
                mCompetitionTextView.setText(gameTicket.getName().replace(" Jackpot", ""));
                mMatchDayTextView.setText(context.getString(R.string.matchday, gameTicket.getMatchDay()));
                mGrillesDescTextView.setText(context.getString(R.string.win_grid_desc));

                mRemainingPlayTextView.setText(context.getString(R.string.win_grille, mGrilleGroup.getGrilles().length));
                mCashTextView.setText(context.getString(R.string.grille_paypal_float, cash));
                mPronoWinTextView.setText(context.getString(R.string.pronos_win, pronoWin));
                mZanicoinTextView.setText(context.getString(R.string.amount_zanicoins_group, zanicoins));
            } catch (Exception e){
                Crashlytics.logException(e);
            }
        }

        @Override
        public void onClick(View view) {
            mListener.onGrilleSelected(mGrilleGroup);
        }
    }

    static class GrilleTournamentPendingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.coverImageView)
        ImageView mCoverImageView;
        @BindView(R.id.competitionTextView)
        TextView mCompetitionTextView;
        @BindView(R.id.dateTextView)
        TextView mDateTextView;
        @BindView(R.id.grillesDescTextView)
        TextView mGrillesDescTextView;

        @BindView(R.id.amountPlayersTextView)
        TextView mAmountPlayersTextView;
        @BindView(R.id.amountPaidPlayersTextView)
        TextView mAmountPaidPlayersTextView;

        @BindView(R.id.rewardImageView)
        ImageView mRewardImageView;
        @BindView(R.id.rewardTextView)
        TextView mRewardTextView;

        private GrilleGroup mGrilleGroup;
        private Listener mListener;

        GrilleTournamentPendingViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        void bindGrille(Context context, GrilleGroup grilleGroup, Listener listener){
            mGrilleGroup = grilleGroup;
            mListener = listener;

            GameTicket gameTicket = mGrilleGroup.getGameTicket();
            Date resultDate = DateFormatter.formatMongoDate(gameTicket.getResultDate());
            Date openDate = DateFormatter.formatMongoDate(gameTicket.getResultDate());

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

            mCompetitionTextView.setText(gameTicket.getName());
            mDateTextView.setText(DateTimeUtils.formatWithStyle(openDate, DateTimeStyle.MEDIUM));

            mAmountPlayersTextView.setText(context.getString(R.string.amount_players_group, gameTicket.getTournament().getTotalPlayers()));
            mAmountPaidPlayersTextView.setText(context.getString(R.string.amount_players_paid_group, gameTicket.getTournament().getTotalPlayersPaid()));

            if (gameTicket.getTournament().getRewardType().equals("ZaniHash")) {
                mGrillesDescTextView.setText(Html.fromHtml(context.getString(R.string.pending_tournament_zh_desc,
                        gameTicket.getTournament().getPot(),
                        DateTimeUtils.formatWithPattern(resultDate, "dd MMM yyyy HH:mm:ss"))));
                mRewardImageView.setImageResource(R.drawable.ic_zanihash_coin);
                mRewardTextView.setText(context.getString(R.string.amount_zanihashs_group, gameTicket.getTournament().getPot()));
            } else {
                mGrillesDescTextView.setText(Html.fromHtml(context.getString(R.string.pending_tournament_desc,
                        gameTicket.getTournament().getPot(),
                        DateTimeUtils.formatWithPattern(resultDate, "dd MMM yyyy HH:mm:ss"))));
                mRewardImageView.setImageResource(R.drawable.ic_zanicoin);
                mRewardTextView.setText(context.getString(R.string.amount_zanicoins_group, gameTicket.getTournament().getPot()));
            }
        }

        @Override
        public void onClick(View view) {
            mListener.onGrilleSelected(mGrilleGroup);
        }
    }

    static class GrilleTournamentDoneViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.coverImageView)
        ImageView mCoverImageView;
        @BindView(R.id.competitionTextView)
        TextView mCompetitionTextView;
        @BindView(R.id.dateTextView)
        TextView mDateTextView;

        @BindView(R.id.grillesDescTextView)
        TextView mGrillesDescTextView;
        @BindView(R.id.amountPlayersTextView)
        TextView mAmountPlayersTextView;

        @BindView(R.id.rewardImageView)
        ImageView mRewardImageView;
        @BindView(R.id.rewardTextView)
        TextView mRewardTextView;


        @BindView(R.id.rankTextView)
        TextView mRankTextView;

        private GrilleGroup mGrilleGroup;
        private Listener mListener;

        GrilleTournamentDoneViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        void bindGrille(Context context, GrilleGroup grilleGroup, Listener listener){
            mGrilleGroup = grilleGroup;
            mListener = listener;

            GameTicket gameTicket = mGrilleGroup.getGameTicket();
            Date openDate = DateFormatter.formatMongoDate(gameTicket.getResultDate());


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

            mCompetitionTextView.setText(gameTicket.getName());
            mDateTextView.setText(DateTimeUtils.formatWithStyle(openDate, DateTimeStyle.MEDIUM));
            mAmountPlayersTextView.setText(context.getString(R.string.amount_players_group,
                    gameTicket.getTournament().getTotalPlayers()));

            if (gameTicket.getTournament().getRewardType().equals("ZaniHash")){
                mGrillesDescTextView.setText(Html.fromHtml(context.getString(R.string.done_tournament_zh_desc,
                        gameTicket.getTournament().getPot())));
                mRewardImageView.setImageResource(R.drawable.ic_zanihash_coin);
                mRewardTextView.setText(context.getString(R.string.amount_zanihashs_group,
                        mGrilleGroup.getGrilles()[0].getPayout().getPoint()));
            } else {
                mGrillesDescTextView.setText(Html.fromHtml(context.getString(R.string.done_tournament_desc,
                        gameTicket.getTournament().getPot())));
                mRewardImageView.setImageResource(R.drawable.ic_zanicoin);
                mRewardTextView.setText(context.getString(R.string.amount_zanicoins_group,
                        mGrilleGroup.getGrilles()[0].getPayout().getPoint()));
            }

            if (mGrilleGroup.getGrilles()[0].getStanding().getRank() == 1) {
                mRankTextView.setText(context.getString(R.string.rank_first_group,
                        mGrilleGroup.getGrilles()[0].getStanding().getRank(),
                        mGrilleGroup.getGrilles()[0].getStanding().getPoints()));
            } else {
                try {
                    mRankTextView.setText(context.getString(R.string.rank_group,
                            mGrilleGroup.getGrilles()[0].getStanding().getRank(),
                            mGrilleGroup.getGrilles()[0].getStanding().getPoints()));
                } catch (Exception e){
                    Crashlytics.logException(e);
                }
            }
        }

        @Override
        public void onClick(View view) {
            mListener.onGrilleSelected(mGrilleGroup);
        }
    }


    public interface Listener {
        void onGrilleSelected(GrilleGroup grille);
    }
}
