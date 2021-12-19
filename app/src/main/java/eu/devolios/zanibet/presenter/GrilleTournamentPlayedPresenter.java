package eu.devolios.zanibet.presenter;

import android.content.Context;

import eu.devolios.zanibet.model.GameTicket;
import eu.devolios.zanibet.model.Grille;
import eu.devolios.zanibet.presenter.contract.GrilleTournamentPlayedContract;
import eu.devolios.zanibet.utils.AnnotationJSONConverterFactory;
import eu.devolios.zanibet.ws.GrilleService;
import eu.devolios.zanibet.ws.Injector;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static eu.devolios.zanibet.utils.AnnotationJSONConverterFactory.JACKSON;

/**
 * Created by Gromat Luidgi on 11/11/2017.
 */

public class GrilleTournamentPlayedPresenter implements GrilleTournamentPlayedContract.Presenter {

    private GrilleTournamentPlayedContract.View mView;
    private Context mContext;
    private GrilleService mGrilleService;

    public GrilleTournamentPlayedPresenter(GrilleTournamentPlayedContract.View view, Context context){
        mView = view;
        mContext = context;
        mGrilleService = Injector.provideGrilleService(context, new AnnotationJSONConverterFactory(JACKSON));
    }

    @Override
    public void loadGrille(GameTicket gameTicket) {
        mView.showContentLoading();
        mGrilleService.getTournamentGrille(gameTicket.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Grille>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Grille grille) {
                        mView.onLoadGrille(grille);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.hideContentLoading();
                    }

                    @Override
                    public void onComplete() {
                        mView.hideContentLoading();
                    }
                });
    }
}
