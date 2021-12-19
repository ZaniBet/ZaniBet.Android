package eu.devolios.zanibet.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.google.firebase.analytics.FirebaseAnalytics;

import eu.devolios.zanibet.utils.ZaniBetTracking;

/**
 * Created by Gromat Luidgi on 09/11/2017.
 */

public class BaseFragment extends Fragment {

    FragmentNavigation mFragmentNavigation;
    ZaniBetTracking mZaniBetTracking;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentNavigation) {
            mFragmentNavigation = (FragmentNavigation) context;
        }

        if (context instanceof ZaniBetTracking){
            mZaniBetTracking = (ZaniBetTracking) context;
        }
    }

    public interface FragmentNavigation{
        void pushFragment(Fragment fragment);
        void replaceFragment(Fragment fragment);
        void popFragment();

    }

}
