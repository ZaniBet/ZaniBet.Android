package eu.devolios.zanibet;

import com.crashlytics.android.Crashlytics;
import com.facebook.AccessToken;
import eu.devolios.zanibet.model.User;
import eu.devolios.zanibet.utils.Constants;
import com.kingfisher.easy_sharedpreference_library.SharedPreferencesManager;

/**
 * Created by Gromat Luidgi on 09/11/2017.
 */

public class DispatchActivity extends ZaniBetDispatchActivity {
    @Override
    protected Class<?> getTargetClass() {
        try {
            if (SharedPreferencesManager.getInstance().getValue(Constants.FIRST_OPEN_PREF, Boolean.class, true)) {
                return OnboardingActivity.class;
            } else if (AccessToken.getCurrentAccessToken() != null && !AccessToken.getCurrentAccessToken().isExpired()) {
                return LoadingActivity.class;
            } else if (User.getAccessToken() != null && !User.getAccessToken().isEmpty()) {
                return LoadingActivity.class;
            } else {
                return LoginActivity.class;
            }
        } catch (Exception e){
            Crashlytics.logException(e);
            return LoadingActivity.class;
        }
    }
}
