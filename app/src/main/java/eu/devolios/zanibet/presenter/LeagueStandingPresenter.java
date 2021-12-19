package eu.devolios.zanibet.presenter;

import android.content.Context;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;

import eu.devolios.zanibet.model.ApiError;
import eu.devolios.zanibet.model.Competition;
import eu.devolios.zanibet.model.Fixture;
import eu.devolios.zanibet.model.GameTicket;
import eu.devolios.zanibet.model.Grille;
import eu.devolios.zanibet.model.GrilleGroup;
import eu.devolios.zanibet.model.LeagueStanding;
import eu.devolios.zanibet.model.User;
import eu.devolios.zanibet.presenter.contract.FixtureStatsContract;
import eu.devolios.zanibet.presenter.contract.LeagueStandingContract;
import eu.devolios.zanibet.utils.Constants;
import eu.devolios.zanibet.ws.CompetitionService;
import eu.devolios.zanibet.ws.FixtureService;
import eu.devolios.zanibet.ws.Injector;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class LeagueStandingPresenter implements LeagueStandingContract.Presenter {

    private Context mContext;
    private LeagueStandingContract.View mView;

    public LeagueStandingPresenter(Context context, LeagueStandingContract.View view){
        mContext = context;
        mView = view;
    }

    @Override
    public void loadStanding(Competition competition) {
        mView.showContentLoading();
        CompetitionService competitionService = Injector.provideJacksonRetrofit(mContext, Constants.BASE_URL)
                .create(CompetitionService.class);
        competitionService.getLeagueStandings(competition.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onSuccess(ResponseBody responseBody) {
                        ArrayList<LeagueStanding> leagueStandingArrayList = new ArrayList<>();

                        try {
                            ObjectMapper objectMapper = new ObjectMapper();
                            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                            JsonNode groupNode = objectMapper.readTree(responseBody.string());

                            for (JsonNode objNode : groupNode){
                                //System.out.println(objNode);
                                LeagueStanding leagueStanding = objectMapper.treeToValue(objNode, LeagueStanding.class);
                                leagueStandingArrayList.add(leagueStanding);
                            }
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                        mView.hideContentLoading();
                        mView.onLoadStanding(leagueStandingArrayList);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.hideContentLoading();
                        mView.showContentError(ApiError.parseRxError(mContext, e));
                    }

                });
    }
}
