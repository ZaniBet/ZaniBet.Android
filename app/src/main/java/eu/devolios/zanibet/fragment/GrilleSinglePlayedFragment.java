package eu.devolios.zanibet.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
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
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.devolios.zanibet.R;
import eu.devolios.zanibet.adapter.MultiBetPlayedListAdapter;
import eu.devolios.zanibet.model.Bet;
import eu.devolios.zanibet.model.Fixture;
import eu.devolios.zanibet.model.GameTicket;
import eu.devolios.zanibet.model.Grille;
import eu.devolios.zanibet.presenter.GrilleSinglePlayedPresenter;
import eu.devolios.zanibet.presenter.contract.GrilleSinglePlayedContract;
import eu.devolios.zanibet.utils.Constants;

public class GrilleSinglePlayedFragment extends Fragment implements GrilleSinglePlayedContract.View {

    @BindView(R.id.listView)
    ListView mListView;
    @BindView(R.id.main_content)
    LinearLayout mMainContentLayout;

    private GameTicket mGameTicket;
    private Grille mGrille;
    private Fixture mFixture;

    private GrilleSinglePlayedPresenter mGrilleSinglePlayedPresenter;
    private MultiBetPlayedListAdapter mMultiBetPlayedListAdapter;
    private ArrayList<Bet> mBetArrayList;
    private FirebaseAnalytics mFirebaseAnalytics;

    private View mLoadingView;

    public static GrilleSinglePlayedFragment newInstance(GameTicket gameTicket) {
        GrilleSinglePlayedFragment fragment = new GrilleSinglePlayedFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("gameticket", gameTicket);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static GrilleSinglePlayedFragment newInstance(GameTicket gameTicket, Grille grille) {
        GrilleSinglePlayedFragment fragment = new GrilleSinglePlayedFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("gameticket", gameTicket);
        bundle.putSerializable("grille", grille);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();

        mGrille = (Grille) args.getSerializable("grille");
        mGameTicket = (GameTicket) args.getSerializable("gameticket");

        if (mGameTicket == null) Objects.requireNonNull(getActivity()).finish();

        mFixture = mGameTicket.getFixtures()[0];
        mGrilleSinglePlayedPresenter = new GrilleSinglePlayedPresenter(this, getActivity());

        if (mGrille != null) {
            mBetArrayList = new ArrayList<>(Arrays.asList(mGrille.getBets()));
        } else {
            mBetArrayList = new ArrayList<>();
            mGrilleSinglePlayedPresenter.loadGrille(mGameTicket);
        }

        mMultiBetPlayedListAdapter = new MultiBetPlayedListAdapter(getActivity(), mGameTicket, mBetArrayList, mFixture);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.grille_single_played_fragment, container, false);
        ButterKnife.bind(this, view);

        mLoadingView = inflater.inflate(R.layout.loading_overlay, container, false);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(Objects.requireNonNull(getActivity()));
        mFirebaseAnalytics.setCurrentScreen(getActivity(), "Détails d'une grille", "GrilleMultiDetailsActivity");

        if (mGrille != null){
            mListView.addFooterView(initListFooterView(getLayoutInflater()));
        }
        mListView.setAdapter(mMultiBetPlayedListAdapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Crashlytics.setString("last_ui_viewed", "GrilleMatchdayPlayFragment");

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private View initListFooterView(LayoutInflater inflater) {
        View footerView = inflater.inflate(R.layout.grille_play_footer, mListView, false);

        TextView referenceTextView = footerView.findViewById(R.id.referenceTextView);
        TextView createdAtTextView = footerView.findViewById(R.id.createdAtTextView);
        TextView updatedAtTextView = footerView.findViewById(R.id.updateAtTextView);

        referenceTextView.setText(Html.fromHtml(getString(R.string.grid_reference, mGrille.getReference())));
        createdAtTextView.setText(Html.fromHtml(getString(R.string.grid_created_at, mGrille.getCreatedAt())));
        updatedAtTextView.setText(Html.fromHtml(getString(R.string.grid_updated_at, mGrille.getUpdatedAt())));

        return footerView;
    }

    @Override
    public void onLoadGrille(Grille grille) {
        mGrille = grille;
        mBetArrayList.addAll(Arrays.asList(grille.getBets()));

        try {
            if (mGrille != null && mListView.getFooterViewsCount() == 0) mListView.addFooterView(initListFooterView(getLayoutInflater()));
        } catch (Exception e){
            Crashlytics.logException(e);
        }
        mMultiBetPlayedListAdapter.notifyDataSetChanged();

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
