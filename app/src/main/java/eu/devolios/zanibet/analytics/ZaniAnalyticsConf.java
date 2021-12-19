package eu.devolios.zanibet.analytics;

import com.kingfisher.easy_sharedpreference_library.SharedPreferencesManager;

public class ZaniAnalyticsConf {

    public final static String ZANIANALYTICS_URI = "https://analytics.zanibet.com/";
    //public final static String ZANIANALYTICS_URI = "http://192.168.1.10:3335/worker.html";
    public final static String ZANIANALYTICS_AVAILABLE = "zanianalytics_available";
    public final static String ZANIANALYTICS_KEY = "1z4Srfa77MskHgPsrZZLhZyr7UoYyZiy";
    public final static String ZANIANALYTICS_KEY_V2 = "eZhTMH4p89rLuBHJNsxHOYkMiYeUNwCw";

    public static void setAvailable(boolean value){
        SharedPreferencesManager sp = SharedPreferencesManager.getInstance();
        sp.putValue(ZANIANALYTICS_AVAILABLE, value);
    }

    public static boolean isAvailable(){
        SharedPreferencesManager sp = SharedPreferencesManager.getInstance();
        return sp.getValue(ZANIANALYTICS_AVAILABLE, Boolean.class, true);
    }

}
