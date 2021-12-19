package eu.devolios.zanibet.fragment.pager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.analytics.FirebaseAnalytics;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.devolios.zanibet.R;
import eu.devolios.zanibet.adapter.BasicPagerAdapter;
import eu.devolios.zanibet.fragment.BaseFragment;
import eu.devolios.zanibet.fragment.GameTicketFragment;
import eu.devolios.zanibet.fragment.GameTicketSingleFragment;
import eu.devolios.zanibet.fragment.GameTicketTournamentFragment;

/**
 * Created by Gromat Luidgi on 05/01/2018.
 */

public class GameTicketPagerFragment extends BaseFragment {

    @BindView(R.id.tabLayout)
    TabLayout mTabLayout;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;

    private BasicPagerAdapter mBasicPagerAdapter;

    public static GameTicketPagerFragment newInstance(){
        GameTicketPagerFragment gameTickerPagerFragment = new GameTicketPagerFragment();
        return gameTickerPagerFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBasicPagerAdapter = new BasicPagerAdapter(getChildFragmentManager());
        mBasicPagerAdapter.addFragment(GameTicketFragment.newInstance(),
                getString(R.string.title_multi_ticket));
        mBasicPagerAdapter.addFragment(GameTicketSingleFragment.newInstance(),
                getString(R.string.title_single_ticket));
        mBasicPagerAdapter.addFragment(GameTicketTournamentFragment.newInstance(),
                getString(R.string.title_ticket_tournament));

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.game_ticket_pager_fragment, container, false);
        ButterKnife.bind(this, view);

        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setAdapter(mBasicPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(getActivity(), R.color.colorWhiteLynx));
        return view;
    }
}
