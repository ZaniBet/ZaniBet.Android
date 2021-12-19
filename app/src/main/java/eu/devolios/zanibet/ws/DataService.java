package eu.devolios.zanibet.ws;

import java.util.Dictionary;

import eu.devolios.zanibet.model.Setting;
import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Gromat Luidgi on 27/12/2017.
 */

public interface DataService {

    @GET("data/images")
    Call<String[]> getDataImages();

    @GET("data/settings")
    Single<Setting[]> getSettings();

    /*@GET("data/settings")
    Single<ResponseBody> getSettings(@Query("access_token") String token);*/

}
