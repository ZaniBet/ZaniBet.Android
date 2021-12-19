package eu.devolios.zanibet.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import eu.devolios.zanibet.R;
import com.joanzapata.iconify.widget.IconTextView;

/**
 * Created by Gromat Luidgi on 29/08/2017.
 */

public class EmptyView {

    public static int EMPTY_GAMETICKET = R.string.empty_gameticket;
    public static int EMPTY_GAMETICKET_SINGLE = R.string.empty_gameticket_single;
    public static int EMPTY_GAMETICKET_TOURNAMENT = R.string.empty_ticket_tournament;


    // public static int EMPTY_COMPETITION_GAMETICKET_SINGLE = R.string.empty_gameticket_single;

    public static int EMPTY_GRILLE_WAITING = R.string.empty_grille_waiting;
    public static int EMPTY_GRILLE_LOOSE = R.string.empty_grille_loose;
    public static int EMPTY_GRILLE_WIN = R.string.empty_grille_win;
    public static int EMPTY_REWARD = R.string.empty_reward;
    public static int EMPTY_PAYOUT = R.string.empty_payout;
    public static int EMPTY_HELP = R.string.empty_help;
    public static int EMPTY_GRILLE_SIMPLE = R.string.empty_grille_simple;
    public static int EMPTY_FIXTURE_STATS = R.string.empty_fixture_stats;
    public static int EMPTY_LEAGUE_STANDING = R.string.empty_league_standing;
    public static int EMPTY_GRILLE_TOURNAMENT = R.string.empty_grid_tournament;

    public static View withIcon(Context context, String message, String icon){
        View emptyView = LayoutInflater.from(context).inflate(R.layout.empty_view, null);
        emptyView.setTag("empty_view");

        IconTextView iconTextView = emptyView.findViewById(R.id.icon_textview);
        TextView messageTextView = emptyView.findViewById(R.id.message_textview);

        iconTextView.setText(icon);
        messageTextView.setText(message);
        return emptyView;
    }

    public static View withDrawable(Context context, String message, Drawable drawable){
        View emptyView = LayoutInflater.from(context).inflate(R.layout.empty_view_drawable, null);
        emptyView.setTag("empty_view");

        ImageView imageView = emptyView.findViewById(R.id.imageView);
        TextView messageTextView = emptyView.findViewById(R.id.message_textview);

        imageView.setImageDrawable(drawable);
        if (message == null) message = "";
        messageTextView.setText(Html.fromHtml(message));
        return emptyView;
    }

}
