package eu.devolios.zanibet.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.github.florent37.tutoshowcase.TutoShowcase;
import com.google.android.gms.common.util.Hex;
import com.kingfisher.easy_sharedpreference_library.SharedPreferencesManager;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.HamButton;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;
import com.shashank.sony.fancygifdialoglib.FancyGifDialog;
import com.shashank.sony.fancygifdialoglib.FancyGifDialogListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.devolios.zanibet.AdsMissionActivity;
import eu.devolios.zanibet.GameTicketSingleFilterActivity;
import eu.devolios.zanibet.GrilleSinglePlayPagerActivity;
import eu.devolios.zanibet.MainActivity;
import eu.devolios.zanibet.R;
import eu.devolios.zanibet.adapter.GameTicketRecyclerAdapter;
import eu.devolios.zanibet.model.ApiError;
import eu.devolios.zanibet.model.Competition;
import eu.devolios.zanibet.model.GameTicket;
import eu.devolios.zanibet.presenter.GameTicketSinglePresenter;
import eu.devolios.zanibet.presenter.contract.GameTicketSingleContract;
import eu.devolios.zanibet.utils.Constants;
import eu.devolios.zanibet.utils.EmptyView;
import ru.alexbykov.nopaginate.paginate.Paginate;
import ru.alexbykov.nopaginate.paginate.PaginateBuilder;


/**
 * Created by Gromat Luidgi on 05/01/2018.
 */

public class GameTicketSingleFragment extends BaseFragment implements GameTicketSingleContract.View, GameTicketRecyclerAdapter.SingleTicketListener {

    @BindView(R.id.singleTickerRecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.main_content)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.fab)
    FloatingActionButton mFloadingButton;
    @BindView(R.id.bmb)
    BoomMenuButton mBoomMenuButton;

    public static boolean FORCE_REFRESH = false;

    private View mErrorView;
    private View mLoadingView;

    private GameTicketSinglePresenter mGameTicketSinglePresenter;
    private GameTicketRecyclerAdapter mGameTicketRecyclerAdapter;
    private ArrayList<GameTicket> mGameTicketArrayList;
    private List<String> mSelectedFilterList;
    private Paginate mPaginate;

    public static GameTicketSingleFragment newInstance() {
        return new GameTicketSingleFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //SharedPreferencesManager.getInstance().remove(Constants.TICKET_SINGLE_FILTER_PREF);

        mGameTicketSinglePresenter = new GameTicketSinglePresenter(getActivity(), this);
        mGameTicketArrayList = new ArrayList<>();
        mSelectedFilterList = new ArrayList<>();
        if (SharedPreferencesManager.getInstance().getValues(Constants.TICKET_SINGLE_FILTER_PREF, String[].class) != null) {
            mSelectedFilterList.addAll(SharedPreferencesManager.getInstance().getValues(Constants.TICKET_SINGLE_FILTER_PREF, String[].class));
        }
        mGameTicketRecyclerAdapter = new GameTicketRecyclerAdapter(getActivity(), mGameTicketArrayList, this);
        mGameTicketSinglePresenter.loadUser();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.game_ticket_single_fragment, container, false);
        ButterKnife.bind(this, view);

        ((MainActivity) Objects.requireNonNull(getActivity())).updateTitle(getString(R.string.app_name));

        mLoadingView = inflater.inflate(R.layout.loading_overlay, container, false);


        mBoomMenuButton.setButtonEnum(ButtonEnum.Ham);
        mBoomMenuButton.setPiecePlaceEnum(PiecePlaceEnum.HAM_2);
        mBoomMenuButton.setButtonPlaceEnum(ButtonPlaceEnum.HAM_2);
        mBoomMenuButton.addBuilder(new HamButton.Builder());
        mBoomMenuButton.addBuilder(new HamButton.Builder());
        mBoomMenuButton.setInFragment(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mGameTicketRecyclerAdapter);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 || dy < 0 && mFloadingButton.isShown())
                    mFloadingButton.hide();
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    mFloadingButton.show();
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        mFloadingButton.setOnClickListener(view1 -> {
            mGameTicketSinglePresenter.loadUser();
            mGameTicketSinglePresenter.refresh();
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Crashlytics.setString("last_ui_viewed", "GameTicketSingleFragment");
        // Activer la pagination
        mPaginate = new PaginateBuilder()
                .with(mRecyclerView)
                .setOnLoadMoreListener(() -> {
                    if (isAdded() && isVisible()) {
                        if (mGameTicketArrayList.size() > 0)
                            mGameTicketSinglePresenter.loadMore();
                    }
                })
                .build();

        if (mGameTicketArrayList.isEmpty() || mGameTicketSinglePresenter.shouldForceRefresh() || FORCE_REFRESH) {
            FORCE_REFRESH = false;
            mPaginate.setNoMoreItems(true);
            mGameTicketSinglePresenter.refresh();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser &&
                !SharedPreferencesManager.getInstance().getValue(Constants.TUTO_SINGLE_TICKET_FILTER_PREF, Boolean.class, false)){
            mRecyclerView.postDelayed(() -> {
                try {
                    if (!mGameTicketArrayList.isEmpty()) {
                        TutoShowcase tutoShowcase = TutoShowcase.from(Objects.requireNonNull(getActivity()));

                        tutoShowcase.setContentView(R.layout.tuto_single_ticket_filter)
                                .on(mRecyclerView.getChildAt(1))
                                .addRoundRect()
                                .withBorder()
                                .onClick(v -> {
                                    SharedPreferencesManager.getInstance().putValue(Constants.TUTO_SINGLE_TICKET_FILTER_PREF, true);
                                    tutoShowcase.dismiss();
                                    showGameTicketOptions(mGameTicketArrayList.get(0));
                                })
                                .show();
                    }
                } catch (Exception e) {
                    Crashlytics.logException(e);
                }
            }, 1000);

        }
    }

    @Override
    public void onDestroyView() {
        if (mPaginate != null) mPaginate.unbind();
        if (mRecyclerView != null) mRecyclerView.clearOnScrollListeners();
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mGameTicketRecyclerAdapter != null)
            mGameTicketRecyclerAdapter.notifyItemChanged(0);

        int countAds = SharedPreferencesManager.getInstance().getValue(Constants.COUNT_ADS_ACTION_PREF, Integer.class, 0);
        if (countAds >= 8) {
            ((MainActivity) Objects.requireNonNull(getActivity())).showInterstitial();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 1) {
            mGameTicketSinglePresenter.refresh();
        }
    }

    @Override
    public void onLoadGameTicket(List<GameTicket> gameTicketList) {
        mGameTicketArrayList.clear();
        mGameTicketArrayList.addAll(gameTicketList);
        mGameTicketRecyclerAdapter.setNetworkError(false);
        mGameTicketRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void showContentLoading() {
        if (isAdded()) {
            try {
                if (mLoadingView != null && mLoadingView.getParent() != null)
                    ((ViewGroup) mLoadingView.getParent()).removeView(mLoadingView);

                if (mLoadingView != null && mCoordinatorLayout != null) {
                    mCoordinatorLayout.addView(mLoadingView);
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
            }
        }
    }

    @Override
    public void hideContentLoading() {
        if (isAdded()) {
            try {
                if (mLoadingView != null && mLoadingView.getParent() == null)
                    return;
                if (mLoadingView != null && mCoordinatorLayout != null) {
                    mCoordinatorLayout.removeView(mLoadingView);
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
            }
        }
    }

    @Override
    public void onLoadUser() {
        if (isAdded()) {
            if (mGameTicketRecyclerAdapter != null)
                mGameTicketRecyclerAdapter.notifyItemChanged(0);
        }
    }

    @Override
    public void setPaginateLoading(boolean value) {
        if (mPaginate != null) mPaginate.showLoading(value);
    }

    @Override
    public void setNoPaginate(boolean value) {
        if (isAdded()) {
            mPaginate.setNoMoreItems(value);
        }
    }

    @Override
    public void showContentError(ApiError apiError) {
        mGameTicketArrayList.clear();
        mGameTicketRecyclerAdapter.setNetworkError(true);
        mGameTicketRecyclerAdapter.notifyDataSetChanged();

        if (isAdded()) {
            try {
                ((MainActivity) Objects.requireNonNull(getActivity())).checkLoggedIn();
            } catch (Exception e) {
                Crashlytics.logException(e);
            }

            if (mErrorView != null) {
                if (mErrorView.getParent() != null) return;
            }

            mErrorView = EmptyView.withDrawable(getActivity(),
                    apiError.getMessage(),
                    ContextCompat.getDrawable(getActivity(),
                            R.drawable.global_error));

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            mCoordinatorLayout.addView(mErrorView, params);
        }
    }

    @Override
    public void hideContentError() {
        if (isAdded()) {
            if (mErrorView != null && mCoordinatorLayout != null) {
                mCoordinatorLayout.removeView(mErrorView);
            }
        }
    }

    @Override
    public void gameTicketSelected(GameTicket gameTicket) {
        GrilleSinglePlayPagerActivity.mGameTicket = gameTicket;
        Intent intent = new Intent(getActivity(), GrilleSinglePlayPagerActivity.class);
        startActivityForResult(intent, 10);
    }

    @Override
    public void showAdsMission() {
        Intent intent = new Intent(getActivity(), AdsMissionActivity.class);
        Objects.requireNonNull(getActivity()).startActivity(intent);
    }

    @Override
    public void showGameTicketOptions(GameTicket gameTicket) {
        //Toast.makeText(getActivity(), "test", Toast.LENGTH_LONG).show();

        Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
            @SuppressLint("ResourceType")
            @Override
            public void run() {
                mBoomMenuButton.clearBuilders();
                Competition competition = Competition.parseCompetition(gameTicket.getCompetition());

                mBoomMenuButton.addBuilder(new HamButton.Builder()
                        .normalImageRes(R.drawable.ic_ban)
                        .imagePadding(new Rect(8, 8, 8, 8))
                        .normalText(getString(R.string.ham_title_exclude_league))
                        .subNormalText(getString(R.string.ham_content_exclude_league, competition.getName()))
                        .normalColorRes(R.color.colorRed400)
                        .highlightedColorRes(R.color.colorRed600)
                        .listener(index -> new FancyGifDialog.Builder(getActivity())
                                .setTitle(getString(R.string.dlg_title_filter_single, competition.getName()))
                                .setMessage(getString(R.string.dlg_content_filter_single, competition.getName()))
                                .setPositiveBtnBackground(getResources().getString(R.color.colorPrimaryDark))
                                .setPositiveBtnText(getString(R.string.yes).toUpperCase())
                                .setNegativeBtnBackground(getResources().getString(R.color.colorRed800))
                                .setNegativeBtnText(getString(R.string.cancel).toUpperCase())
                                .setGifResource(R.drawable.filter_animation)   //Pass your Gif here
                                .isCancellable(true)
                                .OnPositiveClicked(() -> {
                                    Competition.excludeForSingleTicket(gameTicket.getCompetition());
                                    mGameTicketSinglePresenter.refresh();
                                })
                                .OnNegativeClicked(() -> {

                                })
                                .build()));

                mBoomMenuButton.addBuilder(new HamButton.Builder()
                        .normalImageRes(R.drawable.ic_filter_setting)
                        .imagePadding(new Rect(8, 8, 8, 8))
                        .normalText(getString(R.string.ham_title_edit_filter))
                        .normalColorRes(R.color.colorLightGreen400)
                        .highlightedColorRes(R.color.colorLightGreen600)
                        .listener(index -> {
                            Intent intent = new Intent(getActivity(), GameTicketSingleFilterActivity.class);
                            startActivityForResult(intent, 10);
                        }));

                if (mBoomMenuButton.isBoomed()) {
                    mBoomMenuButton.reboom();
                } else {
                    mBoomMenuButton.boom();
                }
            }
        });

    }
}
