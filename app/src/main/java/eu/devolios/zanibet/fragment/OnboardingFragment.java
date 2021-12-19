package eu.devolios.zanibet.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.kingfisher.easy_sharedpreference_library.SharedPreferencesManager;
import com.rilixtech.materialfancybutton.MaterialFancyButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.devolios.zanibet.DispatchActivity;
import eu.devolios.zanibet.R;
import eu.devolios.zanibet.utils.Constants;

/**
 * Created by Gromat Luidgi on 21/12/2017.
 */

public class OnboardingFragment extends Fragment {

    @BindView(R.id.imageView)
    ImageView mImageView;
    @BindView(R.id.titleTextView)
    TextView mTitleTextView;
    @BindView(R.id.descriptionTextView)
    TextView mDescriptionTextView;
    @BindView(R.id.playButton)
    MaterialFancyButton mPlayButton;

    private FirebaseAnalytics mFirebaseAnalytics;

    public static OnboardingFragment newInstance(int position, String image, String title, String descripion){
        OnboardingFragment fragment = new OnboardingFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        bundle.putString("image", image);
        bundle.putString("title", title);
        bundle.putString("description", descripion);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.onboarding_fragment, container, false);
        ButterKnife.bind(this, rootView);


        assert getArguments() != null;
        String image = getArguments().getString("image");
        String title = getArguments().getString("title");
        String description = getArguments().getString("description");
        int position = getArguments().getInt("position");

        if (position == 3){
            mPlayButton.setVisibility(View.VISIBLE);
        }

        mPlayButton.setOnClickListener(view -> {
            logOnboardingDone();
            SharedPreferencesManager.getInstance().putValue(Constants.FIRST_OPEN_PREF, false);
            Intent intent = new Intent(getActivity(), DispatchActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

        mImageView.setImageDrawable(ContextCompat.getDrawable(getActivity(),
                getResources().getIdentifier(image, "drawable", getActivity().getPackageName())));
        mTitleTextView.setText(title);
        mDescriptionTextView.setText(description);
        return rootView;
    }

    private void logOnboardingDone(){
        mFirebaseAnalytics.logEvent("onboarding_done", null);
    }
}
