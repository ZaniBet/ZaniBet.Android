package eu.devolios.zanibet.model;

import eu.devolios.zanibet.utils.Constants;

/**
 * Created by Gromat Luidgi on 16/11/2017.
 */

public class FacebookOauthBody {

    private String client_id;
    private String client_secret;
    private String accessToken;
    private String userId;
    private String locale;

    public FacebookOauthBody(){
        client_id = Constants.CLIENT_ID;
        client_secret = Constants.CLIENT_SECRET;
    }

    public String getClient_id() {
        return this.client_id;
    }

    public String getClient_secret() {
        return this.client_secret;
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }
}


