package eu.devolios.zanibet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.MaterialIcons;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.devolios.zanibet.model.Referral;
import eu.devolios.zanibet.model.User;

public class PartnerBonusActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.coinMultiTextView)
    TextView mCoinMultiTextView;
    @BindView(R.id.coinSimpleTextView)
    TextView mCoinSimpleTextView;
    @BindView(R.id.coinPercentTextView)
    TextView mCoinPercentTextView;
    @BindView(R.id.invitedRewardTextView)
    TextView mInvitedRewardTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner_bonus);

        ButterKnife.bind(this);

        mToolbar.setTitle("Mes avantages uniques");
        mToolbar.setNavigationIcon(new IconDrawable(getApplicationContext(), MaterialIcons.md_arrow_back).colorRes(R.color.colorWhite).actionBarSize());
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        setSupportActionBar(mToolbar);

        Referral referral = User.currentUser().getReferral();
        if (referral != null){
            mCoinMultiTextView.setText(Html.fromHtml(getString(R.string.partner_multi_play, referral.getCoinPerMultiTicketPlay())));
            mCoinSimpleTextView.setText(Html.fromHtml(getString(R.string.partner_simple_play, referral.getCoinPerSimpleTicketPlay())));
            mCoinPercentTextView.setText(Html.fromHtml(getString(R.string.partner_percent_simple, referral.getCoinRewardPercent())));
            mInvitedRewardTextView.setText(Html.fromHtml(getString(R.string.partner_invited_reward, referral.getInvitationBonus())));
        }
    }
}
