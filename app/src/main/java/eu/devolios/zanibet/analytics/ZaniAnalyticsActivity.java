package eu.devolios.zanibet.analytics;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Debug;
import android.os.Handler;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.MaterialIcons;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.devolios.zanibet.BuildConfig;
import eu.devolios.zanibet.R;
import eu.devolios.zanibet.model.User;

public class ZaniAnalyticsActivity extends AppCompatActivity implements IZaniAnalyticsJS {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.webView)
    WebView mWebView;
    @BindView(R.id.main_content)
    RelativeLayout mMainContentLinearLayout;

    @BindView(R.id.statusTextView)
    TextView mStatusTextView;
    @BindView(R.id.zanihashTextView)
    TextView mZaniHashTextView;
    @BindView(R.id.sessionTextView)
    TextView mSessionTextView;
    @BindView(R.id.hashRateTextView)
    TextView mHashRateTextView;
    @BindView(R.id.helpTextView)
    TextView mHelpTextView;
    @BindView(R.id.webTipsTextView)
    TextView mWebTipsTextView;

    private final static int ANALYTICS_STATUS_INIT = -1;
    private final static int ANALYTICS_STATUS_STOP = 0;
    private final static int ANALYTICS_STATUS_RUNNING = 1;
    private final static int ANALYTICS_STATUS_ERROR = 2;


    private ZaniAnalyticsJS mZaniAnalyticsJS;
    private PowerManager mPowerManager;
    private PowerManager.WakeLock mWakeLock;
    private View mLoadingView;
    private long mSessionStartTime = 0;
    private long mSessionStopTime = 0;
    private long mHashSec = 0;
    private int mAnalyticsState = ANALYTICS_STATUS_INIT;
    private int mInitHash = 0;

    private Handler mHandler = new Handler();

    Runnable mStatusRunnable;

    {
        mStatusRunnable = () -> {
            try {
                if (mInitHash == 0){
                    mInitHash = mZaniAnalyticsJS.getHashrate();
                    if (mInitHash > 0){
                        ZaniAnalyticsConf.setAvailable(true);
                        mSessionStartTime = System.currentTimeMillis();
                        mAnalyticsState = ANALYTICS_STATUS_RUNNING;
                    } else {
                        mAnalyticsState = ANALYTICS_STATUS_INIT;
                    }
                }

                switch (mAnalyticsState) {
                    case ANALYTICS_STATUS_INIT:
                        runOnUiThread(() -> {
                            mStatusTextView.setText(Html.fromHtml(getString(R.string.analytics_status_activation)));
                        });
                        break;
                    case ANALYTICS_STATUS_RUNNING:
                        runOnUiThread(() -> {
                            long elapsedSinceStarted = (System.currentTimeMillis() - mSessionStartTime);

                            String formatElapsed =
                                    String.format("%02d", (elapsedSinceStarted/1000/60)/60) + ":" + String.format("%02d", (elapsedSinceStarted/1000/60)%60) + ":" + String.format("%02d", (elapsedSinceStarted/1000)%60);

                            mSessionTextView.setText(Html.fromHtml(getString(R.string.analytics_session_duration, formatElapsed)));
                            mStatusTextView.setText(Html.fromHtml(getString(R.string.analytics_status_running)));

                            if (mLoadingView != null && mMainContentLinearLayout != null){
                                if (mLoadingView.getParent() != null) mMainContentLinearLayout.removeView(mLoadingView);
                            }
                        });
                        break;
                    case ANALYTICS_STATUS_STOP:
                        runOnUiThread(() -> {
                            mStatusTextView.setText(Html.fromHtml(getString(R.string.analytics_status_stop)));
                        });
                        break;
                    case ANALYTICS_STATUS_ERROR:
                        runOnUiThread(() -> {
                            mStatusTextView.setText(Html.fromHtml(getString(R.string.analytics_status_incomp)));
                        });
                        break;
                }
            } finally {
                mHandler.postDelayed(mStatusRunnable, 2000);
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zani_analytics);
        ButterKnife.bind(this);

        mToolbar.setTitle(getString(R.string.tab_zanianalytics));
        mToolbar.setNavigationIcon(new IconDrawable(getApplicationContext(), MaterialIcons.md_arrow_back).colorRes(R.color.colorWhite).actionBarSize());
        mToolbar.setNavigationOnClickListener(v -> onBackPressed());
        setSupportActionBar(mToolbar);

        mLoadingView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.analytics_loading_overlay, mMainContentLinearLayout, false);
        mMainContentLinearLayout.addView(mLoadingView);

        mPowerManager = (PowerManager) getSystemService(POWER_SERVICE);
        mWakeLock = mPowerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"ZaniAnalyticsLock");
        mWakeLock.acquire();

        mZaniAnalyticsJS = new ZaniAnalyticsJS(this, 20);

        WebChromeClient webChromeClient = new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                //Log.i("ZaniAnalytics", consoleMessage.message());
                if (consoleMessage.message().contains("no native wasm support detected") ||
                        consoleMessage.message().contains("no binaryen method succeeded")){
                    mHandler.removeCallbacks(mStatusRunnable);
                    mWebView.loadUrl("about:blank");
                    try {
                        if (mLoadingView != null && mMainContentLinearLayout != null){
                            if (mLoadingView.getParent() != null) mMainContentLinearLayout.removeView(mLoadingView);
                        }
                        mStatusTextView.setText(Html.fromHtml(getString(R.string.analytics_status_incomp)));
                    } catch (Exception e){
                        if (BuildConfig.DEBUG) e.printStackTrace();
                    }

                    try {
                        if (mAnalyticsState != ANALYTICS_STATUS_ERROR) {
                            new MaterialDialog.Builder(ZaniAnalyticsActivity.this)
                                    .negativeColorRes(R.color.colorRed800)
                                    .titleColorRes(R.color.colorRed800)
                                    .contentColorRes(R.color.colorElectromagnetic)
                                    .title(getString(R.string.err_title_oups))
                                    .content(getString(R.string.dlg_content_analytics_error))
                                    .negativeText(getString(R.string.ok_exclamation))
                                    .show();
                        }
                    } catch (Exception e){
                        if (BuildConfig.DEBUG) e.printStackTrace();
                    }
                    mAnalyticsState = ANALYTICS_STATUS_ERROR;
                }
                return super.onConsoleMessage(consoleMessage);
            }
        };

        WebViewClient webViewClient = new WebViewClient() {
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


        mWebView.setWebViewClient(webViewClient);
        mWebView.setWebChromeClient(webChromeClient);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mWebView.getSettings().setSupportMultipleWindows(false);
        mWebView.getSettings().setJavaScriptEnabled(true);

        // Load analytics
        mWebView.addJavascriptInterface(mZaniAnalyticsJS, "zaniHashJS");
        mWebView.loadUrl(ZaniAnalyticsConf.ZANIANALYTICS_URI, getCustomHeaders());

        mStatusTextView.setText(Html.fromHtml(getString(R.string.analytics_status_activation)));
        mHelpTextView.setText(Html.fromHtml(getString(R.string.analytics_tips)));
        mWebTipsTextView.setText(Html.fromHtml(getString(R.string.analytics_tips_web)));

    }

    @Override
    public void onBackPressed() {
        if (mWebView != null){
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
        if (mWakeLock != null && mWakeLock.isHeld()) mWakeLock.release();
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        if (mWebView != null){
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
        if (mWakeLock != null && mWakeLock.isHeld()) mWakeLock.release();
        super.onDestroy();
    }

    private Map<String, String> getCustomHeaders()
    {
        Map<String, String> headers = new HashMap<>();
        headers.put("api-key", ZaniAnalyticsConf.ZANIANALYTICS_KEY);
        headers.put("user-id", User.currentUser().getEmail());
        headers.put("power", String.valueOf(mZaniAnalyticsJS.getPower()));
        return headers;
    }


    @Override
    public void onOpen() {
        Log.i("ZaniAnalytics", "onOpen()");
        mHandler.removeCallbacks(mStatusRunnable);
        if (mAnalyticsState != ANALYTICS_STATUS_ERROR && mAnalyticsState != ANALYTICS_STATUS_STOP) {
            mAnalyticsState = ANALYTICS_STATUS_INIT;
            if (mSessionStartTime == 0) mSessionStartTime = System.currentTimeMillis();
            mHandler.postDelayed(mStatusRunnable, 15000);
        } else if (mAnalyticsState == ANALYTICS_STATUS_ERROR){
            mStatusTextView.setText(Html.fromHtml(getString(R.string.analytics_status_incomp)));
        } else  {
            mAnalyticsState = ANALYTICS_STATUS_RUNNING;
            mHandler.postDelayed(mStatusRunnable, 0);
        }
    }

    @Override
    public void onClose() {
        Log.i("ZaniAnalytics", "onClose()");
        if (mAnalyticsState != ANALYTICS_STATUS_ERROR) mAnalyticsState = ANALYTICS_STATUS_STOP;
        mSessionStopTime = System.currentTimeMillis();
    }

    @Override
    public void onError(String error) {
        Log.e("ZaniAnalytics", "onError() : " + error);
        mAnalyticsState = ANALYTICS_STATUS_ERROR;
        mSessionStopTime = System.currentTimeMillis();

    }

    @Override
    public void onTotalHashChanged(int totalHash) {
        Log.i("ZaniAnalytics", "onTotalHashChanged : " + String.valueOf(totalHash));
        long elapsedSinceStarted = (System.currentTimeMillis() - mSessionStartTime)/1000;
        mHashSec = (totalHash-mInitHash)/elapsedSinceStarted;

        runOnUiThread(() -> {
            if (mAnalyticsState == ANALYTICS_STATUS_RUNNING) {
                mZaniHashTextView.setText(Html.fromHtml(getString(R.string.amount_zanihash, totalHash - mInitHash)));
                mHashRateTextView.setText(Html.fromHtml(getString(R.string.hashrate_second, mHashSec)));
            }
        });
        //Log.i("ZaniAnalytics", "Current Hash/s : " + String.valueOf(mHashSec));
    }

}
