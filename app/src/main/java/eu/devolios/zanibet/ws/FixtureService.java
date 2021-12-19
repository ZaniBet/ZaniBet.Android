package eu.devolios.zanibet.ws;

import eu.devolios.zanibet.model.Grille;
import io.reactivex.Observable;
import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface FixtureService {

    @GET("fixtures/{fixtureId}/stats")
    Single<ResponseBody> getFixtureStats(@Path("fixtureId") String fixtureId);

}
