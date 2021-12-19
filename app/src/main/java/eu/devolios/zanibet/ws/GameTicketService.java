package eu.devolios.zanibet.ws;

import eu.devolios.zanibet.model.GameTicket;

import java.util.List;

import eu.devolios.zanibet.model.Grille;
import eu.devolios.zanibet.model.GrilleStanding;
import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Gromat Luidgi on 09/11/2017.
 */

public interface GameTicketService {

    @GET("gametickets")
    Observable<List<GameTicket>> getGameTickets(@Query("ticketType") String ticketType,
                                                @Query("page") int page,
                                                @Query("limit") int limit,
                                                @Query("filterCompetition") String filterCompetition);

    @GET("gametickets")
    Observable<List<GameTicket>> getMatchdayGameTickets(@Query("ticketType") String ticketType,
                                                @Query("page") int page,
                                                @Query("limit") int limit,
                                                @Query("filterMatchday") boolean filterMatchday);

    @GET("gametickets/{gameticketId}/standing")
    Single<List<GrilleStanding>> getTournamentStanding(@Path("gameticketId") String ticketId);


}
