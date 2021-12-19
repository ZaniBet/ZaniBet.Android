package eu.devolios.zanibet.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import eu.devolios.zanibet.GrilleMultiPlayPagerActivity;
import eu.devolios.zanibet.GrilleSinglePlayPagerActivity;
import eu.devolios.zanibet.MainActivity;
import eu.devolios.zanibet.adapter.GrilleRecyclerAdapter;
import eu.devolios.zanibet.R;
import eu.devolios.zanibet.model.GameTicket;
import eu.devolios.zanibet.model.Grille;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.devolios.zanibet.utils.EmptyView;

/**
 * Created by Gromat Luidgi on 10/11/2017.
 */

public class GrilleListFragment extends BaseFragment implements GrilleRecyclerAdapter.Listener {

    @BindView(R.id.frameLayout)
    FrameLayout mFrameLayout;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    private GrilleRecyclerAdapter mGrilleRecyclerAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private ArrayList<Grille> mGrilleList;
    //private FirebaseAnalytics mFirebaseAnalytics;
    //private AppodealWrapperAdapter mAppodealWrapperAdapter;

    public static GrilleListFragment newInstance(Grille[] grilles){
        GrilleListFragment grilleListFragment = new GrilleListFragment();
        Bundle args = new Bundle();
        args.putSerializable("grilles", grilles);
        grilleListFragment.setArguments(args);
        return grilleListFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
        mGrilleList = new ArrayList<>();
        try {
            Grille[] grilles = (Grille[]) getArguments().getSerializable("grilles");
            mGrilleList.addAll(Arrays.asList(grilles));
        } catch (Exception e){
            Crashlytics.logException(e);
        }
        mGrilleRecyclerAdapter = new GrilleRecyclerAdapter(getActivity(), mGrilleList, this);
        //mAppodealWrapperAdapter = new AppodealWrapperAdapter(mGrilleRecyclerAdapter, 4);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.grille_list_fragment, container, false);
        ButterKnife.bind(this, view);
        ((MainActivity)getActivity()).updateTitle(getActivity().getString(R.string.title_played_grid));

        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mGrilleRecyclerAdapter);
        //mRecyclerView.setAdapter(mAppodealWrapperAdapter);
        //DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(),
                //mLinearLayoutManager.getOrientation());
        //mRecyclerView.addItemDecoration(dividerItemDecoration);

        if (mGrilleList.isEmpty()){
            View errorView = EmptyView.withDrawable(getActivity(),
                    getActivity().getString(R.string.err_grille_list),
                    ContextCompat.getDrawable(getActivity(), R.drawable.internal_error));
            mFrameLayout.addView(errorView);
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Crashlytics.setString("last_ui_viewed", "GrilleListFragment");
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onGrilleSelected(Grille grille) {
        Intent intent;
        if (grille.getType().equals("MULTI")){
            GrilleMultiPlayPagerActivity.mGrille = grille;
            GrilleMultiPlayPagerActivity.mGameTicket = GameTicket.convertFromMap(grille.getGameTicket());
            intent = new Intent(getActivity(), GrilleMultiPlayPagerActivity.class);
            startActivity(intent);
        } else if (grille.getType().equals("SIMPLE")){
            GrilleSinglePlayPagerActivity.mGrille = grille;
            GrilleSinglePlayPagerActivity.mGameTicket = GameTicket.convertFromMap(grille.getGameTicket());
            intent = new Intent(getActivity(), GrilleSinglePlayPagerActivity.class);
            startActivity(intent);
        }

    }
}
