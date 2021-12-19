package eu.devolios.zanibet.ws;

import java.util.List;
import java.util.Observable;

import eu.devolios.zanibet.model.Reward;
import eu.devolios.zanibet.model.Transaction;
import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Gromat Luidgi on 11/11/2017.
 */

public interface TransactionService {

    @GET("transactions/referral")
    Call<List<Transaction>> getReferralTransaction();

    @GET("transactions/zanihash")
    Single<List<Transaction>> getZaniHashTransaction();

}
