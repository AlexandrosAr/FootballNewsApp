package com.example.android.footballnewsapp;

public class Article {

    private String articleTitle;

    private String articleAuthor;

    private String articleTime;

    private String articleType;

    private String articleSection;

    private String articleUrl;

    public Article(String title, String author, String time, String type, String section, String url){

        articleTitle = title;

        articleAuthor = author;

        articleTime = time;

        articleType = type;

        articleSection = section;

        articleUrl = url;
    }

    public String getArticleTitle() {
        return articleTitle;
    }

    public String getArticleAuthor() {
        return articleAuthor;
    }

    public String getArticleTime() {
        return articleTime;
    }

    public String getArticleType() {
        return articleType;
    }

    public String getArticleSection() {
        return articleSection;
    }

    public String getArticleUrl() {
        return articleUrl;
    }



}
