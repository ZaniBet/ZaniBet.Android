package eu.devolios.zanibet.ws;

import eu.devolios.zanibet.model.Payout;
import eu.devolios.zanibet.model.Reward;

import java.util.HashMap;
import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Gromat Luidgi on 15/11/2017.
 */

public interface PayoutService {

    @GET("payouts")
    Call<List<Payout>> getPayouts();

    @POST("payout/reward")
    Call<ResponseBody> postPayoutReward(@Body Reward reward);

    @PUT("payouts/{payoutId}")
    Call<Payout> putPayout(@Path("payoutId") String payoutId, @Body RequestBody requestBody);
}
