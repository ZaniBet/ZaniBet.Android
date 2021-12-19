package eu.devolios.zanibet;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.liangfeizc.RubberIndicator;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.devolios.zanibet.adapter.OnboardingPagerAdapter;

public class OnboardingActivity extends AppCompatActivity {

    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    @BindView(R.id.rubber)
    RubberIndicator mRubberIndicator;
    @BindView(R.id.nextButton)
    Button mNextButton;

    public static final int NUM_PAGES = 3;

    private OnboardingPagerAdapter mOnboardingPagerAdapter;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);
        ButterKnife.bind(this);

        Crashlytics.setString("last_ui_viewed", "OnboardingActivity");

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mRubberIndicator.setCount(NUM_PAGES, 0);

        mOnboardingPagerAdapter = new OnboardingPagerAdapter(getSupportFragmentManager(), this);
        mViewPager.setAdapter(mOnboardingPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                switch(position){
                    case 0:
                        mFirebaseAnalytics.setCurrentScreen(getParent(), "Onboarding 1", null /* class override */);
                        break;
                    case 1:
                        mFirebaseAnalytics.setCurrentScreen(getParent(), "Onboarding 2", null /* class override */);
                        break;
                    case 2:
                        mFirebaseAnalytics.setCurrentScreen(getParent(), "Onboarding 3", null /* class override */);
                        break;
                    default:
                        break;
                }

                if (mRubberIndicator.getFocusPosition() > position){
                    logOnboardingBack(position+1);
                    moveToLeft(null);
                } else {
                    logOnboardingNext(position+1);
                    moveToRight(null);
                }

                if (position == 2){
                    mNextButton.setVisibility(View.GONE);
                } else {
                    mNextButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOnboardingNext(mViewPager.getCurrentItem()+1);
                mViewPager.setCurrentItem(mViewPager.getCurrentItem()+1);
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (mViewPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
        }
    }

    private void logOnboardingNext(int position){
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        mFirebaseAnalytics.logEvent("onboarding_next", bundle);
    }

    private void logOnboardingBack(int position){
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        mFirebaseAnalytics.logEvent("onboarding_back", bundle);
    }

    public void moveToRight(View view) {
        mRubberIndicator.moveToRight();
    }

    public void moveToLeft(View view) {
        mRubberIndicator.moveToLeft();
    }

}
