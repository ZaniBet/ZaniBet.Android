package eu.devolios.zanibet.analytics;

import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

public class ZaniAlarmReceiver extends WakefulBroadcastReceiver {
    PowerManager.WakeLock screenWakeLock;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("ZaniAlarm", "onReceive");
        if (screenWakeLock == null)
        {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            screenWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP,
                    "ScreenLock tag from AlarmListener");
            screenWakeLock.acquire();
        }
        Intent service = new Intent(context, ZaniBetService.class);
        startWakefulService(context, service);
        if (screenWakeLock != null)
            screenWakeLock.release();
    }
}
