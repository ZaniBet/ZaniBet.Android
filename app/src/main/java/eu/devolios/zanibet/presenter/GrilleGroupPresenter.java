package eu.devolios.zanibet.presenter;

import android.content.Context;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

import eu.devolios.zanibet.model.ApiError;
import eu.devolios.zanibet.model.GameTicket;
import eu.devolios.zanibet.model.Grille;
import eu.devolios.zanibet.model.GrilleGroup;
import eu.devolios.zanibet.presenter.contract.GrilleGroupContract;
import eu.devolios.zanibet.utils.AnnotationJSONConverterFactory;
import eu.devolios.zanibet.ws.GrilleService;
import eu.devolios.zanibet.ws.Injector;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static eu.devolios.zanibet.utils.AnnotationJSONConverterFactory.*;

/**
 * Created by Gromat Luidgi on 11/11/2017.
 */

public class GrilleGroupPresenter implements GrilleGroupContract.Presenter {

    private GrilleGroupContract.View mView;
    private GrilleService mGrilleService;
    private ArrayList<GrilleGroup> mGrilleGroupList;
    private Context mContext;
    private String mStatus;

    private int mLimit = 5;
    private int mCurrentPage = 0;
    private boolean mNetworkError = false;

    public GrilleGroupPresenter(Context context, GrilleGroupContract.View view, String status) {
        mContext = context;
        mView = view;
        mStatus = status;
        mGrilleService = Injector.provideGrilleService(context, new AnnotationJSONConverterFactory(JACKSON));
        mGrilleGroupList = new ArrayList<>();
    }

    @Override
    public void refresh() {
        if (mGrilleGroupList.isEmpty() || mGrilleGroupList.size() > mLimit){
            mCurrentPage = 1;
        }

        mGrilleService.getGrillesGroup(mStatus, 0, mCurrentPage*mLimit )
                .flatMap(responseBody -> {
                    try {
                        List<GrilleGroup> grilleGroupArrayList = new ArrayList<>();
                        ObjectMapper objectMapper = new ObjectMapper();
                        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                        JsonNode groupNode = objectMapper.readTree(responseBody.string());

                        for (JsonNode objNode : groupNode){
                            //System.out.println(objNode);
                            Grille[] grilles = objectMapper.treeToValue(objNode, Grille[].class);
                            GameTicket gameTicket = objectMapper.treeToValue(objNode.get(0).path("gameTicket"), GameTicket.class);

                            GrilleGroup grilleGroup = new GrilleGroup();
                            grilleGroup.setGameTicket(gameTicket);
                            grilleGroup.setGrilles(grilles);
                            grilleGroupArrayList.add(grilleGroup);
                        }
                        return Observable.just(grilleGroupArrayList);
                    } catch (Throwable t){
                        Crashlytics.logException(t);
                        return Observable.error(t);
                    }
                })
                .subscribeOn(Schedulers.io()) // “work” on io thread
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<GrilleGroup>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mView.togglePaginateLoading(true);
                        mView.hideContentError();
                    }

                    @Override
                    public void onNext(List<GrilleGroup> list) {
                        mGrilleGroupList.clear();
                        mGrilleGroupList.addAll(list);
                        mView.addGroupGrilles(mGrilleGroupList);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mGrilleGroupList.clear();
                        mView.togglePaginateLoading(false);
                        mView.disablePaginate();
                        mView.onRefresh();
                        mView.showContentError(ApiError.parseRxError(mContext, e));
                    }

                    @Override
                    public void onComplete() {
                        // Succès, l'obsersable ne transmettra plus aucun autre évènement
                        if (mGrilleGroupList.isEmpty() || mGrilleGroupList.size() < mLimit) mView.disablePaginate();
                        mNetworkError = false;
                        mView.togglePaginateLoading(false);
                        mView.onRefresh();
                    }
                });
    }

    @Override
    public void loadMore() {
        if (mGrilleGroupList.isEmpty()){
            mCurrentPage = 0;
        }

        if (mCurrentPage > 19){
            mView.togglePaginateLoading(false);
            mView.disablePaginate();
            return;
        }

        mGrilleService.getGrillesGroup(mStatus, mCurrentPage, mLimit )
                .flatMap(responseBody -> {
                    try {
                        List<GrilleGroup> grilleGroupArrayList = new ArrayList<>();
                        ObjectMapper objectMapper = new ObjectMapper();
                        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                        JsonNode groupNode = objectMapper.readTree(responseBody.string());

                        for (JsonNode objNode : groupNode){
                            System.out.println(objNode);
                            Grille[] grilles = objectMapper.treeToValue(objNode, Grille[].class);
                            GameTicket gameTicket = objectMapper.treeToValue(objNode.get(0).path("gameTicket"), GameTicket.class);

                            GrilleGroup grilleGroup = new GrilleGroup();
                            grilleGroup.setGameTicket(gameTicket);
                            grilleGroup.setGrilles(grilles);
                            grilleGroupArrayList.add(grilleGroup);
                        }
                        return Observable.just(grilleGroupArrayList);
                    } catch (Throwable t){
                        Crashlytics.logException(t);
                        return Observable.error(t);
                    }
                })
                .subscribeOn(Schedulers.io()) // “work” on io thread
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<GrilleGroup>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        // L'observer est prêt à recevoir des notifications
                        if (!mNetworkError) {
                            mView.togglePaginateLoading(true);
                        }

                        mView.hideContentError();
                    }

                    @Override
                    public void onNext(List<GrilleGroup> list) {
                        Log.d("JSON", list.toString());
                        if (list.size() == 0 && mGrilleGroupList.size() > 0) {
                            mView.disablePaginate();
                            return;
                        }

                        mGrilleGroupList.addAll(list);
                        mCurrentPage++;
                        mView.addGroupGrilles(mGrilleGroupList);
                    }

                    @Override
                    public void onError(Throwable e) {
                        // Erreur
                        e.printStackTrace();
                        mNetworkError = true;
                        mView.togglePaginateLoading(false);
                        mView.disablePaginate();
                        mView.onRefresh();
                        mView.showContentError(ApiError.parseRxError(mContext, e));
                    }

                    @Override
                    public void onComplete() {
                        // Succès, l'obsersable ne transmettra plus aucun autre évènement
                        mNetworkError = false;
                        mView.hideContentError();
                        mView.togglePaginateLoading(false);
                        mView.onRefresh();
                    }
                });

    }



}
