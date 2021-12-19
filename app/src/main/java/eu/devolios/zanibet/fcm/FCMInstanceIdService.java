package eu.devolios.zanibet.fcm;

import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import eu.devolios.zanibet.model.User;
import eu.devolios.zanibet.ws.Injector;
import eu.devolios.zanibet.ws.UserService;

/**
 * Created by Gromat Luidgi on 20/11/2017.
 */

public class FCMInstanceIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        try {
            // Get updated InstanceID token.
            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
            Log.d("FCMInstanceIdService", "Refreshed token: " + refreshedToken);

            // If you want to send messages to this application instance or
            // manage this apps subscriptions on the server side, send the
            // Instance ID token to your app server.
            //sendRegistrationToServer(refreshedToken);

            UserService userService = Injector.provideUserService(getApplicationContext());
            User user = new User();
            user.setFcmToken(refreshedToken);
            userService.updateFcmToken(user).execute();
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }




}
