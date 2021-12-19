package eu.devolios.zanibet.safety;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.AsyncTask;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.kingfisher.easy_sharedpreference_library.SharedPreferencesManager;

import java.io.IOException;
import java.util.UUID;

import eu.devolios.zanibet.utils.Constants;

public class Safety {


    public static void checkGooglePlayService(Context context){
        int result = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);
        switch (result){
            case ConnectionResult.SUCCESS:
                break;
            case ConnectionResult.SERVICE_MISSING:
                break;
            case ConnectionResult.SERVICE_DISABLED:
                break;
            case ConnectionResult.SERVICE_INVALID:
                break;
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                break;
            default:
                break;
        }
    }

    public static void setupInstanceId(){
        String instanceId = SharedPreferencesManager.getInstance().getValue(Constants.INSTANCE_ID_PREF, String.class,"");
        if (instanceId == null || instanceId.isEmpty() || instanceId.equals("")){
            SharedPreferencesManager.getInstance().putValue(Constants.INSTANCE_ID_PREF, UUID.randomUUID().toString());
        }
    }

    public static void setupAdvertiserId(final Context context){
        AsyncTask.execute(() -> {
            try {
                AdvertisingIdClient.Info adInfo = AdvertisingIdClient.getAdvertisingIdInfo(context);
                String adId = adInfo != null ? adInfo.getId() : "";
                Log.d("setupAdvertiserId", adId);
                SharedPreferencesManager.getInstance().putValue(Constants.ADS_ID_PREF, adId);
            } catch (IOException | GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException exception) {
                // Error handling if needed
                Crashlytics.logException(exception);
            }
        });
    }

    public static boolean checkVPN(Context context){
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            Network[] networks = cm.getAllNetworks();

            //Log.i("ZaniBet", "Network count: " + networks.length);
            boolean vpnActive = false;
            for (int i = 0; i < networks.length; i++) {

                NetworkCapabilities caps = cm.getNetworkCapabilities(networks[i]);
                //Log.i("ZaniBet", "Network " + i + ": " + networks[i].toString());
                //Log.i("ZaniBet", "VPN transport is: " + caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN));
                if (caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN)){
                    vpnActive = true;
                }
                //Log.i("ZaniBet", "NOT_VPN capability is: " + caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_VPN));
            }
            return vpnActive;
        } catch (Exception e){
            Crashlytics.logException(e);
            return false;
        }
    }

}
