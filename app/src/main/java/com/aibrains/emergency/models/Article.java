package com.aibrains.emergency.models;

public class Article {
    public String headline , subline , imageLink , id;

    public Article() {
    }

    public Article(String headline, String subline, String imageLink, String id) {
        this.headline = headline;
        this.subline = subline;
        this.imageLink = imageLink;
        this.id = id ;
    }
}
