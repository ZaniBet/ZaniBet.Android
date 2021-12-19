package eu.devolios.zanibet.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import eu.devolios.zanibet.MainActivity;
import eu.devolios.zanibet.R;

import butterknife.ButterKnife;
//import mehdi.sakout.aboutpage.AboutPage;

/**
 * Created by Gromat Luidgi on 09/11/2017.
 */

public class AboutFragment extends BaseFragment{

    public static AboutFragment newInstance(){
        AboutFragment aboutFragment = new AboutFragment();
        return aboutFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.about_fragment, container, false);
        ButterKnife.bind(this, view);
        ((MainActivity)getActivity()).updateTitle(getActivity().getString(R.string.title_about));

        /*View aboutPage = new AboutPage(getActivity())
                .isRTL(false)
                .setDescription(getActivity().getString(R.string.about_content))
                .setImage(R.drawable.zanibet_logo)
                //.addItem(versionElement)
                //.addItem(adsElement)
                .addGroup(getActivity().getString(R.string.stay_in_touch))
                .addEmail("contact@zanibet.com")
                .addWebsite("https://www.zanibet.com")
                .addWebsite("https://blog.zanibet.com", "Blog")
                .addFacebook("zanibetfoot")
                .addTwitter("FootballZanibet")
                .addPlayStore("eu.devolios.zanibet")
                .create();
        return aboutPage;*/
        return view;
    }
}
