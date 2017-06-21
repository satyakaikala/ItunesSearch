package com.sunkin.itunessearch.data;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.sunkin.itunessearch.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by kaika on 5/25/2017.
 */

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {

    private final String TAG = SearchAdapter.class.getSimpleName();
    private final Context context;
    private final SearchItemOnClickHandler searchItemOnClickHandler;
    private ArrayList<SearchData> searchData = new ArrayList<>();
    private int itemPosition = -1;
    public SearchAdapter(Context context, SearchItemOnClickHandler searchItemOnClickHandler, ArrayList<SearchData> searchDataArrayList) {
        this.context = context;
        this.searchData = searchDataArrayList;
        this.searchItemOnClickHandler = searchItemOnClickHandler;
    }

    @Override
    public SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_item_list_view, parent, false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SearchViewHolder holder, final int position) {
        SearchData data = searchData.get(position);
        holder.trackName.setText(data.getTrackName());
        holder.trackPrice.setText(String.format("$%s", searchData.get(position).getTrackPrice()));
        Picasso.with(context)
                .load(data.getArtworkUrl30().trim())
                .noFade()
                .fit()
                .placeholder(R.drawable.place_holder_image)
                .error(R.drawable.error_loading_image)
                .into(holder.artImage);
    }

    @Override
    public int getItemCount() {
        return searchData.size();
    }

    public interface SearchItemOnClickHandler {
        void onClickSearchItem(SearchData searchData);
    }

    class SearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.art_image_url)
        ImageView artImage;
        @BindView(R.id.track_name)
        TextView trackName;
        @BindView(R.id.track_price)
        TextView trackPrice;

        private SearchViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            artImage.setOnClickListener(this);
            trackName.setOnClickListener(this);
            trackPrice.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            searchItemOnClickHandler.onClickSearchItem(searchData.get(getAdapterPosition()));
        }
    }

    public void add (SearchData data){
        if (data != null) {
            searchData.add(data);
            notifyDataSetChanged();
        }
    }
}
