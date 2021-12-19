package eu.devolios.zanibet.fragment;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.devolios.zanibet.MainActivity;
import eu.devolios.zanibet.R;
import eu.devolios.zanibet.adapter.RewardZaniHashRecyclerAdapter;
import eu.devolios.zanibet.adapter.decorator.HeaderDecoration;
import eu.devolios.zanibet.model.ApiError;
import eu.devolios.zanibet.model.Reward;
import eu.devolios.zanibet.model.User;
import eu.devolios.zanibet.presenter.RewardZaniHashPresenter;
import eu.devolios.zanibet.presenter.contract.RewardZaniHashContract;
import eu.devolios.zanibet.utils.EmptyView;

/**
 * Created by Gromat Luidgi on 09/11/2017.
 */

public class RewardZaniHashFragment extends BaseFragment implements RewardZaniHashContract.View, RewardZaniHashRecyclerAdapter.Listener {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.frameLayout)
    FrameLayout mFrameLayout;
    @BindView(R.id.loadingIconTextView)
    IconTextView mLoadingIconTextView;

    private TextView mPointsTextView;
    private TextView mPointsValueTextView;

    private RewardZaniHashPresenter mRewardZaniHashPresenter;
    private LinearLayoutManager mLinearLayoutManager;
    private GridLayoutManager mGridLayoutManager;
    private RewardZaniHashRecyclerAdapter mRewardRecyclerAdapter;
    private ArrayList<Reward> mRewardArrayList;
    private MaterialDialog mLoadingDialog;
    private FirebaseAnalytics mFirebaseAnalytics;

    public static RewardZaniHashFragment newInstance(){
        RewardZaniHashFragment rewardFragment = new RewardZaniHashFragment();
        return rewardFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
        mRewardZaniHashPresenter = new RewardZaniHashPresenter(getActivity(), this);
        mRewardArrayList = new ArrayList<Reward>();
        mRewardRecyclerAdapter = new RewardZaniHashRecyclerAdapter(getActivity(), mRewardArrayList, this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reward_fragment, container, false);
        ButterKnife.bind(this, view);
        ((MainActivity)getActivity()).updateTitle(getActivity().getString(R.string.title_shop));


        mFirebaseAnalytics.setCurrentScreen(getActivity(), "Boutique", "RewardFragment");

        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mGridLayoutManager = new GridLayoutManager(getActivity(), 2);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.addItemDecoration(initHeaderDecoration(inflater));
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(0));
        mRecyclerView.setAdapter(mRewardRecyclerAdapter);
        mRewardZaniHashPresenter.load();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Crashlytics.setString("last_ui_viewed", "RewardFragment");

    }

    private HeaderDecoration initHeaderDecoration(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.reward_header_decorator_item, mRecyclerView, false);
        mPointsTextView = view.findViewById(R.id.pointsTextView);
        mPointsValueTextView = view.findViewById(R.id.pointsValueTextView);

        if (User.currentUser().getWallet() != null) {
            mPointsTextView.setText(Objects.requireNonNull(getActivity()).getString(R.string.amount_zanihashs_title,
                    User.currentUser().getWallet().getZaniHash()));
        } else {
            mPointsTextView.setText(Objects.requireNonNull(getActivity()).getString(R.string.amount_zanihashs_title,
                    -1));
        }

        mPointsValueTextView.setText(getActivity().getString(R.string.reward_zanihash_desc));

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
                    ContextCompat.getDrawable(Objects.requireNonNull(getActivity()), R.drawable.ico_gift_card));
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
                ((MainActivity) getActivity()).checkLoggedIn();
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
                mPointsTextView.setText(getActivity().getString(R.string.amount_zanihashs_title,
                        User.currentUser().getWallet().getZaniHash()));
            mRewardRecyclerAdapter.notifyItemChanged(0);
        }
    }

    @Override
    public void onCreatePayout() {
        if (isAdded()) {
            new MaterialDialog.Builder(getActivity())
                    .titleGravity(GravityEnum.CENTER)
                    .contentGravity(GravityEnum.CENTER)
                    .title(getString(R.string.youha))
                    .titleColor(ContextCompat.getColor(getActivity(), R.color.colorAccent))
                    .positiveColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark))
                    .content(getString(R.string.dlg_content_reward_zanihash))
                    .negativeText(getString(R.string.ok_exclamation))
                    .limitIconToDefaultSize()
                    .iconRes(R.drawable.ico_gift_card)
                    .show();
        }
        mRewardZaniHashPresenter.loadUser(); // refresh user data
    }

    @Override
    public void showLoadingDialog() {
        if (isAdded()) {
            mLoadingDialog = new MaterialDialog.Builder(getActivity())
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

    }

    @Override
    public void onRewardSelected(final Reward reward) {
         if (User.currentUser().getWallet().getZaniHash() > reward.getPrice()){
            new MaterialDialog.Builder(getActivity())
                    .titleGravity(GravityEnum.CENTER)
                    .contentGravity(GravityEnum.CENTER)
                    .title(getActivity().getString(R.string.dlg_title_confirm_reward))
                    .titleColor(ContextCompat.getColor(getActivity(), R.color.colorAccent))
                    .negativeColor(ContextCompat.getColor(getActivity(), R.color.colorRed800))
                    .content(getActivity().getString(R.string.reward_confirm_zanihash, reward.getPrice(),
                            reward.getValue(), reward.getName()))
                    .negativeText(getActivity().getString(R.string.cancel))
                    .positiveText(getActivity().getString(R.string.yes))
                    .onPositive((dialog, which) -> mRewardZaniHashPresenter.createPayout(reward))
                    .iconRes(R.drawable.ico_gift)
                    .limitIconToDefaultSize()
                    .show();
        } else {
            new MaterialDialog.Builder(getActivity())
                    .titleGravity(GravityEnum.CENTER)
                    .contentGravity(GravityEnum.CENTER)
                    .title(getString(R.string.err_title_oups))
                    .titleColor(ContextCompat.getColor(getActivity(), R.color.colorRed800))
                    .negativeColor(ContextCompat.getColor(getActivity(), R.color.colorRed800))
                    .content(getString(R.string.err_not_enough_zanihashs))
                    .negativeText(getString(R.string.ok_exclamation))
                    .limitIconToDefaultSize()
                    .iconRes(R.drawable.ic_zanihash)
                    .show();
        }
    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
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
