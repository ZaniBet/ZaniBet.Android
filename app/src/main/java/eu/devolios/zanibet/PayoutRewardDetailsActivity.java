package eu.devolios.zanibet;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.thunder413.datetimeutils.DateTimeUtils;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.MaterialIcons;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.devolios.zanibet.model.Payout;
import eu.devolios.zanibet.model.PayoutInvoice;
import eu.devolios.zanibet.model.Reward;
import eu.devolios.zanibet.utils.DateFormatter;

public class PayoutRewardDetailsActivity extends AppCompatActivity {


    @BindView(R.id.toolbar)
    Toolbar mToolbar;


    @BindView(R.id.payoutImageView)
    ImageView mPayoutImageView;
    @BindView(R.id.titleTextView)
    TextView mTitleTextView;
    @BindView(R.id.descriptionTextView)
    TextView mDescriptionTextView;
    @BindView(R.id.referenceTextView)
    TextView mReferenceTextView;
    @BindView(R.id.dateTextView)
    TextView mDateTextView;
    @BindView(R.id.priceTextView)
    TextView mPriceTextView;
    @BindView(R.id.amountTextView)
    TextView mAmountTextView;
    @BindView(R.id.paymentMethodTextView)
    TextView mPaymentMethodTextView;
    @BindView(R.id.addressTextView)
    TextView mPaymentAddressTextView;
    @BindView(R.id.statusTextView)
    TextView mStatusTextView;

    @BindView(R.id.statusLayout)
    LinearLayout mStatusLayout;
    @BindView(R.id.statusImageView)
    ImageView mStatusImageView;
    @BindView(R.id.statusDescTextView)
    TextView mStatusDescTextView;

    Payout mPayout;
    PayoutInvoice mPayoutInvoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payout_reward_details);

        ButterKnife.bind(this);

        mPayout = (Payout) getIntent().getSerializableExtra("payout");
        if (mPayout == null) {
            finish();
            return;
        }
        mPayoutInvoice = mPayout.getInvoice();
        Reward reward = Reward.convertFromMap(mPayout.getTarget());


        mToolbar.setTitle(getString(R.string.title_payout_id, mPayout.getReference()));
        mToolbar.setNavigationIcon(new IconDrawable(getApplicationContext(), MaterialIcons.md_arrow_back).colorRes(R.color.colorWhite).actionBarSize());
        mToolbar.setNavigationOnClickListener(v -> onBackPressed());
        setSupportActionBar(mToolbar);

        if (reward.getBrand().equals("Amazon")){
            mPayoutImageView.setImageResource(R.drawable.ic_amazon_logo);
        } else if (reward.getBrand().equals("Bitcoin")){
            mPayoutImageView.setImageResource(R.drawable.ic_bitcoin_logo);
        } else {
            mPayoutImageView.setImageResource(R.drawable.ic_paypal_logo);
        }

        mTitleTextView.setText(getString(R.string.payout_title_reward));
        mDescriptionTextView.setText(getString(R.string.payout_desc_reward));
        mReferenceTextView.setText(mPayout.getReference());
        mDateTextView.setText(DateTimeUtils.formatWithPattern(DateFormatter.formatMongoDate(mPayout.getCreatedAt()), "EEEE dd MMMM, yyyy HH:mm"));


        mPriceTextView.setText(getString(R.string.amount_zanicoins, mPayoutInvoice.getPrice()));
        mAmountTextView.setText(getString(R.string.amount_float, mPayoutInvoice.getAmount()));
        mPaymentMethodTextView.setText(mPayoutInvoice.getPaymentMethod());
        mPaymentAddressTextView.setText(mPayoutInvoice.getPaymentAddress());

        if (mPayout.getStatus().equals("waiting_paiement")){
            mStatusTextView.setText(getString(R.string.status_payout_waiting_paiement));
            mStatusDescTextView.setText(Html.fromHtml(getString(R.string.status_payout_desc_waiting_paiement)));
            mStatusImageView.setImageResource(R.drawable.ic_payout_pending);
        } else if (mPayout.getStatus().equals("paid")){
            mStatusTextView.setText(getString(R.string.status_payout_paid));
            mStatusDescTextView.setText(Html.fromHtml(getString(R.string.status_payout_desc_paid)));
            mStatusImageView.setImageResource(R.drawable.ic_payout_success);
        } else if (mPayout.getStatus().equals("verification")){
            mStatusTextView.setText(getString(R.string.status_payout_verification));
            mStatusDescTextView.setText(Html.fromHtml(getString(R.string.status_payout_desc_verification)));
            mStatusImageView.setImageResource(R.drawable.ic_payout_verification);
        } else if (mPayout.getStatus().equals("fraud")){
            mStatusTextView.setText(getString(R.string.status_payout_fraud));
            mStatusDescTextView.setText(Html.fromHtml(getString(R.string.status_payout_desc_fraud)));
            mStatusImageView.setImageResource(R.drawable.ic_payout_error);
        } else if (mPayout.getStatus().equals("missing_data")){
            mStatusTextView.setText(getString(R.string.status_payout_missing_data));
            mStatusDescTextView.setText(Html.fromHtml(getString(R.string.status_payout_desc_missing_data)));
            mStatusImageView.setImageResource(R.drawable.ic_payout_missing_data);
        } else if (mPayout.getStatus().equals("canceled")){
            mStatusTextView.setText(getString(R.string.status_payout_canceled));
            mStatusDescTextView.setText(Html.fromHtml(getString(R.string.status_payout_desc_waiting_paiement)));
            mStatusImageView.setImageResource(R.drawable.ic_payout_error);
        } else if (mPayout.getStatus().equals("invalid_data")){
            mStatusTextView.setText(getString(R.string.status_payout_invalid_data));
            mStatusDescTextView.setText(Html.fromHtml(getString(R.string.status_payout_desc_invalid_data)));
            mStatusImageView.setImageResource(R.drawable.ic_payout_error);
        } else if (mPayout.getStatus().equals("invalid_paypal")){
            mStatusTextView.setText(getString(R.string.status_payout_invalid_paypal));
            mStatusDescTextView.setText(Html.fromHtml(getString(R.string.status_payout_desc_invalid_paypal)));
            mStatusImageView.setImageResource(R.drawable.ic_payout_error);
        } else if (mPayout.getStatus().equals("invalid_bitcoin")){
            mStatusTextView.setText(getString(R.string.status_payout_invalid_bitcoin));
            mStatusDescTextView.setText(Html.fromHtml(getString(R.string.status_payout_desc_invalid_bitcoin)));
            mStatusImageView.setImageResource(R.drawable.ic_payout_error);
        } else {
            mStatusLayout.setVisibility(View.GONE);
            mStatusTextView.setText(mPayout.getStatus());
        }

        /*if (payout.getKind().equals("Reward")){
            mTitleTextView.setText(getString(R.string.payout_title_reward));
            mDescriptionTextView.setText(getString(R.string.payout_desc_reward));
        } else if (payout.getKind().equals("Grille")){
            mTitleTextView.setText(getString(R.string.payout_title_grille));
        }*/

    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mPayout != null && mPayout.getStatus().equals("waiting_paiement")) {
            getMenuInflater().inflate(R.menu.payout_reward_menu, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                int currentIndex = -1;
                if (mPayoutInvoice.getPaymentMethod().equals("PayPal")){
                    currentIndex = 0;
                } else if (mPayoutInvoice.getPaymentMethod().equals("Bitcoin")){
                    currentIndex = 1;
                }

                new MaterialDialog.Builder(this)
                        .title(R.string.dlg_title_edit_payout)
                        .content(R.string.dlg_content_edit_payout)
                        .items(R.array.payment_method)
                        .itemsCallbackSingleChoice(currentIndex, (dialog, view, which, text) -> {

                            return true;
                        })
                        .negativeText(R.string.cancel)
                        .positiveText(R.string.ok)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                               int toto =  dialog.getSelectedIndex();
                                Log.d("Payout", String.valueOf(toto));
                               }
                        })
                        .show();
                return false;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }*/
}

