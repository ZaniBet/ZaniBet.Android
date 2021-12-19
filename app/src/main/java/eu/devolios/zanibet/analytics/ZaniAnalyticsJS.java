package eu.devolios.zanibet.analytics;

import android.webkit.JavascriptInterface;

public class ZaniAnalyticsJS {

    private IZaniAnalyticsJS iZaniAnalyticsJS;
    private int power;
    private int totalHash;

    public ZaniAnalyticsJS(IZaniAnalyticsJS iZaniAnalyticsJS, int power){
        this.iZaniAnalyticsJS = iZaniAnalyticsJS;
        this.power = power;
        this.totalHash = 0;
    }

    @JavascriptInterface
    public void open() {
        this.iZaniAnalyticsJS.onOpen();
    }

    @JavascriptInterface
    public void close() {
        this.iZaniAnalyticsJS.onClose();
    }

    @JavascriptInterface
    public void error(String error) {
        this.iZaniAnalyticsJS.onError(error);
    }

    @JavascriptInterface
    public void setHashRate(int hashrate) {
        this.totalHash = hashrate;
        this.iZaniAnalyticsJS.onTotalHashChanged(this.totalHash);
    }

    public int getHashrate() {
        return totalHash;
    }


    public void setPower(int power){
        this.power = power;
    }

    @JavascriptInterface
    public int getPower() {
        //Log.i("ZaniAnalytics", "Analytics requier power : "+ String.valueOf(power));
        return power;
    }

}
