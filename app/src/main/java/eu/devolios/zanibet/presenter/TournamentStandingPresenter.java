package eu.devolios.zanibet.presenter;

import android.content.Context;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

import eu.devolios.zanibet.model.ApiError;
import eu.devolios.zanibet.model.Competition;
import eu.devolios.zanibet.model.GameTicket;
import eu.devolios.zanibet.model.GrilleStanding;
import eu.devolios.zanibet.model.LeagueStanding;
import eu.devolios.zanibet.presenter.contract.LeagueStandingContract;
import eu.devolios.zanibet.presenter.contract.TournamentStandingContract;
import eu.devolios.zanibet.utils.AnnotationJSONConverterFactory;
import eu.devolios.zanibet.utils.Constants;
import eu.devolios.zanibet.ws.CompetitionService;
import eu.devolios.zanibet.ws.GameTicketService;
import eu.devolios.zanibet.ws.Injector;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class TournamentStandingPresenter implements TournamentStandingContract.Presenter {

    private Context mContext;
    private TournamentStandingContract.View mView;
    private GameTicketService mGameTicketService;

    public TournamentStandingPresenter(Context context, TournamentStandingContract.View view){
        mContext = context;
        mView = view;

        mGameTicketService = Injector.provideGameTickerService(context,
                new AnnotationJSONConverterFactory(AnnotationJSONConverterFactory.JACKSON));
    }

    @Override
    public void loadStanding(GameTicket gameTicket) {
        mView.showContentLoading();
        mGameTicketService.getTournamentStanding(gameTicket.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<GrilleStanding>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onSuccess(List<GrilleStanding> grilleStandings) {
                        mView.hideContentLoading();
                        mView.onLoadStanding(grilleStandings);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.hideContentLoading();
                        mView.showContentError(ApiError.parseRxError(mContext, e));
                    }

                });
    }
}
