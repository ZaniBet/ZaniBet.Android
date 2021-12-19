package eu.devolios.zanibet.model;


import eu.devolios.zanibet.utils.Constants;

/**
 * Created by Gromat Luidgi on 01/08/2017.
 */

public class OauthBody {

    private String grant_type;
    private String client_id;
    private String client_secret;
    private String username;
    private String password;

    public OauthBody(String email, String password){
        this.username = email;
        this.password = password;
        this.grant_type = "password";
        this.client_id = Constants.CLIENT_ID;
        this.client_secret = Constants.CLIENT_SECRET;
    }

    public String getGrant_type() {
        return grant_type;
    }

    public void setGrant_type(String grant_type) {
        this.grant_type = grant_type;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getClient_secret() {
        return client_secret;
    }

    public void setClient_secret(String client_secret) {
        this.client_secret = client_secret;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}
