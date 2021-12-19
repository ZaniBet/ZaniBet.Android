package eu.devolios.zanibet.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Gromat Luidgi on 01/08/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Token {

    private String access_token;
    private String refresh_token;
    private String expire_in;

    public Token(){

    }

    public String getAccessToken() {
        return access_token;
    }

    public void setAccessToken(String accessToken) {
        this.access_token = accessToken;
    }

    public String getRefreshToken() {
        return refresh_token;
    }

    public void setRefreshToken(String refreshToken) {
        this.refresh_token = refreshToken;
    }

    public String getExpireIn() {
        return expire_in;
    }

    public void setExpireIn(String expireIn) {
        this.expire_in = expireIn;
    }
}
