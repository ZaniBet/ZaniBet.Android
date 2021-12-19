package eu.devolios.zanibet.ws;

import java.util.List;

import eu.devolios.zanibet.model.Competition;
import eu.devolios.zanibet.model.Grille;
import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Gromat Luidgi on 11/12/2017.
 */

public interface CompetitionService {

    /*@GET("competitions")
    Call<List<Competition>> getCompetitions(@Query("access_token") String token);*/

    @GET("competitions")
    Single<List<Competition>> getCompetitions();

    @GET("competitions/topic")
    Single<List<Competition>> getCompetitionsTopic();

    @GET("competitions/{competitionId}/standings")
    Single<ResponseBody> getLeagueStandings(@Path("competitionId") String competitionId);

}
