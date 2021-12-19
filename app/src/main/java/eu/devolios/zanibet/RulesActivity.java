package eu.devolios.zanibet;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.MaterialIcons;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.devolios.zanibet.presenter.LoadingPresenter;


/**
 * Created by Gromat Luidgi on 12/12/2017.
 */

public class RulesActivity extends AppCompatActivity {

    @BindView(eu.devolios.zanibet.R.id.rulesTextView)
    TextView mRulesTextView;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_rules);
        ButterKnife.bind(this, this);

        mToolbar.setTitle(getString(R.string.title_rules));
        mToolbar.setNavigationIcon(new IconDrawable(getApplicationContext(), MaterialIcons.md_arrow_back).colorRes(R.color.colorWhite).actionBarSize());
        mToolbar.setNavigationOnClickListener(v -> onBackPressed());
        setSupportActionBar(mToolbar);
        mRulesTextView.setText(Html.fromHtml(getString(eu.devolios.zanibet.R.string.rules)));
    }

}
