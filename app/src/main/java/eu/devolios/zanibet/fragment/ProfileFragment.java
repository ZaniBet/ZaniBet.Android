package eu.devolios.zanibet.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.analytics.FirebaseAnalytics;

import eu.devolios.zanibet.AccountDetailsActivity;
import eu.devolios.zanibet.DispatchActivity;
import eu.devolios.zanibet.InviteCodeActivity;
import eu.devolios.zanibet.InviteFriendsPagerActivity;
import eu.devolios.zanibet.MainActivity;
import eu.devolios.zanibet.ProfileDetailsActivity;
import eu.devolios.zanibet.R;
import eu.devolios.zanibet.adapter.ProfileRecyclerAdapter;
import eu.devolios.zanibet.adapter.decorator.HeaderDecoration;
import eu.devolios.zanibet.model.ApiError;
import eu.devolios.zanibet.model.User;
import eu.devolios.zanibet.presenter.contract.ProfileContract;
import eu.devolios.zanibet.presenter.ProfilePresenter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Gromat Luidgi on 09/11/2017.
 */

public class ProfileFragment extends BaseFragment implements ProfileContract.View, ProfileRecyclerAdapter.Listener {

    @BindView(eu.devolios.zanibet.R.id.recyclerView)
    RecyclerView recyclerView;

    private TextView mPointsTextView;

    private ProfilePresenter mProfilePresenter;
    private LinearLayoutManager mLinearLayoutManager;
    private ProfileRecyclerAdapter mProfileRecyclerAdapter;
    private ArrayList<ProfilePresenter.Menu> mMenuArrayList;
    private FirebaseAnalytics mFirebaseAnalytics;


    public static ProfileFragment newInstance(){
        return new ProfileFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(Objects.requireNonNull(getActivity()));
        mProfilePresenter = new ProfilePresenter(this, getActivity());
        mMenuArrayList = new ArrayList<>();
        mProfileRecyclerAdapter = new ProfileRecyclerAdapter(getActivity(), mMenuArrayList, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mProfileRecyclerAdapter != null){
            if (mPointsTextView != null) mPointsTextView.setText(getActivity().getString(R.string.amount_zanicoins, User.currentUser().getPoint()));
            mProfileRecyclerAdapter.notifyItemChanged(0);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(eu.devolios.zanibet.R.layout.profile_fragment, container, false);
        ButterKnife.bind(this, view);
        ((MainActivity) Objects.requireNonNull(getActivity())).updateTitle(getActivity().getString(R.string.title_profile));
        mFirebaseAnalytics.setCurrentScreen(getActivity(), "Mon Profil", "ProfileFragment");

        mLinearLayoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(mLinearLayoutManager);
        recyclerView.setAdapter(mProfileRecyclerAdapter);
        recyclerView.addItemDecoration(initHeaderDecoration(inflater));
        mProfilePresenter.load();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Crashlytics.setString("last_ui_viewed", "ProfileFragment");

    }

    private HeaderDecoration initHeaderDecoration(LayoutInflater inflater){
        View view = inflater.inflate(eu.devolios.zanibet.R.layout.profile_header_decoration_item, null);
        ImageView imageView = view.findViewById(R.id.imageView);
        TextView pseudoTextView = view.findViewById(R.id.pseudoTextView);
        mPointsTextView = view.findViewById(R.id.pointsTextView);

        pseudoTextView.setText(User.currentUser().getUsername());
        mPointsTextView.setText(getActivity().getString(R.string.amount_zanicoins, User.currentUser().getPoint()));
        HeaderDecoration.Builder builder = new HeaderDecoration.Builder(getActivity()).setView(view);
        return builder.build();
    }

    @Override
    public void onMenuSelected(ProfilePresenter.Menu menu) {
        Intent intent;
        switch (menu.getId()){
            case ProfilePresenter.Menu.PRIVATE_INFO_MENU :
                intent = new Intent(getActivity(), ProfileDetailsActivity.class);
                startActivity(intent);
                break;
            case ProfilePresenter.Menu.PAYOUT_MENU :
                if (mFragmentNavigation != null) mFragmentNavigation.pushFragment(new PayoutFragment());
                break;
            case ProfilePresenter.Menu.RULES_MENU :
                if (mFragmentNavigation != null) mFragmentNavigation.pushFragment(new GameRulesFragment());
                break;
            case ProfilePresenter.Menu.MY_ACCOUNT_MENU :
                intent = new Intent(getActivity(), AccountDetailsActivity.class);
                startActivity(intent);
                break;
            case ProfilePresenter.Menu.ABOUT_MENU :
                if (mFragmentNavigation != null) mFragmentNavigation.pushFragment(new AboutFragment());
                break;
            case ProfilePresenter.Menu.INVITE_CODE_MENU:
                intent = new Intent(getActivity(), InviteCodeActivity.class);
                startActivity(intent);
                break;
            case ProfilePresenter.Menu.INVITE_FRIENDS_MENU:
                intent = new Intent(getActivity(), InviteFriendsPagerActivity.class);
                startActivity(intent);
                break;
            case ProfilePresenter.Menu.DISCONNECT_MENU:{
                intent = new Intent(getActivity(), DispatchActivity.class);
                User.clearUserPreference();
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                Objects.requireNonNull(getActivity()).finish();
                break;
            }
        }
    }

    @Override
    public void addMenus(List<ProfilePresenter.Menu> menuList) {
        mMenuArrayList.clear();
        mMenuArrayList.addAll(menuList);
        mProfileRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoad(User user) {
        if (isAdded()){
            mProfileRecyclerAdapter.notifyItemChanged(0);
        }
    }

    @Override
    public void showLoadError(ApiError apiError) {
        if (isAdded() && isVisible()) {
            ((MainActivity)getActivity()).checkLoggedIn();

            new MaterialDialog.Builder(getActivity())
                    .title(apiError.getTitle())
                    .titleColor(ContextCompat.getColor(getActivity(), eu.devolios.zanibet.R.color.colorRed800))
                    .negativeColor(ContextCompat.getColor(getActivity(), eu.devolios.zanibet.R.color.colorRed800))
                    .content(apiError.getMessage())
                    .negativeText(getString(R.string.ok_exclamation))
                    .show();
        }
    }


}
