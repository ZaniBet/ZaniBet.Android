package eu.devolios.zanibet;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.devolios.zanibet.model.ApiError;
import eu.devolios.zanibet.model.GameTicket;
import eu.devolios.zanibet.model.Grille;
import eu.devolios.zanibet.model.Payout;
import eu.devolios.zanibet.model.PayoutInvoice;
import eu.devolios.zanibet.model.Reward;
import eu.devolios.zanibet.presenter.PayoutGrilleDetailsPresenter;
import eu.devolios.zanibet.presenter.contract.PayoutGrilleDetailsContract;
import eu.devolios.zanibet.utils.DateFormatter;

public class PayoutGrilleDetailsActivity extends AppCompatActivity implements PayoutGrilleDetailsContract.View{


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

    PayoutGrilleDetailsPresenter mPayoutGrilleDetailsPresenter;
    Payout mPayout;
    PayoutInvoice mPayoutInvoice;

    private MaterialDialog mLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payout_grille_details);

        ButterKnife.bind(this);

        mPayoutGrilleDetailsPresenter = new PayoutGrilleDetailsPresenter(this, this);
        mPayout = (Payout) getIntent().getSerializableExtra("payout");
        mPayoutInvoice = mPayout.getInvoice();
        Grille grille = Grille.convertFromMap(mPayout.getTarget());
        GameTicket gameTicket = GameTicket.convertFromMap(grille.getGameTicket());

        mToolbar.setTitle(getString(R.string.title_payout_id, mPayout.getReference()));
        mToolbar.setNavigationIcon(new IconDrawable(getApplicationContext(), MaterialIcons.md_arrow_back).colorRes(R.color.colorWhite).actionBarSize());
        mToolbar.setNavigationOnClickListener(v -> onBackPressed());
        setSupportActionBar(mToolbar);

        mTitleTextView.setText(getString(R.string.payout_title_grille));
        mDescriptionTextView.setText(getString(R.string.payout_desc_grille, gameTicket.getName()));
        mReferenceTextView.setText(mPayout.getReference());
        mDateTextView.setText(DateTimeUtils.formatWithPattern(DateFormatter.formatMongoDate(mPayout.getCreatedAt()), "EEEE dd MMMM, yyyy HH:mm"));

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

        if (mPayoutInvoice != null && mPayoutInvoice.getPaymentMethod() != null){
            if (mPayoutInvoice.getPaymentMethod().equals("PayPal")){
                mPayoutImageView.setImageResource(R.drawable.ic_paypal_logo);
            } else if (mPayoutInvoice.getPaymentMethod().equals("Bitcoin")){
                mPayoutImageView.setImageResource(R.drawable.ic_bitcoin_logo);
            }
        }

        /*if (payout.getKind().equals("Reward")){
            mTitleTextView.setText(getString(R.string.payout_title_reward));
            mDescriptionTextView.setText(getString(R.string.payout_desc_reward));
        } else if (payout.getKind().equals("Grille")){
            mTitleTextView.setText(getString(R.string.payout_title_grille));
        }*/

    }

    @Override
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
                        .positiveText(R.string.ok_exclamation)
                        .onPositive((dialog, which) -> {
                            switch(dialog.getSelectedIndex()){
                                case 0:
                                    mPayoutGrilleDetailsPresenter.updatePaymentMethod(mPayout, "PayPal");
                                    break;
                                case 1:
                                    mPayoutGrilleDetailsPresenter.updatePaymentMethod(mPayout, "Bitcoin");
                                    break;
                                default:
                                    break;
                            }
                           })
                        .show();
                return false;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void showUpdateLoading() {
        mLoadingDialog = new MaterialDialog.Builder(this)
                .title(getString(R.string.ldg_title_updating_payment))
                .content(getString(R.string.ldg_content_updating_payment))
                .progress(true, 0)
                .show();
    }

    @Override
    public void hideUpdateLoading() {
        if (!isFinishing() && mLoadingDialog != null){
            mLoadingDialog.dismiss();
        }
    }

    @Override
    public void onUpdatePaymentMethodSuccess(Payout payout) {
        mPayout = payout;
        mPayoutInvoice = mPayout.getInvoice();

        runOnUiThread(() -> {
            mPaymentMethodTextView.setText(mPayoutInvoice.getPaymentMethod());
            mPaymentAddressTextView.setText(mPayoutInvoice.getPaymentAddress());
            if (mPayoutInvoice.getPaymentMethod().equals("PayPal")){
                mPayoutImageView.setImageResource(R.drawable.ic_paypal_logo);
            } else if (mPayoutInvoice.getPaymentMethod().equals("Bitcoin")){
                mPayoutImageView.setImageResource(R.drawable.ic_bitcoin_logo);
            }
        });
    }

    @Override
    public void onUpdatePaymentMethodError(ApiError apiError) {
        new MaterialDialog.Builder(this)
                .title(apiError.getTitle())
                .titleColorRes(R.color.colorRed800)
                .content(apiError.getMessage())
                .negativeText(getString(R.string.ok_exclamation))
                .limitIconToDefaultSize()
                .iconRes(R.drawable.ico_stop)
                .negativeColorRes(R.color.colorRed800)
                .show();
    }
}

