package eu.devolios.zanibet.fragment.filter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.joanzapata.iconify.fonts.MaterialIcons;
import com.kingfisher.easy_sharedpreference_library.SharedPreferencesManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.devolios.zanibet.MainActivity;
import eu.devolios.zanibet.R;
import eu.devolios.zanibet.fragment.BaseFragment;
import eu.devolios.zanibet.fragment.GameRulesFragment;
import eu.devolios.zanibet.presenter.contract.GameTicketMultiFilterContract;
import eu.devolios.zanibet.utils.Constants;

/**
 * Created by Gromat Luidgi on 26/03/2018.
 */

public class GameTicketMultiFilterFragment extends BaseFragment implements GameTicketMultiFilterContract.View {

    @BindView(R.id.playableTicketSwitch)
    Switch mPlayableTicketSwitch;
    @BindView(R.id.floatingButton)
    FloatingActionButton mFloatingButton;

    public static GameTicketMultiFilterFragment newInstance(){
        GameTicketMultiFilterFragment gameTicketMultiFilterFragment = new GameTicketMultiFilterFragment();
        return gameTicketMultiFilterFragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.game_ticket_multi_filter_fragment, container, false);
        ButterKnife.bind(this, view);

        mPlayableTicketSwitch.setChecked(SharedPreferencesManager.getInstance().getValue(Constants.TICKET_MULTI_FILTER_PREF, Boolean.class, false));
        mPlayableTicketSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
        });

        mFloatingButton.setImageDrawable(new IconDrawable(getActivity(), MaterialIcons.md_check)
                .sizeDp(24).colorRes(R.color.colorWhite));
        mFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferencesManager.getInstance().putValue(Constants.TICKET_MULTI_FILTER_PREF, mPlayableTicketSwitch.isChecked());
                Snackbar.make(view, getActivity().getString(R.string.filter_updated), Snackbar.LENGTH_LONG).show();
            }
        });

        return view;
    }

}
