package com.example.android.footballnewsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import org.json.JSONException;

import java.util.List;

public class ArticleLoader extends AsyncTaskLoader<List<Article>> {

    private static final String LOG_TAG = ArticleLoader.class.getName();

    //query url
    private String apiUrl;

    public ArticleLoader(Context context, String url){
        super(context);
        apiUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Article> loadInBackground() {
        //if there is no url dont start a background thread
        if (apiUrl == null){
            return null;
        }

        //Make a http connection an parse the json data
        List<Article> articles = null;
        try{
            articles = QueryUtils.fetchArticlesData(apiUrl);
        } catch (JSONException e){
            Log.e(LOG_TAG, "There is a problem with json parsing", e);
        }
        return articles;
    }
}
