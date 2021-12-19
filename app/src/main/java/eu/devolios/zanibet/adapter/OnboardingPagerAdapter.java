package eu.devolios.zanibet.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import eu.devolios.zanibet.OnboardingActivity;
import eu.devolios.zanibet.R;
import eu.devolios.zanibet.fragment.OnboardingFragment;

/**
 * Created by Gromat Luidgi on 21/12/2017.
 */

public class OnboardingPagerAdapter extends FragmentStatePagerAdapter {

    private Context mContext;

    public OnboardingPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0:
                return OnboardingFragment.newInstance(1,"ic_fan",
                        mContext.getString(R.string.onboarding1_title),
                        mContext.getString(R.string.onboarding1_content));
            case 1:
                return OnboardingFragment.newInstance(2,"ic_versus",
                        mContext.getString(R.string.onboarding2_title),
                        mContext.getString(R.string.onboarding2_content));
            case 2:
                return OnboardingFragment.newInstance(3,"ic_champion_round",
                        mContext.getString(R.string.onboarding3_title),
                        mContext.getString(R.string.onboarding3_content));
            default:
                return OnboardingFragment.newInstance(1,"onboarding1",
                        mContext.getString(R.string.onboarding1_title),
                        mContext.getString(R.string.onboarding1_content));
        }
    }

    @Override
    public int getCount() {
        return OnboardingActivity.NUM_PAGES;
    }
}
