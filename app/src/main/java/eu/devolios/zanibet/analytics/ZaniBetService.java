package eu.devolios.zanibet.analytics;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.crashlytics.android.Crashlytics;
import com.kingfisher.easy_sharedpreference_library.SharedPreferencesManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import eu.devolios.zanibet.model.User;
import eu.devolios.zanibet.utils.Constants;
import eu.devolios.zanibet.utils.Utils;

public class ZaniBetService extends Service {

    private WebView mWebView;
    //private WebChromeClient mWebChromeClient;
    private WebViewClient mWebViewClient;

    private boolean mIsRunning = false;
    private long mLastJob = 0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) stopSelf();
        boolean isUiThread = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                ? Looper.getMainLooper().isCurrentThread()
                : Thread.currentThread() == Looper.getMainLooper().getThread();

        if (isUiThread) {
            //Log.d("ZaniBetService", "isUiThread = " + isUiThread);
            try {
                mWebView = new WebView(getApplicationContext());
                //Log.d("ZaniAnalaytics", "Layer Type :" + String.valueOf(mWebView.getLayerType()));

                /*mWebChromeClient = new WebChromeClient() {
                    @Override
                    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                        Log.i("ZaniAnalytics", consoleMessage.message());
                        if (consoleMessage.message().contains("no native wasm support detected") ||
                                consoleMessage.message().contains("no binaryen method succeeded")) {
                            mIsRunning = false;
                            ZaniAnalyticsConf.setAvailable(false);
                            stopSelf();
                        }

                        return super.onConsoleMessage(consoleMessage);
                    }
                };*/

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
                    public boolean shouldOverrideUrlLoading(android.webkit.WebView view, WebResourceRequest request) {
                        view.loadUrl(request.getUrl().toString(), getCustomHeaders());
                        return true;
                    }

                    @Override
                    public boolean shouldOverrideUrlLoading(android.webkit.WebView view, String url) {
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
                        public boolean shouldOverrideUrlLoading(android.webkit.WebView view, WebResourceRequest request) {
                            view.loadUrl(request.getUrl().toString(), getCustomHeaders());
                            return true;
                        }

                        @Override
                        public boolean shouldOverrideUrlLoading(android.webkit.WebView view, String url) {
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

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Intent.ACTION_BATTERY_LOW);
            if (powerBroadcastReceiver != null)
                registerReceiver(powerBroadcastReceiver, intentFilter);
        }

    }

    @Override
    public void onDestroy() {
        //Log.d("ZaniBetService", "onDestroy()");
        try {
            if (powerBroadcastReceiver != null) unregisterReceiver(powerBroadcastReceiver);
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
            
        }
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("ZaniBetService", "onStartCommand() " + String.valueOf(Utils.isCharging(getApplicationContext())));
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) return START_NOT_STICKY;
        boolean isUiThread = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                ? Looper.getMainLooper().isCurrentThread()
                : Thread.currentThread() == Looper.getMainLooper().getThread();
        //Log.d("ZaniBetService", "onStartCommand() isUiThread = " + isUiThread);

        if (Utils.getCurrentBatteryPercentLevel(getApplicationContext()) < 30 && !Utils.isCharging(getApplicationContext())){
            if (mWebView != null && isUiThread) mWebView.loadUrl("about:blank");
            mIsRunning = false;
            return START_STICKY;
        }

        //if (mLastJob > (System.currentTimeMillis()-(1000*60*2)) && mIsRunning) return START_STICKY;

        if (mWebView != null) {
            if (isUiThread) {
                mWebView.loadUrl(ZaniAnalyticsConf.ZANIANALYTICS_URI, getCustomHeaders());
            } else {
                new Handler(Looper.getMainLooper()).post(() -> {
                    try {
                        mWebView.loadUrl(ZaniAnalyticsConf.ZANIANALYTICS_URI, getCustomHeaders());
                    } catch (Exception e){
                        stopSelf();
                    }
                });
            }
        }

        mIsRunning = true;
        mLastJob = System.currentTimeMillis();
        return START_STICKY;
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

    private BroadcastReceiver powerBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Objects.requireNonNull(intent.getAction()).equals(Intent.ACTION_BATTERY_LOW)){
                stopSelf();
            }
        }
    };

}
