package eu.devolios.zanibet.presenter;

import android.content.Context;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

import eu.devolios.zanibet.model.ApiError;
import eu.devolios.zanibet.model.Fixture;
import eu.devolios.zanibet.model.GameTicket;
import eu.devolios.zanibet.model.User;
import eu.devolios.zanibet.presenter.contract.FixtureStatsContract;
import eu.devolios.zanibet.utils.Constants;
import eu.devolios.zanibet.ws.FixtureService;
import eu.devolios.zanibet.ws.GameTicketService;
import eu.devolios.zanibet.ws.Injector;
import io.reactivex.Observer;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class FixtureStatsPresenter implements FixtureStatsContract.Presenter {

    private Context mContext;
    private FixtureStatsContract.View mView;

    public FixtureStatsPresenter(Context context, FixtureStatsContract.View view){
        mContext = context;
        mView = view;
    }

    @Override
    public void loadStats(String fixtureId) {
        mView.showContentLoading();
        FixtureService fixtureService = Injector.provideJacksonRetrofit(mContext, Constants.BASE_URL)
                .create(FixtureService.class);

        fixtureService.getFixtureStats(fixtureId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onSuccess(ResponseBody responseBody) {
                        ArrayList<Fixture> lastFixtueHome = new ArrayList<>();
                        ArrayList<Fixture> lastFixtureAway = new ArrayList<>();

                        try {
                            ObjectMapper objectMapper = new ObjectMapper();
                            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                            JsonNode groupNode = objectMapper.readTree(responseBody.string());
                            JsonNode lastHomeFixturesNode = groupNode.get("lastHomeFixtures");
                            JsonNode lastAwayFixturesNode = groupNode.get("lastAwayFixtures");

                            for (JsonNode objNode : lastHomeFixturesNode){
                                Fixture fixture = objectMapper.treeToValue(objNode, Fixture.class);
                                lastFixtueHome.add(fixture);
                            }

                            for (JsonNode objNode : lastAwayFixturesNode){
                                Fixture fixture = objectMapper.treeToValue(objNode, Fixture.class);
                                lastFixtureAway.add(fixture);
                            }
                        } catch (Exception e){
                            e.printStackTrace();
                        }

                        mView.hideContentLoading();
                        mView.onLoadStats(lastFixtueHome, lastFixtureAway);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.hideContentLoading();
                        mView.showContentError(ApiError.parseRxError(mContext, e));
                    }
                });
    }
}
