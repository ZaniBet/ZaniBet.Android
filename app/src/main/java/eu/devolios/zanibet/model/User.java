package eu.devolios.zanibet.model;

import com.crashlytics.android.Crashlytics;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import eu.devolios.zanibet.utils.Constants;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kingfisher.easy_sharedpreference_library.SharedPreferencesManager;


/**
 * Created by Gromat Luidgi on 31/07/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

    public static void clearUserPreference(){
        SharedPreferencesManager.getInstance().putValue(Constants.IS_LOGGED_IN_PREF, false);
        SharedPreferencesManager.getInstance().remove(Constants.CURRENT_USER_PREF);
        SharedPreferencesManager.getInstance().remove(Constants.ACCESS_TOKEN_PREF);
        SharedPreferencesManager.getInstance().remove(Constants.REFRESH_TOKEN_PREF);
        SharedPreferencesManager.getInstance().remove(Constants.TOKEN_EXPIRE_IN_PREF);
        SharedPreferencesManager.getInstance().remove(Constants.INVITATION_CODE_ACTIVE_PREF);
        SharedPreferencesManager.getInstance().remove(Constants.WELCOME_SHOWED_PREF);
        SharedPreferencesManager.getInstance().remove(Constants.TICKET_SINGLE_FILTER_PREF);
        SharedPreferencesManager.getInstance().remove(Constants.TICKET_MULTI_FILTER_PREF);

        if (AccessToken.getCurrentAccessToken() != null){
            LoginManager.getInstance().logOut();
        }
    }

    public static User currentUser(){
        User user;
        try {
            user = SharedPreferencesManager.getInstance().getValue(Constants.CURRENT_USER_PREF, User.class, new User());
        } catch (Exception e){
            Crashlytics.logException(e);
            user = new User();
        }
        return user;
    }

    public static void saveUser(User user){
        SharedPreferencesManager.getInstance().putValue(Constants.CURRENT_USER_PREF, user);
    }

    public static void setAccessToken(String accessToken){
        SharedPreferencesManager.getInstance().putValue(Constants.ACCESS_TOKEN_PREF, accessToken);
    }

    public static String getAccessToken(){
        return SharedPreferencesManager.getInstance().getValue(Constants.ACCESS_TOKEN_PREF, String.class);
    }

    private String _id;
    private String createdAt;
    private String role;
    private String username;
    private int usernameEditAttempt;
    private String email;
    private String bitcoin;
    private String paypal;
    private String password;
    private String firstname;
    private String lastname;
    private String gender;
    private String birthday;
    private Address address;
    private int point;
    private String facebookId;
    private String facebookAccessToken;
    private String fcmToken;
    private String locale;
    private boolean emailVerified;
    private int jeton;
    private Referral referral;
    private Manager manager;
    private boolean zaniHashEnabled;
    private Wallet wallet;


    public String getCreatedAt() {
        return createdAt;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getId() {
        return _id;
    }

    public void setId(String _id) {
        this._id = _id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPoint() {
        return this.point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public String getBitcoin() {
        return bitcoin;
    }

    public void setBitcoin(String bitcoin) {
        this.bitcoin = bitcoin;
    }

    public String getPaypal() {
        return this.paypal;
    }

    public void setPaypal(String paypal) {
        this.paypal = paypal;
    }

    public String getFirstname() {
        return this.firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return this.lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public Address getAddress() {
        return this.address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getFacebookId() {
        return this.facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public String getFacebookAccessToken() {
        return this.facebookAccessToken;
    }

    public void setFacebookAccessToken(String facebookAccessToken) {
        this.facebookAccessToken = facebookAccessToken;
    }

    public String getFcmToken() {
        return this.fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public String getLocale() {
        return this.locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public int getJeton() {
        return jeton;
    }

    public void setJeton(int jeton) {
        this.jeton = jeton;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public Referral getReferral() {
        return referral;
    }

    public void setReferral(Referral referral) {
        this.referral = referral;
    }

    public Manager getManager() {
        return manager;
    }

    public void setManager(Manager manager) {
        this.manager = manager;
    }

    public boolean isZaniHashEnabled() {
        return zaniHashEnabled;
    }

    public void setZaniHashEnabled(boolean zaniHashEnabled) {
        this.zaniHashEnabled = zaniHashEnabled;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    public int getUsernameEditAttempt() {
        return usernameEditAttempt;
    }

    public void setUsernameEditAttempt(int usernameEditAttempt) {
        this.usernameEditAttempt = usernameEditAttempt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
