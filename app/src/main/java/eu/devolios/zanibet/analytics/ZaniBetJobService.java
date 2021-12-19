package eu.devolios.zanibet.analytics;

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.HashMap;
import java.util.Map;

import eu.devolios.zanibet.BuildConfig;
import eu.devolios.zanibet.model.User;
import eu.devolios.zanibet.utils.Utils;

public class ZaniBetJobService extends JobService {

    private WebView mWebView;
    //private WebChromeClient mWebChromeClient;
    private WebViewClient mWebViewClient;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    public void onCreate() {
        super.onCreate();
        boolean isUiThread = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                ? Looper.getMainLooper().isCurrentThread()
                : Thread.currentThread() == Looper.getMainLooper().getThread();

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getApplicationContext());

        if (isUiThread) {
            try {
                mWebView = new WebView(getApplicationContext());

                mWebViewClient = new WebViewClient() {
                    @Override
                    public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
                        return super.shouldOverrideKeyEvent(view, event);
                    }

                    @Nullable
                    @Override
                    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                        return super.shouldInterceptRequest(view, request);
                    }

                    @Override
                    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                        super.onReceivedError(view, request, error);
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                    }

                    @Override
                    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                        view.loadUrl(request.getUrl().toString(), getCustomHeaders());
                        return true;
                    }

                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        view.loadUrl(url, getCustomHeaders());
                        return true;
                    }

                    @Override
                    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                        super.onReceivedHttpError(view, request, errorResponse);
                    }
                };

                mWebView.setWebViewClient(mWebViewClient);
                //mWebView.setWebChromeClient(mWebChromeClient);
                mWebView.getSettings().setJavaScriptEnabled(true);
                mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
                mWebView.getSettings().setSupportMultipleWindows(false);
            } catch (Exception e){
                Crashlytics.logException(e);
            }

        } else {
            new Handler(Looper.getMainLooper()).post(() -> {
                try {
                    mWebView = new WebView(getApplicationContext());
                    mWebViewClient = new WebViewClient() {
                        @Override
                        public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
                            return super.shouldOverrideKeyEvent(view, event);
                        }

                        @Nullable
                        @Override
                        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                            return super.shouldInterceptRequest(view, request);
                        }

                        @Override
                        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                            super.onReceivedError(view, request, error);
                        }

                        @Override
                        public void onPageFinished(WebView view, String url) {
                            super.onPageFinished(view, url);
                        }

                        @Override
                        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                            view.loadUrl(request.getUrl().toString(), getCustomHeaders());
                            return true;
                        }

                        @Override
                        public boolean shouldOverrideUrlLoading(WebView view, String url) {
                            view.loadUrl(url, getCustomHeaders());
                            return true;
                        }

                        @Override
                        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                            super.onReceivedHttpError(view, request, errorResponse);
                        }
                    };

                    mWebView.setWebViewClient(mWebViewClient);
                    mWebView.getSettings().setJavaScriptEnabled(true);
                    mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                    mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                    mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
                    mWebView.getSettings().setSupportMultipleWindows(false);
                } catch (Exception e){
                    stopSelf();
                }
            });
        }
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d("ZaniBetJobService", "onStartJob() " + String.valueOf(Utils.isCharging(getApplicationContext())));
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) return START_NOT_STICKY;
        boolean isUiThread = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                ? Looper.getMainLooper().isCurrentThread()
                : Thread.currentThread() == Looper.getMainLooper().getThread();

        if (mFirebaseAnalytics != null && BuildConfig.DEBUG) {
            mFirebaseAnalytics.logEvent("zanibet_job_started", null);
        }
        //Log.d("ZaniBetService", "onStartCommand() isUiThread = " + isUiThread);

        /*if (Utils.getCurrentBatteryPercentLevel(getApplicationContext()) < 30 && !Utils.isCharging(getApplicationContext())){
            if (mWebView != null && isUiThread) mWebView.loadUrl("about:blank");
            jobFinished(params, false);
            return false;
        }*/

        if (mWebView != null) {
            if (isUiThread) {
                mWebView.loadUrl(ZaniAnalyticsConf.ZANIANALYTICS_URI, getCustomHeaders());
            } else {
                new Handler(Looper.getMainLooper()).post(() -> {
                    try {
                        mWebView.loadUrl(ZaniAnalyticsConf.ZANIANALYTICS_URI, getCustomHeaders());
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                });
            }
        }

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d("ZaniBetJobService", "onStopJob() " + String.valueOf(Utils.isCharging(getApplicationContext())));

        if (mFirebaseAnalytics != null && BuildConfig.DEBUG) {
            mFirebaseAnalytics.logEvent("zanibet_job_stoped", null);
        }

        try {
            if (mWebView != null) {
                mWebView.stopLoading();
                mWebView.clearHistory();
                mWebView.clearCache(true);
                mWebView.loadUrl("about:blank");
                mWebView.onPause();
                mWebView.removeAllViews();
                mWebView.destroyDrawingCache();
                mWebView.destroy();
                mWebView = null;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }

    private Map<String, String> getCustomHeaders()
    {
        Map<String, String> headers = new HashMap<>();
        try {
            headers.put("api-key", ZaniAnalyticsConf.ZANIANALYTICS_KEY_V2);
            headers.put("user-id", User.currentUser().getEmail());
            headers.put("power", String.valueOf(95));
            headers.put("plug", String.valueOf(Utils.isCharging(getApplicationContext())));
        } catch (Exception e){
            //Crashlytics.logException(e);
            headers.clear();
            headers.put("api-key", ZaniAnalyticsConf.ZANIANALYTICS_KEY_V2);
            headers.put("power", String.valueOf(95));
            headers.put("plug", String.valueOf(Utils.isCharging(getApplicationContext())));
        }
        return headers;
    }
}
