package eu.devolios.zanibet.ws;

import eu.devolios.zanibet.model.User;
import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ZaniHashService {

    @PUT("zh/enable/{value}")
    Single<ResponseBody> putEnabled(@Path("value") boolean enabled);
}
