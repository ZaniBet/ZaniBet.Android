package eu.devolios.zanibet.utils;

import android.os.Bundle;

public interface ZaniBetTracking {

    void setCurrentScreen(String screenName, String activityName);
    void logEvent(String event, Bundle bundle);
}
