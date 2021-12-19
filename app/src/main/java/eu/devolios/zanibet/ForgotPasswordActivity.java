package eu.devolios.zanibet;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import eu.devolios.zanibet.model.ApiError;
import eu.devolios.zanibet.presenter.contract.ForgotPasswordContract;
import eu.devolios.zanibet.presenter.ForgotPasswordPresenter;

import com.crashlytics.android.Crashlytics;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ForgotPasswordActivity extends AppCompatActivity implements ForgotPasswordContract.View, Validator.ValidationListener {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.emailEditText)
    @NotEmpty
    @Email
    EditText mEmailEditText;

    @BindView(R.id.restorePasswordButton)
    Button mRestorePasswordButton;

    @BindView(R.id.helpButton)
    Button mHelpButton;

    private ForgotPasswordPresenter mForgotPasswordPresenter;
    private MaterialDialog mLoadingDialog;
    private MaterialDialog mErrorDialog;
    private Validator mValidator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        ButterKnife.bind(this);

        mForgotPasswordPresenter = new ForgotPasswordPresenter(getApplicationContext(), this);

        mToolbar.setBackgroundColor(Color.TRANSPARENT);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mValidator = new Validator(this);
        mValidator.setValidationListener(this);

        mRestorePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mValidator.validate();
            }
        });
    }

    @Override
    public void showResetPasswordLoading() {
        mLoadingDialog = new MaterialDialog.Builder(this)
                .title(getString(R.string.ldg_title_restore_password))
                .content(getString(R.string.ldg_content_restore_password))
                .progress(true, 0)
                .show();
    }

    @Override
    public void hideResetPasswordLoading() {
            if (mLoadingDialog != null) mLoadingDialog.dismiss();

    }

    @Override
    public void onResetPassword() {
        new MaterialDialog.Builder(this)
                .titleGravity(GravityEnum.CENTER)
                .contentGravity(GravityEnum.CENTER)
                .title(getString(R.string.youha))
                .titleColor(ContextCompat.getColor(this, R.color.colorAccent))
                .positiveColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
                .content(getString(R.string.dlg_content_restore_password))
                .positiveText(getString(R.string.ok_exclamation))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        finish();
                    }
                })
                .iconRes(R.drawable.ico_ball)
                .show();
    }

    @Override
    public void showResetPasswordError(ApiError error) {
        try {
            new MaterialDialog.Builder(this)
                    .titleGravity(GravityEnum.CENTER)
                    .contentGravity(GravityEnum.CENTER)
                    .title(error.getTitle())
                    .titleColor(ContextCompat.getColor(this, R.color.colorRed800))
                    .negativeColor(ContextCompat.getColor(this, R.color.colorRed800))
                    .content(error.getMessage())
                    .negativeText(getString(R.string.ok_exclamation))
                    .iconRes(R.drawable.ico_stop)
                    .show();
        } catch(Exception e){
            Crashlytics.logException(e);
        }
    }

    @Override
    public void onValidationSucceeded() {
        mForgotPasswordPresenter.resetPassword(mEmailEditText.getText().toString());
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);
            try {
                if (view.getTag().equals("edit_email")) {
                    message = getString(R.string.edit_err_email);
                }
            } catch (Exception e){
                Crashlytics.logException(e);
            }
            // Display error messages ;)
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }

        }
    }

}
