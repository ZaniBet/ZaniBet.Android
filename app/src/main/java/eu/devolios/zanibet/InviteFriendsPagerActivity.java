package eu.devolios.zanibet;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.MaterialIcons;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.devolios.zanibet.adapter.BasicPagerAdapter;
import eu.devolios.zanibet.fragment.InviteFriendsFragment;
import eu.devolios.zanibet.fragment.InvitedFriendsFragment;
import eu.devolios.zanibet.fragment.PartnershipFragment;
import eu.devolios.zanibet.model.User;

public class InviteFriendsPagerActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tabLayout)
    TabLayout mTabLayout;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;

    private BasicPagerAdapter mBasicPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pager_invite_friends);
        ButterKnife.bind(this);

        mToolbar.setTitle(getString(R.string.title_share_zanibet));
        mToolbar.setNavigationIcon(new IconDrawable(getApplicationContext(), MaterialIcons.md_arrow_back).colorRes(R.color.colorWhite).actionBarSize());
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        setSupportActionBar(mToolbar);

        mBasicPagerAdapter = new BasicPagerAdapter(getSupportFragmentManager());
        mBasicPagerAdapter.addFragment(InviteFriendsFragment.newInstance(),
                getString(R.string.invite));
        mBasicPagerAdapter.addFragment(InvitedFriendsFragment.newInstance(),
                getString(R.string.my_invited));

        if (User.currentUser().getRole() != null && User.currentUser().getRole().equals("partner")){
            mBasicPagerAdapter.addFragment(PartnershipFragment.newInstance(),
                    "Partenariat");
        }

        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setAdapter(mBasicPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }
}
