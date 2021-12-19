package eu.devolios.zanibet.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import eu.devolios.zanibet.MainActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.devolios.zanibet.R;

/**
 * Created by Gromat Luidgi on 09/11/2017.
 */

public class GameRulesFragment extends BaseFragment {

@BindView(eu.devolios.zanibet.R.id.rulesTextView)
    TextView mRulesTextView;

    public static GameRulesFragment newInstance(){
        GameRulesFragment gameRulesFragment = new GameRulesFragment();
        return gameRulesFragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(eu.devolios.zanibet.R.layout.game_rules_fragment, container, false);
        ButterKnife.bind(this, view);
        ((MainActivity)getActivity()).updateTitle(getActivity().getString(R.string.title_rules));

        mRulesTextView.setText(getActivity().getString(eu.devolios.zanibet.R.string.rules));

        return view;
    }
}
