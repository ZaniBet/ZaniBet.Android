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

public class PartnerPayoutStats extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.amountCoinMultiTextView)
    TextView mAmountCoinMultiTextView;
    @BindView(R.id.amountCoinSimpleTextView)
    TextView mAmountCoinSimpleTextView;
    @BindView(R.id.totalCoinTextView)
    TextView mTotalCoinTextView;
    @BindView(R.id.totalTranactionTextView)
    TextView mTotalTransactionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner_payout_stats);

        ButterKnife.bind(this);

        mToolbar.setTitle("Vos gains");
        mToolbar.setNavigationIcon(new IconDrawable(getApplicationContext(), MaterialIcons.md_arrow_back).colorRes(R.color.colorWhite).actionBarSize());
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        setSupportActionBar(mToolbar);

        Referral referral = User.currentUser().getReferral();
        mAmountCoinMultiTextView.setText(Html.fromHtml(getString(R.string.partner_coin_multi_play,
                referral.getTotalCoinMultiTicketPlay())));
        mAmountCoinSimpleTextView.setText(Html.fromHtml(getString(R.string.partner_coin_simple_play,
                referral.getTotalCoinSimpleTicketPlay())));
        mTotalCoinTextView.setText(Html.fromHtml(getString(R.string.partner_total_coin,
                referral.getTotalCoin())));
        mTotalTransactionTextView.setText(Html.fromHtml(getString(R.string.partner_total_transaction,
                referral.getTotalTransaction())));

    }
}
