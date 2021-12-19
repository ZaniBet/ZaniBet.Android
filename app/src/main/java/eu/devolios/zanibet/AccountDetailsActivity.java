package eu.devolios.zanibet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;

import eu.devolios.zanibet.model.ApiError;
import eu.devolios.zanibet.model.User;
import eu.devolios.zanibet.presenter.contract.AccountDetailsContract;
import eu.devolios.zanibet.presenter.AccountDetailsPresenter;

import com.crashlytics.android.Crashlytics;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.joanzapata.iconify.fonts.MaterialIcons;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Pattern;
import com.rilixtech.materialfancybutton.MaterialFancyButton;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Gromat Luidgi on 16/11/2017.
 */

public class AccountDetailsActivity extends AppCompatActivity implements AccountDetailsContract.View, Validator.ValidationListener {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @NotEmpty
    @Length(min = 3, max = 30)
    @Pattern(regex = "^[a-z0-9_-]{3,30}$", caseSensitive = false)
    @BindView(R.id.usernameInput)
    EditText mUsernameInput;

    @BindView(R.id.usernameHelpTextView)
    TextView mUsernameHelpTextView;

    @BindView(R.id.emailInput)
    @NotEmpty
    @Email
    EditText mEmailInput;

    @BindView(R.id.removeAccountButton)
    MaterialFancyButton mRemoveAccountButton;

    private AccountDetailsPresenter mAccountDetailsPresenter;
    private Validator mValidator;
    private MaterialDialog mLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_details);
        ButterKnife.bind(this);

        mAccountDetailsPresenter = new AccountDetailsPresenter(getApplicationContext(), this);
        mToolbar.setTitle(getString(R.string.menu_my_account));
        mToolbar.setNavigationIcon(new IconDrawable(getApplicationContext(), MaterialIcons.md_arrow_back).colorRes(R.color.colorWhite).actionBarSize());
        mToolbar.setNavigationOnClickListener(v -> onBackPressed());
        setSupportActionBar(mToolbar);

        if (User.currentUser().getUsernameEditAttempt() == 0){
            mUsernameInput.setEnabled(true);
            mUsernameHelpTextView.setText(getString(R.string.username_edit_warning));
        } else {
            mUsernameInput.setEnabled(false);
            mUsernameHelpTextView.setText(getString(R.string.username_max_edit));
        }

        mUsernameInput.setText(User.currentUser().getUsername());
        mEmailInput.setText(User.currentUser().getEmail());

        mValidator = new Validator(this);
        mValidator.setValidationListener(this);

        mRemoveAccountButton.setOnClickListener(v -> {
            new MaterialDialog.Builder(this)
                    .iconRes(R.drawable.ic_siren)
                    .contentGravity(GravityEnum.CENTER)
                    .content(getString(R.string.dlg_content_remove_account))
                    .negativeText(getString(R.string.cancel))
                    .negativeColorRes(R.color.colorPrimary)
                    .positiveText(getString(R.string.yes_delete_account))
                    .positiveColorRes(R.color.colorRed800)
                    .onPositive((dialog, which) -> {
                        mAccountDetailsPresenter.removeAccount();
                    })
                    .limitIconToDefaultSize()
                    .show();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.confirmation_menu, menu);
        menu.findItem(R.id.action_confirm).setIcon(
                new IconDrawable(this, MaterialIcons.md_check)
                        .colorRes(R.color.colorWhite)
                        .actionBarSize());
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_confirm:
                mValidator.validate();
                return false;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onUpdateAccount() {
        try {
            new MaterialDialog.Builder(this)
                    .title(getString(R.string.youha))
                    .titleColor(ContextCompat.getColor(this, R.color.colorAccent))
                    .positiveColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
                    .content(getString(R.string.dlg_content_update_account))
                    .positiveText(getString(R.string.ok_exclamation))
                    .onPositive((dialog, which) -> finish())
                    .show();
        } catch (Exception e){
            Crashlytics.logException(e);
        }
    }

    @Override
    public void showUpdateLoading() {
        mLoadingDialog = new MaterialDialog.Builder(this)
                .title(getString(R.string.ldg_title_update_email))
                .content(getString(R.string.ldg_content_remove_account))
                .progress(true, 0)
                .show();
    }

    @Override
    public void hideUpdateLoading() {
        if (mLoadingDialog != null) mLoadingDialog.dismiss();
    }

    @Override
    public void showRemoveLoading() {
        mLoadingDialog = new MaterialDialog.Builder(this)
                .title(getString(R.string.ldg_title_update_email))
                .content(getString(R.string.ldg_content_remove_account))
                .progress(true, 0)
                .show();
    }

    @Override
    public void hideRemoveLoading() {
        if (mLoadingDialog != null) mLoadingDialog.dismiss();
    }

    @Override
    public void showUpdateError(ApiError apiError) {
        try {
            new MaterialDialog.Builder(this)
                    .title(apiError.getTitle())
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
    public void onRemoveAccount() {
        User.clearUserPreference();
        startActivity(new Intent(this, DispatchActivity.class));
        finish();
    }

    @Override
    public void onValidationSucceeded() {
        User user = new User();
        user.setEmail(mEmailInput.getText().toString());
        user.setUsername(mUsernameInput.getText().toString());
        try {
            new MaterialDialog.Builder(this)
                    .content(getString(R.string.dlg_content_confirm_update_account))
                    .negativeText(getString(R.string.cancel))
                    .negativeColorRes(R.color.colorRed800)
                    .positiveText(getString(R.string.ok_exclamation))
                    .positiveColor(ContextCompat.getColor(this, R.color.colorPrimary))
                    .onPositive((dialog, which) -> mAccountDetailsPresenter.updateAccount(user))
                    .show();
        } catch (Exception e){
            Crashlytics.logException(e);
        }

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
