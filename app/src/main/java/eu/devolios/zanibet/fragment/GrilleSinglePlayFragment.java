package eu.devolios.zanibet.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.crashlytics.android.Crashlytics;
import com.github.thunder413.datetimeutils.DateTimeStyle;
import com.github.thunder413.datetimeutils.DateTimeUtils;
import com.rilixtech.materialfancybutton.MaterialFancyButton;
import com.shashank.sony.fancygifdialoglib.FancyGifDialog;
import com.shashank.sony.fancygifdialoglib.FancyGifDialogListener;

import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.devolios.zanibet.R;
import eu.devolios.zanibet.adapter.MultiBetListAdapter;
import eu.devolios.zanibet.model.ApiError;
import eu.devolios.zanibet.model.BetType;
import eu.devolios.zanibet.model.Competition;
import eu.devolios.zanibet.model.GameTicket;
import eu.devolios.zanibet.model.Grille;
import eu.devolios.zanibet.presenter.GrilleSinglePlayPresenter;
import eu.devolios.zanibet.presenter.contract.GrilleSinglePlayContract;
import eu.devolios.zanibet.utils.DateFormatter;

/**
 * Created by Gromat Luidgi on 05/01/2018.
 */

public class GrilleSinglePlayFragment extends Fragment implements GrilleSinglePlayContract.View, MultiBetListAdapter.Listener {


    @BindView(R.id.listView)
    ListView mListView;

    private GrilleSinglePlayPresenter mGrilleSinglePlayPresenter;
    private MultiBetListAdapter mMultiBetListAdapter;
    private GameTicket mGameTicket;
    private HashMap<String, Integer> mBetsHashMap;
    private BetType[] mBetTypeArray;

    private MaterialDialog mLoadingDialog;
    private MaterialFancyButton mPlayButton;

    public static GrilleSinglePlayFragment newInstance(GameTicket gameTicket) {
        GrilleSinglePlayFragment fragment = new GrilleSinglePlayFragment();
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

        mGrilleSinglePlayPresenter = new GrilleSinglePlayPresenter(getActivity(), this);
        mBetTypeArray = mGameTicket.getBetsType();
        mBetsHashMap = new HashMap<>();
        mMultiBetListAdapter = new MultiBetListAdapter(getActivity(), mBetTypeArray, mBetsHashMap, mGameTicket.getFixtures()[0], this);
        mGrilleSinglePlayPresenter.load(mGameTicket);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.grille_single_play_fragment, container, false);
        ButterKnife.bind(this, view);

        //if (mGameTicket == null) mFragmentNavigation.popFragment();

        //mListView.addHeaderView(initListHeaderView(inflater));
        mListView.addFooterView(initListFooter(inflater));
        mListView.setAdapter(mMultiBetListAdapter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @SuppressLint("ResourceType")
    private View initListFooter(LayoutInflater inflater){
        @SuppressLint("InflateParams") View footerView = inflater.inflate(R.layout.bet_multi_list_footer, null);
        mPlayButton = footerView.findViewById(R.id.playButton);

        if (mGameTicket.getNumberOfGrillePlay() > 0){
            mPlayButton.setEnabled(false);
        }
        mPlayButton.setOnClickListener(view -> {

            /*new MaterialDialog.Builder(getActivity())
                    .content(Html.fromHtml(getString(R.string.dlg_content_confirm_single_grid, mGameTicket.getJeton(),
                            mGameTicket.getFixtures()[0].getHomeTeam().getName(),
                            mGameTicket.getFixtures()[0].getAwayTeam().getName())))
                    .positiveText(getString(R.string.yes))
                    .positiveColorRes(R.color.colorPrimaryDark)
                    .negativeText(getString(R.string.cancel))
                    .negativeColorRes(R.color.colorRed800)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            mPlayButton.setEnabled(false);
                            mGrilleSinglePlayPresenter.playGrille(mGameTicket);
                        }
                    })
                    .show();*/

            new FancyGifDialog.Builder(getActivity())
                    .setTitle(getString(R.string.dlg_title_confirm_prediction))
                    .setMessage(Html.fromHtml(getString(R.string.dlg_content_confirm_single_grid, mGameTicket.getJeton(),
                            mGameTicket.getFixtures()[0].getHomeTeam().getName(),
                            mGameTicket.getFixtures()[0].getAwayTeam().getName())).toString())
                    .setPositiveBtnBackground(getResources().getString(R.color.colorPrimaryDark))
                    .setPositiveBtnText(getString(R.string.yes).toUpperCase())
                    .setNegativeBtnBackground(getResources().getString(R.color.colorRed800))
                    .setNegativeBtnText(getString(R.string.cancel).toUpperCase())
                    .setGifResource(R.drawable.soccer_tv)   //Pass your Gif here
                    .isCancellable(true)
                    .OnPositiveClicked(() -> {
                        mPlayButton.setEnabled(false);
                        mGrilleSinglePlayPresenter.playGrille(mGameTicket);
                    })
                    .OnNegativeClicked(() -> {

                    })
                    .build();
        });
        return footerView;
    }


    @Override
    public void onLoad() {

    }

    @Override
    public void onUpdateBet(HashMap<String, Integer> bets) {
        mMultiBetListAdapter.updateBets(bets);

    }

    @Override
    public void onPlayGrille(Grille grille) {
        if (isAdded()) {
            if (mPlayButton != null) mPlayButton.setEnabled(false);
            //String resultDate = mGameTicket.getResultDate();
            Date resultDate = DateFormatter.formatMongoDate(mGameTicket.getResultDate());

            getActivity().setResult(1);

            new MaterialDialog.Builder(Objects.requireNonNull(getActivity()))
                    .title(getActivity().getString(R.string.youha))
                    .titleColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark))
                    .positiveColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark))
                    .content(Html.fromHtml(getActivity().getString(R.string.dlg_content_bet_confirmation, DateTimeUtils.formatWithStyle(resultDate, DateTimeStyle.FULL))))
                    .positiveText(getActivity().getString(R.string.ok_exclamation))
                    .onAny((dialog, which) -> {
                        mGameTicket.setNumberOfGrillePlay(1);
                        getActivity().finish();
                        //if (mFragmentNavigation != null) mFragmentNavigation.popFragment();
                    })
                    .iconRes(R.drawable.ic_success)
                    .limitIconToDefaultSize()
                    .show();
            }
    }

    @Override
    public void showPlayTicketError(ApiError error) {
        if (isAdded()) {
            try {
                if (mPlayButton != null) mPlayButton.setEnabled(true);
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
    public void showLoadingDialog(String title, String content) {
        if (isAdded()) {
            try {
                mLoadingDialog = new MaterialDialog.Builder(Objects.requireNonNull(getActivity()))
                        .title(title)
                        .content(content)
                        .progress(true, 0)
                        .show();
            } catch(Exception e){
                Crashlytics.logException(e);
            }
        }
    }

    @Override
    public void hideLoadingDialog() {
        if (isAdded()) {
            if (mLoadingDialog != null) mLoadingDialog.dismiss();
        }
    }

    @Override
    public void onBetChange(RadioGroup radioGroup) {
        RadioButton radioButton = (RadioButton) radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());
        if (radioButton != null){
            mGrilleSinglePlayPresenter.updateBet(radioGroup.getTag().toString(), (int)radioButton.getTag());
            //Log.d("BET CHANGE", "Bet Type : " + radioGroup.getTag() + " - Result : " + radioButton.getTag());
        }
    }
}
