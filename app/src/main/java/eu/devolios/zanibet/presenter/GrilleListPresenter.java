package eu.devolios.zanibet.presenter;

import android.content.Context;

import eu.devolios.zanibet.presenter.contract.GrilleListContract;
import eu.devolios.zanibet.utils.AnnotationJSONConverterFactory;
import eu.devolios.zanibet.ws.GrilleService;
import eu.devolios.zanibet.ws.Injector;

import static eu.devolios.zanibet.utils.AnnotationJSONConverterFactory.JACKSON;

/**
 * Created by Gromat Luidgi on 11/11/2017.
 */

public class GrilleListPresenter implements GrilleListContract.Presenter {

    private GrilleListContract.View mView;
    private GrilleService mGrilleService;
    private Context mContext;

    public GrilleListPresenter(Context context, GrilleListContract.View view){
        mContext = context;
        mView = view;
        mGrilleService = Injector.provideGrilleService(context, new AnnotationJSONConverterFactory(JACKSON));
    }

}
