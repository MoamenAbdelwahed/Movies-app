package com.example.moamen.moviesapp;

/**
 * Created by Moamen on 4/22/2016.
 */
public class Reviews {
    private String author;
    private String content;
    public Reviews(String params){
        String[] info = params.split("__");
        author = info[0];
        content = info[1];
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }
}
