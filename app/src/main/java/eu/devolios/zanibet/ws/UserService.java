package eu.devolios.zanibet.ws;

import eu.devolios.zanibet.model.Device;
import eu.devolios.zanibet.model.Nonce;
import eu.devolios.zanibet.model.SafetyResponse;
import eu.devolios.zanibet.model.User;

import io.reactivex.Single;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Gromat Luidgi on 31/07/2017.
 */

public interface UserService {
    @POST("user")
    Call<User> createUser(@Body User user);

    @POST("user/device")
    Single<ResponseBody> registerDevice(@Body Device device);

    @GET("user")
    Single<User> getCurrentUser();

    @GET("user/nonce")
    Single<Nonce> getNonce();

    @PUT("user/paiement")
    Call<User> updatePaiement( @Body User user);

    @PUT("user")
    Call<User> updateAccount( @Body User user);

    @PUT("user/fcmtoken")
    Call<ResponseBody> updateFcmToken( @Body User user);

    @PUT("user/extra")
    Call<ResponseBody> updateExtra( @Body User user);

    @PUT("user/jeton")
    Call<ResponseBody> getMoreJeton();

    @PUT("user/invitation")
    Call<Integer> setInvitationCode(@Body RequestBody requestBody);

    @PUT("user/invitation/custom")
    Call<String> setCustomInvitationCode(@Body RequestBody requestBody);

    @DELETE("user")
    Call<ResponseBody> removeAccount();
}
