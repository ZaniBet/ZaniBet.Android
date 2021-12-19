package eu.devolios.zanibet.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.github.thunder413.datetimeutils.DateTimeUtils;

import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.devolios.zanibet.GlideApp;
import eu.devolios.zanibet.R;
import eu.devolios.zanibet.model.GameTicket;
import eu.devolios.zanibet.model.Grille;
import eu.devolios.zanibet.utils.DateFormatter;
import eu.devolios.zanibet.utils.GlideRoundTransformation;

/**
 * Created by Gromat Luidgi on 11/11/2017.
 */

public class GrilleRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final static int ADS_TYPE = 0;
    private final static int WAITING_RESULT_TYPE = 1;
    private final static int LOOSE_TYPE = 2;
    private final static int WIN_TYPE = 3;


    private Context mContext;
    private ArrayList<Grille> mGrilleList;
    private Listener mListener;

    public GrilleRecyclerAdapter(Context context, ArrayList<Grille> grilleList, Listener listener){
        mContext = context;
        mGrilleList = grilleList;
        mListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        Grille grille = mGrilleList.get(position);
        if (grille.getStatus().equals("win")){
            return WIN_TYPE;
        } else if (grille.getStatus().equals("loose")){
            return LOOSE_TYPE;
        } else {
            return WAITING_RESULT_TYPE;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView;
        switch (viewType){
            case WAITING_RESULT_TYPE:
                inflatedView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.grille_item, parent, false);
                return new GrilleViewHolder(inflatedView);
            case LOOSE_TYPE:
                inflatedView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.grille_item_loose, parent, false);
                return new GrilleLooseViewHolder(inflatedView);
            case WIN_TYPE:
                inflatedView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.grille_item_win, parent, false);
                return new GrilleWinViewHolder(inflatedView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof GrilleViewHolder) {
            Grille grille = mGrilleList.get(position);
            ((GrilleViewHolder) holder).bindGrille(mContext, grille, mListener);
        } else if (holder instanceof GrilleLooseViewHolder) {
            Grille grille = mGrilleList.get(position);
            ((GrilleLooseViewHolder) holder).bindGrille(mContext, grille, mListener);
        } else if (holder instanceof GrilleWinViewHolder) {
            Grille grille = mGrilleList.get(position);
            ((GrilleWinViewHolder) holder).bindGrille(mContext, grille, mListener);
        }
    }

    @Override
    public int getItemCount() {
        return mGrilleList.size();
    }

    static class GrilleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.coverImageView)
        ImageView mCoverImageView;
        @BindView(R.id.competitionTextView)
        TextView mCompetitionTextView;
        @BindView(R.id.createdAtTextView)
        TextView mCreatedAtTextView;
        @BindView(R.id.grillesDescTextView)
        TextView mGrilleDescTextView;

        private Grille mGrille;
        private Listener mListener;

        public GrilleViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        public void bindGrille(Context context, Grille grille, Listener listener){
            mGrille = grille;
            mListener = listener;
            GameTicket gameTicket = GameTicket.convertFromMap(mGrille.getGameTicket());
            //GameTicket gameTicket = (GameTicket)mGrille.getGameTicket();
            Date createdAt = DateFormatter.formatMongoDate(mGrille.getCreatedAt());
            Date resultDate = DateFormatter.formatMongoDate(gameTicket.getResultDate());

            GlideApp.with(context)
                    .load(R.drawable.grille_placeholder)
                    .transforms(new CenterCrop(), new GlideRoundTransformation(24,0))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(mCoverImageView);

            mCompetitionTextView.setText(context.getString(R.string.compet_day, gameTicket.getName().replace(" Jackpot", ""), gameTicket.getMatchDay()));
            if (gameTicket.getType().equals("MATCHDAY")) {
                mGrilleDescTextView.setText(context.getString(R.string.grille_pending_desc));
            } else {
                if (gameTicket.getFixtures()[0].getStatus().equals("soon")) {
                    mGrilleDescTextView.setText(context.getString(R.string.grille_desc_match_soon,
                            DateTimeUtils.formatWithPattern(resultDate, "dd MMM yyyy HH:mm")));
                } else if (gameTicket.getFixtures()[0].getStatus().equals("playing")){
                    mGrilleDescTextView.setText(context.getString(R.string.grille_desc_match_playing,
                            DateTimeUtils.formatWithPattern(resultDate, "HH:mm")));
                } else {
                    mGrilleDescTextView.setText(context.getString(R.string.grille_desc_calculating_result));
                }
            }
            mCreatedAtTextView.setText(DateTimeUtils.formatWithPattern(createdAt, "dd MMM yyyy HH:mm:ss") + " UTC");
        }

        @Override
        public void onClick(View view) {
            mListener.onGrilleSelected(mGrille);
        }
    }

    static class GrilleLooseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.coverImageView)
        ImageView mCoverImageView;
        @BindView(R.id.competitionTextView)
        TextView mCompetitionTextView;
        @BindView(R.id.updateAtTextView)
        TextView mUpdateAtTextView;
        @BindView(R.id.pointsTextView)
        TextView mPointsTextView;
        @BindView(R.id.grillesDescTextView)
        TextView mGrilleDescTextView;

        private Grille mGrille;
        private Listener mListener;

        public GrilleLooseViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        public void bindGrille(Context context, Grille grille, Listener listener){
            mGrille = grille;
            mListener = listener;

            //GameTicket gameTicket = (GameTicket)mGrille.getGameTicket();
            GameTicket gameTicket = GameTicket.convertFromMap(mGrille.getGameTicket());
            Date createdAt = DateFormatter.formatMongoDate(mGrille.getCreatedAt());

            GlideApp.with(context)
                    .load(R.drawable.grille_placeholder)
                    .transforms(new CenterCrop(), new GlideRoundTransformation(24,0))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(mCoverImageView);

            mCompetitionTextView.setText(context.getString(R.string.compet_day, gameTicket.getName().replace(" Jackpot", ""), gameTicket.getMatchDay()));
            mGrilleDescTextView.setText(context.getString(R.string.grille_lost_desc, mGrille.getNumberOfBetsWin(), mGrille.getBets().length));
            mUpdateAtTextView.setText(DateTimeUtils.formatWithPattern(createdAt, "dd MMM yyyy HH:mm:ss") + " UTC");
            mPointsTextView.setText(context.getString(R.string.number, mGrille.getPayout().getPoint()));
        }

        @Override
        public void onClick(View view) {
            mListener.onGrilleSelected(mGrille);
        }
    }

    static class GrilleWinViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.coverImageView)
        ImageView mCoverImageView;
        @BindView(R.id.competitionTextView)
        TextView mCompetitionTextView;
        @BindView(R.id.updateAtTextView)
        TextView mUpdateAtTextView;
        @BindView(R.id.cashTextView)
        TextView mCashTextView;
        @BindView(R.id.grillesDescTextView)
        TextView mGrilleDescTextView;

        private Grille mGrille;
        private Listener mListener;

        public GrilleWinViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        public void bindGrille(Context context, Grille grille, Listener listener){
            mGrille = grille;
            mListener = listener;

            //GameTicket gameTicket = (GameTicket)mGrille.getGameTicket();
            GameTicket gameTicket = GameTicket.convertFromMap(mGrille.getGameTicket());
            Date createdAt = DateFormatter.formatMongoDate(mGrille.getCreatedAt());

            GlideApp.with(context)
                    .load(R.drawable.grille_placeholder)
                    .transforms(new CenterCrop(), new GlideRoundTransformation(24,0))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(mCoverImageView);

            mCompetitionTextView.setText(context.getString(R.string.compet_day, gameTicket.getName().replace(" Jackpot", ""), gameTicket.getMatchDay()));
            mGrilleDescTextView.setText(context.getString(R.string.grille_winning_desc));
            mUpdateAtTextView.setText(DateTimeUtils.formatWithPattern(createdAt, "dd MMM yyyy HH:mm:ss") + " UTC");
            mCashTextView.setText(context.getString(R.string.amount_float, mGrille.getPayout().getAmount()));
        }

        @Override
        public void onClick(View view) {
            mListener.onGrilleSelected(mGrille);
        }
    }

    public interface Listener {
        void onGrilleSelected(Grille grille);
    }
}
