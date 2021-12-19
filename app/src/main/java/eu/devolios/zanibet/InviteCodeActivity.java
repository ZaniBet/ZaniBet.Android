package eu.devolios.zanibet;

import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.crashlytics.android.Crashlytics;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.MaterialIcons;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.rilixtech.materialfancybutton.MaterialFancyButton;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.devolios.zanibet.model.ApiError;
import eu.devolios.zanibet.presenter.InvitationCodePresenter;
import eu.devolios.zanibet.presenter.contract.InvitationCodeContract;

public class InviteCodeActivity extends AppCompatActivity implements InvitationCodeContract.View, Validator.ValidationListener {


    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.invitationCodeEditText)
    @NotEmpty
    EditText mInvitationCodeEditText;
    @BindView(R.id.validateCodeButton)
    MaterialFancyButton mValidateCodeButton;
    @BindView(R.id.invitationWelcomeTextView)
    TextView mInvitationWelcomeTextView;

    private Validator mValidator;
    private InvitationCodePresenter mInvitationCodePresenter;
    private MaterialDialog mLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_code);
        ButterKnife.bind(this);

        mToolbar.setTitle(getString(R.string.invite_code));
        mToolbar.setNavigationIcon(new IconDrawable(getApplicationContext(), MaterialIcons.md_arrow_back).colorRes(R.color.colorWhite).actionBarSize());
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        setSupportActionBar(mToolbar);

        mInvitationCodePresenter = new InvitationCodePresenter(getApplicationContext(), this);

        mValidator = new Validator(this);
        mValidator.setValidationListener(this);

        mValidateCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mValidator.validate();
            }
        });

        mInvitationWelcomeTextView.setText(Html.fromHtml(getString(R.string.invitation_welcome)));
    }

    @Override
    public void showLoadingDialog() {
        mLoadingDialog = new MaterialDialog.Builder(this)
                .title(getString(R.string.ldg_title_update_paiement))
                .content(getString(R.string.ldg_content_update_paiement))
                .progress(true, 0)
                .show();
    }

    @Override
    public void hideLoadingDialog() {
        if (mLoadingDialog != null){
            mLoadingDialog.dismiss();
        }
    }

    @Override
    public void onValidateCode(int bonus) {
        new MaterialDialog.Builder(this)
                .title(getString(R.string.youha))
                .titleColor(ContextCompat.getColor(this, R.color.colorAccent))
                .positiveColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
                .content(getString(R.string.dlg_content_invitation_success, bonus))
                .positiveText(getString(R.string.ok_exclamation))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        finish();
                    }
                })
                .show();
    }

    @Override
    public void showErrorDialog(ApiError apiError) {
        try {
            new MaterialDialog.Builder(this)
                    .title(getString(R.string.err_title_oups))
                    .titleColor(ContextCompat.getColor(this, R.color.colorRed800))
                    .negativeColor(ContextCompat.getColor(this, R.color.colorRed800))
                    .content(apiError.getMessage())
                    .negativeText(getString(R.string.ok_exclamation))
                    .show();
        } catch (Exception e){
            Crashlytics.logException(e);
        }
    }

    @Override
    public void onValidationSucceeded() {
        mInvitationCodePresenter.validateCode(mInvitationCodeEditText.getText().toString());
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            // Display error messages ;)
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }
}
