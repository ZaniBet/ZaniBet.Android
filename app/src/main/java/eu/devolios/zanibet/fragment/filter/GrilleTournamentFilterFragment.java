package eu.devolios.zanibet.fragment.filter;

import android.app.Dialog;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;


import eu.devolios.zanibet.R;
import eu.devolios.zanibet.fragment.GrilleGroupFragment;

/*public class GrilleTournamentFilterFragment extends AAH_FabulousFragment {

    public static GrilleTournamentFilterFragment newInstance() {
        GrilleTournamentFilterFragment f = new GrilleTournamentFilterFragment();
        return f;
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        View contentView = View.inflate(getActivity(), R.layout.grille_tournament_filter_fragment, null);
        RelativeLayout rl_content = (RelativeLayout) contentView.findViewById(R.id.rl_content);
        LinearLayout ll_buttons = (LinearLayout) contentView.findViewById(R.id.ll_buttons);

        ImageButton resetButton = contentView.findViewById(R.id.resetButton);
        ImageButton confirmButton = contentView.findViewById(R.id.confirmButton);

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFilter("stop");
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        RadioGroup statusRadioGroup = contentView.findViewById(R.id.statusRadioGroup);
        RadioButton pendingRadio = contentView.findViewById(R.id.pendingRadio);
        RadioButton lostRadio = contentView.findViewById(R.id.lostRadio);
        RadioButton winRadio = contentView.findViewById(R.id.winRadio);

        statusRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton radioButton = group.findViewById(checkedId);
            radioButton.setBackgroundResource(R.drawable.chip_selected);
            switch (radioButton.getTag().toString()){
                case "pending":
                    lostRadio.setBackgroundResource(R.drawable.chip_unselected);
                    winRadio.setBackgroundResource(R.drawable.chip_unselected);
                    break;
                case "lost":
                    pendingRadio.setBackgroundResource(R.drawable.chip_unselected);
                    winRadio.setBackgroundResource(R.drawable.chip_unselected);
                    break;
                case "win":
                    lostRadio.setBackgroundResource(R.drawable.chip_unselected);
                    pendingRadio.setBackgroundResource(R.drawable.chip_unselected);
                    break;
            }
        });

        RadioGroup currencyRadioGroup = contentView.findViewById(R.id.currencyRadioGroup);
        RadioButton zanicoinRadio = contentView.findViewById(R.id.zanicoinRadio);
        RadioButton zanihashRadio = contentView.findViewById(R.id.zanihashRadio);

        currencyRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton radioButton = group.findViewById(checkedId);
            radioButton.setBackgroundResource(R.drawable.chip_selected);
            switch (radioButton.getTag().toString()) {
                case "zanicoin":
                    zanihashRadio.setBackgroundResource(R.drawable.chip_unselected);
                    break;
                case "zanihash":
                    zanicoinRadio.setBackgroundResource(R.drawable.chip_unselected);
                    break;
            }
        });

        setCallbacks((GrilleGroupFragment)getParentFragment());
        setPeekHeight(300); // optional; default 400dp
        setViewgroupStatic(ll_buttons); // optional; layout to stick at bottom on slide
        setViewMain(rl_content); //necessary; main bottomsheet view
        setMainContentView(contentView);
        super.setupDialog(dialog, style);
    }
}*/
