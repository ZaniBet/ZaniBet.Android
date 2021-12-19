package eu.devolios.zanibet;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.applovin.sdk.AppLovinSdk;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.crashlytics.android.Crashlytics;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.EntypoModule;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.joanzapata.iconify.fonts.MaterialModule;
import com.kingfisher.easy_sharedpreference_library.SharedPreferencesManager;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

import java.io.File;

import eu.devolios.zanibet.utils.CacheHelper;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * Created by Gromat Luidgi on 09/11/2017.
 */

public class ZaniBetApplication extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();

        handleGMS70416429();
        if (getPackageName().equals("eu.devolios.zanibet")) AppLovinSdk.initializeSdk(this);
        SharedPreferencesManager.init(this, true);

        try {
            Iconify.with(new FontAwesomeModule())
                    .with(new MaterialModule())
                    .with(new EntypoModule());
        } catch (Exception e){
            Crashlytics.logException(e);
        }

        try {
            Picasso.setSingletonInstance(getDefaultPicassoClient(this));
        } catch (Exception e){
            Crashlytics.logException(e);
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static Picasso getDefaultPicassoClient(Context context) {
        Picasso picasso;
        final File cacheDir = CacheHelper.getImagesCacheDir(context);
        OkHttpClient client = new OkHttpClient.Builder()
                .addNetworkInterceptor(chain -> {
                    Response originalResponse = chain.proceed(chain.request());
                    return CacheHelper.addCacheControl(originalResponse.newBuilder()).build();
                })
                .cache(new Cache(cacheDir, CacheHelper.MAX_DISK_CACHE))
                .build();
        OkHttp3Downloader downloader = new OkHttp3Downloader(client);
        picasso = new Picasso.Builder(context.getApplicationContext())
                .defaultBitmapConfig(Bitmap.Config.ARGB_8888)
                .memoryCache(new LruCache(context))
                .downloader(downloader)
                .build();
        return picasso;
    }



    private Thread.UncaughtExceptionHandler mDefaultExceptionHandler;
    private long mUIThreadId;

    /**
     * Hack for gms bug https://issuetracker.google.com/issues/70416429
     * https://stackoverflow.com/questions/47726111/gms-illegalstateexception-results-have-already-been-set
     */
    private void handleGMS70416429() {
        mDefaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        mUIThreadId = Thread.currentThread().getId();
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            if (e != null && t.getId() != mUIThreadId && e.getStackTrace() != null && e.getStackTrace().length > 0
                    && e.getStackTrace()[0].toString().contains("com.google.android.gms")
                    && e.getMessage() != null && e.getMessage().contains("Results have already been set")) {
                return; // non-UI thread
            }
            if (mDefaultExceptionHandler != null)
                mDefaultExceptionHandler.uncaughtException(t, e);
        });
    }
}
