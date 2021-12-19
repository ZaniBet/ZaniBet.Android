package eu.devolios.zanibet.fragment.filter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.joanzapata.iconify.fonts.MaterialIcons;
import com.kingfisher.easy_sharedpreference_library.SharedPreferencesManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.devolios.zanibet.MainActivity;
import eu.devolios.zanibet.R;
import eu.devolios.zanibet.fragment.BaseFragment;
import eu.devolios.zanibet.fragment.GameRulesFragment;
import eu.devolios.zanibet.model.Competition;
import eu.devolios.zanibet.presenter.GameTicketSingleFilterPresenter;
import eu.devolios.zanibet.presenter.contract.GameTicketSingleFilterContract;
import eu.devolios.zanibet.utils.Constants;
import iammert.com.expandablelib.ExpandableLayout;
import iammert.com.expandablelib.Section;

/**
 * Created by Gromat Luidgi on 26/03/2018.
 */

public class GameTicketSingleFilterFragment extends BaseFragment implements GameTicketSingleFilterContract.View {

    @BindView(R.id.expandableLayout)
    ExpandableLayout mExpandableLayout;
    @BindView(R.id.floatingButton)
    FloatingActionButton mFloatingButton;

    private GameTicketSingleFilterPresenter mGameTicketSingleFilterPresenter;
    private List<String> mSelectedFilterList;
    private List<CheckBox> mCheckBoxList;

    public static GameTicketSingleFilterFragment newInstance(){
        GameTicketSingleFilterFragment gameTicketSingleFilterFragment = new GameTicketSingleFilterFragment();
        return gameTicketSingleFilterFragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGameTicketSingleFilterPresenter = new GameTicketSingleFilterPresenter(getActivity(), this);
        mSelectedFilterList = new ArrayList<>();
        mCheckBoxList = new ArrayList<>();

        if (SharedPreferencesManager.getInstance().getValues(Constants.TICKET_SINGLE_FILTER_PREF, String[].class) != null) {
            mSelectedFilterList.addAll(SharedPreferencesManager.getInstance().getValues(Constants.TICKET_SINGLE_FILTER_PREF, String[].class));
        }
        //System.out.println(mSelectedFilterList);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.game_ticket_single_filter_fragment, container, false);
        ButterKnife.bind(this, view);

        final CheckBox.OnClickListener checkBoxClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSelectedFilterList.contains(view.getTag()) == false){
                    //Log.d("Add Filter", view.getTag().toString());
                    mSelectedFilterList.add(view.getTag().toString());
                } else {
                    //Log.d("Remove Filter", view.getTag().toString());
                    mSelectedFilterList.remove(view.getTag().toString());
                }
            }
        };

        mExpandableLayout.setRenderer(new ExpandableLayout.Renderer<String, Competition>() {
            @Override
            public void renderParent(View view, String country, boolean isExpanded, int parentPosition) {
                ((TextView) view.findViewById(R.id.countryTextView)).setText(country);
                int flag = getActivity().getResources()
                        .getIdentifier(country.toLowerCase().replace(" ", "_") + "_flag_round",
                        "drawable", getActivity().getPackageName());
                if (flag != 0){
                    ((ImageView) view.findViewById(R.id.flagImageView)).setImageResource(flag);
                } else {
                    ((ImageView) view.findViewById(R.id.flagImageView)).setImageResource(R.drawable.world_flag_round);
                }
            }

            @Override
            public void renderChild(View view, Competition competition, int parentPosition, int childPosition) {
                CheckBox checkBox = ((CheckBox) view.findViewById(R.id.leagueCheckBox));
                checkBox.setText(competition.getName());
                checkBox.setTag(competition.getId());
                checkBox.setOnClickListener(checkBoxClickListener);

                if (mSelectedFilterList.contains(competition.getId())){
                    checkBox.setChecked(true);
                }

                if (!mCheckBoxList.contains(checkBox)) mCheckBoxList.add(checkBox);
            }
        });

        List<Competition> competitionList = Competition.getCompetitions();

        Collections.sort(competitionList, new Comparator<Competition>() {
            @Override
            public int compare(Competition o1, Competition o2) {
                return o1.getCountry().compareTo(o2.getCountry());
            }
        });

        HashMap<String, List<Competition>> competitionMap = new LinkedHashMap<>();
        for (Competition competition : competitionList){
            List<Competition> cList = new ArrayList<>();
            for (Competition competition2 : competitionList){
                if (competition2.getCountry().equals(competition.getCountry())){
                    cList.add(competition2);
                }
            }
            competitionMap.put(competition.getCountry(), cList);

            /*if (competitionMap.containsKey(competition.getCountry())){
                List<Competition> cList = competitionMap.get(competition.getCountry());
                cList.add(competition);
                competitionMap.put(competition.getCountry(), cList);
            } else {
                List<Competition> cList = new ArrayList<>();
                cList.add(competition);
                competitionMap.put(competition.getCountry(), cList);
            }*/
        }


        boolean firstEntry = true;
        for (Map.Entry<String, List<Competition>> entry : competitionMap.entrySet()){
            Section<String, Competition> section = new Section<>();
            section.parent = entry.getKey();
            section.children = entry.getValue();
            if (firstEntry) section.expanded = true;
            firstEntry = false;

            for (Competition comp : entry.getValue()){
                if (mSelectedFilterList.contains(comp.getId())) section.expanded = true;
            }
            mExpandableLayout.addSection(section);
        }

        //mGameTicketSingleFilterPresenter.loadFilter();

        mFloatingButton.setImageDrawable(new IconDrawable(getActivity(), MaterialIcons.md_check)
                .sizeDp(24).colorRes(R.color.colorWhite));
        mFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFloatingButton.setEnabled(false);
                SharedPreferencesManager.getInstance().putValue(Constants.TICKET_SINGLE_FILTER_PREF, mSelectedFilterList);
                mFloatingButton.setEnabled(true);
                Snackbar.make(view, getActivity().getString(R.string.filter_updated), Snackbar.LENGTH_LONG).show();
            }
        });

        return view;
    }


    @Override
    public void showLoadingDialog() {

    }

    @Override
    public void filterLoaded() {

    }

    @Override
    public void filterSaved() {

    }
}
