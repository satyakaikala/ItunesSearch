package com.sunkin.itunessearch.ui;


import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.os.Bundle;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.sunkin.itunessearch.ConnectionListener;
import com.sunkin.itunessearch.R;
import com.sunkin.itunessearch.Utility;
import com.sunkin.itunessearch.data.SearchAdapter;
import com.sunkin.itunessearch.data.SearchData;
import com.sunkin.itunessearch.fetch.FetchSearchItems;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.sunkin.itunessearch.Utility.FALSE;
import static com.sunkin.itunessearch.Utility.TRUE;

public class MainActivity extends AppCompatActivity implements SearchAdapter.SearchItemOnClickHandler, FetchSearchItems.ResponseHandler {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String SEARCH_TEXT_FAB = "search_text_fab";
    public static final String FRAGMENT_TAG = "search_dialog_fragment";
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.empty_list_view)
    TextView emptyTextView;
    @BindView(R.id.progress)
    ProgressBar progressBar;
    @BindView(R.id.adView)
    AdView adView;

    private SearchAdapter searchAdapter;
    private ArrayList<SearchData> searchDataArrayList;
    private SearchView searchView;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;

    private ConnectionListener networkChangeReceiver = new ConnectionListener();
    IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Log.d(TAG, "OnCreate");

        searchDataArrayList = new ArrayList<>();
        searchAdapter = new SearchAdapter(MainActivity.this, MainActivity.this, searchDataArrayList);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        MobileAds.initialize(this, getString(R.string.adAppId));
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

    }

    @Override
    protected void onStart() {
        super.onStart();
        this.registerReceiver(networkChangeReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.unregisterReceiver(networkChangeReceiver);
    }

    public void button(@SuppressWarnings("UnusedParameters") View view) {
        Log.d(TAG, "Fab selected");
        Bundle args = new Bundle();
        SearchDialog dialog = new SearchDialog();
        args.putString(SEARCH_TEXT_FAB, searchView.getQuery().toString());
        dialog.setArguments(args);
        dialog.show(getFragmentManager(), FRAGMENT_TAG);
        searchView.onActionViewCollapsed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    protected void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Utility.saveSearchKeyword(this, query);
            doSearch();
            searchView.setQuery("", false);
            searchView.clearFocus();
        }
    }

    @Override
    public void onClickSearchItem(SearchData searchData) {
        Log.d(TAG, "Selected item info : " + searchData.toString());
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT, searchData);
        startActivity(intent);
    }

    /**
     * Method used to save search keyword and entity
     */
    void doSearch() {
        Log.d(TAG, "Search started, searching for " + Utility.getSearchKeyword(this) + " in " + Utility.getSearchEntity(this) + " category");
        String keyword = Utility.getSearchKeyword(this);
        String entity = Utility.getSearchEntity(this);

        FetchSearchItems doSearch = new FetchSearchItems(this, MainActivity.this);
        doSearch.execute(keyword, entity);
    }

    @Override
    public void updateSearchResults(ArrayList<SearchData> searchData) {
        displayItems(searchData);
    }

    public void displayItems(ArrayList<SearchData> searchData) {
        if (searchData.size() != 0) {
            emptyTextView.setVisibility(View.GONE);
            Log.d(TAG, "Received response successfully : " + searchData.toString());
            searchAdapter = new SearchAdapter(MainActivity.this, MainActivity.this, searchData);
            recyclerView.setAdapter(searchAdapter);
            StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(staggeredGridLayoutManager);
            searchAdapter.notifyDataSetChanged();
        } else {
            emptyTextView.setVisibility(View.VISIBLE);
            showSnackBar(getString(R.string.error_in_keyword));
        }
    }

    private void showSnackBar(String msg) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.coordinator), msg, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    @Override
    public void searchStarted() {
        Log.d(TAG, "Search started, showing progress ");
        emptyTextView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void searchCompleted() {
        Log.d(TAG, "Search completed, stopped progress ");
        progressBar.setVisibility(View.GONE);
        searchView.clearFocus();
        searchView.onActionViewCollapsed();
    }
}
