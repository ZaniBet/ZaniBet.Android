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

public class PartnerInvitedStatsActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.amountInvitedTextView)
    TextView mAmountInvitedTextView;
    @BindView(R.id.amountInvitedMonthTextView)
    TextView mAmountInvitedMonthTextView;
    @BindView(R.id.amountInvitedTodayTextView)
    TextView mAmountInvitedTodayTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner_invited_stats);
        ButterKnife.bind(this);

        mToolbar.setTitle("Statistiques parrainage");
        mToolbar.setNavigationIcon(new IconDrawable(getApplicationContext(), MaterialIcons.md_arrow_back).colorRes(R.color.colorWhite).actionBarSize());
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        setSupportActionBar(mToolbar);

        Referral referral = User.currentUser().getReferral();
        mAmountInvitedTextView.setText(Html.fromHtml(getString(R.string.partner_total_invited, referral.getTotalReferred())));
        mAmountInvitedMonthTextView.setText(Html.fromHtml(getString(R.string.partner_total_invited_month, referral.getTotalReferredMonth())));
        mAmountInvitedTodayTextView.setText(Html.fromHtml(getString(R.string.partner_total_invited_today, referral.getTotalReferredToday())));

    }
}
