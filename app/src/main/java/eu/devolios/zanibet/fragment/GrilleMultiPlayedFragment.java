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
import eu.devolios.zanibet.adapter.BetPlayedListAdapter;
import eu.devolios.zanibet.model.Bet;
import eu.devolios.zanibet.model.Fixture;
import eu.devolios.zanibet.model.GameTicket;
import eu.devolios.zanibet.model.Grille;
import eu.devolios.zanibet.presenter.contract.GrilleDetailsContract;
import eu.devolios.zanibet.utils.Constants;

public class GrilleMultiPlayedFragment extends Fragment implements GrilleDetailsContract.View {

    @BindView(R.id.listView)
    ListView mListView;
    @BindView(R.id.bannerContainer)
    LinearLayout mBannerContainer;

    private GameTicket mGameTicket;
    private Grille mGrille;
    private BetPlayedListAdapter mBetPlayedListAdapter;
    private ArrayList<Bet> mBetArrayList;
    private ArrayList<Fixture> mFixtureArrayList;
    private FirebaseAnalytics mFirebaseAnalytics;


    public static GrilleMultiPlayedFragment newInstance(GameTicket gameTicket, Grille grille) {
        GrilleMultiPlayedFragment fragment = new GrilleMultiPlayedFragment();
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
        //mFixture = mGameTicket.getFixtures()[0];

        mFixtureArrayList = new ArrayList<>(Arrays.asList(mGameTicket.getFixtures()));
        mBetArrayList = new ArrayList<>(Arrays.asList(mGrille.getBets()));
        mBetPlayedListAdapter = new BetPlayedListAdapter(getActivity(), mFixtureArrayList, mBetArrayList, mGrille);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.grille_multi_played_fragment, container, false);
        ButterKnife.bind(this, view);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(Objects.requireNonNull(getActivity()));
        mListView.addFooterView(initListFooterView(getLayoutInflater()));
        mListView.setAdapter(mBetPlayedListAdapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Crashlytics.setString("last_ui_viewed", "GrilleMultiPlayedFragment");
        mFirebaseAnalytics.setCurrentScreen(Objects.requireNonNull(getActivity()), "Détails d'une grille multi jouée", "GrilleMultiPlayedFragment");
    }

    @Override
    public void onDestroyView() {
        try {
            mBannerContainer.removeAllViews();
        } catch (Exception e){
            Crashlytics.logException(e);
        }
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


}
