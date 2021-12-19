package eu.devolios.zanibet.analytics;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.crashlytics.android.Crashlytics;
import com.rilixtech.materialfancybutton.MaterialFancyButton;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.devolios.zanibet.R;
import eu.devolios.zanibet.fragment.BaseFragment;
import eu.devolios.zanibet.model.ApiError;
import eu.devolios.zanibet.presenter.ZaniHashPresentationPresenter;
import eu.devolios.zanibet.presenter.contract.ZaniHashPresentationContract;


/**
 * Created by Gromat Luidgi on 11/03/2018.
 */

public class ZaniAnalyticsPresentatitionFragment extends BaseFragment implements ZaniHashPresentationContract.View {

    @BindView(R.id.startZaniAnalyticsButton)
    MaterialFancyButton mStartZaniAnalyticsButton;


    @BindView(R.id.descriptionTextView)
    TextView mDescriptionTextView;
    @BindView(R.id.questionTextView)
    TextView mQuestionTextView;

    private ZaniHashPresentationPresenter mZaniHashPresentationPresenter;
    private MaterialDialog mLoadingDialog;

    public static ZaniAnalyticsPresentatitionFragment newInstance(){
        return new ZaniAnalyticsPresentatitionFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mZaniHashPresentationPresenter = new ZaniHashPresentationPresenter(getActivity(), this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mStartZaniAnalyticsButton.setEnabled(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.zanihash_presentation_fragment, container, false);
        ButterKnife.bind(this, view);

        mDescriptionTextView.setText(Html.fromHtml(Objects.requireNonNull(getActivity()).getString(R.string.zanianalytics_desc)));
        mQuestionTextView.setText(Html.fromHtml(getActivity().getString(R.string.zanianalytics_question)));

        mStartZaniAnalyticsButton.setOnClickListener(v -> {
            getActivity().stopService(new Intent(getActivity(), ZaniBetService.class));
            mStartZaniAnalyticsButton.setEnabled(false);
            mZaniHashPresentationPresenter.enableZaniAnalytics(true);
            getActivity().startActivity(new Intent(getActivity(), ZaniAnalyticsActivity.class));
        });

        return view;
    }

    @Override
    public void onEnableZaniAnalytics(boolean value) {

    }

    @Override
    public void showEnableZaniAnalyticsLoadingDialog() {
        if (isAdded()){
            mLoadingDialog = new MaterialDialog.Builder(Objects.requireNonNull(getActivity()))
                    .title(getActivity().getString(R.string.loading))
                    .content(getActivity().getString(R.string.loading))
                    .progress(true, 0)
                    .show();
        }
    }

    @Override
    public void hideEnableZaniAnalyticsLoadingDialog() {
        if (isAdded()){
            if (mLoadingDialog != null) mLoadingDialog.dismiss();
        }
    }

    @Override
    public void showEnableZaniAnalyticsErrorDialog(ApiError apiError) {
        try {
            if (isAdded()) {
                mStartZaniAnalyticsButton.setEnabled(true);
                new MaterialDialog.Builder(Objects.requireNonNull(getActivity()))
                        .title(getActivity().getString(R.string.err_title_oups))
                        .content(apiError.getMessage())
                        .negativeColorRes(R.color.colorRed800)
                        .titleColorRes(R.color.colorRed800)
                        .negativeText(getActivity().getString(R.string.ok_exclamation))
                        .show();
            }
        } catch (Exception e){
            Crashlytics.logException(e);
        }
    }
}
