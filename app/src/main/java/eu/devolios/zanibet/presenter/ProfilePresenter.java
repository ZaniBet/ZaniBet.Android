package eu.devolios.zanibet.presenter;

import android.content.Context;
import android.graphics.drawable.Drawable;

import eu.devolios.zanibet.R;
import eu.devolios.zanibet.model.ApiError;
import eu.devolios.zanibet.model.User;
import eu.devolios.zanibet.presenter.contract.ProfileContract;
import eu.devolios.zanibet.utils.Constants;
import eu.devolios.zanibet.utils.DateFormatter;
import eu.devolios.zanibet.ws.Injector;
import eu.devolios.zanibet.ws.UserService;

import com.github.thunder413.datetimeutils.DateTimeUnits;
import com.github.thunder413.datetimeutils.DateTimeUtils;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.MaterialIcons;
import com.kingfisher.easy_sharedpreference_library.SharedPreferencesManager;

import java.util.ArrayList;
import java.util.Date;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Gromat Luidgi on 10/11/2017.
 */

public class ProfilePresenter implements ProfileContract.Presenter {

    private ProfileContract.View mView;
    private Context mContext;
    private UserService mUserService;

    public ProfilePresenter(ProfileContract.View view, Context context) {
        mView = view;
        mContext = context;
        mUserService = Injector.provideUserService(context);
    }

    @Override
    public void load() {
        ArrayList<Menu> menuArrayList = new ArrayList<>();
        menuArrayList.add(new Menu(Menu.MY_ACCOUNT_MENU,
                mContext.getDrawable(R.drawable.ic_account_secure),
                mContext.getString(R.string.menu_my_account),
                ""));

        menuArrayList.add(new Menu(Menu.PRIVATE_INFO_MENU,
                mContext.getDrawable(R.drawable.ic_wallet1),
                mContext.getString(R.string.menu_paiement_info),
                mContext.getString(R.string.menu_paiement_detail)));

        menuArrayList.add(new Menu(Menu.PAYOUT_MENU,
                mContext.getDrawable(R.drawable.ic_payment_invoice),
                mContext.getString(R.string.menu_paiement_history), ""));

        if (SharedPreferencesManager.getInstance().getValue(Constants.INVITATION_CODE_ACTIVE_PREF, Boolean.class, true)) {
            if (User.currentUser().getReferral() != null) {
                if (User.currentUser().getReferral().getReferrerCode() == null || User.currentUser().getReferral().getReferrerCode().isEmpty()) {

                    Date createdAt = DateFormatter.formatMongoDate(User.currentUser().getCreatedAt());
                    if (DateTimeUtils.getDateDiff(new Date(), createdAt, DateTimeUnits.DAYS) < 7) {
                        menuArrayList.add(new Menu(Menu.INVITE_CODE_MENU,
                                mContext.getDrawable(R.drawable.ic_invitation_gift),
                                mContext.getString(R.string.menu_invite_code), ""));
                    }
                }
            }
        }

        menuArrayList.add(new Menu(Menu.INVITE_FRIENDS_MENU,
                mContext.getDrawable(R.drawable.ic_share),
                mContext.getString(R.string.menu_share), mContext.getString(R.string.menu_share_desc)));

        /*menuArrayList.add(new Menu(Menu.ABOUT_MENU,
                new IconDrawable(mContext, MaterialIcons.md_business).sizeDp(24).colorRes(R.color.colorPrimaryDark),
                mContext.getString(R.string.menu_about), ""));*/

        /*menuArrayList.add(new Menu(Menu.DISCONNECT_MENU,
                new IconDrawable(mContext, MaterialIcons.md_exit_to_app).sizeDp(24).colorRes(R.color.colorPrimaryDark),
                mContext.getString(R.string.menu_disconnect), ""));*/


        mView.addMenus(menuArrayList);

        mUserService.getCurrentUser()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<User>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(User user) {
                        User.saveUser(user);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.showLoadError(ApiError.parseRxError(mContext, e));
                    }
                });
    }

    public static class Menu {

        public final static int PRIVATE_INFO_MENU = 0;
        public final static int PAYOUT_MENU = 1;
        public final static int RULES_MENU = 2;
        public final static int MY_ACCOUNT_MENU = 3;
        public final static int ABOUT_MENU = 4;
        public final static int DISCONNECT_MENU = 5;
        public final static int INVITE_CODE_MENU = 6;
        public final static int INVITE_FRIENDS_MENU = 7;

        private int id;
        private Drawable icon;
        private String title;
        private String description;

        public Menu(int id, Drawable icon, String title, String description) {
            this.id = id;
            this.icon = icon;
            this.title = title;
            this.description = description;
        }

        public int getId() {
            return this.id;
        }

        public Drawable getIcon() {
            return this.icon;
        }

        public String getTitle() {
            return this.title;
        }

        public String getDescription() {
            return this.description;
        }
    }
}
