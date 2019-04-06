package com.example.android.footballnewsapp;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ArticleAdapter extends ArrayAdapter<Article> {


    public ArticleAdapter(Activity context, ArrayList<Article> articles) {
        super((Context)context, 0, articles);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listItemView = convertView;

        if (listItemView==null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        Article currentArticle = getItem(position);

        //set the article name to textView

        TextView articleTitleView = (TextView) listItemView.findViewById(R.id.title);
        articleTitleView.setText(currentArticle.getArticleTitle());

        TextView articleAuthorView = (TextView) listItemView.findViewById(R.id.author);
        articleAuthorView.setText(currentArticle.getArticleAuthor());

        TextView articleTimeView = (TextView) listItemView.findViewById(R.id.release_time);
        articleTimeView.setText(getTime(currentArticle.getArticleTime()));

        String date = getStringDate(currentArticle.getArticleTime());
        String articleDate = getDate(date);

        TextView articleDateView = (TextView) listItemView.findViewById(R.id.release_date);
        articleDateView.setText(articleDate);

        TextView articleTypeView = (TextView) listItemView.findViewById(R.id.article_type);
        articleTypeView.setText(currentArticle.getArticleType());

        TextView articleSectionView = (TextView) listItemView.findViewById(R.id.article_section);
        articleSectionView.setText(currentArticle.getArticleSection());


        return listItemView;
    }

    public  String getTime(String articleTime){
        String[] tokens = articleTime.split("T");
        String[] time = tokens[1].split("Z");
        String[] t = time[0].split(":");
        String finalTime = t[0]+":"+t[1];
        return finalTime;
    }

    public String getStringDate(String articleTime){
        String[] tokens = articleTime.split("T");
        String[] reformatDate = tokens[0].split("-");
        String reformatedDate = reformatDate[2]+"-"+reformatDate[1]+"-"+reformatDate[0];
        return reformatedDate;
    }

    public String getDate(String d){
        SimpleDateFormat dateFormater = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat dateFormater1 = new SimpleDateFormat("EEEE MMMM yyyy");
        Date dateObject = null;
        String date = null;
        try {
            dateObject = dateFormater.parse(d);
            date = dateFormater1.format(dateObject);
        } catch (ParseException e){
            e.printStackTrace();
        }
        return date;
    }

}
