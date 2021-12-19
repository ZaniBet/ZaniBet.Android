package eu.devolios.zanibet;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.blikoon.qrcodescanner.QrCodeActivity;
import com.crashlytics.android.Crashlytics;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.MaterialIcons;
import com.mikepenz.iconics.context.IconicsLayoutInflater2;
import com.mikepenz.iconics.view.IconicsButton;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Optional;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.devolios.zanibet.model.Address;
import eu.devolios.zanibet.model.ApiError;
import eu.devolios.zanibet.model.User;
import eu.devolios.zanibet.presenter.ProfileDetailsPresenter;
import eu.devolios.zanibet.presenter.contract.ProfileDetailsContract;


/**
 * Created by Gromat Luidgi on 16/11/2017.
 */

public class ProfileDetailsActivity extends AppCompatActivity implements ProfileDetailsContract.View, Validator.ValidationListener {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.lastnameInput)
    @NotEmpty
    EditText mLastnameInput;

    @BindView(R.id.firstnameInput)
    @NotEmpty
    EditText mFirstnameInput;

    @BindView(R.id.addressInput)
    @NotEmpty
    EditText mAddressInput;

    @BindView(R.id.zipcodeInput)
    @NotEmpty
    EditText mZipcodeInput;

    @BindView(R.id.cityInput)
    @NotEmpty
    EditText mCityInput;

    @BindView(R.id.countryInput)
    @NotEmpty
    EditText mCountryInput;

    //@BindView(R.id.countryInput)
    //CountryCodePicker mCountryInput;

    @BindView(R.id.paypalInput)
    @Optional
    //@Email
    EditText mPaypalInput;

    @BindView(R.id.bitcoinInput)
    @Optional
    EditText mBitcoinInput;

    @BindView(R.id.bitcoinHelpTextView)
    TextView mBitcoinHelpTextView;
    @BindView(R.id.qrcodeButton)
    IconicsButton mQrcodeButton;

    private static final int REQUEST_CODE_QR_SCAN = 101;

    private Validator mValidator;
    private ProfileDetailsPresenter mProfilDetailsPresenter;
    private MaterialDialog mLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflaterCompat.setFactory2(getLayoutInflater(), new IconicsLayoutInflater2(getDelegate()));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_details);
        ButterKnife.bind(this);

        Crashlytics.setString("last_ui_viewed", "ProfileDetailsActivity");

        mProfilDetailsPresenter = new ProfileDetailsPresenter(getApplicationContext(), this);

        //mToolbar.setBackgroundColor(Color.TRANSPARENT);
        mToolbar.setTitle(getString(R.string.menu_paiement_info));
        mToolbar.setNavigationIcon(new IconDrawable(getApplicationContext(), MaterialIcons.md_arrow_back).colorRes(R.color.colorWhite).actionBarSize());
        mToolbar.setNavigationOnClickListener(v -> onBackPressed());
        setSupportActionBar(mToolbar);

        mLastnameInput.setText(User.currentUser().getLastname());
        mFirstnameInput.setText(User.currentUser().getFirstname());
        if (User.currentUser().getAddress() != null) {
            mAddressInput.setText(User.currentUser().getAddress().getStreet());
            mZipcodeInput.setText(User.currentUser().getAddress().getZipcode());
            mCityInput.setText(User.currentUser().getAddress().getCity());
            mCountryInput.setText(User.currentUser().getAddress().getCountry());
        }
        mPaypalInput.setText(User.currentUser().getPaypal());
        mBitcoinInput.setText(User.currentUser().getBitcoin());
        mBitcoinHelpTextView.setText(Html.fromHtml(getString(R.string.bitcoin_warning)));

        mValidator = new Validator(this);
        mValidator.setValidationListener(this);

        mQrcodeButton.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this,
                            Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    // No explanation needed; request the permission
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                            1);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
            } else {
                // Permission has already been granted
                Intent i = new Intent(ProfileDetailsActivity.this,QrCodeActivity.class);
                startActivityForResult( i,REQUEST_CODE_QR_SCAN);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.confirmation_menu, menu);
        menu.findItem(R.id.action_confirm).setIcon(
                new IconDrawable(this, MaterialIcons.md_check)
                        .colorRes(R.color.colorWhite)
                        .actionBarSize());
        /*SpannableString s = new SpannableString(getString(R.string.validate));
        s.setSpan(new ForegroundColorSpan(Color.WHITE), 0, s.length(), 0);
        menu.findItem(R.id.action_confirm).setTitle(s);*/

        //menu.findItem(R.id.action_confirm).setIcon(new IconicsDrawable(this, FontAwesome.Icon.faw_save).actionBar()
        //.colorRes(R.color.colorWhite));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_confirm:
                // Procéder à la validation du formulaire
                mValidator.validate();
                return false;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != Activity.RESULT_OK)
        {
            if(data==null)
                return;
            //Getting the passed result
            String result = data.getStringExtra("com.blikoon.qrcodescanner.error_decoding_image");
            if( result != null)
            {

            }
            return;

        }
        if(requestCode == REQUEST_CODE_QR_SCAN)
        {
            if(data==null)
                return;
            //Getting the passed result
            String result = data.getStringExtra("com.blikoon.qrcodescanner.got_qr_scan_relult");
            Log.d("QRCode Result","Have scan result in your app activity :"+ result);
            mBitcoinInput.setText(result);
        }
    }

    @Override
    public void onValidationSucceeded() {
        User user = new User();
        Address address = new Address();
        address.setCity(mCityInput.getText().toString());
        address.setStreet(mAddressInput.getText().toString());
        address.setCountry(mCountryInput.getText().toString());
        //address.setCountry(mCountryInput.getSelectedCountryEnglishName());
        address.setZipcode(mZipcodeInput.getText().toString());
        user.setAddress(address);
        user.setFirstname(mFirstnameInput.getText().toString());
        user.setLastname(mLastnameInput.getText().toString());
        user.setPaypal(mPaypalInput.getText().toString());
        user.setBitcoin(mBitcoinInput.getText().toString());
        mProfilDetailsPresenter.updateProfile(user);
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

    @Override
    public void onUpdateProfile(User user) {
        if (!isFinishing()) {
            new MaterialDialog.Builder(this)
                    .title(getString(R.string.youha))
                    .titleColor(ContextCompat.getColor(this, R.color.colorAccent))
                    .positiveColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
                    .content(getString(R.string.dlg_content_update_paiement))
                    .positiveText(getString(R.string.ok_exclamation))
                    .onPositive((dialog, which) -> finish())
                    .show();
        }
    }

    @Override
    public void showUpdateLoading() {
        mLoadingDialog = new MaterialDialog.Builder(this)
                .title(getString(R.string.ldg_title_update_paiement))
                .content(getString(R.string.ldg_content_update_paiement))
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
    public void showUpdateError(ApiError apiError) {
        try {
            if (!isFinishing()) {
                new MaterialDialog.Builder(this)
                        .title(apiError.getTitle())
                        .titleColor(ContextCompat.getColor(this, R.color.colorRed800))
                        .negativeColor(ContextCompat.getColor(this, R.color.colorRed800))
                        .content(apiError.getMessage())
                        .negativeText(getString(R.string.ok_exclamation))
                        .show();
            }
        } catch (Exception e){
            Crashlytics.logException(e);
        }
    }
}
