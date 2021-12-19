package eu.devolios.zanibet.analytics;

public interface IZaniAnalyticsJS {

    void onOpen();
    void onClose();
    void onError(String error);
    void onTotalHashChanged(int hashrate);
}
