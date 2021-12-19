package eu.devolios.zanibet.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.github.thunder413.datetimeutils.DateTimeUtils;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.devolios.zanibet.R;
import eu.devolios.zanibet.model.Competition;
import eu.devolios.zanibet.model.Fixture;
import eu.devolios.zanibet.model.GameTicket;
import eu.devolios.zanibet.utils.DateFormatter;

/**
 * Created by Gromat Luidgi on 27/03/2018.
 */

public class FixtureDetailsFragment extends Fragment {

    @BindView(R.id.dateTextView)
    TextView mDateTextView;
    @BindView(R.id.statusTextView)
    TextView mStatusTextView;
    @BindView(R.id.venueTextView)
    TextView mVenueTextView;
    @BindView(R.id.countryTextView)
    TextView mCountryTextView;
    @BindView(R.id.competitionTextView)
    TextView mCompetitionTextView;
    @BindView(R.id.divisionTextView)
    TextView mDivisionTextView;
    @BindView(R.id.matchDayTextView)
    TextView mMatchdayTextView;

    private GameTicket mGameTicket;

    public static FixtureDetailsFragment newInstance(GameTicket gameTicket){
        FixtureDetailsFragment fixtureDetailsFragment = new FixtureDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("gameticket", gameTicket);
        fixtureDetailsFragment.setArguments(bundle);
        return fixtureDetailsFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assert getArguments() != null;
        mGameTicket = (GameTicket) getArguments().getSerializable("gameticket");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fixture_details_fragment, container, false);
        ButterKnife.bind(this, view);

        Competition competition;
        if (mGameTicket.getCompetition() instanceof Competition){
            competition = (Competition) mGameTicket.getCompetition();
        } else if (mGameTicket.getCompetition() instanceof String){
            // TODO : Correction à effectuer coté serveur
            competition = new Competition();
            try {
                competition.setName((String) mGameTicket.getCompetition());
            } catch (Exception e){
                Crashlytics.logException(e);
            }
        } else {
            competition = Competition.parseCompetition(mGameTicket.getCompetition());
        }

        Fixture fixture = mGameTicket.getFixtures()[0];
        mCompetitionTextView.setText(competition.getName());

        if (fixture.getVenue() != null){
            mVenueTextView.setText(fixture.getVenue().getName());
        } else {
            mVenueTextView.setText(getActivity().getString(R.string.data_unaivalable));
        }

        if (fixture.getStatus().equals("soon")){
            mStatusTextView.setText(getActivity().getString(R.string.fixture_status_soon));
        } else if (fixture.getStatus().equals("playing")){
            mStatusTextView.setText(getActivity().getString(R.string.fixture_status_playing));
        } else if (fixture.getStatus().equals("canceled")){
            mStatusTextView.setText(getActivity().getString(R.string.fixture_status_canceled));
        } else if (fixture.getStatus().equals("postphoned")){
            mStatusTextView.setText(getActivity().getString(R.string.fixture_status_postphoned));
        } else if (fixture.getStatus().equals("done")){
            mStatusTextView.setText(getActivity().getString(R.string.fixture_status_done));
        }

        mCountryTextView.setText(competition.getCountry());
        mDivisionTextView.setText(String.valueOf(competition.getDivision()));

        mMatchdayTextView.setText(getString(R.string.matchday, mGameTicket.getMatchDay()));

        Date startDate = DateFormatter.formatMongoDate(fixture.getDate());
        mDateTextView.setText(DateTimeUtils.formatWithPattern(startDate, "dd MMM yyyy HH:mm"));

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Crashlytics.setString("last_ui_viewed", "FixtureDetailsFragment");
    }
}
