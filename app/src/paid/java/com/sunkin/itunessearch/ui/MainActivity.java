package com.sunkin.itunessearch.ui;


import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.os.Bundle;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sunkin.itunessearch.ConnectionListener;
import com.sunkin.itunessearch.R;
import com.sunkin.itunessearch.Utility;
import com.sunkin.itunessearch.data.SearchAdapter;
import com.sunkin.itunessearch.data.SearchData;
import com.sunkin.itunessearch.database.SearchContract;
import com.sunkin.itunessearch.fetch.FetchSearchItems;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.sunkin.itunessearch.Utility.FALSE;
import static com.sunkin.itunessearch.Utility.TRUE;

public class MainActivity extends AppCompatActivity implements SearchAdapter.SearchItemOnClickHandler, FetchSearchItems.ResponseHandler, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String SEARCH_TEXT_FAB = "search_text_fab";
    public static final String FRAGMENT_TAG = "search_dialog_fragment";
    public static final String ANONYMOUS = "anonymous";
    private String mUsername;
    private static final int LOADER = 0;
    private static final int RC_SIGN_IN = 1;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.empty_list_view)
    TextView emptyTextView;
    @BindView(R.id.progress)
    ProgressBar progressBar;

    private SearchAdapter searchAdapter;
    private ArrayList<SearchData> searchDataArrayList;
    private SearchView searchView;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    private ConnectionListener networkChangeReceiver = new ConnectionListener();
    IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Log.d(TAG, "OnCreate");
        getSupportLoaderManager().initLoader(LOADER, null, this);
        searchDataArrayList = new ArrayList<>();
        searchAdapter = new SearchAdapter(MainActivity.this, MainActivity.this, searchDataArrayList);
        firebaseAuth = FirebaseAuth.getInstance();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    Toast.makeText(MainActivity.this, "You are now signed in. Welcome to Itunes Search.", Toast.LENGTH_SHORT).show();
                    onSignedInInitailize(user.getDisplayName());
                } else {
                    onSignesOutCleanUp();
                    //we can pass this providers as setProviders is deprecated
                    List<AuthUI.IdpConfig> providers = Arrays.asList(
                            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                            new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build()
                    );

                    startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
                            .setTheme(R.style.GreenTheme)
                            .setIsSmartLockEnabled(false)
                            .setProviders(providers)
                            .build(), RC_SIGN_IN);
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.registerReceiver(networkChangeReceiver, filter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
        searchAdapter.clear();
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.unregisterReceiver(networkChangeReceiver);
    }

    private void onSignedInInitailize(String userName) {
        mUsername = userName;
    }

    private void onSignesOutCleanUp() {
        mUsername = ANONYMOUS;
        searchAdapter.clear();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.favorites:
                searchView.clearFocus();
                searchView.onActionViewCollapsed();
                showFavoritesList();
                return true;
            case R.id.sign_out_menu:
                AuthUI.getInstance().signOut(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

    @Override
    public void handleFavoriteAdd(SearchData data) {
        Log.d(TAG, "Adding to favorite list");
        if (data != null) {
            data.setIsFavorite(TRUE);
            Utility.saveFav(this, data);
        }
    }

    @Override
    public void handleFavoriteRemove(SearchData searchData) {
        if (searchData != null) {
            searchData.setIsFavorite(FALSE);
            Utility.removeFav(this, searchData);
        }
        showFavoritesList();
//        emptyTextView.setVisibility(View.VISIBLE);
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

    public void showFavoritesList() {
        displayItems(Utility.getFavoriteCollection(this));
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
            Snackbar snackbar = Snackbar.make(findViewById(R.id.coordinator), "Please enter valid Search keyword", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
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
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, SearchContract.SearchEntry.CONTENT_URI,
                SearchContract.SearchEntry.SEARCH_COLUMNS,
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.getCount() != 0) {

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

}
