package eu.devolios.zanibet.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rilixtech.materialfancybutton.MaterialFancyButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.devolios.zanibet.PartnerBonusActivity;
import eu.devolios.zanibet.PartnerInvitedStatsActivity;
import eu.devolios.zanibet.PartnerPayoutStats;
import eu.devolios.zanibet.R;
import eu.devolios.zanibet.model.Manager;
import eu.devolios.zanibet.model.User;


/**
 * Created by Gromat Luidgi on 11/03/2018.
 */


public class PartnershipFragment extends Fragment {

    @BindView(R.id.managerEmailTextView)
    TextView mManagerEmailTextView;
    @BindView(R.id.managerNameTextView)
    TextView mManagerNameTextView;
    @BindView(R.id.managerSkypeTextView)
    TextView mManagerSkypeTextView;

    @BindView(R.id.advantageButton)
    MaterialFancyButton mBonusButton;
    @BindView(R.id.invitedStatsButton)
    MaterialFancyButton mInvitedStatsButton;
    @BindView(R.id.revenuStatsButton)
    MaterialFancyButton mRevenuStatsButton;


    public static PartnershipFragment newInstance(){
        PartnershipFragment partnershipFragment = new PartnershipFragment();
        return partnershipFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.partnership_fragment, container, false);
        ButterKnife.bind(this, view);

        Manager manager = User.currentUser().getManager();
        if (manager != null) {
            mManagerEmailTextView.setText(manager.getEmail());
            mManagerNameTextView.setText(manager.getName());
            mManagerSkypeTextView.setText(manager.getSkype());
        }

        mBonusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PartnerBonusActivity.class);
                getActivity().startActivity(intent);
            }
        });

        mInvitedStatsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PartnerInvitedStatsActivity.class);
                getActivity().startActivity(intent);
            }
        });

        mRevenuStatsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PartnerPayoutStats.class);
                getActivity().startActivity(intent);
            }
        });
        return view;
    }

}
