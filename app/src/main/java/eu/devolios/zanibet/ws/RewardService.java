package eu.devolios.zanibet.ws;

import eu.devolios.zanibet.model.Reward;

import java.util.List;

import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Gromat Luidgi on 11/11/2017.
 */

public interface RewardService {

    @GET("rewards")
    Call<List<Reward>> getRewards();

    @GET("rewards/zh")
    Single<List<Reward>> getRewardsZaniHash();

}
