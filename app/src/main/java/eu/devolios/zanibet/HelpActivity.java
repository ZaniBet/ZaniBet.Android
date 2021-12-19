package eu.devolios.zanibet;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;

import eu.devolios.zanibet.adapter.HelpRecyclerAdapter;
import eu.devolios.zanibet.model.ApiError;
import eu.devolios.zanibet.model.Help;
import eu.devolios.zanibet.presenter.contract.HelpContract;
import eu.devolios.zanibet.presenter.HelpPresenter;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.MaterialIcons;
import com.joanzapata.iconify.widget.IconTextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.devolios.zanibet.utils.EmptyView;


/**
 * Created by Gromat Luidgi on 15/11/2017.
 */

public class HelpActivity extends AppCompatActivity implements HelpContract.View, HelpRecyclerAdapter.Listener {

    @BindView(R.id.frameLayout)
    FrameLayout mFrameLayout;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.loadingIconTextView)
    IconTextView mLoadingIconTextView;

    private HelpPresenter mHelpPresenter;
    private HelpRecyclerAdapter mHelpRecyclerAdapter;
    private ArrayList<Help> mHelpArrayList;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actvity_help);
        ButterKnife.bind(this);

        mHelpPresenter = new HelpPresenter(getApplicationContext(), this);

        mToolbar.setBackgroundColor(Color.TRANSPARENT);
        mToolbar.setTitle("");
        mToolbar.setNavigationIcon(new IconDrawable(getApplicationContext(), MaterialIcons.md_arrow_back).colorRes(R.color.colorWhite).actionBarSize());
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        setSupportActionBar(mToolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mHelpArrayList = new ArrayList<>();
        mHelpRecyclerAdapter = new HelpRecyclerAdapter(getApplicationContext(), this, mHelpArrayList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, linearLayoutManager.getOrientation()));
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mHelpRecyclerAdapter);
        mHelpPresenter.load();
    }

    @Override
    public void addHelps(List<Help> helps) {
        mHelpArrayList.clear();
        Help rules = new Help();
        rules.setSubject(getString(R.string.menu_rules));
        rules.setId("rules");
        rules.setIcon("fa_gavel");
        rules.setCaption(getString(R.string.rules_desc));
        mHelpArrayList.add(rules);
        mHelpArrayList.addAll(helps);
        mHelpRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void showLoadingContent() {
        if (mHelpArrayList.size() == 0){
            mLoadingIconTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hideLoadingContent() {
        mLoadingIconTextView.setVisibility(View.GONE);
    }

    @Override
    public void showContentError(ApiError apiError) {
        View errorView = EmptyView.withDrawable(getApplicationContext(),
                apiError.getMessage(),
                ContextCompat.getDrawable(getApplicationContext(), R.drawable.global_error));
        mFrameLayout.addView(errorView);
    }

    @Override
    public void hideContentError() {

    }

    @Override
    public void helpSelected(Help help) {
        if (help.getId().equals("rules")) {
            Intent intent = new Intent(getApplicationContext(), RulesActivity.class);
            startActivity(intent);
        } else if (help.getIcon().equals("fa_user_secret")){
            Intent intent = new Intent(getApplicationContext(), WebViewActivity.class);
            intent.putExtra("uri", "https://www.zanibet.com/privacy");
            startActivity(intent);
        } else {
            Intent intent = new Intent(getApplicationContext(), HelpDetailsActivity.class);
            intent.putExtra("help", help);
            startActivity(intent);
        }
    }
}
