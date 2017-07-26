package com.sunkin.itunessearch.ui;


import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sunkin.itunessearch.ConnectionListener;
import com.sunkin.itunessearch.FirebaseHelper;
import com.sunkin.itunessearch.R;
import com.sunkin.itunessearch.ResponseHandler;
import com.sunkin.itunessearch.Utility;
import com.sunkin.itunessearch.data.SearchAdapter;
import com.sunkin.itunessearch.data.SearchData;
import com.sunkin.itunessearch.data.SearchResponse;
import com.sunkin.itunessearch.database.SearchContract;
import com.sunkin.itunessearch.network.ApiClient;
import com.sunkin.itunessearch.network.SearchNetworkInterface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import static com.sunkin.itunessearch.Utility.FALSE;
import static com.sunkin.itunessearch.Utility.TRUE;

public class MainActivity extends AppCompatActivity implements SearchAdapter.SearchItemOnClickHandler, ResponseHandler, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String SEARCH_TEXT_FAB = "search_text_fab";
    public static final String SEARCH_DIALOG_FRAGMENT = "search_dialog_fragment";
    public static final String ALERT_DIALOG_FRAGMENT = "alert_dialog_fragment";
    public static final String ANONYMOUS = "anonymous";
    private static final String SEARCH_DATA_KEY = "search_data_key";
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
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseHelper firebaseHelper;
    private FirebaseAuth.AuthStateListener authStateListener;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private CompositeDisposable compositeDisposable;

    private ConnectionListener networkChangeReceiver = new ConnectionListener();
    IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Log.d(TAG, "OnCreate");
        initSthetho();
        Utility.saveHomeScreenShowed(this, true);
        getSupportLoaderManager().initLoader(LOADER, null, this);
        searchDataArrayList = new ArrayList<>();
        searchAdapter = new SearchAdapter(MainActivity.this, MainActivity.this, searchDataArrayList);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseHelper = new FirebaseHelper(this, databaseReference);
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    Toast.makeText(MainActivity.this, R.string.user_loggedin_msg, Toast.LENGTH_SHORT).show();
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

    private void initSthetho() {
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(
                                Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(
                                Stetho.defaultInspectorModulesProvider(this))
                        .build());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(SEARCH_DATA_KEY, searchDataArrayList);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        searchDataArrayList = savedInstanceState.getParcelableArrayList(SEARCH_DATA_KEY);
        if (searchDataArrayList != null && searchDataArrayList.size() != 0) {
            searchAdapter = new SearchAdapter(this, this, searchDataArrayList);
            recyclerView.setAdapter(searchAdapter);
            emptyTextView.setVisibility(View.GONE);
        }
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
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (networkChangeReceiver != null) {
            Log.d(TAG, "Unregistering receiver");
            this.unregisterReceiver(networkChangeReceiver);
            networkChangeReceiver = null;
        }
    }

    private void onSignedInInitailize(String userName) {
        mUsername = userName;
        Log.d(TAG, "onSignedInInitailize");
        searchDataArrayList = Utility.getFavoriteCollection(this);
        if (searchDataArrayList != null &&
                searchDataArrayList.size() != 0 &&
                Utility.homeScreenEverShowed(this)) {
            Utility.saveHomeScreenShowed(this, false);
            showAlertDialog();
        }
    }

    private void showAlertDialog() {
        AlertDialog alertDialog = new AlertDialog();
        alertDialog.show(getFragmentManager(), ALERT_DIALOG_FRAGMENT);

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
        dialog.show(getFragmentManager(), SEARCH_DIALOG_FRAGMENT);
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
        if (data != null && !Utility.isItemExists(this, data)) {
            data.setIsFavorite(TRUE);
            Utility.saveFav(this, data);
            firebaseHelper.save(data);
            showSnackBar(getString(R.string.added_to_fav));
        } else {
            showSnackBar(getString(R.string.fav_already_exists_msg));
        }
    }

    @Override
    public void handleFavoriteRemove(SearchData searchData) {
        if (searchData != null && Utility.isItemExists(this, searchData)) {
            searchData.setIsFavorite(FALSE);
            Utility.removeFav(this, searchData);
            showSnackBar(getString(R.string.removed_from_fav));
        } else {
            showSnackBar(getString(R.string.item_not_exists));
        }
        showFavoritesList();
    }

    void foundFavs() {
        showFavoritesList();
    }

    /**
     * Method used to save search keyword and entity
     */
    void doSearch() {
        Log.d(TAG, "Search started, searching for " + Utility.getSearchKeyword(this) + " in " + Utility.getSearchEntity(this) + " category");
        String keyword = Utility.getSearchKeyword(this);
        String entity = Utility.getSearchEntity(this);

        Observable<SearchResponse> responseObservable = ApiClient.getClient().create(SearchNetworkInterface.class)
                .getSearchResults(keyword, entity)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());

        responseObservable.subscribe(this::handleSuccess, this::handleError);
    }

    private void handleError(Throwable e) {
        Log.e(TAG, "Error: " + e.getLocalizedMessage(), e);
    }

    public void handleSuccess(SearchResponse response){
        searchDataArrayList = response.getResults();
        if (searchDataArrayList.size() != 0) {
            emptyTextView.setVisibility(View.GONE);
            Log.d(TAG, "Received response successfully : " + searchDataArrayList.toString());
            searchAdapter = new SearchAdapter(MainActivity.this, MainActivity.this, searchDataArrayList);
            recyclerView.setAdapter(searchAdapter);
            searchAdapter.notifyDataSetChanged();
        } else {
            emptyTextView.setVisibility(View.VISIBLE);
            showSnackBar(getString(R.string.error_in_keyword));
        }
    }

    @Override
    public void updateSearchResults(ArrayList<SearchData> searchData) {
//        searchDataArrayList = searchData;
//        if (searchDataArrayList.size() != 0) {
//            emptyTextView.setVisibility(View.GONE);
//            Log.d(TAG, "Received response successfully : " + searchData.toString());
//            searchAdapter = new SearchAdapter(MainActivity.this, MainActivity.this, searchDataArrayList);
//            recyclerView.setAdapter(searchAdapter);
//            searchAdapter.notifyDataSetChanged();
//        } else {
//            emptyTextView.setVisibility(View.VISIBLE);
//            showSnackBar(getString(R.string.error_in_keyword));
//        }
    }

    public void showFavoritesList() {
        searchDataArrayList = (Utility.getFavoriteCollection(this));
        if (searchDataArrayList.size() != 0) {
            emptyTextView.setVisibility(View.GONE);
            searchAdapter = new SearchAdapter(MainActivity.this, this, searchDataArrayList);
            staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(staggeredGridLayoutManager);
            recyclerView.setAdapter(searchAdapter);
            searchAdapter.notifyDataSetChanged();
        } else {
            searchAdapter.clear();
            searchAdapter.notifyDataSetChanged();
            emptyTextView.setVisibility(View.VISIBLE);
            showSnackBar(getString(R.string.fav_collection_empty));
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, SearchContract.SearchEntry.CONTENT_URI,
                SearchContract.SearchEntry.SEARCH_COLUMNS,
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.getCount() != 0) {
            searchAdapter.setCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        searchAdapter.setCursor(null);
    }

}
