package com.example.moamen.moviesapp;

import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 * Created by Moamen on 3/24/2016.
 */
public class MoviesAdapter extends ArrayAdapter<Movies> {

    public MoviesAdapter(AppCompatActivity context, ArrayList<Movies> movies){
        super(context,0, movies);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View gridView = convertView;
        if(gridView == null) {
            gridView = LayoutInflater.from(getContext()).inflate(R.layout.grid_view, parent, false);
        }

        Movies currentMovie = getItem(position);
        ImageView imageView = (ImageView) gridView.findViewById(R.id.imageView);
        Picasso.with(getContext()).load("http://image.tmdb.org/t/p/w185/" + currentMovie.getPoster()).into(imageView);

        return gridView;
    }
}
