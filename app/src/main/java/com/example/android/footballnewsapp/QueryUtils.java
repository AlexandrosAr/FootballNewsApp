package com.example.android.footballnewsapp;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class QueryUtils {

    public static final String LOG_TAG = QueryUtils.class.getSimpleName();

    //the constructor of QueryUtils is private because no-one should create a new inastance of the class
    //we will use only the static functions of this class
    private QueryUtils(){

    }

    public static List<Article> fetchArticlesData(String url_request) throws JSONException {
        //creation of a url object
        URL url = createUrl(url_request);

        //Perform a http request and get a JSON response
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e){
            Log.e(LOG_TAG, "Problem with retrieving the jsonResponse from makeHttpRequest", e);

        }

        //Extract the important fields of json response and create the arrayList
        List<Article> articles = extractFeaturesFromJson(jsonResponse);
        return articles;
    }



    //Returns a url object depending on the given string url

    public static URL createUrl(String url_string){
        URL url = null;
        try {
            url = new URL(url_string);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem with url creation", e);
        }
        return url;
    }

    //Make an http request and return a json response
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if(url==null){
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try{
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            //if the request is succesfull returns the code 200
            //then we read the stream
            if(urlConnection.getResponseCode() == 200){
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code "+urlConnection.getResponseCode());
            }

        } catch (IOException e){
            Log.e(LOG_TAG, "Problem retrieving the jsonResponse", e);
        } finally {
            if(urlConnection != null){
                urlConnection.disconnect();
            }

            if(inputStream != null){
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream){
        StringBuilder output = new StringBuilder();
        try {
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line = bufferedReader.readLine();
                while (line != null) {
                    output.append(line);
                    line = bufferedReader.readLine();
                }
            }
        } catch (IOException e){
            Log.e(LOG_TAG, "Problem reading a line ", e);
        }
        return output.toString();
    }

    //Return the arrayList of articles after retrieving the data from json parsing
    private static List<Article> extractFeaturesFromJson(String jsonResponse) throws JSONException {

        ArrayList<Article> articles = new ArrayList<Article>();

        JSONObject root = new JSONObject(jsonResponse);

        JSONObject response = root.getJSONObject("response");

        JSONArray results = response.getJSONArray("results");

        //loop through all results in array
        for (int i = 0; i < results.length(); i++){

            JSONObject article = results.getJSONObject(i);

            JSONArray tags = article.getJSONArray("tags");

            String articleAuthor = null;
            for (int j = 0; j < tags.length(); j++){

                JSONObject author = tags.optJSONObject(j);

                articleAuthor = author.optString("webTitle");
            }

            String articleTitle = article.getString("webTitle");

            String articleTime = article.getString("webPublicationDate");

            String articleType = article.getString("type");

            String articleSection = article.getString("sectionName");

            String articleUrl = article.getString("webUrl");

            articles.add(new Article(articleTitle, articleAuthor, articleTime, articleType, articleSection, articleUrl));

        }
        return articles;

    }





}
