package com.example.moamen.moviesapp;

import java.io.Serializable;

/**
 * Created by Moamen on 3/24/2016.
 */
public class Movies implements Serializable {
    private String id;
    private String poster;
    private String title;
    private String overview;
    private String releaseDate;
    private String voteAverage;
    private String[] trailers;
    private String[] reviews;

    public Movies(String movie){
        String[] info = movie.split("__");
        id = info[0];
        poster = info[1];
        title = info[2];
        voteAverage = info[3];
        releaseDate = info[4];
        overview = info[5];
    }

    public String getId() {
        return id;
    }

    public String getPoster(){
        return this.poster;
    }
    public String getTitle(){
        return this.title;
    }
    public String getOverview() {
        return overview;
    }
    public String getReleaseDate() {
        return releaseDate;
    }
    public String getVoteAverage() {
        return voteAverage;
    }
}
