package eu.devolios.zanibet.fragment.pager;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.devolios.zanibet.R;
import eu.devolios.zanibet.adapter.BasicPagerAdapter;
import eu.devolios.zanibet.fragment.BaseFragment;
import eu.devolios.zanibet.fragment.RewardFragment;
import eu.devolios.zanibet.fragment.RewardZaniHashFragment;

/**
 * Created by Gromat Luidgi on 26/03/2018.
 */

public class RewardPagerFragment extends BaseFragment {

    @BindView(R.id.tabLayout)
    TabLayout mTabLayout;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;

    private BasicPagerAdapter mBasicPagerAdapter;

    public static RewardPagerFragment newInstance(){
        return new RewardPagerFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBasicPagerAdapter = new BasicPagerAdapter(getChildFragmentManager());
        mBasicPagerAdapter.addFragment(RewardFragment.newInstance(),
                getString(R.string.title_reward));
        mBasicPagerAdapter.addFragment(RewardZaniHashFragment.newInstance(),
                getString(R.string.title_bonuses));
        //mBasicPagerAdapter.addFragment(TransactionFragment.newInstance(), getString(R.string.zanihash_wallet));
        //mBasicPagerAdapter.addFragment(TransactionStatsFragment.newInstance(), getString(R.string.zanihash_wallet));
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.shop_pager_fragment, container, false);
        ButterKnife.bind(this, view);

        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setAdapter(mBasicPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(Objects.requireNonNull(getActivity()), R.color.colorWhiteLynx));
        return view;
    }
}
