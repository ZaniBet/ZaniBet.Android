package eu.devolios.zanibet.ws;

import eu.devolios.zanibet.model.Grille;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Gromat Luidgi on 10/11/2017.
 */

public interface GrilleService {


    @POST("grilles")
    Single<Grille> postGrille(@Body Grille grille);

    @POST("grilles/single")
    Single<Grille> postGrilleSingle(@Body Grille grille);

    @POST("grilles/tournament")
    Single<Grille> postGrilleTournament(@Body Grille grille);


    @GET("grilles")
    Observable<ResponseBody> getGrillesGroup(@Query("status") String status,
                                             @Query("page") int page,
                                             @Query("limit") int limit);

    @GET("grilles/single/gameticket/{gameticketId}")
    Observable<Grille> getSingleGrille(@Path("gameticketId") String gameticketId);

    @GET("grilles/tournament/gameticket/{gameticketId}")
    Observable<Grille> getTournamentGrille(@Path("gameticketId") String gameticketId);


    @PUT("grilles/{grilleId}/cancel")
    Single<ResponseBody> cancelGrille(@Path("grilleId") String grilleId);
}
