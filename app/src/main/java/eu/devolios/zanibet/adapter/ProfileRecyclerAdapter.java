package eu.devolios.zanibet.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import eu.devolios.zanibet.R;
import eu.devolios.zanibet.presenter.ProfilePresenter;
import eu.devolios.zanibet.presenter.ProfilePresenter.Menu;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Gromat Luidgi on 10/11/2017.
 */

public class ProfileRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    static final int SINGLE_LINE_TYPE = 1;
    static final int TWO_LINES_TYPE = 2;

    private Context mContext;
    private ArrayList<Menu> mMenuArrayList;
    private Listener mListener;
    //int selected_position = -1;

    public ProfileRecyclerAdapter(Context context, ArrayList<Menu> menuArrayList, Listener listener){
        mContext = context;
        mMenuArrayList = menuArrayList;
        mListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        ProfilePresenter.Menu menu = mMenuArrayList.get(position);
        if(menu.getDescription() == null || menu.getDescription().isEmpty()){
            return SINGLE_LINE_TYPE;
        } else {
            return TWO_LINES_TYPE;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view;
        switch (viewType){
            case SINGLE_LINE_TYPE:
                view = inflater.inflate(R.layout.menu_single_line_item, parent, false);
                return new SingleLineViewHolder(view);
            case TWO_LINES_TYPE:
                view = inflater.inflate(R.layout.menu_two_lines_item, parent, false);
                return new TwoLinesViewHolder(view);
        }

        return null;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SingleLineViewHolder){
            //holder.itemView.setBackgroundColor(selected_position == position ? Color.GREEN : Color.TRANSPARENT);
            //holder.itemView.setSelected(selected_position == position);
            ((SingleLineViewHolder) holder).bindMenu(mMenuArrayList.get(position), mListener,mContext);
        } else if (holder instanceof TwoLinesViewHolder){
            //holder.itemView.setSelected(selected_position == position);
            //holder.itemView.setBackgroundColor(selected_position == position ? Color.GREEN : Color.TRANSPARENT);
            ((TwoLinesViewHolder) holder).bindMenu(mMenuArrayList.get(position), mListener, mContext);
        }
    }

    @Override
    public int getItemCount() {
        return mMenuArrayList.size();
    }


    static class SingleLineViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.imageView)
        ImageView mImageView;
        @BindView(R.id.titleTextView)
        TextView mTitleTextView;

        private Menu mMenu;
        private Listener mListener;

        public SingleLineViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        public void bindMenu(Menu menu, Listener listener, Context context){
            mMenu = menu;
            mListener = listener;
            mImageView.setImageDrawable(menu.getIcon());
            mTitleTextView.setText(menu.getTitle());
        }

        @Override
        public void onClick(View view) {
            mListener.onMenuSelected(mMenu);
        }
    }

    public class TwoLinesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.imageView)
        ImageView mImageView;
        @BindView(R.id.titleTextView)
        TextView mTitleTextView;
        @BindView(R.id.descriptionTextView)
        TextView mDescriptionTextView;

        private Menu mMenu;
        private Listener mListener;

        public TwoLinesViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        public void bindMenu(Menu menu, Listener listener, Context context){
            mMenu = menu;
            mListener = listener;
            mImageView.setImageDrawable(menu.getIcon());
            mTitleTextView.setText(menu.getTitle());
            mDescriptionTextView.setText(menu.getDescription());
        }

        @Override
        public void onClick(View view) {
            //if (getAdapterPosition() == RecyclerView.NO_POSITION) return;

            //notifyItemChanged(selected_position);
            //selected_position = getLayoutPosition();
            //notifyItemChanged(selected_position);

            mListener.onMenuSelected(mMenu);
        }
    }

    public interface Listener {
        void onMenuSelected(Menu menu);
    }

}
