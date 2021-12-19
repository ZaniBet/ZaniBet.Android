package eu.devolios.zanibet.ws;

import eu.devolios.zanibet.model.Help;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Gromat Luidgi on 15/11/2017.
 */

public interface HelpService {

    @GET("helps")
    Call<List<Help>> getHelps();
}
