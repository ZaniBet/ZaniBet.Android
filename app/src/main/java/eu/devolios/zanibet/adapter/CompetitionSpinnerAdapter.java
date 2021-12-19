package eu.devolios.zanibet.adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.List;

import eu.devolios.zanibet.R;
import eu.devolios.zanibet.model.Competition;

/**
 * Created by Created by Gromat Luidgi on 04/03/2018.
 */

public class CompetitionSpinnerAdapter extends ArrayAdapter<Competition> {

    public CompetitionSpinnerAdapter(@NonNull Context context, int resource, @NonNull List<Competition> objects) {
        super(context, resource, objects);
        if (objects != null) objects.add(0, new Competition());
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(getContext())
                .inflate(R.layout.filter_item_normal, parent, false);
        TextView competitionTextView = convertView.findViewById(R.id.competitionTextView);
        ImageView imageView = convertView.findViewById(R.id.iconImageView);

        if (position == 0){
            competitionTextView.setText(getContext().getText(R.string.current_filter_all_league));
            imageView.setImageResource(R.drawable.world_flag_round);
        } else {
            Competition competition = getItem(position);
            competitionTextView.setText(Html.fromHtml(getContext()
                    .getString(R.string.current_filter, competition.getName())));
            if (competition.getCountry() != null){
                int flag = getContext().getResources()
                        .getIdentifier(competition.getCountry().toLowerCase().replace(" ", "_") + "_flag_round",
                        "drawable", getContext().getPackageName());
                if (flag != 0){
                    imageView.setImageResource(flag);
                } else {
                    imageView.setImageResource(R.drawable.zanibet_logo);
                }
            }
        }

        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(getContext())
                .inflate(R.layout.filter_item_dropdown, parent, false);
        TextView competitionTextView = convertView.findViewById(R.id.competitionTextView);
        ImageView imageView = convertView.findViewById(R.id.iconImageView);

        if (position == 0){
            competitionTextView.setText(getContext().getText(R.string.filter_all_league));
            imageView.setImageResource(R.drawable.world_flag_round);
        } else {
            Competition competition = getItem(position);
            competitionTextView.setText(competition.getName());
            if (competition.getCountry() != null){
                int flag = getContext().getResources()
                        .getIdentifier(competition.getCountry().toLowerCase().replace(" ", "_") + "_flag_round",
                                "drawable", getContext().getPackageName());
                if (flag != 0){
                    imageView.setImageResource(flag);
                } else {
                    imageView.setImageResource(R.drawable.zanibet_logo);
                }
            }
        }

        return convertView;
    }

}
