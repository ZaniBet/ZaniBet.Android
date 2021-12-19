package eu.devolios.zanibet.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.devolios.zanibet.R;
import eu.devolios.zanibet.adapter.BetPlayedListAdapter;
import eu.devolios.zanibet.adapter.BetTournamentPlayedListAdapter;
import eu.devolios.zanibet.model.Bet;
import eu.devolios.zanibet.model.Fixture;
import eu.devolios.zanibet.model.GameTicket;
import eu.devolios.zanibet.model.Grille;
import eu.devolios.zanibet.presenter.GrilleTournamentPlayedPresenter;
import eu.devolios.zanibet.presenter.contract.GrilleDetailsContract;
import eu.devolios.zanibet.presenter.contract.GrilleTournamentPlayContract;
import eu.devolios.zanibet.presenter.contract.GrilleTournamentPlayedContract;
import eu.devolios.zanibet.utils.Constants;

public class GrilleTournamentPlayedFragment extends Fragment implements GrilleTournamentPlayedContract.View {

    @BindView(R.id.main_content)
    LinearLayout mMainContentLayout;
    @BindView(R.id.listView)
    ListView mListView;

    private GameTicket mGameTicket;
    private Grille mGrille;
    private BetTournamentPlayedListAdapter mBetPlayedListAdapter;
    private ArrayList<Bet> mBetArrayList;
    private ArrayList<Fixture> mFixtureArrayList;
    private FirebaseAnalytics mFirebaseAnalytics;
    private GrilleTournamentPlayedPresenter mGrilleTournamentPlayedPresenter;

    private View mLoadingView;

    public static GrilleTournamentPlayedFragment newInstance(GameTicket gameTicket) {
        GrilleTournamentPlayedFragment fragment = new GrilleTournamentPlayedFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("gameticket", gameTicket);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        mGameTicket = (GameTicket) args.getSerializable("gameticket");
        mFixtureArrayList = new ArrayList<>(Arrays.asList(mGameTicket.getFixtures()));
        mBetArrayList = new ArrayList<>();
        mBetPlayedListAdapter = new BetTournamentPlayedListAdapter(getActivity(), mFixtureArrayList, mBetArrayList);
        mGrilleTournamentPlayedPresenter = new GrilleTournamentPlayedPresenter(this, getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.grille_tournament_played_fragment, container, false);
        ButterKnife.bind(this, view);

        mLoadingView = inflater.inflate(R.layout.loading_overlay, container, false);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
        if (mGrille != null) {
            mListView.addFooterView(initListFooterView(getLayoutInflater()));
        }
        mListView.setAdapter(mBetPlayedListAdapter);
        mGrilleTournamentPlayedPresenter.loadGrille(mGameTicket);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Crashlytics.setString("last_ui_viewed", "GrilleMultiPlayedFragment");
        mFirebaseAnalytics.setCurrentScreen(getActivity(), "Détails d'une grille multi jouée", "GrilleMultiPlayedFragment");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private View initListFooterView(LayoutInflater inflater) {
        View footerView = inflater.inflate(R.layout.grille_play_footer, mListView, false);

        TextView referenceTextView = (TextView) footerView.findViewById(R.id.referenceTextView);
        TextView createdAtTextView = (TextView) footerView.findViewById(R.id.createdAtTextView);
        TextView updatedAtTextView = (TextView) footerView.findViewById(R.id.updateAtTextView);

        referenceTextView.setText(Html.fromHtml(getString(R.string.grid_reference, mGrille.getReference())));
        createdAtTextView.setText(Html.fromHtml(getString(R.string.grid_created_at, mGrille.getCreatedAt())));
        updatedAtTextView.setText(Html.fromHtml(getString(R.string.grid_updated_at, mGrille.getUpdatedAt())));

        return footerView;
    }


    @Override
    public void onLoadGrille(Grille grille) {
        mGrille = grille;
        mBetArrayList.addAll(Arrays.asList(grille.getBets()));
        if (mGrille != null) {
            if (isAdded()) mListView.addFooterView(initListFooterView(getLayoutInflater()));
            mBetPlayedListAdapter.setGrille(mGrille);
        }
        mBetPlayedListAdapter.notifyDataSetChanged();
    }

    @Override
    public void showContentLoading() {
        if (isAdded()) {
            try {
                if(mLoadingView != null && mMainContentLayout != null){
                    if (mLoadingView.getParent() != null) mMainContentLayout.removeView(mLoadingView);
                }

                if (mLoadingView != null && mMainContentLayout != null) {
                    mMainContentLayout.addView(mLoadingView);
                }
            } catch(Exception e){
                Crashlytics.logException(e);
            }
        }
    }

    @Override
    public void hideContentLoading() {
        if (isAdded()) {
            try {
                if(mLoadingView != null && mMainContentLayout != null){
                    if (mLoadingView.getParent() != null) mMainContentLayout.removeView(mLoadingView);
                }
            } catch (Exception e){
                Crashlytics.logException(e);
            }
        }
    }
}
