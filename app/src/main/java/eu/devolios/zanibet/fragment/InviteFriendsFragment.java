package eu.devolios.zanibet.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.rilixtech.materialfancybutton.MaterialFancyButton;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.devolios.zanibet.R;
import eu.devolios.zanibet.model.ApiError;
import eu.devolios.zanibet.model.User;
import eu.devolios.zanibet.presenter.InviteFriendsPresenter;
import eu.devolios.zanibet.presenter.contract.InviteFriendsContract;

import static android.view.View.GONE;


/**
 * Created by Gromat Luidgi on 11/03/2018.
 */

public class InviteFriendsFragment extends BaseFragment implements InviteFriendsContract.View{

    @BindView(R.id.inviteRewardTextView)
    TextView mInviteRewardTextView;
    @BindView(R.id.inviteDescTextView)
    TextView mInviteDescTextView;
    @BindView(R.id.inviteCodeTextView)
    TextView mInviteCodeTextView;
    @BindView(R.id.shareButton)
    MaterialFancyButton mShareButton;
    @BindView(R.id.editCodeButton)
    MaterialFancyButton mEditCodeButton;

    private InviteFriendsPresenter mInviteFriendsPresenter;
    private MaterialDialog mLoadingDialog;


    public static InviteFriendsFragment newInstance(){
        return new InviteFriendsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInviteFriendsPresenter = new InviteFriendsPresenter(getActivity(), this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.invite_friends_fragment, container, false);
        ButterKnife.bind(this, view);

        if (User.currentUser().getReferral() != null && User.currentUser().getReferral().getInvitationCodeEditAttempt() > 0){
            mEditCodeButton.setVisibility(GONE);
        }

        try {
            mInviteCodeTextView.setText(User.currentUser().getReferral().getInvitationCode());
            mInviteRewardTextView.setText(Html.fromHtml(getString(R.string.invite_reward,
                    User.currentUser().getReferral().getCoinRewardPercent(),
                    User.currentUser().getReferral().getCoinPerMultiTicketPlay())));
            mInviteDescTextView.setText(Html.fromHtml(getString(R.string.invite_friends_desc,
                    User.currentUser().getReferral().getInvitationBonus())));
        } catch(Exception e){
            e.printStackTrace();
        }

        mEditCodeButton.setOnClickListener(v -> new MaterialDialog.Builder(Objects.requireNonNull(getActivity()))
                .title(R.string.dlg_title_edit_invitation_code)
                .content(Html.fromHtml(getString(R.string.dlg_content_edit_invitation_code)))
                .inputType(InputType.TYPE_CLASS_TEXT)
                .inputRangeRes(4, 8, R.color.colorRed800)
                .input(R.string.invite_code, R.string.empty, (dialog, input) -> {
                    // Do something
                    mInviteFriendsPresenter.setCustomInvitationCode(input.toString());
                }).show());

        mShareButton.setOnClickListener(view1 ->
                FirebaseDynamicLinks.getInstance().createDynamicLink()
                 .setLink(Uri.parse("https://api.zanibet.com/referrer/" +
                         User.currentUser().getReferral().getInvitationCode()))
                 .setDynamicLinkDomain(getString(R.string.invitation_dynamic_link_domain))
                 .setAndroidParameters(
                         new DynamicLink.AndroidParameters.Builder("eu.devolios.zanibet")
                                 .setMinimumVersion(22)
                                 .build())
                 .setIosParameters(
                         new DynamicLink.IosParameters.Builder("eu.devolios.zanibet")
                                 .setAppStoreId("1321151766")
                                 .setMinimumVersion("1.4")
                                 .build())
                 .buildShortDynamicLink()
                .addOnCompleteListener(Objects.requireNonNull(getActivity()), task -> {
                    if (task.isSuccessful()) {
                        // Short link created
                        Uri shortLink = task.getResult().getShortLink();
                        Uri flowchartLink = task.getResult().getPreviewLink();

                        Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title, User.currentUser().getUsername()))
                                .setMessage(getString(R.string.invitation_message, shortLink))
                                .setDeepLink(shortLink)
                                .setCustomImage(Uri.parse(getString(R.string.invitation_custom_image)))
                                .setCallToActionText(getString(R.string.invitation_cta))
                                .build();
                        startActivityForResult(intent, 100);
                    } else {
                        // Error
                        // ...
                    }
                }));
        return view;
    }

    @Override
    public void showLoadingDialog() {
        mLoadingDialog = new MaterialDialog.Builder(Objects.requireNonNull(getActivity()))
                .title(getString(R.string.invite_code))
                .content(getString(R.string.ldg_content_update_invitation_code))
                .progress(true, 0)
                .show();
    }

    @Override
    public void hideLoadingDialog() {
        if (isVisible() && mLoadingDialog != null){
            mLoadingDialog.dismiss();
        }
    }

    @Override
    public void showUpdateInvitationCodeSuccess(String code) {
        try {
            if (isVisible()){
                new MaterialDialog.Builder(Objects.requireNonNull(getActivity()))
                        .title(getString(R.string.youha))
                        .titleColorRes(R.color.colorAccent)
                        .positiveColorRes(R.color.colorPrimaryDark)
                        .content(getString(R.string.dlg_content_invitation_code_updated))
                        .positiveText(getString(R.string.ok_exclamation))
                        .onPositive((dialog, which) -> {
                            mEditCodeButton.setVisibility(GONE);
                            mInviteCodeTextView.setText(code);
                        })
                        .show();
            }
        } catch (Exception e){
            Crashlytics.logException(e);
        }
    }

    @Override
    public void showUpdateInvitationCodeError(ApiError apiError) {
        try {
            if (isVisible()) {
                new MaterialDialog.Builder(Objects.requireNonNull(getActivity()))
                        .title(apiError.getTitle())
                        .titleColorRes(R.color.colorRed800)
                        .negativeColorRes(R.color.colorRed800)
                        .content(apiError.getMessage())
                        .negativeText(getString(R.string.ok_exclamation))
                        .show();
            }
        } catch (Exception e){
            Crashlytics.logException(e);
        }
    }
}
