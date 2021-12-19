package eu.devolios.zanibet.fragment;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.joanzapata.iconify.widget.IconTextView;
import com.shashank.sony.fancygifdialoglib.FancyGifDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.devolios.zanibet.MainActivity;
import eu.devolios.zanibet.R;
import eu.devolios.zanibet.adapter.RewardRecyclerAdapter;
import eu.devolios.zanibet.adapter.decorator.HeaderDecoration;
import eu.devolios.zanibet.model.ApiError;
import eu.devolios.zanibet.model.Reward;
import eu.devolios.zanibet.model.User;
import eu.devolios.zanibet.presenter.RewardPresenter;
import eu.devolios.zanibet.presenter.contract.RewardContract;
import eu.devolios.zanibet.utils.EmptyView;

/**
 * Created by Gromat Luidgi on 09/11/2017.
 */

public class RewardFragment extends BaseFragment implements RewardContract.View, RewardRecyclerAdapter.Listener {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.frameLayout)
    FrameLayout mFrameLayout;
    @BindView(R.id.loadingIconTextView)
    IconTextView mLoadingIconTextView;

    private TextView mPointsTextView;

    private RewardPresenter mRewardPresenter;
    private RewardRecyclerAdapter mRewardRecyclerAdapter;
    private ArrayList<Reward> mRewardArrayList;
    private MaterialDialog mLoadingDialog;
    private FirebaseAnalytics mFirebaseAnalytics;

    public static RewardFragment newInstance(){
        return new RewardFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(Objects.requireNonNull(getActivity()));
        mRewardPresenter = new RewardPresenter(getActivity(), this);
        mRewardArrayList = new ArrayList<>();
        mRewardRecyclerAdapter = new RewardRecyclerAdapter(getActivity(), mRewardArrayList, this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reward_fragment, container, false);
        ButterKnife.bind(this, view);
        ((MainActivity) Objects.requireNonNull(getActivity())).updateTitle(getActivity().getString(R.string.title_shop));


        mFirebaseAnalytics.setCurrentScreen(getActivity(), "Boutique", "RewardFragment");

        GridLayoutManager mGridLayoutManager = new GridLayoutManager(getActivity(), 2);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.addItemDecoration(initHeaderDecoration(inflater));
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(0));
        mRecyclerView.setAdapter(mRewardRecyclerAdapter);
        mRewardPresenter.load();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Crashlytics.setString("last_ui_viewed", "RewardFragment");

    }

    @SuppressLint("SetTextI18n")
    private HeaderDecoration initHeaderDecoration(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.reward_header_decorator_item, mRecyclerView, false);
        mPointsTextView = view.findViewById(R.id.pointsTextView);
        TextView pointsValueTextView = view.findViewById(R.id.pointsValueTextView);

        mPointsTextView.setText(User.currentUser().getPoint() + " ZANICOINS");
        pointsValueTextView.setText(Objects.requireNonNull(getActivity()).getString(R.string.shop_description));
        HeaderDecoration.Builder builder = HeaderDecoration.with(mRecyclerView);
        builder.setView(view);
        return builder.build();
    }

    @Override
    public void addRewards(List<Reward> rewardList) {
        //mRewardPresenter.loadUser(); // refresh user data

        mRewardArrayList.clear();
        mRewardArrayList.addAll(rewardList);
        mRewardRecyclerAdapter.notifyDataSetChanged();

        if (mRewardArrayList.size() == 0){
            View emptyView = EmptyView.withDrawable(getActivity(),getString(EmptyView.EMPTY_REWARD),
                    ContextCompat.getDrawable(Objects.requireNonNull(getActivity()), R.drawable.ico_gift_bag));
            mFrameLayout.addView(emptyView);
        }
    }

    @Override
    public void showContentLoading() {
        if (isAdded()) {
            if (mRewardArrayList.size() == 0) {
                mLoadingIconTextView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void hideContentLoading() {
        if (isAdded()) {
            mLoadingIconTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public void showContentError(ApiError apiError) {
        mRewardArrayList.clear();
        mRewardRecyclerAdapter.notifyDataSetChanged();

        if (isAdded()) {
            try {
                ((MainActivity) Objects.requireNonNull(getActivity())).checkLoggedIn();
            } catch (Exception e){
                Crashlytics.logException(e);
            }

            View errorView = EmptyView.withDrawable(getActivity(),
                    apiError.getMessage(),
                    ContextCompat.getDrawable(getActivity(), R.drawable.global_error));
            mFrameLayout.addView(errorView);
        }
    }

    @Override
    public void onLoadUser() {
        if (isAdded()) {
            if (mPointsTextView != null)
                mPointsTextView.setText(Objects.requireNonNull(getActivity()).getString(R.string.amount_zanicoins, User.currentUser().getPoint()));
            mRewardRecyclerAdapter.notifyItemChanged(0);
        }
    }

    @Override
    public void onCreatePayout() {
        if (isAdded()) {
            new MaterialDialog.Builder(Objects.requireNonNull(getActivity()))
                    .titleGravity(GravityEnum.CENTER)
                    .contentGravity(GravityEnum.CENTER)
                    .title(getString(R.string.youha))
                    .titleColorRes( R.color.colorAccent)
                    .positiveColorRes( R.color.colorPrimaryDark)
                    .content(getString(R.string.dlg_content_payout_reward))
                    .negativeText(getString(R.string.ok_exclamation))
                    .iconRes(R.drawable.ico_gift_card)
                    .limitIconToDefaultSize()
                    .show();
        }
        mRewardPresenter.loadUser(); // refresh user data
    }

    @Override
    public void showLoadingDialog() {
        if (isAdded()) {
            mLoadingDialog = new MaterialDialog.Builder(Objects.requireNonNull(getActivity()))
                    .title(getString(R.string.reward))
                    .content(getString(R.string.ldg_content_payout_reward))
                    .progress(true, 0)
                    .show();
        }
    }

    @Override
    public void hideLoadingDialog() {
        if (isAdded()) {
            if (mLoadingDialog != null) {
                mLoadingDialog.dismiss();
            }
        }
    }

    @Override
    public void showCreatePayoutError(ApiError apiError) {
        new MaterialDialog.Builder(Objects.requireNonNull(getActivity()))
                .titleGravity(GravityEnum.CENTER)
                .contentGravity(GravityEnum.CENTER)
                .title(apiError.getTitle())
                .titleColorRes( R.color.colorRed800)
                .negativeColorRes(R.color.colorRed800)
                .content(apiError.getMessage())
                .negativeText(getString(R.string.ok_exclamation))
                .iconRes(R.drawable.ico_stop)
                .limitIconToDefaultSize()
                .show();
    }

    @SuppressLint("ResourceType")
    @Override
    public void onRewardSelected(final Reward reward) {
         if (User.currentUser().getPoint() > reward.getPrice()){
            /*new MaterialDialog.Builder(Objects.requireNonNull(getActivity()))
                    .titleGravity(GravityEnum.CENTER)
                    .contentGravity(GravityEnum.CENTER)
                    .title(getActivity().getString(R.string.dlg_title_confirm_reward))
                    .titleColorRes(R.color.colorAccent)
                    .negativeColorRes(R.color.colorRed800)
                    .content(getActivity().getString(R.string.reward_confirm,
                            reward.getPrice(),
                            reward.getName(),
                            reward.getValue()))
                    .negativeText(getActivity().getString(R.string.cancel))
                    .positiveText(getActivity().getString(R.string.yes))
                    .onPositive((dialog, which) -> mRewardPresenter.createPayout(reward))
                    .iconRes(R.drawable.ico_gift)
                    .limitIconToDefaultSize()
                    .show();*/

             new FancyGifDialog.Builder(getActivity())
                     .setTitle(getString(R.string.dlg_title_confirm_reward))
                     .setMessage(getString(R.string.reward_confirm, reward.getPrice(),
                             reward.getName(),
                             reward.getValue()))
                     .setPositiveBtnBackground(getResources().getString(R.color.colorPrimaryDark))
                     .setPositiveBtnText(getString(R.string.yes).toUpperCase())
                     .setNegativeBtnBackground(getResources().getString(R.color.colorRed800))
                     .setNegativeBtnText(getString(R.string.cancel).toUpperCase())
                     .setGifResource(R.drawable.gift_love)   //Pass your Gif here
                     .isCancellable(true)
                     .OnPositiveClicked(() -> {
                         mRewardPresenter.createPayout(reward);
                     })
                     .OnNegativeClicked(() -> {

                     })
                     .build();
        } else {
            new MaterialDialog.Builder(Objects.requireNonNull(getActivity()))
                    .titleGravity(GravityEnum.CENTER)
                    .contentGravity(GravityEnum.CENTER)
                    .title(getString(R.string.err_title_oups))
                    .titleColorRes( R.color.colorRed800)
                    .negativeColorRes(R.color.colorRed800)
                    .content(getString(R.string.err_not_enough_fund))
                    .negativeText(getString(R.string.ok_exclamation))
                    .iconRes(R.drawable.ico_gift_bag)
                    .limitIconToDefaultSize()
                    .show();
        }
    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;
            outRect.top = space;

            if (parent.getChildLayoutPosition(view) == 0){
                outRect.top = 0;
            }
            // Add top margin only for the first item to avoid double space between items
            /*if (parent.getChildLayoutPosition(view) == 0) {
                outRect.top = space;
            } else {
                outRect.top = 0;
            }*/
        }
    }
}
