package com.example.android.footballnewsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Article>> {

    //format=json&order-by=newest&show-tags=contributor&q=football&section=football&api-key=c2451dc1-cae2-496b-8037-c63814390c12&page-size=20
    private static final String REQUEST_URL = "https://content.guardianapis.com/search";

    private static final String LOG_TAG = MainActivity.class.getName();

    private static final int ARTICLE_LOADER_ID = 1;

    private ArticleAdapter articleAdapter;

    private TextView emptyStateTextView;

    private NetworkInfo networkInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //find the listview of our layout, create an ArticleAdapter with an empty List, and connect it to out listView
        ListView listView = (ListView) findViewById(R.id.article_list);
        articleAdapter = new ArticleAdapter(this, new ArrayList<Article>());
        listView.setAdapter(articleAdapter);

        //Set emptyListView state to our listView
        emptyStateTextView = (TextView) findViewById(R.id.empty_view);
        listView.setEmptyView(emptyStateTextView);

        //SetOnItemClickListener to use our intent when tapping to an article
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //find the article which was tapped
                Article currentArticle = articleAdapter.getItem(i);
                //convert the string url of the article to an url object to pass to intent
                Uri articleUrl = Uri.parse(currentArticle.getArticleUrl());

                //Create a new intent to view the url of the article
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, articleUrl);

                //Send the intent to launch a new activity
                startActivity(websiteIntent);

            }
        });


        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        // Get details on the currently active default data network
        networkInfo = connMgr.getActiveNetworkInfo();

        //if there is a connection then fetch the data from the json
        if (networkInfo != null && networkInfo.isConnected()){
            //Get a reference to a LoaderManager
            LoaderManager loaderManager = getLoaderManager();
            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(ARTICLE_LOADER_ID, null, this);
        } else {
            //Otherwise display error
            //find the indicator View and hide it
            View loadingIndicator = (View) findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);
            //Update emptyState with connection error problem
            emptyStateTextView.setText(R.string.no_connection);

        }

    }

    @Override
    public Loader<List<Article>> onCreateLoader(int i, Bundle bundle) {
        //Create a new loader

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // getString retrieves a String value from the preferences. The second parameter is the default value for this preference.
        String pageSize = sharedPrefs.getString(
                getString(R.string.settings_page_size_key),
                getString(R.string.settings_page_size_default));

        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));

        // parse breaks apart the URI string that's passed into its parameter
        Uri baseUri = Uri.parse(REQUEST_URL);

        // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        Uri.Builder uriBuilder = baseUri.buildUpon();

        // Append query parameter and its value. For example, the `format=geojson`
        uriBuilder.appendQueryParameter("format", "json");
        uriBuilder.appendQueryParameter("order-by", orderBy);
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        uriBuilder.appendQueryParameter("q", "football");
        uriBuilder.appendQueryParameter("section", "football");
        uriBuilder.appendQueryParameter("api-key", "c2451dc1-cae2-496b-8037-c63814390c12");
        uriBuilder.appendQueryParameter("page-size", pageSize);



        return new ArticleLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Article>> loader, List<Article> articles) {
        //Hide the loading indicator because the loading has finished
        View loadingIndicator = (View) findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        //Set the emptyStateTextView to no article found
        //emptyStateTextView.setText(R.string.empty_view);

        //Clear the adapter of the previous news
        articleAdapter.clear();

        //add the new article to adapter
        if ( articles != null && !articles.isEmpty()){
            articleAdapter.addAll(articles);
        } else {

            if(!networkInfo.isConnected()){
                //Set the emptyStateTextView to no article found
                emptyStateTextView.setText(R.string.empty_view);
            } else {
                //Update emptyState with connection error problem
                emptyStateTextView.setText(R.string.no_connection);
            }
        }

    }

    @Override
    public void onLoaderReset(Loader<List<Article>> loader) {
        //Loader reset, so we can clear our data
        articleAdapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_settings){
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
