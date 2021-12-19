package eu.devolios.zanibet;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.crashlytics.android.Crashlytics;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import eu.devolios.zanibet.model.ApiError;
import eu.devolios.zanibet.model.User;
import eu.devolios.zanibet.presenter.contract.LoginContract;
import eu.devolios.zanibet.presenter.LoginPresenter;
import eu.devolios.zanibet.utils.Constants;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.kingfisher.easy_sharedpreference_library.SharedPreferencesManager;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.fabric.sdk.android.services.common.Crash;

public class LoginActivity extends AppCompatActivity implements LoginContract.View, Validator.ValidationListener {

    @BindView(R.id.loginButton)
    Button mLoginButton;
    @BindView(R.id.facebookButton)
    LoginButton mFacebookLoginButton;
    @BindView(R.id.registerButton)
    Button mRegisterButton;
    @BindView(R.id.forgotPasswordButton)
    Button mForgotPasswordButton;

    @BindView(R.id.emailEditText)
    @NotEmpty
    @Email
    EditText mEmailEditText;

    @BindView(R.id.passwordEditText)
    @Password(min = 6, scheme = Password.Scheme.ANY)
    EditText mPasswordEditText;

    private LoginPresenter mLoginPresenter;
    private MaterialDialog mLoadingDialog;
    private MaterialDialog mErrorDialog;
    private Validator mValidator;
    private CallbackManager mCallbackManager;

    private FirebaseAnalytics mFirebaseAnalytics;


    private static final int EMAIL_TAG = 0;
    private static final int PASSWORD_TAG = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        Crashlytics.setString("last_ui_viewed", "LoginActivity");
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mLoginPresenter = new LoginPresenter(getApplicationContext(), this);

        mEmailEditText.setTag(EMAIL_TAG);
        mPasswordEditText.setTag(PASSWORD_TAG);

        mEmailEditText.setText(SharedPreferencesManager.getInstance().getValue(Constants.LAST_LOGIN_EMAIL_PREF, String.class));

        mValidator = new Validator(this);
        mValidator.setValidationListener(this);

        mLoginButton.setOnClickListener(view -> mValidator.validate());

        mRegisterButton.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(intent);
        });

        mForgotPasswordButton.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
            startActivity(intent);
        });

        mCallbackManager = CallbackManager.Factory.create();
        mFacebookLoginButton.setReadPermissions(Arrays.asList("email"));
        mFacebookLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                mLoginPresenter.loginFacebook(loginResult);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                error.printStackTrace();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onLogin(String method) {
        logLoginEvent(method);
        SharedPreferencesManager.getInstance().putValue(Constants.IS_LOGGED_IN_PREF, true);
        mLoginPresenter.loadUser();
    }

    @Override
    public void onLoadUser(User user) {
        Intent intent = new Intent(getApplicationContext(), LoadingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void showLoginLoading() {
            mLoadingDialog = new MaterialDialog.Builder(this)
                    .title(getString(R.string.ldg_title_signin))
                    .content(getString(R.string.ldg_content_signin))
                    .progress(true, 0)
                    .show();

    }

    @Override
    public void hideLoginLoading() {
        if (!isFinishing() && mLoadingDialog != null) {
            try {
                mLoadingDialog.dismiss();
            } catch(Exception e){
                Crashlytics.logException(e);
            }
        }
    }

    @Override
    public void showLoginError(ApiError error) {
        try {
            User.clearUserPreference();
            mErrorDialog = new MaterialDialog.Builder(this)
                    .titleGravity(GravityEnum.CENTER)
                    .contentGravity(GravityEnum.CENTER)
                    .title(error.getTitle())
                    .titleColorRes(R.color.colorRed800)
                    .negativeColorRes(R.color.colorRed800)
                    .content(Html.fromHtml(error.getMessage()))
                    .negativeText(getString(R.string.ok_exclamation))
                    .iconRes(R.drawable.ico_stop)
                    .limitIconToDefaultSize()
                    .show();
        } catch (Exception e){
            Crashlytics.logException(e);
        }
    }

    @Override
    public void onValidationSucceeded() {
        mLoginPresenter.login(mEmailEditText.getText().toString(), mPasswordEditText.getText().toString());
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            // Display error messages ;)
            if (view instanceof EditText) {
                int tag = (int)view.getTag();
                if (tag == EMAIL_TAG){
                    ((EditText) view).setError(getString(R.string.edit_err_email));
                } else if (tag == PASSWORD_TAG){
                    ((EditText) view).setError(getString(R.string.edit_err_password));
                } else {
                    ((EditText) view).setError(message);
                }
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void logLoginEvent(String method){
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.SIGN_UP_METHOD, method);
        if (mFirebaseAnalytics != null) mFirebaseAnalytics.logEvent("login", bundle);
    }
}
