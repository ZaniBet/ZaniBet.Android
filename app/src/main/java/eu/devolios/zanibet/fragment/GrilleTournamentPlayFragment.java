package eu.devolios.zanibet.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.rilixtech.materialfancybutton.MaterialFancyButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.devolios.zanibet.R;
import eu.devolios.zanibet.adapter.BetTournamentListAdapter;
import eu.devolios.zanibet.model.ApiError;
import eu.devolios.zanibet.model.Fixture;
import eu.devolios.zanibet.model.GameTicket;
import eu.devolios.zanibet.model.Grille;
import eu.devolios.zanibet.presenter.GrilleTournamentPlayPresenter;
import eu.devolios.zanibet.presenter.contract.GrilleTournamentPlayContract;

/**
 * Created by Gromat Luidgi on 09/11/2017.
 */

public class GrilleTournamentPlayFragment extends BaseFragment implements GrilleTournamentPlayContract.View, BetTournamentListAdapter.Listener {

    @BindView(R.id.listView)
    ListView mListView;

    private MaterialFancyButton mPlayButton;
    private GrilleTournamentPlayPresenter mGrilleTournamentPlayPresenter;
    private GameTicket mGameTicket;
    private BetTournamentListAdapter mBetListAdapter;
    private List<Fixture> mFixtureList;
    private HashMap<String, Integer> mBetsHashMap;
    private Grille mGrille;
    private FirebaseAnalytics mFirebaseAnalytics;


    public static GrilleTournamentPlayFragment newInstance(GameTicket gameTicket) {
        GrilleTournamentPlayFragment fragment = new GrilleTournamentPlayFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("gameticket", gameTicket);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null){
            mGameTicket = (GameTicket) bundle.getSerializable("gameticket");
        } else {
            return;
        }

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(Objects.requireNonNull(getActivity()));

        mGrilleTournamentPlayPresenter = new GrilleTournamentPlayPresenter(getActivity(), this);
        mFixtureList = new ArrayList<>(Arrays.asList(mGameTicket.getFixtures()));
        mBetsHashMap = new HashMap<>();
        mBetListAdapter = new BetTournamentListAdapter(getActivity(), mFixtureList, mBetsHashMap, this);
        mGrilleTournamentPlayPresenter.load(mGameTicket);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.grille_multi_play_fragment, container, false);
        ButterKnife.bind(this, view);
        mFirebaseAnalytics.setCurrentScreen(Objects.requireNonNull(getActivity()), "Jouer une grille", "GrilleMatchdayPlayFragment");

        mListView.addHeaderView(initListHeaderView(inflater));
        mListView.addFooterView(initListFooter(inflater));
        mListView.setAdapter(mBetListAdapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Crashlytics.setString("last_ui_viewed", "GrilleMultiPlayFragment");
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private View initListHeaderView(LayoutInflater inflater) {
        @SuppressLint("InflateParams") View headerView = inflater.inflate(R.layout.title_header_decoration, null);
        TextView titleTextView = headerView.findViewById(R.id.titleTextView);
        titleTextView.setText(getActivity().getString(R.string.select_prediction));
        return headerView;
    }

    private View initListFooter(LayoutInflater inflater){
        View footerView = inflater.inflate(R.layout.bet_list_footer, null);
        MaterialFancyButton resetButton = footerView.findViewById(R.id.resetButton);
        MaterialFancyButton flashButton = footerView.findViewById(R.id.flashButton);
        mPlayButton = footerView.findViewById(R.id.playButton);
        mPlayButton.setText(getActivity().getString(R.string.validate));
        mPlayButton.setEnabled(true);

        resetButton.setOnClickListener(view -> {
            logResetGrille();
            mGrilleTournamentPlayPresenter.clearBets();
        });

        flashButton.setOnClickListener(view -> {
            logFlashGrille();
            mGrilleTournamentPlayPresenter.flashGrille();
        });

        mPlayButton.setOnClickListener(view -> {
            mPlayButton.setEnabled(false);
            new MaterialDialog.Builder(getActivity())
                    .titleGravity(GravityEnum.CENTER)
                    .contentGravity(GravityEnum.CENTER)
                    .title(getActivity().getString(R.string.dlg_title_confirm_grid_validation))
                    .content(Html.fromHtml(getActivity().getString(R.string.dlg_content_tourn_confirm_grid,
                            mGameTicket.getTournament().getPlayCost()+mGameTicket.getTournament().getFees(), mGameTicket.getName())))
                    .positiveColor(ContextCompat.getColor(getActivity(), R.color.colorRed800))
                    .positiveText(getActivity().getString(R.string.btn_yes))
                    .onPositive((dialog, which) -> mGrilleTournamentPlayPresenter.playGrille(mGameTicket))
                    .negativeColor(ContextCompat.getColor(getActivity(), R.color.colorRed800))
                    .negativeText(getActivity().getString(R.string.btn_no))
                    .onNegative((dialog, which) -> mPlayButton.setEnabled(true))
                    .show();
        });

        return footerView;
    }


    // Presenter Callback
    @Override
    public void onLoad() {

    }

    @Override
    public void onPlayGrille(Grille grille) {
        if (isAdded()){
            try {
                mGrille = grille;
                logValidateGridEvent();

                new MaterialDialog.Builder(Objects.requireNonNull(getActivity()))
                        .titleGravity(GravityEnum.CENTER)
                        .contentGravity(GravityEnum.CENTER)
                        .title("Youha !")
                        .titleColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark))
                        .positiveColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark))
                        .content(Html.fromHtml(getActivity().getString(R.string.dlg_content_grid_tourn_confirmation, mGameTicket.getName(), mGameTicket.getResultDate())))
                        .positiveText(getActivity().getString(R.string.ok_exclamation))
                        .iconRes(R.drawable.ic_success)
                        .limitIconToDefaultSize()
                        .onPositive((dialog, which) -> {
                            getActivity().finish();
                        })
                        .show();
            } catch (Exception e){
                Crashlytics.logException(e);
            }

            getActivity().setResult(1);
        }
    }

    @Override
    public void showPlayTicketError(ApiError error) {
        if (isAdded()) {
            if (mPlayButton != null) mPlayButton.setEnabled(true);

            try {
                new MaterialDialog.Builder(Objects.requireNonNull(getActivity()))
                        .titleGravity(GravityEnum.CENTER)
                        .contentGravity(GravityEnum.CENTER)
                        .title(error.getTitle())
                        .titleColor(ContextCompat.getColor(getActivity(), R.color.colorRed800))
                        .negativeColor(ContextCompat.getColor(getActivity(), R.color.colorRed800))
                        .content(error.getMessage())
                        .negativeText(getActivity().getString(R.string.ok_exclamation))
                        .iconRes(R.drawable.ico_stop)
                        .limitIconToDefaultSize()
                        .show();
            } catch (Exception e){
                Crashlytics.logException(e);
            }
        }
    }

    @Override
    public void onUpdateBet(HashMap<String, Integer> bets) {
        mBetListAdapter.updateBets(bets);
    }

    @Override
    public void onClearBets() {
        mBetListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFlashTicket() {
        mBetListAdapter.notifyDataSetChanged();
    }

    // Adapter callback
    @Override
    public void onBetChange(RadioGroup radioGroup) {
        RadioButton radioButton = (RadioButton) radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());
        if (radioButton != null)
            mGrilleTournamentPlayPresenter.updateBet(radioGroup.getTag().toString(), (int)radioButton.getTag());
        //Log.d("BET CHANGE", "Fixture : " + radioGroup.getTag() + " - Result : " + radioButton.getTag());
    }


    private void logValidateGridEvent(){
        if (mFirebaseAnalytics != null && mGrille != null && mGameTicket != null){
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, mGrille.get_id());
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, mGameTicket.getName() + " J" + mGameTicket.getMatchDay());
            bundle.putInt("number_validate_grid", mGameTicket.getNumberOfGrillePlay());
            mFirebaseAnalytics.logEvent("validate_grid_tournament", bundle);
        }
    }

    private void logFlashGrille(){
        if (mFirebaseAnalytics != null && mGameTicket != null){
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, mGameTicket.getId());
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, mGameTicket.getName() + " J" + mGameTicket.getMatchDay());
            bundle.putInt("number_validate_grid", mGameTicket.getNumberOfGrillePlay());
            mFirebaseAnalytics.logEvent("flash_grid_tournament", bundle);
        }
    }

    private void logResetGrille(){
        if (mFirebaseAnalytics != null && mGameTicket != null){
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, mGameTicket.getId());
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, mGameTicket.getName() + " J" + mGameTicket.getMatchDay());
            bundle.putInt("number_validate_grid", mGameTicket.getNumberOfGrillePlay());
            mFirebaseAnalytics.logEvent("reset_grid_tournament", bundle);
        }
    }
}
