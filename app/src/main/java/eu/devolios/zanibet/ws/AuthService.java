package eu.devolios.zanibet.ws;

import eu.devolios.zanibet.model.FacebookOauthBody;
import eu.devolios.zanibet.model.OauthBody;
import eu.devolios.zanibet.model.Token;
import eu.devolios.zanibet.model.User;

import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Gromat Luidgi on 31/07/2017.
 */

public interface AuthService {

    @POST("oauth/token")
    Single<Token> postLogin(@Body OauthBody oauthBody);

    @POST("auth/facebook")
    Single<Token> postLoginWithFacebook(@Body FacebookOauthBody facebookOauthBody);

    @GET("auth/tokenValidity")
    Call<User> getTokenValidity();

    @GET("auth/checkUpdate/{versionCode}")
    Call<Boolean> checkUpdate(@Path("versionCode") int versionCode);

    @GET("auth/checkMinorUpdate")
    Call<Boolean> checkMinorUpdate(@Query("versionName") String versionName);

    @PUT("auth/reset-password")
    Call<Boolean> putResetPassword(@Body User user);
}
