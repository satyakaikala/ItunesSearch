package com.sunkin.itunessearch.ui;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.os.Bundle;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;

import com.sunkin.itunessearch.R;
import com.sunkin.itunessearch.data.SearchAdapter;
import com.sunkin.itunessearch.data.SearchData;
import com.sunkin.itunessearch.data.SearchResponse;
import com.sunkin.itunessearch.network.ApiClient;
import com.sunkin.itunessearch.network.SearchNetworkInterface;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements SearchAdapter.SearchItemOnClickHandler{

    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    private SearchAdapter searchAdapter;
    private ArrayList<SearchData> searchDataArrayList;

    private static SearchNetworkInterface searchNetwork;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        searchNetwork = ApiClient.getClient().create(SearchNetworkInterface.class);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void  handleIntent(Intent intent){
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Call<SearchResponse> call = searchNetwork.getSearchResults(query, "musicVideo");
            call.enqueue(new Callback<SearchResponse>() {
                @Override
                public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                    searchDataArrayList = response.body().getResults();
                    searchAdapter = new SearchAdapter(MainActivity.this, MainActivity.this, searchDataArrayList);
                    recyclerView.setAdapter(searchAdapter);
                    StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                    recyclerView.setLayoutManager(staggeredGridLayoutManager);
                    searchAdapter.notifyDataSetChanged();
                }

                @Override
                public void onFailure(Call<SearchResponse> call, Throwable t) {
                    Log.d(TAG, "Search failure" + t.toString());

                }
            });
        }
    }

    @Override
    public void onClickSearchItem(SearchData searchData) {

    }
}
