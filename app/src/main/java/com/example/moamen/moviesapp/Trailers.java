package com.example.moamen.moviesapp;

import java.io.Serializable;

/**
 * Created by Moamen on 4/21/2016.
 */
public class Trailers implements Serializable {
    private String name;
    private String key;

    public Trailers(String trailer){
        String[] info = trailer.split("__");
        name = info[0];
        key = info[1];
    }

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }
}
