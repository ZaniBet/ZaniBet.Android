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

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.EntypoIcons;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.joanzapata.iconify.fonts.MaterialIcons;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.devolios.zanibet.MainActivity;
import eu.devolios.zanibet.R;
import eu.devolios.zanibet.adapter.IconOnlyPagerAdapter;
import eu.devolios.zanibet.fragment.BaseFragment;
import eu.devolios.zanibet.fragment.GrilleGroupFragment;

/**
 * Created by Gromat Luidgi on 09/11/2017.
 */

public class GrilleGroupPagerFragment extends BaseFragment {

    @BindView(R.id.tabLayout)
    TabLayout mTabLayout;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;

    public static final int CURRENT_GRILLE = 1;
    public static final int LOOSE_GRILLE = 2;
    public static final int WIN_GRILLE = 3;
    public static final int SIMPLE_GRILLE = 4;
    public static final int TOURNAMENT_GRILLE = 5;

    private IconOnlyPagerAdapter mGrillePagerAdapter;

    public static GrilleGroupPagerFragment newInstance(){
        return new GrilleGroupPagerFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGrillePagerAdapter = new IconOnlyPagerAdapter(getChildFragmentManager());
        mGrillePagerAdapter.addFragment(GrilleGroupFragment.newInstance(CURRENT_GRILLE));
        mGrillePagerAdapter.addFragment(GrilleGroupFragment.newInstance(LOOSE_GRILLE));
        mGrillePagerAdapter.addFragment(GrilleGroupFragment.newInstance(SIMPLE_GRILLE));
        mGrillePagerAdapter.addFragment(GrilleGroupFragment.newInstance(WIN_GRILLE));
        mGrillePagerAdapter.addFragment(GrilleGroupFragment.newInstance(TOURNAMENT_GRILLE));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.grille_pager_fragment, container, false);
        ButterKnife.bind(this, view);
        ((MainActivity) Objects.requireNonNull(getActivity())).updateTitle(getString(R.string.title_my_grid));

        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setAdapter(mGrillePagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(getActivity(), R.color.colorWhiteLynx));

        for (int i = 0; i < mTabLayout.getTabCount(); i++) {
            /*mTabLayout.getTabAt(i).setIcon(new IconDrawable(getActivity(), FontAwesomeIcons.fa_check)
            .colorRes(R.color.colorWhiteLynx)
            .sizeDp(24));*/
            switch (i){
                case 0:
                    Objects.requireNonNull(mTabLayout.getTabAt(i)).setIcon(new IconDrawable(getActivity(), FontAwesomeIcons.fa_clock_o)
                            .colorRes(R.color.colorWhiteLynx)
                            .sizeDp(24));
                    //mTabLayout.getTabAt(i).setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_ttab_clock));
                    break;
                case 1:
                    Objects.requireNonNull(mTabLayout.getTabAt(i)).setIcon(new IconDrawable(getActivity(), MaterialIcons.md_view_carousel)
                            .colorRes(R.color.colorWhiteLynx)
                            .sizeDp(24));
                    //mTabLayout.getTabAt(i).setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_ttab_clock));
                    break;
                case 2:
                    Objects.requireNonNull(mTabLayout.getTabAt(i)).setIcon(new IconDrawable(getActivity(), MaterialIcons.md_view_day)
                            .colorRes(R.color.colorWhiteLynx)
                            .sizeDp(24));
                    //mTabLayout.getTabAt(i).setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_ttab_clock));
                    break;
                case 3:
                    Objects.requireNonNull(mTabLayout.getTabAt(i)).setIcon(new IconDrawable(getActivity(), FontAwesomeIcons.fa_paypal)
                            .colorRes(R.color.colorWhiteLynx)
                            .sizeDp(24));
                    //mTabLayout.getTabAt(i).setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_ttab_jackpot));
                    break;
                case 4:
                    Objects.requireNonNull(mTabLayout.getTabAt(i)).setIcon(new IconDrawable(getActivity(), EntypoIcons.entypo_trophy)
                            .colorRes(R.color.colorWhiteLynx)
                            .sizeDp(24));
                    //mTabLayout.getTabAt(i).setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_ttab_tournament));
                    break;
            }
        }

        return view;
    }
}
