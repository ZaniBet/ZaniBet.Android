package eu.devolios.zanibet;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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
import eu.devolios.zanibet.presenter.contract.RegisterContract;
import eu.devolios.zanibet.presenter.RegisterPresenter;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.kingfisher.easy_sharedpreference_library.SharedPreferencesManager;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.Min;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.mobsandgeeks.saripaar.annotation.Pattern;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.devolios.zanibet.utils.Constants;


public class RegisterActivity extends AppCompatActivity implements RegisterContract.View, Validator.ValidationListener {


    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.registerButton)
    Button mRegisterButton;
    @BindView(R.id.loginButton)
    Button mLoginButton;
    @BindView(R.id.facebookButton)
    LoginButton mFacebookLoginButton;

    @BindView(R.id.pseudoEditText)
    @Length(min = 3, max = 30)
    @Pattern(regex = "^[a-z0-9_-]{3,30}$", caseSensitive = false)
    @NotEmpty
    EditText mPseudoEditText;

    @BindView(R.id.emailEditText)
    @NotEmpty
    @Email
    EditText mEmailEditText;

    @BindView(R.id.passwordEditText)
    @Password(min = 6, scheme = Password.Scheme.ANY)
    EditText mPasswordEditText;

    private RegisterPresenter mRegisterPresenter;
    private MaterialDialog mLoadingDialog;
    private Validator mValidator;
    private CallbackManager mCallbackManager;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        Crashlytics.setString("last_ui_viewed", "RegisterActivity");
        mRegisterPresenter = new RegisterPresenter(getApplicationContext(), this);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        mToolbar.setBackgroundColor(Color.TRANSPARENT);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mValidator = new Validator(this);
        mValidator.setValidationListener(this);

        mCallbackManager = CallbackManager.Factory.create();
        mFacebookLoginButton.setReadPermissions(Arrays.asList("email"));
        mFacebookLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                logSignupEvent("Facebook");
                mRegisterPresenter.signupFacebook(loginResult);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                error.printStackTrace();
            }
        });

        mRegisterButton.setOnClickListener(view -> {
            mRegisterButton.setEnabled(false);
            mValidator.validate();
        });

        mLoginButton.setOnClickListener(view -> {
            try {
                onBackPressed();
            } catch(Exception e){
                Crashlytics.logException(e);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void showSignupLoading() {
        mLoadingDialog = new MaterialDialog.Builder(this)
                .title(getString(R.string.ldg_title_signup))
                .content(getString(R.string.ldg_content_signup))
                .progress(true, 0)
                .show();
    }

    @Override
    public void hideSignupLoading() {
            if (mLoadingDialog != null) mLoadingDialog.dismiss();

    }

    @Override
    public void onSignup() {
        logSignupEvent("Basic");
        mRegisterPresenter.login(mEmailEditText.getText().toString(), mPasswordEditText.getText().toString());
    }

    @Override
    public void showSignupError(ApiError error) {
            try {
                mRegisterButton.setEnabled(true);
                new MaterialDialog.Builder(this)
                        .titleGravity(GravityEnum.CENTER)
                        .contentGravity(GravityEnum.CENTER)
                        .title(error.getTitle())
                        .titleColorRes(R.color.colorRed800)
                        .negativeColorRes(R.color.colorRed800)
                        .content(error.getMessage())
                        .negativeText(getString(R.string.ok_exclamation))
                        .iconRes(R.drawable.ico_stop)
                        .limitIconToDefaultSize()
                        .show();
            } catch (Exception e){
                Crashlytics.logException(e);
            }

    }

    @Override
    public void onLogin() {
        SharedPreferencesManager.getInstance().putValue(Constants.IS_LOGGED_IN_PREF, true);
        SharedPreferencesManager.getInstance().putValue(Constants.WELCOME_SHOWED_PREF, false);
        Intent intent = new Intent(getApplicationContext(), LoadingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void showLoginError(ApiError error) {
        User.clearUserPreference();
        mRegisterButton.setEnabled(true);
        new MaterialDialog.Builder(this)
                .title(error.getTitle())
                .titleColorRes(R.color.colorRed800)
                .negativeColorRes(R.color.colorRed800)
                .iconRes(R.drawable.ico_stop)
                .limitIconToDefaultSize()
                .content(error.getMessage())
                .negativeText(getString(R.string.ok_exclamation))
                .show();
    }

    @Override
    public void onValidationSucceeded() {
        mRegisterPresenter.signup(mPseudoEditText.getText().toString(),
                mEmailEditText.getText().toString(),
                mPasswordEditText.getText().toString());
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            if (view.getTag().equals("edit_email")){
                message = getString(R.string.edit_err_email);
            } else if (view.getTag().equals("edit_password")){
                message = getString(R.string.edit_err_password);
            }

            // Afficher le message d'erreur
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
        mRegisterButton.setEnabled(true);
    }

    private void logSignupEvent(String method){
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.SIGN_UP_METHOD, method);
        if (mFirebaseAnalytics != null) mFirebaseAnalytics.logEvent("signup", bundle);
    }
}
